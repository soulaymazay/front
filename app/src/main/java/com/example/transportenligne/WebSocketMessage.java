package com.example.transportenligne;

public class WebSocketMessage {
    public String type;
    public String clientDestination;
    public String clientPosition;
    public String clientDestinationGPS;
    public String clientPositionGPS;
    public String chauffeurPosition;
    public int clientId;
    public int chauffeurId;
    public String moyen;
    public int courseId;

public WebSocketMessage(String type, String clientDestination, int clientId, int chauffeurId)
{this.clientId=clientId;
    this.chauffeurId=chauffeurId;
    this.type=type;
    this.clientDestination = clientDestination;
}
public WebSocketMessage()
{

}
}
