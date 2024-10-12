package com.example.transportenligne.Models;

import java.time.LocalDateTime;

public class Course {
    public int id;
    public int chauffeur;
    public int client;
    public String moyen;
    public String chauffeurGPS;
    public String destinationGPS;
    public String positionGPS;
    public String etat;
    public String inputposition;
    public String inputdestination;
    public LocalDateTime StartDateTime;
    public LocalDateTime FinishDateTime;
    public String avis;
    public String chauffeurName;
    public String clientName;
    public String moyenName;
}
