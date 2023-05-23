package com.projet.marathon.controllers;

import com.projet.marathon.DbConnexion;
import com.projet.marathon.entities.Marathon;
import com.projet.marathon.entities.Sponsor;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SponsorController implements Initializable {
    int selectedId = 0;
    int selectedMarathonId = 0;
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    ResultSet mrs = null;

    @FXML
    private TextField nom_field;
    @FXML
    private TextField logo_field;
    @FXML
    private TextField montant_field;
    @FXML
    private ChoiceBox marathon_choicebox;

    @FXML
    private TableView<Sponsor> sponsor_table;
    @FXML
    private TableColumn<Sponsor, Integer> id_column;
    @FXML
    private TableColumn<Sponsor, String> nom_column;
    @FXML
    private TableColumn<Sponsor, String> logo_column;
    @FXML
    private TableColumn<Sponsor, Integer> montant_column;
    @FXML
    private TableColumn<Sponsor, String> marathon_column;

    @FXML
    private Button ajouter_btn;
    @FXML
    private Button modifier_btn;
    @FXML
    private Button supprimer_btn;

    public Marathon getMarathon(int id_marathon) {
        Marathon marathon = new Marathon();
        String query = "select id, nom from marathon where id = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            st.setInt(1, id_marathon);
            mrs = st.executeQuery();
            while (mrs.next()){
                marathon.setId(mrs.getInt("id"));
                marathon.setNom(mrs.getString("nom"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return marathon;
    }
    public ObservableList<Sponsor> getSponsors() {
        ObservableList<Sponsor> sponsors = FXCollections.observableArrayList();
        String query = "select * from sponsor";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()){
                Sponsor s = new Sponsor();
                s.setId(rs.getInt("id"));
                s.setNom(rs.getString("nom"));
                s.setLogo(rs.getString("logo"));
                s.setMontant(rs.getInt("montant"));
                s.setMarathon(getMarathon(rs.getInt("id_marathon")));
                sponsors.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sponsors;
    }
    public void listerSponsors () {
        ObservableList<Sponsor> list = getSponsors();
        System.out.println(list);
        sponsor_table.setItems(list);
        id_column.setCellValueFactory (new PropertyValueFactory<Sponsor, Integer>("id"));
        nom_column.setCellValueFactory (new PropertyValueFactory<Sponsor, String>("nom"));
        logo_column.setCellValueFactory (new PropertyValueFactory<Sponsor, String>("logo"));
        montant_column.setCellValueFactory (new PropertyValueFactory<Sponsor, Integer>("montant"));
        marathon_column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarathon().getNom()));
    }

    @FXML
    void Ajouter(ActionEvent event) {
        System.out.println("ajouter");
        String insert = "insert into sponsor (nom, logo, montant, id_marathon) values (?, ?, ?, ?)";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(insert);
            st.setString(1, nom_field.getText());
            st.setString(2, logo_field.getText());
            st.setInt(3, Integer.parseInt(montant_field.getText()));
            SponsorController.ChoiceItem selectedChoiceItem = (SponsorController.ChoiceItem) marathon_choicebox.getValue();
            if (selectedChoiceItem != null)
                selectedMarathonId = selectedChoiceItem.getId();
            st.setInt(4, selectedMarathonId);
            st.executeUpdate();
            listerSponsors();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void getData (MouseEvent event) {
        Sponsor s = sponsor_table.getSelectionModel().getSelectedItem();
        selectedId = s.getId();
        nom_field.setText(s.getNom());
        logo_field.setText(s.getLogo());
        montant_field.setText(String.valueOf(s.getMontant()));
        //marathon_choicebox.setValue(s.getMarathon());
        Marathon m = s.getMarathon();
        SponsorController.ChoiceItem choiceItem = new SponsorController.ChoiceItem(m.getNom(), m.getId());
        marathon_choicebox.setValue(choiceItem);
    }
    @FXML
    void Modifier(ActionEvent event) {
        System.out.println("modifier");
        String update = "update sponsor set nom = ?, logo = ?, montant = ?, id_marathon = ? where id = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(update);
            st.setString(1, nom_field.getText());
            st.setString(2, logo_field.getText());
            st.setInt(3, Integer.parseInt(montant_field.getText()));
            SponsorController.ChoiceItem selectedChoiceItem = (SponsorController.ChoiceItem) marathon_choicebox.getValue();
            if (selectedChoiceItem != null)
                selectedMarathonId = selectedChoiceItem.getId();
            st.setInt(4, selectedMarathonId);
            st.setInt(5, selectedId);
            st.executeUpdate();
            listerSponsors();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void Supprimer(ActionEvent event) {
        System.out.println("supprimer");
        String delete = "delete from sponsor where id = ?";
        con = DbConnexion.getCon();
        try {
            st= con.prepareStatement(delete);
            st.setInt(1, selectedId);
            st.executeUpdate();
            listerSponsors();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /*private void getMarathons(Event event) {
        String marathon = (String) marathon_choicebox.getValue();
        System.out.println(marathon);
    }*/
    public ObservableList<Marathon> getMarathons() {
        ObservableList<Marathon> marathons = FXCollections.observableArrayList();
        String query = "select * from marathon where etat = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            st.setString(1,"pas encore");
            rs = st.executeQuery();
            while (rs.next()){
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
    public ObservableList<SponsorController.ChoiceItem> genererMarathonsChoises() {
        ObservableList<Marathon> marathons = getMarathons();
        ObservableList<SponsorController.ChoiceItem> items = FXCollections.observableArrayList();
        for (Marathon marathon : marathons) {
            String nom = marathon.getNom();
            int id = marathon.getId();
            SponsorController.ChoiceItem choiceItem = new SponsorController.ChoiceItem(nom, id);
            items.add(choiceItem);
        }

        return items;
    }
    private static Stage stage;
    private static Scene scene;
    private static Parent root;
    @FXML
    public void back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/part3/gestion_marathon.fxml"));

        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listerSponsors();
        marathon_choicebox.setConverter(new SponsorController.ChoiceItemStringConverter());
        marathon_choicebox.getItems().addAll(genererMarathonsChoises());
        marathon_choicebox.setOnAction(this::getChoiceBoxValue); // Add event listener
    }
    private void getChoiceBoxValue(Event event) {
        SponsorController.ChoiceItem selectedChoiceItem = (SponsorController.ChoiceItem) marathon_choicebox.getValue();
        if (selectedChoiceItem != null) {
            selectedMarathonId = selectedChoiceItem.getId();
            System.out.println("id: " + selectedMarathonId);
            //listerSponsors();
        }
    }
    private void AlertMeWarning(String title, String description) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(description);
        alert.showAndWait();
        sponsor_table.refresh();
    }
    public static class ChoiceItem {
        private String value;
        private int id;

        public ChoiceItem(String value, int id) {
            this.value = value;
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    public static class ChoiceItemStringConverter extends StringConverter<SponsorController.ChoiceItem> {
        @Override
        public String toString(SponsorController.ChoiceItem choiceItem) {
            if (choiceItem == null) {
                return null;
            }
            return choiceItem.getValue();
        }

        @Override
        public SponsorController.ChoiceItem fromString(String string) {
            throw new UnsupportedOperationException("Not supported");
        }
    }
    private void AlertMe(String title, String description , Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(description);
        alert.showAndWait();
    }
}
