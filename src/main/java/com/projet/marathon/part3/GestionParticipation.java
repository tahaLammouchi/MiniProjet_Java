package com.projet.marathon.part3;

import com.projet.marathon.DbConnexion;
import com.projet.marathon.email.Email;
import com.projet.marathon.entities.Coureur;
import com.projet.marathon.entities.Marathon;
import com.projet.marathon.part2.Statique.RoleUtilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GestionParticipation implements Initializable {
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    ResultSet mrs = null;
    private HashMap<Integer,String> marathonsMap = new HashMap<>();

    @FXML
    ChoiceBox<String> marathon;
    @FXML
    TableView<Coureur> tableView = new TableView<>();
    @FXML
    TableColumn<Coureur,String> nom_column = new TableColumn<>();
    @FXML
    TableColumn<Coureur,String> prenom_column = new TableColumn<>();
    @FXML
    TableColumn<Coureur, Date> date_column = new TableColumn<>();
    @FXML
    TableColumn<Coureur,String> telephone_column = new TableColumn<>();
    @FXML
    TableColumn<Coureur,String> email_column = new TableColumn<>();
    @FXML
    TableColumn<Coureur,String> etat_column = new TableColumn<>();

    public List<Marathon> getMarathons() {
        List<Marathon> marathons = new ArrayList<>();
        String query = "select * from marathon where etat = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            st.setString(1, "pas encore");
            rs = st.executeQuery();
            while (rs.next()) {
                Marathon m = new Marathon();
                m.setId(rs.getInt("id"));
                m.setNom(rs.getString("nom"));
                marathons.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return marathons;
    }
    public Integer getSelectedMarathonId() {
        String selectedMarathonName = marathon.getValue();
        for (Map.Entry<Integer, String> entry : marathonsMap.entrySet()) {
            if (entry.getValue().equals(selectedMarathonName)) {
                return entry.getKey();
            }
        }
        return null; // Retourner null si aucun marathon n'est sélectionné ou si l'ID n'est pas trouvé
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        marathon.setValue("Listes Marathons");
        nom_column.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenom_column.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        date_column.setCellValueFactory(new PropertyValueFactory<>("naissance"));
        telephone_column.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        email_column.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        etat_column.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Appeler la méthode getMarathons() pour récupérer la liste des marathons
        List<Marathon> marathonsList = getMarathons();

        // Créer une liste de noms de marathons
        List<String> marathonsNames = new ArrayList<>();
        marathonsMap = new HashMap<>();

        for (Marathon m : marathonsList) {
            marathonsNames.add(m.getNom());
            marathonsMap.put(m.getId(), m.getNom());
        }

        // Ajouter tous les noms de marathons à la ChoiceBox "etat"
        marathon.getItems().addAll(marathonsNames);

        marathon.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            chargerParticipation();
        });
    }
    public void chargerParticipation() {
        // Obtenez l'ID du marathon sélectionné
        Integer selectedMarathonId = getSelectedMarathonId();

        // Vérifiez si un marathon est sélectionné
        if (selectedMarathonId != null) {
            // Exécutez la requête pour récupérer les données des coureurs liées au marathon sélectionné
            String query = "SELECT * FROM coureur WHERE coureur.id_marathon = ?";
            try {
                st = con.prepareStatement(query);
                st.setInt(1, selectedMarathonId);
                rs = st.executeQuery();

                // Effacez les anciennes données de la TableView
                tableView.getItems().clear();

                // Parcourez les résultats de la requête et ajoutez les coureurs à la TableView
                while (rs.next()) {
                    Coureur coureur = new Coureur();
                    coureur.setId(rs.getInt("id"));
                    coureur.setNom(rs.getString("nom"));
                    coureur.setPrenom(rs.getString("prenom"));
                    coureur.setNaissance(rs.getDate("naissance"));
                    coureur.setTelephone(rs.getInt("telephone"));
                    coureur.setAdresse(rs.getString("adresse"));
                    coureur.setEtat(rs.getString("etat"));
                    // Ajoutez d'autres propriétés du coureur si nécessaire

                    tableView.getItems().add(coureur);
                    System.out.println(rs.getString("adresse"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Aucun marathon sélectionné, effacez les données de la TableView
            tableView.getItems().clear();
        }
    }


    @FXML
    public void accepter(ActionEvent event) {
        Coureur selectedCoureur = tableView.getSelectionModel().getSelectedItem();
        if (selectedCoureur != null) {
            selectedCoureur.setEtat("confirme");

            // Mettre à jour le champ "etat" dans la base de données
            String query = "UPDATE coureur SET etat = ? WHERE id = ?";
            con = DbConnexion.getCon();
            try {
                st = con.prepareStatement(query);
                st.setString(1, selectedCoureur.getEtat());
                st.setInt(2, selectedCoureur.getId());
                st.executeUpdate();
                Email.envoyerEmail(selectedCoureur.getAdresse(),"Confirmation de participation","Après examen de votre demande de participation, nous sommes heureux de vous informer que votre demande a été confirmée avec succès.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Rafraîchir la TableView pour refléter les changements
            tableView.refresh();
        }
    }
    @FXML
    public void refuser(ActionEvent event) {
        Coureur selectedCoureur = tableView.getSelectionModel().getSelectedItem();
        if (selectedCoureur != null) {
            selectedCoureur.setEtat("annule");

            // Mettre à jour le champ "etat" dans la base de données
            String query = "UPDATE coureur SET etat = ? WHERE id = ?";
            con = DbConnexion.getCon();
            try {
                st = con.prepareStatement(query);
                st.setString(1, selectedCoureur.getEtat());
                st.setInt(2, selectedCoureur.getId());
                st.executeUpdate();
                Email.envoyerEmail(selectedCoureur.getAdresse(),"Demande refusé","Après examen de votre demande de participation, nous regrettons de vous informer que votre demande n'a pas été retenue.");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Rafraîchir la TableView pour refléter les changements
            tableView.refresh();
        }
    }
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    public void Logout(ActionEvent event) throws IOException {
        RoleUtilisateur.id = 0;
        RoleUtilisateur.role = "";
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/part2/login.fxml"));

        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/part3/gestion_marathon.fxml"));

        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
