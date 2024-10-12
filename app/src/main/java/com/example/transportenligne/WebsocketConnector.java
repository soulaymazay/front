package com.example.transportenligne;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebsocketConnector extends Service {
    static WebSocket ws = null;
    PositionSender positionSender;

    private final Handler mHandler = new Handler();
    @Override
    public void onCreate() {
        Log.d("WebsocketConnector", "onCreate: Creating Websocket");
        super.onCreate();

        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        try {
            String role = "";
            if (JWTUtils.IsClient(this)) {
                Log.d("WebsocketConnector", "onCreate: IsClient");

                role = "Client";
            } else {
                Log.d("WebsocketConnector", "onCreate: isChauffeur");

                role = "Chauffeur";
            }
            ws = factory.createSocket(Global.wsUrl + "?" + role + "=" + JWTUtils.GetId(this));


            ws.setUserInfo(JWTUtils.GetJWT(this));
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    Log.d("WebsocketConnector", "Message Received: " + message);
                    JsonElement jsonElement = JsonParser.parseString(message);
                    if(jsonElement.isJsonObject()) {
                        WebSocketMessage socketMessage = new Gson().fromJson(message, WebSocketMessage.class);
                        Log.d("WebsocketConnector", "Serialized message type: " + socketMessage.type);

                        RequestNotification(socketMessage);
                    }
                    // if json array
                    else{
                        Log.d("chauufeurdata", "onTextMessage: data received");
                        Intent intent = new Intent ("ChauffeurData");
                        intent.putExtra("message", message);
                        LocalBroadcastManager.getInstance(WebsocketConnector.this).sendBroadcast(intent);
                    }
                }
            });

            Thread thread = new Thread(() -> {
                try {
                    ws.connect();
                    Log.d("WebsocketConnector", "Connected to ws: " + Global.wsUrl + " " + ws.getState());
                    ;
                } catch (Exception e) {
                    Log.d("WebsocketConnector", "Exception connecting to websocket");

                    e.printStackTrace();
                }
            });
            thread.start();

            int NOTIFICATION_ID = 102;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(NOTIFICATION_ID, new NotificationCompat.Builder(this, "Client")
                        .setSmallIcon(R.drawable.imageapp)
                        .setContentTitle("Transport en ligne")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int notificationId = intent.getIntExtra("notificationId", 101);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(notificationId);
        var message = intent.getStringExtra("message");
        Log.d("WebsocketConnector", "Sending to ws server:" + message);
        Log.d("WebsocketConnector", "notificationid:" + notificationId);
        WebSocketMessage socketMessage = new Gson().fromJson(message, WebSocketMessage.class);
        SendMessage(socketMessage);
        return Service.START_STICKY_COMPATIBILITY;
    }

    private void RequestNotification(WebSocketMessage message) {
        Intent messageintent = new Intent(WebsocketConnector.this, LocalNotificationManager.class);
        var jsonmessage = new Gson().toJson(message);
        messageintent.putExtra("message", jsonmessage);
        sendBroadcast(messageintent);
    }



public static void SendMessage(WebSocketMessage message)
{
    var serializedobject=new Gson().toJson(message);
    if(ws!=null)
    ws.sendText(serializedobject);
}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
