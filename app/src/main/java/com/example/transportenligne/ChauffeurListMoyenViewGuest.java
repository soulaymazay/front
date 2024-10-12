package com.example.transportenligne;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.Moyen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChauffeurListMoyenViewGuest extends AppCompatActivity {
    ListView listView;
    MoyenListAdapter adapter;
   List<Moyen> moyens;
   String positionText;String positionGPS;String destinationGPS;String destinationInput;

    int id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chauffeur_list_moyen_view);
        moyens=new ArrayList<Moyen>();

        listView=findViewById(R.id.listview);
        var intent=getIntent();
        positionText= intent.getStringExtra("positionText");
        positionGPS= intent.getStringExtra("positionGPS");
        destinationGPS= intent.getStringExtra("destinationGPS");
        destinationInput= intent.getStringExtra("destinationInput");
        GetMoyens();
            id=intent.getIntExtra("id",0);
        adapter=new MoyenListAdapter(this,moyens);
        listView.setAdapter(adapter);
        if(id!=0)
        {
        listView.setOnItemClickListener((adapter, v, position, arg3) -> {
            var message = new WebSocketMessage();
            message.type="ClientRequest";
            message.clientId=Integer.parseInt(JWTUtils.GetId(ChauffeurListMoyenViewGuest.this));
            message.chauffeurId= id ;
            message.clientDestination =destinationInput;
            message.clientPositionGPS=positionGPS;
            message.clientPosition=positionText;
            message.clientDestinationGPS=destinationGPS;
            message.moyen=moyens.get(position).nom+" "+moyens.get(position).marque;

            Intent acceptIntent = new Intent(ChauffeurListMoyenViewGuest.this, WebsocketConnector.class);
            acceptIntent.putExtra("message",new Gson().toJson(message));
                startService(acceptIntent);
            Toast.makeText(ChauffeurListMoyenViewGuest.this, "Demande Envoyé", Toast.LENGTH_SHORT).show();
            var homeintent=new Intent(ChauffeurListMoyenViewGuest.this, ClientDashboard.class);
            startActivity(homeintent);
        });
    }
    }
    @Override
    public void onResume() {
        super.onResume();

       GetMoyens();
    }
    private void GetMoyens() {
        JsonArrayRequest jsonRequest2;
        String path;
        if(id!=0)
        {
            path=Global.GetMoyen+"/"+id;
        }
        else{
            path=Global.Moyen+"/etat/Accepted";
        }
            try {
            // Create a new JsonObjectRequest with the request method, URL, and the JSON data
            //JsonObjectRequest finalJsonRequest = jsonRequest;
            jsonRequest2 = new JsonArrayRequest(Request.Method.GET, path, null,
                    response -> {

                        Log.d("moyen", "moyen list response est la suivante: " + response.toString());

                        Type listType = new TypeToken<ArrayList<Moyen>>(){}.getType();
                        moyens = new Gson().fromJson(response.toString(), listType);
                        Log.d("list", "GetMoyens: users count: "+moyens.size());
                        adapter.clear();
                        adapter.addAll(moyens);
                        adapter.notifyDataSetChanged();
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle errors that occurred during the request
                            if (error instanceof TimeoutError) {
                                // Retry the request
                                Log.d("MyApp", "retry ....");

                            } else {
                                error.printStackTrace();
                            }
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + JWTUtils.GetJWT(ChauffeurListMoyenViewGuest.this));
                    return headers;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

// Add the request to the RequestQueue
        jsonRequest2.setRetryPolicy(new RetryPolicy() {
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
                Log.d("MyApp", error.toString());


                Volley.newRequestQueue(getApplicationContext()).add(jsonRequest2);
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(jsonRequest2);
        /////////////////////////
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.item_done)
        {
            String itemSelected="selected items: \n";
            for(int i=0;i<listView.getCount();i++)
            {
                if(listView.isItemChecked(i))
                {
                    itemSelected+=listView.getItemAtPosition(i)+"\n";

                }
            }
            Toast.makeText(this,itemSelected,Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
