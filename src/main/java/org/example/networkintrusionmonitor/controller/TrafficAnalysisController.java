package org.example.networkintrusionmonitor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.networkintrusionmonitor.model.ConnectionInfo;
import org.example.networkintrusionmonitor.model.IpTrafficInfo;
import org.example.networkintrusionmonitor.model.TrafficStatistics;

public class TrafficAnalysisController {
    @FXML
    private Label activeConnectionsCount;
    @FXML private TableView<ConnectionInfo> connectionsTable;
    @FXML private TableView<IpTrafficInfo> ipTrafficTable;
    @FXML private ListView<String> alertsList;
    @FXML private Label totalPacketsLabel;
    @FXML private Label totalBytesLabel;
    @FXML private Label avgPacketSizeLabel;
    @FXML private Label topProtocolLabel;
    @FXML private TableColumn<ConnectionInfo, String> sourceIpColumn;
    @FXML private TableColumn<ConnectionInfo, String> destIpColumn;
    @FXML private TableColumn<ConnectionInfo, String> protocolColumn;
    @FXML private TableColumn<ConnectionInfo, String> stateColumn;

    @FXML private TableColumn<IpTrafficInfo, String> ipAddressColumn;
    @FXML private TableColumn<IpTrafficInfo, Long> incomingPacketsColumn;
    @FXML private TableColumn<IpTrafficInfo, Long> outgoingPacketsColumn;
    @FXML private TableColumn<IpTrafficInfo, Long> totalBytesColumn;

    private ObservableList<ConnectionInfo> connections = FXCollections.observableArrayList();
    private ObservableList<IpTrafficInfo> ipTraffic = FXCollections.observableArrayList();
    private ObservableList<String> alerts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up cell value factories for connections table
        sourceIpColumn.setCellValueFactory(cellData -> cellData.getValue().sourceIpProperty());
        destIpColumn.setCellValueFactory(cellData -> cellData.getValue().destinationIpProperty());
        protocolColumn.setCellValueFactory(cellData -> cellData.getValue().protocolProperty());
        stateColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());

        // Set up cell value factories for IP traffic table
        ipAddressColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty());
        incomingPacketsColumn.setCellValueFactory(cellData -> cellData.getValue().incomingPacketsProperty().asObject());
        outgoingPacketsColumn.setCellValueFactory(cellData -> cellData.getValue().outgoingPacketsProperty().asObject());
        totalBytesColumn.setCellValueFactory(cellData -> cellData.getValue().totalBytesProperty().asObject());

        connectionsTable.setItems(connections);
        ipTrafficTable.setItems(ipTraffic);
//        alertsList.setItems(alerts);
    }

    public void updateStatistics(TrafficStatistics stats) {
        activeConnectionsCount.setText("Active Connections: " + stats.getActiveConnectionsCount());
        connections.setAll(stats.getConnections());
        ipTraffic.setAll(stats.getIpTraffic());
        alerts.setAll(stats.getAlerts());

        totalPacketsLabel.setText(String.format("%d", stats.getTotalPackets()));
        totalBytesLabel.setText(String.format("%.2f MB", stats.getTotalBytes() / (1024.0 * 1024.0)));
        avgPacketSizeLabel.setText(String.format("%.2f bytes", stats.getAveragePacketSize()));
        topProtocolLabel.setText(stats.getTopProtocol());
    }
}
