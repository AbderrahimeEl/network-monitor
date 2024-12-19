module org.example.networkintrusionmonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.pcap4j.core;
    requires java.sql;


    opens org.example.networkintrusionmonitor to javafx.fxml;
    exports org.example.networkintrusionmonitor;
    opens org.example.networkintrusionmonitor.controller to javafx.fxml;
    opens org.example.networkintrusionmonitor.model to javafx.base;
    exports org.example.networkintrusionmonitor.model to javafx.base;
    exports org.example.networkintrusionmonitor.controller;
}