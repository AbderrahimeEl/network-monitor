package org.example.networkintrusionmonitor.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import org.example.networkintrusionmonitor.model.NetworkInterfaceInfo;
import org.example.networkintrusionmonitor.model.PacketInfo;
import org.example.networkintrusionmonitor.repository.PacketInfoRepository;
import org.example.networkintrusionmonitor.service.NetworkCaptureService;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public class PacketCaptureController {
    private final Pair<String, NetworkInterfaceInfo> EMPTY_NETWORK_INTERFACE_INFO = new Pair<>(null, null);
    public ComboBox<Pair<String, NetworkInterfaceInfo>> networkInterfacesComboBox;

    @FXML
    private Button startCaptureButton;
    @FXML
    private Button stopCaptureButton;
    @FXML
    private TableView<PacketInfo> packetTableView;
    @FXML
    private TableColumn<PacketInfo, String> sourceIpColumn;
    @FXML
    private TableColumn<PacketInfo, String> lengthColumn;
    @FXML
    private TableColumn<PacketInfo, String> destIpColumn;
    @FXML
    private TableColumn<PacketInfo, String> protocolColumn;
    @FXML
    private TableColumn<PacketInfo, LocalDateTime> timestampColumn;
    @FXML
    private TableColumn<PacketInfo, String> rawPacketDataColumn;
    @FXML
    private TableColumn<PacketInfo, String> hexStreamColumn;
    @FXML
    private TableColumn<PacketInfo, String> decodedContentColumn;

    private NetworkInterfaceInfo networkInterface;
    private NetworkCaptureService captureService;
    private PacketInfoRepository repository;
    private List<PacketInfo> capturedPackets;

    @FXML
    public void initialize() {
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        sourceIpColumn.setCellValueFactory(new PropertyValueFactory<>("sourceIp"));
        destIpColumn.setCellValueFactory(new PropertyValueFactory<>("destinationIp"));
        protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("packetLength"));
        rawPacketDataColumn.setCellValueFactory(new PropertyValueFactory<>("rawPacketData"));
        hexStreamColumn.setCellValueFactory(new PropertyValueFactory<>("hexStream"));
        decodedContentColumn.setCellValueFactory(new PropertyValueFactory<>("decodedContent"));

        initSelectInterfaceComboBox();
        setOnClickSelectInterfaceComboBox();
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

    private static class NetworkInterfaceInfoComboCell extends ListCell<Pair<String, NetworkInterfaceInfo>> {
        @Override
        protected void updateItem(Pair<String, NetworkInterfaceInfo> pair, boolean bln) {
            super.updateItem(pair, bln);
            setText(pair != null ? (pair.getValue() != null ? pair.getValue().getName() : "Select a network interface") : null);
        }
    }
}
