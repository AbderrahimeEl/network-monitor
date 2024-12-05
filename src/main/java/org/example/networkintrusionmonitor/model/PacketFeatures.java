package org.example.networkintrusionmonitor.model;

public class PacketFeatures {
    private long sourceIpNumeric;
    private long destinationIpNumeric;
    private int sourcePort;
    private int destinationPort;
    private int protocol;
    private long packetLength;
    private int packetType;

    // Getters and setters
    public long getSourceIpNumeric() {
        return sourceIpNumeric;
    }

    public void setSourceIpNumeric(long sourceIpNumeric) {
        this.sourceIpNumeric = sourceIpNumeric;
    }

    public long getDestinationIpNumeric() {
        return destinationIpNumeric;
    }

    public void setDestinationIpNumeric(long destinationIpNumeric) {
        this.destinationIpNumeric = destinationIpNumeric;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public long getPacketLength() {
        return packetLength;
    }

    public void setPacketLength(long packetLength) {
        this.packetLength = packetLength;
    }

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }
}
