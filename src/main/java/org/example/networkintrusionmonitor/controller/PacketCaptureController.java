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
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
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

public class PacketCaptureController {
    private final Pair<String, NetworkInterfaceInfo> EMPTY_NETWORK_INTERFACE_INFO = new Pair<>(null, null);
    public ComboBox<Pair<String, NetworkInterfaceInfo>> networkInterfacesComboBox;
    public TextArea packetRawDataArea;
    public TextArea hexStreamArea;
    public TextArea decodedContentArea;
    public MenuItem activeConnectionsMenuItem;
    public TextField protocolSearchField;

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
        TableColumn<PacketInfo, String> rawPacketDataColumn = new TableColumn<>("Info");

        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        sourceIpColumn.setCellValueFactory(new PropertyValueFactory<>("sourceIp"));
        destIpColumn.setCellValueFactory(new PropertyValueFactory<>("destinationIp"));
        protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("packetLength"));
        rawPacketDataColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRawPacketData().substring(0, 90)));

        packetTableView.getColumns().addAll(
                timestampColumn,
                sourceIpColumn,
                destIpColumn,
                lengthColumn,
                protocolColumn,
                rawPacketDataColumn
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

        DropShadow dropShadow = new DropShadow(
                BlurType.ONE_PASS_BOX,
                Color.color(0.6392, 0.6392, 0.6392, 1.0),
                10.0,
                0,
                0,
                0
        );

        stopCaptureButton.setEffect(dropShadow);
        startCaptureButton.setEffect(dropShadow);
        startCaptureButton.setTextFill(Color.color(1, 1, 1));
        startCaptureButton.setBackground(new Background(new BackgroundFill(Color.color(0.4, 0.44, 1, 1.0), new CornerRadii(3.0), null)));

        startCaptureButton.setDisable(true);

        protocolSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchByProtocol(null);
        });
    }

    private void setOnClickSelectInterfaceComboBox() {
        networkInterfacesComboBox.setOnAction(event -> {
            Pair<String, NetworkInterfaceInfo> selectedItem = networkInterfacesComboBox.getSelectionModel().getSelectedItem();
            NetworkInterfaceInfo selectedInterface = selectedItem.getValue();

            if (selectedInterface != null) {
                setNetworkInterface(selectedInterface);
            }

            startCaptureButton.setDisable(networkInterfacesComboBox.getSelectionModel().getSelectedItem() == null
                    || networkInterfacesComboBox.getSelectionModel().isSelected(0));
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
        TextArea textArea = new TextArea(message);
        textArea.setWrapText(true);
        textArea.setEditable(false);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(textArea);

        alert.showAndWait();
    }

    @FXML
    public void displayActiveConnections() {
        if (capturedPackets == null || capturedPackets.isEmpty()) {
            showError("No data has been captured for analysis. \nPlease select an interface, press the start button to begin capturing packets, and make sure to press the stop button when you're finished.");
            return;
        }

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
        if (capturedPackets == null || capturedPackets.isEmpty()) {
            showError("No data has been captured for analysis. \nPlease select an interface, press the start button to begin capturing packets, and make sure to press the stop button when you're finished.");
            return;
        }

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
        if (capturedPackets == null || capturedPackets.isEmpty()) {
            showError("No data has been captured for analysis. \nPlease select an interface, press the start button to begin capturing packets, and make sure to press the stop button when you're finished.");
            return;
        }

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

    public void displayAlerts(ActionEvent actionEvent) {
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

    public void searchByProtocol(ActionEvent actionEvent) {
        if (repository == null) {
            repository = new PacketInfoRepository();
        }

        String protocol = protocolSearchField.getText().trim();

        if (protocol.isEmpty()) {
            packetTableView.getItems().addAll(repository.getAllPackets());
            return;
        }

        try {
            List<PacketInfo> filteredPackets;
            filteredPackets = repository.getPacketsByProtocol(protocol);

            Platform.runLater(() -> {
                packetTableView.getItems().clear();
                packetTableView.getItems().addAll(filteredPackets);
            });

        } catch (Exception e) {
            showError("Error fetching packets: " + e.getMessage());
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
