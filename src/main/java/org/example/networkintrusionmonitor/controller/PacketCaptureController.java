package org.example.networkintrusionmonitor.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.networkintrusionmonitor.model.*;
import org.example.networkintrusionmonitor.repository.PacketInfoRepository;
import org.example.networkintrusionmonitor.service.NetworkCaptureService;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PacketCaptureController {
    private final Pair<String, NetworkInterfaceInfo> EMPTY_NETWORK_INTERFACE_INFO = new Pair<>(null, null);
    public ComboBox<Pair<String, NetworkInterfaceInfo>> networkInterfacesComboBox;
    public Button analyzeButton;
    public TextArea packetRawDataArea;
    public TextArea hexStreamArea;
    public TextArea decodedContentArea;
    public MenuItem activeConnectionsMenuItem;

    @FXML
    private Button startCaptureButton;
    @FXML
    private Button stopCaptureButton;
    @FXML
    private TableView<PacketInfo> packetTableView;

    private NetworkInterfaceInfo networkInterface;
    private NetworkCaptureService captureService;
    private PacketInfoRepository repository;
    private List<PacketInfo> capturedPackets;

    @FXML
    public void initialize() {
        TableColumn<PacketInfo, LocalDateTime> timestampColumn = new TableColumn<>("Timestamp");
        TableColumn<PacketInfo, String> sourceIpColumn = new TableColumn<>("Source IP");
        TableColumn<PacketInfo, String> destIpColumn = new TableColumn<>("Destination IP");
        TableColumn<PacketInfo, String> lengthColumn = new TableColumn<>("Length");
        TableColumn<PacketInfo, String> protocolColumn = new TableColumn<>("Protocol");
        TableColumn<PacketInfo, String> rawPacketDataColumn = new TableColumn<>("Raw Packet Data");
        TableColumn<PacketInfo, String> hexStreamColumn = new TableColumn<>("Hex Stream");
        TableColumn<PacketInfo, String> decodedContentColumn = new TableColumn<>("Decoded Content");

        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        sourceIpColumn.setCellValueFactory(new PropertyValueFactory<>("sourceIp"));
        destIpColumn.setCellValueFactory(new PropertyValueFactory<>("destinationIp"));
        protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("packetLength"));
        rawPacketDataColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRawPacketData().substring(0, 90)));

        hexStreamColumn.setCellValueFactory(new PropertyValueFactory<>("hexStream"));
        decodedContentColumn.setCellValueFactory(new PropertyValueFactory<>("decodedContent"));

        packetTableView.getColumns().addAll(
                timestampColumn,
                sourceIpColumn,
                destIpColumn,
                lengthColumn,
                protocolColumn,
                rawPacketDataColumn,
                hexStreamColumn,
                decodedContentColumn
        );

        timestampColumn.setMinWidth(150);
        timestampColumn.setMaxWidth(150);

        sourceIpColumn.setMinWidth(80);
        sourceIpColumn.setMaxWidth(80);

        destIpColumn.setMinWidth(100);
        destIpColumn.setMaxWidth(100);

        lengthColumn.setMinWidth(60);
        lengthColumn.setMaxWidth(60);

        protocolColumn.setMinWidth(70);
        protocolColumn.setMaxWidth(70);

        initSelectInterfaceComboBox();
        setOnClickSelectInterfaceComboBox();

        // Optional: Add listener to show packet details when a row is selected
        packetTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                packetRawDataArea.setText(newSelection.getRawPacketData());
                hexStreamArea.setText(newSelection.getHexStream());
                decodedContentArea.setText(newSelection.getDecodedContent());
            }
        });
    }

    private void setOnClickSelectInterfaceComboBox() {
        networkInterfacesComboBox.setOnAction(event -> {
            Pair<String, NetworkInterfaceInfo> selectedItem = networkInterfacesComboBox.getSelectionModel().getSelectedItem();
            NetworkInterfaceInfo selectedInterface = selectedItem.getValue();

            if (selectedInterface != null) {
                setNetworkInterface(selectedInterface);
            }
        });
    }

    public void setNetworkInterface(NetworkInterfaceInfo networkInterface) {
        this.networkInterface = networkInterface;
        this.captureService = new NetworkCaptureService();
    }

    private void initSelectInterfaceComboBox() {
        networkInterfacesComboBox.setEditable(false);

        networkInterfacesComboBox.setCellFactory(x -> new NetworkInterfaceInfoComboCell());
        networkInterfacesComboBox.setButtonCell(new NetworkInterfaceInfoComboCell());

        Function<NetworkInterfaceInfo, Pair<String, NetworkInterfaceInfo>> categoryObjectFunction = category -> new Pair<>(category.getName(), category);

        networkInterfacesComboBox.getItems().add(EMPTY_NETWORK_INTERFACE_INFO);

        try {
            List<Pair<String, NetworkInterfaceInfo>> interfaces =
                    Pcaps.findAllDevs()
                            .stream()
                            .map(intefacePcapNetworkInterface -> new Pair<>(intefacePcapNetworkInterface.getName(), new NetworkInterfaceInfo(intefacePcapNetworkInterface)))
                            .toList();

            interfaces.forEach(interfacePair -> networkInterfacesComboBox.getItems().add(interfacePair));
        } catch (PcapNativeException e) {
            showError("Error loading network interfaces: " + e.getMessage());
        }

        networkInterfacesComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void startCapture() {
        try {
            captureService.startCapture(networkInterface.getNetworkInterface());
            startCaptureButton.setDisable(true);
            stopCaptureButton.setDisable(false);
        } catch (Exception e) {
            showError("Failed to start capture: " + e.getMessage());
        }
    }

    @FXML
    public void stopCapture() {
        try {
            if (captureService != null) {
                captureService.stopCapture();
            }

            if (repository == null) {
                repository = new PacketInfoRepository();
            }

            capturedPackets = repository.getAllPackets();

            Platform.runLater(() -> {
                packetTableView.getItems().clear();
                packetTableView.getItems().addAll(capturedPackets);
            });

            startCaptureButton.setDisable(false);
            stopCaptureButton.setDisable(true);
        } catch (Exception e) {
            showError("Failed to stop capture and retrieve packets: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void displayActiveConnections() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/networkintrusionmonitor/active-connections.fxml"));
            Parent root = loader.load();

            TrafficAnalysisController controller = loader.getController();

            // Calculate statistics
            TrafficStatistics stats = calculateTrafficStatistics();
            controller.updateStatistics(stats, AnalysisType.ACTIVE_CONNECTIONS);

            // Show in a new window
            Stage stage = new Stage();
            stage.setTitle("Traffic Analysis");
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void displayGlobalStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/networkintrusionmonitor/global-statistics.fxml"));
            Parent root = loader.load();

            TrafficAnalysisController controller = loader.getController();

            // Calculate statistics
            TrafficStatistics stats = calculateTrafficStatistics();
            controller.updateStatistics(stats, AnalysisType.GLOBAL_STATISTICS);

            // Show in a new window
            Stage stage = new Stage();
            stage.setTitle("Traffic Analysis");
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void displayTrafficByIP() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/networkintrusionmonitor/traffic-by-ip.fxml"));
            Parent root = loader.load();

            TrafficAnalysisController controller = loader.getController();

            // Calculate statistics
            TrafficStatistics stats = calculateTrafficStatistics();
            controller.updateStatistics(stats, AnalysisType.TRAFIC_BY_IP);

            // Show in a new window
            Stage stage = new Stage();
            stage.setTitle("Traffic Analysis");
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TrafficStatistics calculateTrafficStatistics() {
        TrafficStatistics stats = new TrafficStatistics();

        // Calculate active connections
        Map<String, ConnectionInfo> activeConnections = new HashMap<>();

        // Calculate traffic by IP
        Map<String, IpTrafficInfo> ipTrafficMap = new HashMap<>();

        long totalPackets = 0;
        long totalBytes = 0;
        Map<String, Integer> protocolCount = new HashMap<>();

        // Process captured packets
        for (PacketInfo packet : capturedPackets) {
            totalPackets++;
            totalBytes += packet.getPacketLength();

            // Update connections
            String connectionKey = packet.getSourceIp() + "-" + packet.getDestinationIp();
            ConnectionInfo connection = activeConnections.computeIfAbsent(
                    connectionKey,
                    k -> new ConnectionInfo(packet.getSourceIp(), packet.getDestinationIp(),
                            packet.getProtocol(), "ACTIVE")
            );
            connection.incrementPackets();
            connection.addBytes(packet.getPacketLength());

            // Update IP traffic statistics
            updateIpTrafficStats(ipTrafficMap, packet);

            // Update protocol statistics
            protocolCount.merge(packet.getProtocol(), 1, Integer::sum);
        }

        // Find top protocol
        String topProtocol = protocolCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        // Set statistics
        stats.setActiveConnectionsCount(activeConnections.size());
        stats.setConnections(new ArrayList<>(activeConnections.values()));
        stats.setIpTraffic(new ArrayList<>(ipTrafficMap.values()));
        stats.setTotalPackets(totalPackets);
        stats.setTotalBytes(totalBytes);
        stats.setAveragePacketSize(totalPackets > 0 ? (double) totalBytes / totalPackets : 0);
        stats.setTopProtocol(topProtocol);

        return stats;
    }

    private void updateIpTrafficStats(Map<String, IpTrafficInfo> ipTrafficMap, PacketInfo packet) {
        // Update source IP statistics
        IpTrafficInfo sourceStats = ipTrafficMap.computeIfAbsent(
                packet.getSourceIp(),
                k -> new IpTrafficInfo(packet.getSourceIp())
        );
        sourceStats.addOutgoingPacket(packet.getPacketLength());

        // Update destination IP statistics
        IpTrafficInfo destStats = ipTrafficMap.computeIfAbsent(
                packet.getDestinationIp(),
                k -> new IpTrafficInfo(packet.getDestinationIp())
        );
        destStats.addIncomingPacket(packet.getPacketLength());
    }

    public void displayAllerts(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/networkintrusionmonitor/intrusion-alerts.fxml"));
            Parent root = loader.load();

            TrafficAnalysisController controller = loader.getController();

            controller.updateStatistics(new TrafficStatistics(), AnalysisType.ALERTS);

            // Show in a new window
            Stage stage = new Stage();
            stage.setTitle("Traffic Analysis");
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class NetworkInterfaceInfoComboCell extends ListCell<Pair<String, NetworkInterfaceInfo>> {
        @Override
        protected void updateItem(Pair<String, NetworkInterfaceInfo> pair, boolean bln) {
            super.updateItem(pair, bln);
            setText(pair != null ? (pair.getValue() != null ?
                    pair.getValue().getName() + (pair.getValue().getDescription() != null ? " - " + pair.getValue().getDescription() : "")
                    : "Select a network interface") : null);
        }
    }
}
