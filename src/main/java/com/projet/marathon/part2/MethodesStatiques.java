package com.projet.marathon.part2;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public  class MethodesStatiques {
    private static Stage stage;
    private static Scene scene;
    private static Parent root;

    public static void redirect(ActionEvent event, String page) throws IOException {
        FXMLLoader loader = new FXMLLoader(MethodesStatiques.class.getClass().getResource(page));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static void afficherAlerteError(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void afficherAlerteSuccess(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
