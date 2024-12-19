package org.example.networkintrusionmonitor.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IpTrafficInfo {
    private String ipAddress;
    private long incomingPackets;
    private long outgoingPackets;
    private long totalBytes;

    public IpTrafficInfo(String ipAddress) {
        this.ipAddress = ipAddress;
        this.incomingPackets = 0;
        this.outgoingPackets = 0;
        this.totalBytes = 0;
    }

    // Getters
    public String getIpAddress() { return ipAddress; }
    public long getIncomingPackets() { return incomingPackets; }
    public long getOutgoingPackets() { return outgoingPackets; }
    public long getTotalBytes() { return totalBytes; }

    public void addIncomingPacket(long bytes) {
        incomingPackets++;
        totalBytes += bytes;
    }

    public void addOutgoingPacket(long bytes) {
        outgoingPackets++;
        totalBytes += bytes;
    }

    public StringProperty ipAddressProperty() {
        return new SimpleStringProperty(ipAddress);
    }

    public LongProperty incomingPacketsProperty() {
        return new SimpleLongProperty(incomingPackets);
    }

    public LongProperty outgoingPacketsProperty() {
        return new SimpleLongProperty(outgoingPackets);
    }

    public LongProperty totalBytesProperty() {
        return new SimpleLongProperty(totalBytes);
    }
} 
