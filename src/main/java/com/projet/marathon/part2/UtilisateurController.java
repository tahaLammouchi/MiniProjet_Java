package com.projet.marathon.part2;

import com.projet.marathon.entities.Utilisateur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UtilisateurController {
    public boolean inscrireUtilisateur(Utilisateur utilisateur) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marathon", "root", "")) {
            String query = "INSERT INTO utilisateur (pseudo, password,role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, utilisateur.getPseudo());
            stmt.setString(2, utilisateur.getPassword());
            stmt.setString(3, "coureur");


            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
