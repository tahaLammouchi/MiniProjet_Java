module com.projet.marathon {
    requires javafx.controls;
    requires javafx.fxml;
    /*requires mysql.connector.java;*/
    requires java.sql;
    requires java.mail;

    opens com.projet.marathon to javafx.fxml;
    exports com.projet.marathon;
    exports com.projet.marathon.controllers;
    opens com.projet.marathon.controllers to javafx.fxml;
    exports com.projet.marathon.entities;
    opens com.projet.marathon.entities to javafx.fxml;

    exports com.projet.marathon.part2;
    opens com.projet.marathon.part2 to javafx.fxml;
    exports com.projet.marathon.part2.Statique;
    opens com.projet.marathon.part2.Statique to javafx.fxml;

    exports com.projet.marathon.part3;
    opens com.projet.marathon.part3 to javafx.fxml;
}