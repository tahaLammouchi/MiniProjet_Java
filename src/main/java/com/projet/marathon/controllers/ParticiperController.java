package com.projet.marathon.controllers;

import com.projet.marathon.DbConnexion;
import com.projet.marathon.entities.Marathon;
import com.projet.marathon.part2.Statique.RoleUtilisateur;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticiperController implements Initializable {
    int selectedMarathonId = 0;
    private static Stage stage;
    private static Scene scene;
    private static Parent root;
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    @FXML
    private TextField adresse_field;

    @FXML
    private DatePicker naissance_picker;
    @FXML
    private ChoiceBox marathon_choicebox;
    @FXML
    private TextField nom_field;

    @FXML
    private TextField prenom_field;

    @FXML
    private TextField telephone_field;

    public ObservableList<Marathon> getMarathons() {
        ObservableList<Marathon> marathons = FXCollections.observableArrayList();
        String query = "select * from marathon where etat = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            st.setString(1,"en cours");
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
    public ObservableList<ParticiperController.ChoiceItem> genererMarathonsChoises() {
        ObservableList<Marathon> marathons = getMarathons();
        ObservableList<ParticiperController.ChoiceItem> items = FXCollections.observableArrayList();
        for (Marathon marathon : marathons) {
            String nom = marathon.getNom();
            int id = marathon.getId();
            ParticiperController.ChoiceItem choiceItem = new ParticiperController.ChoiceItem(nom, id);
            items.add(choiceItem);
        }

        return items;
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
    public static class ChoiceItemStringConverter extends StringConverter<ParticiperController.ChoiceItem> {
        @Override
        public String toString(ParticiperController.ChoiceItem choiceItem) {
            if (choiceItem == null) {
                return null;
            }
            return choiceItem.getValue();
        }

        @Override
        public ParticiperController.ChoiceItem fromString(String string) {
            throw new UnsupportedOperationException("Not supported");
        }
    }
    @FXML
    void Envoyer(ActionEvent event) {
        System.out.println("envoyer");
        String insert = "insert into coureur (nom, prenom, naissance, telephone, adresse, id_marathon) values (?, ?, ?, ?, ?, ?)";
        con = DbConnexion.getCon();
        try {
            if(selectedMarathonId == 0)
                AlertMe("marathon erreur" , "selectionner un marathon !", Alert.AlertType.WARNING);
            else if(nom_field.getText().isBlank() || prenom_field.getText().isBlank() || naissance_picker.getValue() == null)
                AlertMe("epmty field erreur" , "saisir tout les valeurs !", Alert.AlertType.WARNING);
            else if(!isNumber(telephone_field.getText()))
                AlertMe("telephone pas valid" , "saisir un numero valid !", Alert.AlertType.WARNING);
            else if(!isEmail(adresse_field.getText()))
                AlertMe("adresse pas valid" , "saisir un email valid comme address !", Alert.AlertType.WARNING);

            st = con.prepareStatement(insert);
            st.setString(1, nom_field.getText());
            st.setString(2, prenom_field.getText());
            st.setDate(3, Date.valueOf(naissance_picker.getValue()));
            st.setInt(4, Integer.parseInt(telephone_field.getText()));
            st.setString(5, adresse_field.getText());
            st.setInt(6, selectedMarathonId);
            st.executeUpdate();
            AlertMe("envoyer" , "envoyer success !", Alert.AlertType.CONFIRMATION);

        } catch (SQLException e) {
            AlertMe("erreur" , "envoyer failed !", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        marathon_choicebox.setConverter(new ParticiperController.ChoiceItemStringConverter());
        marathon_choicebox.getItems().addAll(genererMarathonsChoises());
        marathon_choicebox.setOnAction(this::getChoiceBoxValue); // Add event listener
    }
    private void getChoiceBoxValue(Event event) {
        ParticiperController.ChoiceItem selectedChoiceItem = (ParticiperController.ChoiceItem) marathon_choicebox.getValue();
        if (selectedChoiceItem != null) {
            selectedMarathonId = selectedChoiceItem.getId();
            System.out.println("id: " + selectedMarathonId);
        }
    }
    private void AlertMe(String title, String description , Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(description);
        alert.showAndWait();
    }
    private static boolean isEmail(String email){
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$" );
        Matcher m = p.matcher(email.toUpperCase());
        return m.matches();
    }
    private static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}