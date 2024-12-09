package org.example.networkintrusionmonitor.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class PacketInfo {
    private Long id;
    private String sourceIp;
    private int sourcePort;
    private String destinationIp;
    private int destinationPort;
    private String protocol;
    private long packetLength;
    private String packetType;
    private LocalDateTime timestamp;
    private String rawPacketData;
    private String hexStream;
    private String decodedContent;

    public PacketInfo(String sourceIp, int sourcePort, String destinationIp,
                      int destinationPort, String protocol, long packetLength,
                      String packetType, LocalDateTime timestamp, String rawPacketData,
                      String hexStream, String decodedContent) {
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
        this.protocol = protocol;
        this.packetLength = packetLength;
        this.packetType = packetType;
        this.timestamp = timestamp;
        this.rawPacketData = rawPacketData;
        this.hexStream = hexStream;
        this.decodedContent = decodedContent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public long getPacketLength() {
        return packetLength;
    }

    public void setPacketLength(long packetLength) {
        this.packetLength = packetLength;
    }

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRawPacketData() {
        return rawPacketData;
    }

    public void setRawPacketData(String rawPacketData) {
        this.rawPacketData = rawPacketData;
    }

    public String getHexStream() {
        return hexStream;
    }

    public void setHexStream(String hexStream) {
        this.hexStream = hexStream;
    }

    public String getDecodedContent() {
        return decodedContent;
    }

    public void setDecodedContent(String decodedContent) {
        this.decodedContent = decodedContent;
    }

    public PacketFeatures getFeatures() {
        PacketFeatures features = new PacketFeatures();
        features.setSourceIpNumeric(ipToLong(sourceIp));
        features.setDestinationIpNumeric(ipToLong(destinationIp));
        features.setSourcePort(sourcePort);
        features.setDestinationPort(destinationPort);
        features.setProtocol(protocolToNumeric(protocol));
        features.setPacketLength(packetLength);
        features.setPacketType(1);
        return features;
    }

    private long ipToLong(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= Integer.parseInt(octets[i]) & 0xFF;
        }
        return result;
    }

    private int protocolToNumeric(String protocol) {
        switch (protocol.toUpperCase()) {
            case "TCP":
                return 6;
            case "UDP":
                return 17;
            case "ICMP":
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "PacketInfo{" +
                "id=" + id +
                ", sourceIp='" + sourceIp + '\'' +
                ", sourcePort=" + sourcePort +
                ", destinationIp='" + destinationIp + '\'' +
                ", destinationPort=" + destinationPort +
                ", protocol='" + protocol + '\'' +
                ", packetLength=" + packetLength +
                ", packetType='" + packetType + '\'' +
                ", timestamp=" + timestamp +
                ", rawPacketData='" + rawPacketData + '\'' +
                ", hexStream='" + hexStream + '\'' +
                ", decodedContent='" + decodedContent + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PacketInfo that = (PacketInfo) o;
        return sourcePort == that.sourcePort &&
                destinationPort == that.destinationPort &&
                packetLength == that.packetLength &&
                Objects.equals(id, that.id) &&
                Objects.equals(sourceIp, that.sourceIp) &&
                Objects.equals(destinationIp, that.destinationIp) &&
                Objects.equals(protocol, that.protocol) &&
                Objects.equals(packetType, that.packetType) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(rawPacketData, that.rawPacketData) &&
                Objects.equals(hexStream, that.hexStream) &&
                Objects.equals(decodedContent, that.decodedContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sourceIp, sourcePort, destinationIp, destinationPort, protocol, packetLength,
                packetType, timestamp, rawPacketData, hexStream, decodedContent);
    }
}