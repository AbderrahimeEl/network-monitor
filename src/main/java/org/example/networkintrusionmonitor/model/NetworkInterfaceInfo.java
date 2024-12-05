package org.example.networkintrusionmonitor.model;

import org.pcap4j.core.PcapNetworkInterface;

public class NetworkInterfaceInfo {
    private final PcapNetworkInterface networkInterface;
    private final String name;
    private final String description;

    public NetworkInterfaceInfo(PcapNetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
        this.name = networkInterface.getName();
        this.description = networkInterface.getDescription();
    }

    public PcapNetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + (description != null ? " - " + description : "");
    }
}