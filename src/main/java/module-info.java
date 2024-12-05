module org.example.networkintrusionmonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.pcap4j.core;


    opens org.example.networkintrusionmonitor to javafx.fxml;
    exports org.example.networkintrusionmonitor;
    exports org.example.networkintrusionmonitor.controller to javafx.fxml;
    opens org.example.networkintrusionmonitor.controller to javafx.fxml;
}