module com.example.enterpriseproject3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.j;
    requires java.naming;

    opens com.example.enterpriseproject3 to javafx.fxml;
    exports com.example.enterpriseproject3;
}