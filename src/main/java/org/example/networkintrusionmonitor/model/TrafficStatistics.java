package org.example.networkintrusionmonitor.model;

import java.util.ArrayList;
import java.util.List;

public class TrafficStatistics {
    private int activeConnectionsCount;
    private List<ConnectionInfo> connections;
    private List<IpTrafficInfo> ipTraffic;
    private List<String> alerts;
    private long totalPackets;
    private long totalBytes;
    private double averagePacketSize;
    private String topProtocol;

    // Default constructor
    public TrafficStatistics() {
        this.connections = new ArrayList<>();
        this.ipTraffic = new ArrayList<>();
        this.alerts = new ArrayList<>();
        this.activeConnectionsCount = 0;
        this.totalPackets = 0;
        this.totalBytes = 0;
        this.averagePacketSize = 0.0;
        this.topProtocol = "";
    }

    // Full constructor
    public TrafficStatistics(int activeConnectionsCount, List<ConnectionInfo> connections,
                             List<IpTrafficInfo> ipTraffic, List<String> alerts,
                             long totalPackets, long totalBytes,
                             double averagePacketSize, String topProtocol) {
        this.activeConnectionsCount = activeConnectionsCount;
        this.connections = connections;
        this.ipTraffic = ipTraffic;
        this.alerts = alerts;
        this.totalPackets = totalPackets;
        this.totalBytes = totalBytes;
        this.averagePacketSize = averagePacketSize;
        this.topProtocol = topProtocol;
    }

    // Getters
    public int getActiveConnectionsCount() {
        return activeConnectionsCount;
    }

    // Setters
    public void setActiveConnectionsCount(int activeConnectionsCount) {
        this.activeConnectionsCount = activeConnectionsCount;
    }

    public List<ConnectionInfo> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionInfo> connections) {
        this.connections = connections;
    }

    public List<IpTrafficInfo> getIpTraffic() {
        return ipTraffic;
    }

    public void setIpTraffic(List<IpTrafficInfo> ipTraffic) {
        this.ipTraffic = ipTraffic;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<String> alerts) {
        this.alerts = alerts;
    }

    public long getTotalPackets() {
        return totalPackets;
    }

    public void setTotalPackets(long totalPackets) {
        this.totalPackets = totalPackets;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public double getAveragePacketSize() {
        return averagePacketSize;
    }

    public void setAveragePacketSize(double averagePacketSize) {
        this.averagePacketSize = averagePacketSize;
    }

    public String getTopProtocol() {
        return topProtocol;
    }

    public void setTopProtocol(String topProtocol) {
        this.topProtocol = topProtocol;
    }

    // Helper methods
    public void addConnection(ConnectionInfo connection) {
        this.connections.add(connection);
        this.activeConnectionsCount = this.connections.size();
    }

    public void addIpTraffic(IpTrafficInfo trafficInfo) {
        this.ipTraffic.add(trafficInfo);
    }

    public void addAlert(String alert) {
        this.alerts.add(alert);
    }

    public void updateAveragePacketSize() {
        if (totalPackets > 0) {
            this.averagePacketSize = (double) totalBytes / totalPackets;
        }
    }

    @Override
    public String toString() {
        return "TrafficStatistics{" +
                "activeConnections=" + activeConnectionsCount +
                ", totalPackets=" + totalPackets +
                ", totalBytes=" + totalBytes +
                ", averagePacketSize=" + averagePacketSize +
                ", topProtocol='" + topProtocol + '\'' +
                ", alerts=" + alerts.size() +
                '}';
    }
}