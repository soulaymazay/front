package com.example.transportenligne;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.Chauffeur;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ChauffeurDashboard extends AppCompatActivity {

    SwitchCompat etatSwitch;
    TextView nameText;
    ImageView profileImage;
    JsonObjectRequest jsonRequest; NavigationView navigationView;
    PositionSender positionSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activitydashbord_chauffeur);
        nameText = findViewById(R.id.nameText);
        etatSwitch = findViewById(R.id.etatSwitch);
        profileImage = findViewById(R.id.userprofilepicture);
        DrawerLayout drawerLayout = findViewById(R.id.drawerlayaout);
        findViewById(R.id.imagemenu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

         navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        GetProfilePicture();

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.menuparametreMoyen) {
                // Start the ParametreActivity
                Intent intent = new Intent(ChauffeurDashboard.this, ChauffeurListMoyenEdit.class);
                startActivity(intent);
            } else if (itemId == R.id.menuinfo) {
                // Start the AvisActivity
                Intent intent = new Intent(ChauffeurDashboard.this, Main_infoapp.class);
                startActivity(intent);
            } else if (itemId == R.id.logout) {
                JWTUtils.Logout(ChauffeurDashboard.this);
            }
            // Close the drawer after selecting an item
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        nameText.setText(getString(R.string.WelcomeText) + JWTUtils.GetValue(this, "name"));
        etatSwitch.setOnCheckedChangeListener(SwitchListener);
        String path = Global.ChauffeurAPI + "/" + JWTUtils.GetId(this);
        HttpRequest.mQueue = Volley.newRequestQueue(this);
        HttpRequest.mQueue.add(HttpRequest.objectRequest(Request.Method.GET, path, null, this, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("moyen", "moyen list response est la suivante: " + response.toString());
                var chauffeur = new Gson().fromJson(response.toString(), Chauffeur.class);
                var state = chauffeur.etat.equals("online");
                etatSwitch.setChecked(state);
                if(chauffeur.user.image!=null) {

                    byte[] decodedString = Base64.decode(chauffeur.user.image,Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                var stringimage= new String(decodedString, StandardCharsets.UTF_8);
                profileImage.setImageBitmap(decodedByte);}
            }

            @Override
            public void onError(int statusCode, String message) {
                Log.e("State ERROR", "error getting state " + statusCode + " " + message);
            }
        }));

        String profilepicpath = Global.ProfilePicture + "/" + JWTUtils.GetId(this);

    }
    private void GetProfilePicture() {
        String path=Global.apiUrl+"/user/getimage/"+JWTUtils.GetId(this);
        HttpRequest.mQueue=  Volley.newRequestQueue( ChauffeurDashboard.this);
        HttpRequest.mQueue.add(HttpRequest.stringRequest(Request.Method.GET, path,null, ChauffeurDashboard.this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                View hView = navigationView.getHeaderView(0);
                ImageView image=(ImageView) hView.findViewById(R.id.userprofilepicture);
                TextView username=(TextView) hView.findViewById(R.id.usernameheader);
                username.setText(JWTUtils.GetValue(ChauffeurDashboard.this,"name"));
                if(result!=null) {

                    byte[] decodedString = Base64.decode(result,Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    var stringimage= new String(decodedString, StandardCharsets.UTF_8);
                    image.setImageBitmap(decodedByte);}
            }
            @Override
            public void onError(int statusCode,String message) {
                Log.e("avis ERROR", "error profile "+statusCode+" "+message);
                Toast.makeText(ChauffeurDashboard.this, "Erreur", Toast.LENGTH_SHORT).show();
            }
        }));
    }
    private CompoundButton.OnCheckedChangeListener SwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SwitchState(isChecked);
        }
    };

    private void SwitchState(boolean isChecked) {

        JSONObject requestData = new JSONObject();
        try {if(isChecked)
            requestData.put("etat", "online");
            else             requestData.put("etat", "offline");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonRequest = new JsonObjectRequest(Request.Method.PUT, Global.ChauffeurAPI+"/"+JWTUtils.GetId(ChauffeurDashboard.this), requestData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + JWTUtils.GetJWT(ChauffeurDashboard.this));
                return headers;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
    }
    private RetryPolicy retryPolicy=new RetryPolicy() {
        @Override
        public int getCurrentTimeout() {
            return 60000; // Timeout in milliseconds
        }

        @Override
        public int getCurrentRetryCount() {
            return 3; // Number of retries
        }

        @Override
        public void retry(VolleyError error) throws VolleyError {
            // You can log the retry attempt here
            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
        }
    };

}
