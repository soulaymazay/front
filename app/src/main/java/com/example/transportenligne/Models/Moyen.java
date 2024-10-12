package com.example.transportenligne.Models;

public class Moyen {
    public int id;
    public int userId;
    public String nom;

    public Moyen(int id, String nom, String marque, String couleur, String annee,String model, String etat, String image) {
        this.id = id;
        this.model=model;
        this.nom = nom;
        this.marque = marque;
        this.couleur = couleur;
        this.annee = annee;
        this.etat = etat;
        this.image = image;
    }

    public String marque;
    public String model;
    public String couleur;
    public String annee;
    public String etat;
    public String image;
}
