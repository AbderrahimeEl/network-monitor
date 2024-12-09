package org.example.networkintrusionmonitor.service;


import org.example.networkintrusionmonitor.model.PacketInfo;
import org.example.networkintrusionmonitor.repository.PacketInfoRepository;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.IpNumber;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkCaptureService {
    private final PacketInfoRepository repository;
    private PcapNetworkInterface nif;
    private PcapHandle handle;
    private boolean isCapturing = false;
    private ExecutorService executorService;

    public NetworkCaptureService() {
        this.repository = new PacketInfoRepository();
        // Initialize packet_info table
        repository.initializeTable();
    }

    public void startCapture(PcapNetworkInterface networkInterface) throws PcapNativeException {
        repository.truncatePacketInfoTable();

        if (networkInterface == null) {
            throw new IllegalArgumentException("Network interface cannot be null");
        }

        // Open the network interface for capturing
        handle = networkInterface.openLive(
                65536, // Max capture size
                PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, // Promiscuous mode
                10 // Timeout in milliseconds
        );

        // Set up packet listener
        PacketListener listener = packet -> {
            if (packet != null) {
                processPacket(packet);
            }
        };

        // Start capturing in a separate thread
        executorService = Executors.newSingleThreadExecutor();
        isCapturing = true;

        executorService.submit(() -> {
            try {
                // Capture packets indefinitely
                while (isCapturing) {
                    handle.loop(-1, listener); // -1 means infinite loop
                }
            } catch (InterruptedException | NotOpenException | PcapNativeException e) {
                e.printStackTrace();
            } finally {
                stopCapture();
            }
        });
    }

    private void processPacket(Packet packet) {
        try {
            // Check if the packet contains an IPv4 packet
            IpV4Packet ipPacket = packet.get(IpV4Packet.class);
            if (ipPacket == null) {
                System.out.println("Packet is not an IPv4 packet: " + packet);
                return;
            }

            String sourceIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
            String destIp = ipPacket.getHeader().getDstAddr().getHostAddress();

            String protocol = determineProtocol(packet);
            int sourcePort = extractSourcePort(packet);
            int destPort = extractDestinationPort(packet);

            // Extract the hex stream from the raw packet data
            String rawData = packet.toString();
            String hexStream = extractHexStream(rawData);

            // Convert hex stream to readable content
            String decodedContent = decodeHexToString(hexStream);

            // Create PacketInfo object
            PacketInfo packetInfo = new PacketInfo(
                    sourceIp,
                    sourcePort,
                    destIp,
                    destPort,
                    protocol,
                    packet.length(),
                    packet.getClass().getSimpleName(),
                    LocalDateTime.now(),
                    rawData,
                    hexStream,
                    decodedContent);

            // Save to database
            repository.savePacketInfo(packetInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String determineProtocol(Packet packet) {
        // Check if the packet is an EthernetPacket
        if (packet instanceof EthernetPacket ethernetPacket) {
            Packet payload = ethernetPacket.getPayload();

            // Check if the payload is an IpV4Packet
            if (payload instanceof IpV4Packet ipPacket) {
                IpNumber protocol = ipPacket.getHeader().getProtocol();

                // Determine protocol based on IP number
                switch (protocol.valueAsString()) {
                    case "6":
                        return "TCP";
                    case "17":
                        return "UDP";
                    case "1":
                        return "ICMP";
                    case "2":
                        return "IGMP"; // Internet Group Management Protocol
                    case "89":
                        return "OSPF"; // Open Shortest Path First
                    case "50":
                        return "ESP"; // Encapsulating Security Payload
                    case "51":
                        return "AH"; // Authentication Header
                    case "41":
                        return "IPv6"; // IPv6 encapsulation
                    case "4":
                        return "IP-in-IP"; // IP in IP encapsulation
                    case "132":
                        return "SCTP"; // Stream Control Transmission Protocol
                    case "94":
                        return "IPIP"; // IP-within-IP Encapsulation Protocol
                    case "58":
                        return "ICMPv6"; // ICMP for IPv6
                    case "47":
                        return "GRE"; // Generic Routing Encapsulation
                    case "115":
                        return "L2TP"; // Layer Two Tunneling Protocol
                    default:
                        return "Unknown (" + protocol.valueAsString() + ")";
                }
            } else {
                System.out.println("Payload is not an IpV4Packet.");
            }
        } else {
            System.out.println("Packet is not an EthernetPacket.");
        }
        return "Unknown";
    }

    private int extractSourcePort(Packet packet) {
        try {
            if (packet.contains(TcpPacket.class)) {
                return packet.get(TcpPacket.class).getHeader().getSrcPort().valueAsInt();
            }
            if (packet.contains(UdpPacket.class)) {
                return packet.get(UdpPacket.class).getHeader().getSrcPort().valueAsInt();
            }
        } catch (Exception e) {
            // Ignore if port extraction fails
        }
        return -1;
    }

    private int extractDestinationPort(Packet packet) {
        try {
            if (packet.contains(TcpPacket.class)) {
                return packet.get(TcpPacket.class).getHeader().getDstPort().valueAsInt();
            }
            if (packet.contains(UdpPacket.class)) {
                return packet.get(UdpPacket.class).getHeader().getDstPort().valueAsInt();
            }
        } catch (Exception e) {
            // Ignore if port extraction fails
        }
        return -1;
    }

    public void stopCapture() {
        isCapturing = false;

        if (handle != null) {
            try {
                handle.breakLoop();
            } catch (NotOpenException e) {
                System.err.println("Handle is not open, cannot break loop: " + e.getMessage());
            }

            handle.close();
        }

        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private String extractHexStream(String rawData) {
        int hexStartIndex = rawData.indexOf("Hex stream:");
        if (hexStartIndex == -1) {
            return "";
        }

        return rawData.substring(hexStartIndex + 11).trim(); // Extract everything after "Hex stream:"
    }

    private String decodeHexToString(String hexStream) {
        if (hexStream == null || hexStream.isEmpty()) {
            return "";
        }

        StringBuilder decoded = new StringBuilder();
        String[] hexBytes = hexStream.split("\\s+"); // Split hex by spaces

        for (String hexByte : hexBytes) {
            try {
                int charCode = Integer.parseInt(hexByte, 16);
                if (charCode >= 32 && charCode <= 126) { // Only include printable ASCII characters
                    decoded.append((char) charCode);
                } else {
                    decoded.append('.'); // Non-printable characters as "."
                }
            } catch (NumberFormatException e) {
                decoded.append('.'); // Invalid hex byte
            }
        }

        return decoded.toString();
    }

}
