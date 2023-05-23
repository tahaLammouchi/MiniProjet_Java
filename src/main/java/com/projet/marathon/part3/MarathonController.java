package com.projet.marathon.part3;

import com.projet.marathon.DbConnexion;
import com.projet.marathon.entities.Marathon;
import com.projet.marathon.part2.Statique.RoleUtilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MarathonController implements Initializable {
    private  String[] etats = {"pas encore","en cours","termine"};
    @FXML
    private TextField id;
    @FXML
    private TextField nom;
    @FXML
    private TextField lieu_dep;
    @FXML
    private TextField lieu_arv;
    @FXML
    private TextField distance;
    @FXML
    private ChoiceBox<String> etat;
    @FXML
    private DatePicker date;
    private LocalDate dateSelectionne;



    @FXML
    TableView<Marathon> table = new TableView<>();
    @FXML
    private TableColumn<Marathon, Integer> id_column;
    @FXML
    private TableColumn<Marathon, String> nom_column = new TableColumn<>();
    @FXML
    private TableColumn<Marathon, String> lieu_dep_column = new TableColumn<>();
    @FXML
    private TableColumn<Marathon,String> lieu_arv_column = new TableColumn<>();
    @FXML
    private TableColumn<Marathon,String> distance_column = new TableColumn<>();
    @FXML
    private TableColumn<Marathon,Date> date_column = new TableColumn<>();
    @FXML
    private TableColumn<Marathon,String> etat_column = new TableColumn<>();

    public MarathonController() {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        etat.getItems().addAll(etats);
        id.setPromptText("ID");
        nom.setPromptText("Nom");
        lieu_dep.setPromptText("Lieu de départ");
        lieu_arv.setPromptText("Lieu d'arrivée");
        distance.setPromptText("Distance");
        date.setPromptText("Date");
        // Configurer les cellules des colonnes avec les propriétés de la classe Marathon
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
        nom_column.setCellValueFactory(new PropertyValueFactory<>("nom"));
        lieu_dep_column.setCellValueFactory(new PropertyValueFactory<>("lieu_depart"));
        lieu_arv_column.setCellValueFactory(new PropertyValueFactory<>("lieu_arrive"));
        distance_column.setCellValueFactory(new PropertyValueFactory<>("distance"));
        date_column.setCellValueFactory(new PropertyValueFactory<>("date"));
        etat_column.setCellValueFactory(new PropertyValueFactory<>("etat"));



        // Récupérer les données des marathons depuis la base de données et les afficher dans la TableView
        Connection con = DbConnexion.getCon();
        String sql = "SELECT * FROM marathon";
        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String lieuDep = resultSet.getString("lieu_dep");
                String lieuArv = resultSet.getString("lieu_arv");
                float distance = resultSet.getFloat("distance");
                Date date = resultSet.getDate("date");
                String etat = resultSet.getString("etat");


                Marathon marathon = new Marathon();
                marathon.setId(id);
                marathon.setNom(nom);
                marathon.setLieu_arrive(lieuArv);
                marathon.setLieu_depart(lieuDep);
                marathon.setDistance(distance);
                marathon.setDate(date);
                marathon.setEtat(etat);

                table.getItems().add(marathon);
            }
            table.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution de la requête SQL : " + e.getMessage());
        }
    }

    @FXML
    public void ajouter() {
        if (id.getText().isEmpty() || nom.getText().isEmpty() || lieu_dep.getText().isEmpty()
                || lieu_arv.getText().isEmpty() || distance.getText().isEmpty() || date.getValue() == null || etat.getValue()==null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Error!");
            alert.setHeaderText(null);
            alert.setContentText("Priére de renseigner les champs manquants");
            alert.showAndWait();
        } else {
            String nomText = nom.getText();
            String lieu_depText = lieu_dep.getText(); // Correction : lieu_dep au lieu de lieu_arv
            String lieu_arvText = lieu_arv.getText();
            float distanceValue = Integer.parseInt(distance.getText());
            int idValue = Integer.parseInt(id.getText());
            dateSelectionne = date.getValue();
            String etat_Text  = etat.getValue();

            PreparedStatement st = null;
            ResultSet rs = null;
            Connection con = DbConnexion.getCon();
            String sql = "INSERT INTO marathon (id, nom, lieu_dep, lieu_arv, distance, date, etat) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try {
                st = con.prepareStatement(sql);
                st.setInt(1, idValue);
                st.setString(2, nomText);
                st.setString(3, lieu_depText);
                st.setString(4, lieu_arvText);
                st.setFloat(5, distanceValue);
                st.setDate(6, Date.valueOf(dateSelectionne));
                st.setString(7,etat_Text);

                st.executeUpdate();
                nom.clear();
                lieu_dep.clear();
                lieu_arv.clear();
                distance.clear();
                etat.setValue(null);
                date.setValue(null);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Ajout réussie");
                alert.setHeaderText(null);
                alert.setContentText("Le marathon a été ajouté avec succès.");
                alert.showAndWait();

                // Actualiser la TableView avec toutes les données de la base de données
                table.getItems().clear(); // Effacer les données précédentes de la TableView

                // Récupérer les données des marathons depuis la base de données et les afficher dans la TableView
                String selectAllSql = "SELECT * FROM marathon";
                try (Statement selectAllStatement = con.createStatement();
                     ResultSet resultSet = selectAllStatement.executeQuery(selectAllSql)) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String nom = resultSet.getString("nom");
                        String lieuDep = resultSet.getString("lieu_dep");
                        String lieuArv = resultSet.getString("lieu_arv");
                        float distance = resultSet.getFloat("distance");
                        Date date = resultSet.getDate("date");
                        String etat = resultSet.getString("etat");


                        Marathon marathon = new Marathon();
                        marathon.setId(id);
                        marathon.setNom(nom);
                        marathon.setLieu_arrive(lieuArv);
                        marathon.setLieu_depart(lieuDep);
                        marathon.setDistance(distance);
                        marathon.setDate(date);
                        marathon.setEtat(etat);

                        table.getItems().add(marathon);
                    }
                    table.refresh();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println("Erreur lors de l'exécution de la requête SQL : " + e.getMessage());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'exécution de la requête SQL : " + e.getMessage());
            }
        }
    }

    @FXML
    public void modifier() {
        if (id.getText().isEmpty() || nom.getText().isEmpty() || lieu_dep.getText().isEmpty()
                || lieu_arv.getText().isEmpty() || distance.getText().isEmpty() || date.getValue() == null || etat.getValue()==null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Error!");
            alert.setHeaderText(null);
            alert.setContentText("Priére de renseigner les champs manquants");
            alert.showAndWait();
        } else {
            String nomText = nom.getText();
            String lieu_depText = lieu_dep.getText();
            String lieu_arvText = lieu_arv.getText();
            float distanceValue = Float.parseFloat(distance.getText());
            int idValue = Integer.parseInt(id.getText());
            dateSelectionne = date.getValue();
            String etat_Text  = etat.getValue();

            PreparedStatement st = null;
            Connection con = DbConnexion.getCon();
            String sql = "UPDATE marathon SET nom = ?, lieu_dep = ?, lieu_arv = ?, distance = ?, date = ?, etat = ? WHERE id = ?";
            try {
                st = con.prepareStatement(sql);
                st.setString(1, nomText);
                st.setString(2, lieu_depText);
                st.setString(3, lieu_arvText);
                st.setFloat(4, distanceValue);
                st.setDate(5, Date.valueOf(dateSelectionne));
                st.setString(6,etat_Text);
                st.setInt(7, idValue);
                st.executeUpdate();
                nom.clear();
                lieu_dep.clear();
                lieu_arv.clear();
                distance.clear();
                date.setValue(null);
                etat.setValue(null);
                id.clear();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Modification réussite");
                alert.setHeaderText(null);
                alert.setContentText("Le marathon a été modifié avec succès.");
                alert.showAndWait();

                // Actualiser la TableView avec toutes les données de la base de données
                table.getItems().clear(); // Effacer les données précédentes de la TableView

                // Récupérer les données des marathons depuis la base de données et les afficher dans la TableView
                String selectAllSql = "SELECT * FROM marathon";
                try (Statement selectAllStatement = con.createStatement();
                     ResultSet resultSet = selectAllStatement.executeQuery(selectAllSql)) {
                    while (resultSet.next()) {
                        Integer marathonId = resultSet.getInt("id");
                        String marathonNom = resultSet.getString("nom");
                        String marathonLieuDep = resultSet.getString("lieu_dep");
                        String marathonLieuArv = resultSet.getString("lieu_arv");
                        Integer marathonDistance = resultSet.getInt("distance");
                        Date date = resultSet.getDate("date");
                        String etat = resultSet.getString("etat");

                        Marathon marathon = new Marathon();
                        marathon.setId(marathonId);
                        marathon.setNom(marathonNom);
                        marathon.setLieu_arrive(marathonLieuArv);
                        marathon.setLieu_depart(marathonLieuDep);
                        marathon.setDistance(marathonDistance);
                        marathon.setDate(date);
                        marathon.setEtat(etat);

                        table.getItems().add(marathon);
                    }
                    table.refresh();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println("Erreur lors de l'exécution de la requête SQL : " + e.getMessage());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'exécution de la requête SQL : " + e.getMessage());
            }
        }
    }

    @FXML
    public void supprimer() {
        Marathon marathonSelectionne = table.getSelectionModel().getSelectedItem();
        if (marathonSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sélection invalide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un marathon à supprimer.");
            alert.showAndWait();
        } else {
            Integer idMarathon = marathonSelectionne.getId();

            PreparedStatement st = null;
            Connection con = DbConnexion.getCon();
            String sql = "DELETE FROM marathon WHERE id = ?";
            try {
                st = con.prepareStatement(sql);
                st.setInt(1, idMarathon);
                st.executeUpdate();

                // Supprimer le marathon de la TableView
                table.getItems().remove(marathonSelectionne);
                table.refresh();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Suppression réussie");
                alert.setHeaderText(null);
                alert.setContentText("Le marathon a été supprimé avec succès.");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'exécution de la requête SQL : " + e.getMessage());
            }
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
    public void gestionAgents(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gestion_agentchrono.fxml"));

        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void gestionSponsors(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gestion_sponsor.fxml"));

        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void gestionParticipations(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/part3/gestion_participation.fxml"));

        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void getData(MouseEvent mouseEvent) {
        Marathon m = table.getSelectionModel().getSelectedItem();
        Integer selectedId = m.getId();
        id.setText(String.valueOf(selectedId));
        nom.setText(m.getNom());
        lieu_dep.setText(m.getLieu_depart());
        lieu_arv.setText(m.getLieu_arrive());
        distance.setText(String.valueOf(m.getDistance()));

        // Option 1 : Convertir java.sql.Date en java.util.Date puis en LocalDate
        java.util.Date utilDate = new java.util.Date(m.getDate().getTime());
        LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        date.setValue(localDate);

        etat.setValue(m.getEtat());
    }

}