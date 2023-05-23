package com.projet.marathon.entities;

import java.util.Date;

public class Coureur {
    private int id;
    private String nom;
    private String prenom;
    private Date naissance;
    public int telephone;
    public String adresse;
    public String etat;
    public Float temps;

    public Marathon marathon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getNaissance() {
        return naissance;
    }

    public void setNaissance(Date naissance) {
        this.naissance = naissance;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Float getTemps() {
        return temps;
    }

    public void setTemps(Float temps) {
        this.temps = temps;
    }

    public Marathon getMarathon() {
        return marathon;
    }

    public void setMarathon(Marathon marathon) {
        this.marathon = marathon;
    }

    public Coureur(int id, String nom, String prenom, Date naissance, int telephone, String adresse, String etat) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.naissance = naissance;
        this.telephone = telephone;
        this.adresse = adresse;
        this.etat = etat;
    }

    public Coureur() {
    }
}
