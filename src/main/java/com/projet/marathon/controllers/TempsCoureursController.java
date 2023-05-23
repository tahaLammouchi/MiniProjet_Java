package com.projet.marathon.controllers;

import com.projet.marathon.entities.Coureur;
import com.projet.marathon.DbConnexion;
import com.projet.marathon.entities.Marathon;
import com.projet.marathon.part2.Statique.RoleUtilisateur;
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

public class TempsCoureursController implements Initializable {
    int selectedId = 0;
    int selectedMarathonId = 0;
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    ResultSet mrs = null;
    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private TextField temps_field;
    @FXML
    private ChoiceBox marathon_choicebox;
    @FXML
    private TableView<Coureur> coureur_table;
    @FXML
    private TableColumn<Coureur, Integer> id_column;
    @FXML
    private TableColumn<Coureur, String> nom_column;
    @FXML
    private TableColumn<Coureur, String> prenom_column;
    @FXML
    private TableColumn<Coureur, String> marathon_column;
    @FXML
    private TableColumn<Coureur, String> temps_column;
    @FXML
    private Button saisir_btn;
    @FXML
    private Button abandon_btn;

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
    public ObservableList<Coureur> getCoureurs() {
        ObservableList<Coureur> coureurs = FXCollections.observableArrayList();
        String query;
        if(selectedMarathonId != 0)
            query = "select * from coureur where etat = ? and id_marathon = ?";
        else
            query = "select * from coureur c inner join marathon m ON c.id_marathon = m.id WHERE c.etat = ? AND m.etat = ?";

        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            if(selectedMarathonId != 0){
                st.setString(1,"confirme");
                st.setInt(2,selectedMarathonId);
                System.out.println(st.toString());
            }
            else {
                st.setString(1,"confirme");
                st.setString(2,"termine");
            }
            rs = st.executeQuery();
            while (rs.next()){
                Coureur c = new Coureur();
                c.setId(rs.getInt("id"));
                c.setNom(rs.getString("nom"));
                c.setPrenom(rs.getString("prenom"));
                c.setTemps(rs.getFloat("temps"));

                c.setMarathon(getMarathon(rs.getInt("id_marathon")));
                coureurs.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return coureurs;
    }
    public ObservableList<Marathon> getMarathons() {
        ObservableList<Marathon> marathons = FXCollections.observableArrayList();
        String query = "select * from marathon where etat = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            st.setString(1,"termine");
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
    public ObservableList<ChoiceItem> genererMarathonsChoises() {
        ObservableList<Marathon> marathons = getMarathons();
        ObservableList<ChoiceItem> items = FXCollections.observableArrayList();
        for (Marathon marathon : marathons) {
            String nom = marathon.getNom();
            int id = marathon.getId();
            ChoiceItem choiceItem = new ChoiceItem(nom, id);
            items.add(choiceItem);
        }

        return items;
    }
    public void listerCoureurs () {
        ObservableList<Coureur> list = getCoureurs();
        coureur_table.setItems(list);
        id_column.setCellValueFactory (new PropertyValueFactory<Coureur, Integer>("id"));
        nom_column.setCellValueFactory (new PropertyValueFactory<Coureur, String>("nom"));
        prenom_column.setCellValueFactory (new PropertyValueFactory<Coureur, String>("prenom"));
        marathon_column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarathon().getNom()));
        temps_column.setCellValueFactory (new PropertyValueFactory<Coureur, String>("temps"));
    }
    @FXML
    void getData (MouseEvent event) {
        Coureur c = coureur_table.getSelectionModel().getSelectedItem();
        selectedId = c.getId();
        temps_field.setText(String.valueOf(c.getTemps()));
    }
    @FXML
    void Saisir(ActionEvent event) {
        System.out.println("saisir");
        String update = "update coureur set temps = ? where id = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(update);
            st.setString(1, temps_field.getText());
            st.setInt(2, selectedId);
            st.executeUpdate();
            listerCoureurs();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void Abandon(ActionEvent event) {
        System.out.println("abandon");
        String update = "update coureur set temps = ? where id = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(update);
            st.setFloat(1, (-1));
            st.setInt(2, selectedId);
            st.executeUpdate();
            listerCoureurs();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void DeciderVainqueur(ActionEvent event) {
        System.out.println("decider vinqueur");
        // i have an observable list type Coureur, Coureur who have an attribut named temps
        // i want to get the Coureur name for the one who have the maximum float temps
        // and save it in the string bellow
        String vainqueur = "";
        Coureur vainqueurCoureur = null;
        float maxTemps = Float.MIN_VALUE;

        for (Coureur coureur : getCoureurs()) {
            if (coureur.getTemps() > maxTemps) {
                maxTemps = coureur.getTemps();
                vainqueurCoureur = coureur;
            }
        }
        if (vainqueurCoureur != null) {
            vainqueur = vainqueurCoureur.getNom() + vainqueurCoureur.getPrenom();
        }

        String update = "update marathon set vainqueur = ? where id = ?";
        con = DbConnexion.getCon();
        try {
            st = con.prepareStatement(update);
            st.setString(1, vainqueur);
            st.setInt(2, selectedId);
            st.executeUpdate();
            listerCoureurs();
            System.out.println(vainqueur);
        } catch (SQLException e) {
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
        listerCoureurs();
        marathon_choicebox.setConverter(new ChoiceItemStringConverter());
        marathon_choicebox.getItems().addAll(genererMarathonsChoises());
        marathon_choicebox.setOnAction(this::getChoiceBoxValue); // Add event listener
    }
    private void getChoiceBoxValue(Event event) {
        ChoiceItem selectedChoiceItem = (ChoiceItem) marathon_choicebox.getValue();
        if (selectedChoiceItem != null) {
            selectedMarathonId = selectedChoiceItem.getId();
            System.out.println("id: " + selectedMarathonId);
            listerCoureurs();
        }
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
    public static class ChoiceItemStringConverter extends StringConverter<ChoiceItem> {
        @Override
        public String toString(ChoiceItem choiceItem) {
            if (choiceItem == null) {
                return null;
            }
            return choiceItem.getValue();
        }

        @Override
        public ChoiceItem fromString(String string) {
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
