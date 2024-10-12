package com.example.transportenligne;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.Course;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class LocalNotificationManager extends BroadcastReceiver {
    int flag;
    String channelId = new String();

    Context context;
    int notificationId;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "onTextMessage notif: ");
        notificationId= (int)System.currentTimeMillis();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
            flag |= PendingIntent.FLAG_IMMUTABLE;

        }
        else flag=0;
        this.context=context;
        String jsonmessage = intent.getStringExtra("message");
        WebSocketMessage socketMessage = new Gson().fromJson(jsonmessage, WebSocketMessage.class);
        String title = "this shouldn't appear";
        int courseId=socketMessage.courseId;

        String message = "this message should not appear";
        if (socketMessage.chauffeurId>0) {
            channelId = "Chauffeur";
        }
        if (socketMessage.chauffeurId>0) {
            channelId = "Client";
        }
        switch (socketMessage.type) {
            case "ClientRequest": {

                String path=Global.Course+"/"+courseId;
                HttpRequest. mQueue = Volley.newRequestQueue(context);
                HttpRequest.mQueue.add(HttpRequest.objectRequest(Request.Method.GET,path,null,context, new VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("moyen", "course received " + response.toString());
                        Gson gson=new Gson();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            }).create();

                        }
                        var course = gson.fromJson(response.toString(), Course.class);
                        course.chauffeur=Integer.parseInt(JWTUtils.GetId(context));
                        BuildNotification(course);
                    }
                    @Override
                    public void onError(int statusCode,String message) {
                        Log.e("course ERROR", "error getting course "+statusCode+" "+message);
                    }
                }));
                break;
            }
            case "ClientRequestRefused": {
                title = "Demande refusé";
                message = "cliquez ici pour choisir à nouveau";
                BuildNotification(title,message);
                break;
            }
            case "ClientRequestAccepted": {
                title = "Demande Accepté";
                message="votre demande a été acceptée";
                BuildNotification(title,message);

                break;
            }
        }

    }

    private NotificationCompat.Builder CreateNotificationBuilder() {
        var builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.imageapp)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder;
    }

    private void BuildNotification(Course course) {
        var builder=CreateNotificationBuilder();
        builder.setContentTitle("Demande Client");
        builder.setContentText("Position: "+course.inputposition +"\n"+"Destination: "+course.inputdestination);
        var pendingAcceptIntent =   CreateAcceptIntent(course);

        var pendingRejectIntent = CreateRejectIntent(course);
        NotificationCompat.Action.Builder notifbuilder2 = new NotificationCompat.Action.Builder(0, "Refuser", pendingRejectIntent);
        NotificationCompat.Action rejectAction = notifbuilder2.build();
        builder.addAction(rejectAction);

        Intent viewIntent = new Intent(context, ClientChauffeurPosition.class);
        var serializedcourse=new Gson().toJson(course);
        viewIntent.putExtra("message",serializedcourse);
        viewIntent.putExtra("notificationId",notificationId);
        var pendingViewintent = PendingIntent.getActivity(context, 0, viewIntent,flag);
        NotificationCompat.Action.Builder notifbuilder = new NotificationCompat.Action.Builder(0, "Accepter", pendingAcceptIntent);
        NotificationCompat.Action acceptAction = notifbuilder.build();
        NotificationCompat.Action.Builder notifbuilder3 = new NotificationCompat.Action.Builder(0, "Details", pendingViewintent);
        NotificationCompat.Action action3= notifbuilder3.build();
        builder.addAction(acceptAction);
        builder.addAction(action3);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        var notification= builder.build();
        notification.flags|=Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId,notification);
    }

    private PendingIntent CreateAcceptIntent(Course course) {
        var acceptmessage = new WebSocketMessage();
        acceptmessage.type="ClientRequestAccepted";
        acceptmessage.chauffeurId=course.chauffeur;
        acceptmessage.clientId= course.client; ;
        acceptmessage.courseId= course.id;

        Intent acceptIntent = new Intent(context, WebsocketConnector.class);
        acceptIntent.setAction(new Random().nextInt(50) + "_action");
        acceptIntent.putExtra("message",new Gson().toJson(acceptmessage));
        acceptIntent.putExtra("notificationId",notificationId);
        return PendingIntent.getService(context, 0, acceptIntent,flag);
    }
    private PendingIntent CreateRejectIntent(Course course) {
        var rejectmessage = new WebSocketMessage();
        rejectmessage.type="ClientRequestRefused";
        rejectmessage.chauffeurId=course.chauffeur;
        rejectmessage.courseId= course.id;

        rejectmessage.clientId= course.client;
        Intent rejectIntent = new Intent(context, WebsocketConnector.class);
        var jsonrejectmessage=new Gson().toJson(rejectmessage);
        rejectIntent.setAction(new Random().nextInt(50) + "_action");

        rejectIntent.putExtra("message",jsonrejectmessage);
        rejectIntent.putExtra("notificationId",notificationId);
        return       PendingIntent.getService(context, 0, rejectIntent,flag);

    }

    private void BuildNotification(String title,String message) {
        var builder=CreateNotificationBuilder();
        builder.setContentTitle(title);
        builder.setContentText(message);
        var notification= builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notification.flags|=Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId,notification);

    }

}
