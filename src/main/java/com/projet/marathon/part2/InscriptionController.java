package com.projet.marathon.part2;

import com.projet.marathon.entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class InscriptionController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField pseudo;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    UtilisateurController controller = new UtilisateurController();


    private boolean pseudoExiste(String pseudo) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marathon", "root", "")) {
            String query = "SELECT COUNT(*) FROM utilisateur WHERE pseudo = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, pseudo);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @FXML
    public void inscrire(ActionEvent event) throws IOException {
        String pseudoText = pseudo.getText();
        String passwordText = password.getText();

        // Vérifier que les champs sont remplis
        if (pseudoText.isEmpty() || passwordText.isEmpty()) {
            MethodesStatiques.afficherAlerteError("Champs non remplis", "Veuillez remplir tous les champs.");
            return;

        }

        // Vérifier si le pseudo n'existe pas déjà dans la base de données
        if (pseudoExiste(pseudoText)) {
            MethodesStatiques.afficherAlerteError("Utilisateur existant", "Le pseudo existe déjà.");
            return;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPseudo(pseudoText);
        utilisateur.setPassword(passwordText);
        // Insérer l'utilisateur dans la base de données
        if (controller.inscrireUtilisateur(utilisateur)) {
            MethodesStatiques.afficherAlerteSuccess("Inscription réussie", "Vous êtes maintenant inscrit !");
            switchLogin(event);
        } else {
            MethodesStatiques.afficherAlerteError("Erreur d'inscription", "Une erreur s'est produite lors de l'inscription.");
        }
    }

    @FXML
    public void switchLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/part2/login.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}

