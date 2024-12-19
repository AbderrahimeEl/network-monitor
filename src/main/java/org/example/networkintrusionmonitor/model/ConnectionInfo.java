package org.example.networkintrusionmonitor.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConnectionInfo {
    private String sourceIp;
    private String destIp;
    private String protocol;
    private String state;
    private long packetCount;
    private long byteCount;

    public ConnectionInfo(String sourceIp, String destIp, String protocol, String state) {
        this.sourceIp = sourceIp;
        this.destIp = destIp;
        this.protocol = protocol;
        this.state = state;
        this.packetCount = 0;
        this.byteCount = 0;
    }

    // Getters and Setters
    public String getSourceIp() { return sourceIp; }
    public String getDestIp() { return destIp; }
    public String getProtocol() { return protocol; }
    public String getState() { return state; }
    public long getPacketCount() { return packetCount; }
    public long getByteCount() { return byteCount; }

    public void incrementPackets() {
        this.packetCount++;
    }

    public void addBytes(long bytes) {
        this.byteCount += bytes;
    }

    public StringProperty sourceIpProperty() {
        return new SimpleStringProperty(sourceIp);
    }

    public StringProperty destinationIpProperty() {
        return new SimpleStringProperty(destIp);
    }

    public StringProperty protocolProperty() {
        return new SimpleStringProperty(protocol);
    }

    public StringProperty stateProperty() {
        return new SimpleStringProperty(state);
    }
}