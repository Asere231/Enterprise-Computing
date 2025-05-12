module com.example.enterpriseproject1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.enterpriseproject1 to javafx.fxml;
    exports com.example.enterpriseproject1;
}