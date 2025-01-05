package org.example.networkintrusionmonitor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.networkintrusionmonitor.model.ConnectionInfo;
import org.example.networkintrusionmonitor.model.IpTrafficInfo;
import org.example.networkintrusionmonitor.model.TrafficStatistics;

public class TrafficAnalysisController {
    private final ObservableList<ConnectionInfo> connections = FXCollections.observableArrayList();
    private final ObservableList<IpTrafficInfo> ipTraffic = FXCollections.observableArrayList();
    @FXML
    private Label activeConnectionsCount;
    @FXML
    private TableView<ConnectionInfo> connectionsTable;
    @FXML
    private TableView<IpTrafficInfo> ipTrafficTable;
    @FXML
    private Label totalPacketsLabel;
    @FXML
    private Label totalBytesLabel;
    @FXML
    private Label avgPacketSizeLabel;
    @FXML
    private Label topProtocolLabel;
    @FXML
    private TableColumn<ConnectionInfo, String> sourceIpColumn;
    @FXML
    private TableColumn<ConnectionInfo, String> destIpColumn;
    @FXML
    private TableColumn<ConnectionInfo, String> protocolColumn;
    @FXML
    private TableColumn<ConnectionInfo, String> stateColumn;
    @FXML
    private TableColumn<IpTrafficInfo, String> ipAddressColumn;
    @FXML
    private TableColumn<IpTrafficInfo, Long> incomingPacketsColumn;
    @FXML
    private TableColumn<IpTrafficInfo, Long> outgoingPacketsColumn;
    @FXML
    private TableColumn<IpTrafficInfo, Long> totalBytesColumn;
    private AnalysisType analysisType;

    @FXML
    public void initialize() {
        try {
            sourceIpColumn.setCellValueFactory(cellData -> cellData.getValue().sourceIpProperty());
            destIpColumn.setCellValueFactory(cellData -> cellData.getValue().destinationIpProperty());
            protocolColumn.setCellValueFactory(cellData -> cellData.getValue().protocolProperty());
            stateColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
            connectionsTable.setItems(connections);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Set up cell value factories for IP traffic table
            ipAddressColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty());
            incomingPacketsColumn.setCellValueFactory(cellData -> cellData.getValue().incomingPacketsProperty().asObject());
            outgoingPacketsColumn.setCellValueFactory(cellData -> cellData.getValue().outgoingPacketsProperty().asObject());
            totalBytesColumn.setCellValueFactory(cellData -> cellData.getValue().totalBytesProperty().asObject());
            System.out.println("ipTraffic");
            ipTrafficTable.setItems(ipTraffic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStatistics(TrafficStatistics stats, AnalysisType analysisType) {
        this.analysisType = analysisType;

        connections.setAll(stats.getConnections());
        ipTraffic.setAll(stats.getIpTraffic());

        if (analysisType == AnalysisType.ACTIVE_CONNECTIONS) {
            activeConnectionsCount.setText("Active Connections: " + stats.getActiveConnectionsCount());
            connectionsTable.setItems(connections);
            connectionsTable.refresh();
        }

        if (analysisType == AnalysisType.TRAFIC_BY_IP) {
            ipTrafficTable.setItems(ipTraffic);
            ipTrafficTable.refresh();
        }

        if (analysisType == AnalysisType.GLOBAL_STATISTICS) {
            totalPacketsLabel.setText(String.format("%d", stats.getTotalPackets()));
            totalBytesLabel.setText(String.format("%.2f MB", stats.getTotalBytes() / (1024.0 * 1024.0)));
            avgPacketSizeLabel.setText(String.format("%.2f bytes", stats.getAveragePacketSize()));
            topProtocolLabel.setText(stats.getTopProtocol());
        }
    }
}
