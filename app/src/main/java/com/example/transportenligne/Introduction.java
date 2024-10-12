package com.example.transportenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Admin.AdminDashboard;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.LoginResponse;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Introduction extends AppCompatActivity {
    ImageButton imageButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageButton = findViewById(R.id.flesh);
        createNotificationChannel("Chauffeur","Chauffeur","Chauffeur notifications");
        createNotificationChannel("Client","Client","Client notifications");
        String token=JWTUtils.GetJWT(this);
        String refresh_token=JWTUtils.GetRefreshToken(this);
        Intent i;
        if(!token.isBlank() && !refresh_token.isBlank())
        {

            if(JWTUtils.TokenExpired(this))
            {
                JSONObject requestData=new JSONObject();
                try {
                    requestData.put("refresh_token",JWTUtils.GetRefreshToken(this));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                HttpRequest.mQueue=  Volley.newRequestQueue( this);
                HttpRequest.mQueue.add(HttpRequest.objectRequest(Request.Method.POST,
                        Global.Login,requestData, this, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        var loginresponse=new Gson().fromJson(result, LoginResponse.class);
                        JWTUtils.SaveJWT(Introduction.this,loginresponse.token);
                    }
                    @Override
                    public void onError(int statusCode,String message) {
                        Log.e("Refresh token ERROR", "error login "+statusCode+" "+message);
                    }
                }));
            }
            Intent websocketIntent=new Intent(this,WebsocketConnector.class);

            var roles =JWTUtils.GetRoles(this);


            Log.e("role is: ", roles.toString());
            if(JWTUtils.HasRole(this,"ROLE_CHAUFFEUR")) {
                i = new Intent(getApplicationContext(), ChauffeurDashboard.class);
                websocketIntent.putExtra("role","Chauffeur");
            }
            else if(JWTUtils.HasRole(this,"ROLE_ADMIN")) {
                i = new Intent(getApplicationContext(), AdminDashboard.class);
                i.putExtra("role","Admin");
            } else if(JWTUtils.HasRole(this,"ROLE_GERANT")) {
                i = new Intent(getApplicationContext(), AdminDashboard.class);
                i.putExtra("role","Gerant");
            }
            else{
                i = new Intent(getApplicationContext(), ClientDashboard.class);
                websocketIntent.putExtra("role","Client");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(websocketIntent);
            }
            startActivity(i);finish();
        }
                imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Introduction.this, MainActivity_accueil.class);
            startActivity(intent);
        });
    }
    private void createNotificationChannel(String channelID,String channel_name,String channel_description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, channel_name, importance);
            channel.setDescription(channel_description);
            channel.setDescription(channel_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
