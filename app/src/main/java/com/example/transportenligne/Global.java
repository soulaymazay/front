package com.example.transportenligne;

public class Global {
    //  static String url="192.168.1.103";
    //static String url="10.0.2.2";
    public static  String url="192.168.1.24";
    public static  String port="8004";
    public static  String wsPort="3001";
    public static  String apiUrl="http://"+url+":"+port+"/api";
    public static  String wsUrl="ws://"+url+":"+wsPort;
public static String ChauffeurAPI=apiUrl+"/chauffeur";
    public static  String addClient=apiUrl+"/client";
    public static  String Login=apiUrl+"/login_check";
    public static String RefreshToken=apiUrl+"/token/refresh";
    public static  String GetMoyen=ChauffeurAPI+"/getmoyen";
    public static String Moyen =apiUrl+"/moyen";
    public static String GetClients=addClient+"";
    public static String ListChauffeurEtatComptePending =ChauffeurAPI+"/getby/etatcompte/pending";
    public static String ListChauffeurEtatCompteAccepted =ChauffeurAPI+"/getby/etatcompte/accepted";
    public static String ListChauffeurOnlineAndAccepted =ChauffeurAPI+"/getonlineandacceptedchauffeurs";

    public static String EtatCompteAccepted =ChauffeurAPI+"/etatcompteaccepted";
    public static String GetOnlineChauffeurs =ChauffeurAPI+"/getby/etat/online";


    public static String GetChauffeurs=ChauffeurAPI;
    public static String ProfilePicture=apiUrl+"/user/getimage";
    public static String Course=apiUrl+"/course";
    public static String Avis=Course+"/avis";

}
