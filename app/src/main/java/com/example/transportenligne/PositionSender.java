package com.example.transportenligne;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketState;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class PositionSender {


    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    Context context;
    Activity activity;
    Task<Location> locationResult;
    Timer timer = new Timer();

    public PositionSender(Context context, Activity activity) throws ExecutionException, InterruptedException {
        this.context = context;
        this.activity = activity;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationResult = fusedLocationProviderClient.getLastLocation();
        Tasks.await(locationResult);

        locationResult.addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Log.d("loca", "getDeviceLocation: Success Task");

                    if (lastKnownLocation != null) {
                        Log.d("loca", "getDeviceLocation: got location");

                        var message = new WebSocketMessage();
                        if (JWTUtils.IsClient(context)) {
                            message.type = "UpdateClientPosition";
                            message.clientId = Integer.parseInt(JWTUtils.GetId(context));
                            message.clientPosition = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                        } else {
                            message.type = "UpdateChauffeurPosition";
                            message.chauffeurId = Integer.parseInt(JWTUtils.GetId(context));
                            message.chauffeurPosition = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                        }

                        Intent intent = new Intent(context, WebsocketConnector.class);
                        var jsonmessage = new Gson().toJson(message);

                        intent.putExtra("message", jsonmessage);
                        context.startService(intent);
                    }
                }}
        });
        locationResult.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void Send(){

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(WebsocketConnector.ws!=null)
                {
                var state=WebsocketConnector.ws.getState();
                if(state== WebSocketState.OPEN)
            {
                try {
                    getDeviceLocation();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }}}}, 0, 10000);
    }
    private void getDeviceLocation() throws ExecutionException, InterruptedException {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        locationResult = fusedLocationProviderClient.getLastLocation();
        Tasks.await(locationResult);        Log.d("loca", "getDeviceLocation: Starting Task");

    }

}