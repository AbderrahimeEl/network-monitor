module org.example.networkintrusionmonitor {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.networkintrusionmonitor to javafx.fxml;
    exports org.example.networkintrusionmonitor;
}