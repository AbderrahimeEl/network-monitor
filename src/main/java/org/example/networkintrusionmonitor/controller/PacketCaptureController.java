package org.example.networkintrusionmonitor.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.Pair;
import org.example.networkintrusionmonitor.model.NetworkInterfaceInfo;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;

import java.util.List;
import java.util.function.Function;

public class PacketCaptureController {
    private final Pair<String, NetworkInterfaceInfo> EMPTY_NETWORK_INTERFACE_INFO = new Pair<>(null, null);
    public ComboBox<Pair<String, NetworkInterfaceInfo>> networkInterfacesComboBox;

    private NetworkInterfaceInfo networkInterface;

    @FXML
    public void initialize() {
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

        System.out.println(networkInterface.getName());
    }

    private void initSelectInterfaceComboBox() {
        networkInterfacesComboBox.setEditable(false);

        networkInterfacesComboBox.setCellFactory(x -> new NetworkInterfaceInfoComboCell());
        networkInterfacesComboBox.setButtonCell(new NetworkInterfaceInfoComboCell());

        Function<NetworkInterfaceInfo, Pair<String, NetworkInterfaceInfo>> categoryObjectFunction = category -> new Pair<>(category.getName(), category);

        networkInterfacesComboBox.getItems().add(EMPTY_NETWORK_INTERFACE_INFO);

        try {
            List<Pair<String, NetworkInterfaceInfo>> interfaces = Pcaps.findAllDevs().stream().map(intefacePcapNetworkInterface -> new Pair<>(intefacePcapNetworkInterface.getName(), new NetworkInterfaceInfo(intefacePcapNetworkInterface))).toList();

            interfaces.forEach(interfacePair -> networkInterfacesComboBox.getItems().add(interfacePair));
        } catch (PcapNativeException e) {
            showError("Error loading network interfaces: " + e.getMessage());
        }

        networkInterfacesComboBox.getSelectionModel().selectFirst();
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
