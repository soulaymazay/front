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
import com.example.transportenligne.Models.Course;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.Moyen;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChauffeurListMoyenView extends AppCompatActivity {
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
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e("moyenlist", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }
        positionText= intent.getStringExtra("positionText");
        positionGPS= intent.getStringExtra("positionGPS");
        destinationGPS= intent.getStringExtra("destinationGPS");
        destinationInput= intent.getStringExtra("destinationInput");
            id=intent.getIntExtra("id",0);
        GetMoyens();

        adapter=new MoyenListAdapter(this,moyens);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
              Course course=new Course();
                course.chauffeur =id;
                course.client =Integer.parseInt(JWTUtils.GetId(ChauffeurListMoyenView.this));
                course.destinationGPS=destinationGPS;
                course.positionGPS=positionGPS;
                course.inputdestination =destinationInput;
                course.inputposition =positionText;
                course.moyen = String.valueOf(moyens.get(position).id);
                String path=Global.Course;
                var gson=new Gson().toJson(course);
                JSONObject requestdata = null;
                try {
                    requestdata = new JSONObject(gson);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                HttpRequest.mQueue=  Volley.newRequestQueue( ChauffeurListMoyenView.this);
                HttpRequest.mQueue.add(HttpRequest.objectRequest(Request.Method.POST, path,requestdata, ChauffeurListMoyenView.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        var message = new WebSocketMessage();
                        message.type="ClientRequest";
                        message.chauffeurId=id;
                        message.clientId= Integer.parseInt(JWTUtils.GetId(ChauffeurListMoyenView.this));
                        Gson gson=new Gson();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            }).create();

                        }
                       var course = gson.fromJson(result.toString(), Course.class);                        message.courseId=course.id;
                        message.moyen= String.valueOf(moyens.get(position).id);
                        Intent acceptIntent = new Intent(ChauffeurListMoyenView.this, WebsocketConnector.class);
                        acceptIntent.putExtra("message",new Gson().toJson(message));
                        startService(acceptIntent);
                        Toast.makeText(ChauffeurListMoyenView.this, "Demande Envoyé", Toast.LENGTH_SHORT).show();
                        var homeintent=new Intent(ChauffeurListMoyenView.this, ClientDashboard.class);
                        startActivity(homeintent);
                        finish();
                    }
                    @Override
                    public void onError(int statusCode,String message) {
                        var homeintent=new Intent(ChauffeurListMoyenView.this, ClientDashboard.class);

                        startActivity(homeintent);

                        finish();
                    }
                }));

            }
        });
    }

    private void GetMoyens() {
        JsonArrayRequest jsonRequest2;

            try {
            // Create a new JsonObjectRequest with the request method, URL, and the JSON data
            //JsonObjectRequest finalJsonRequest = jsonRequest;
            jsonRequest2 = new JsonArrayRequest(Request.Method.GET, Global.ChauffeurAPI+"/getmoyenaccepted/"+id, null,
                    response -> {

                        Log.d("moyen", "moyen list response est la suivante: " + response.toString());

                        Type listType = new TypeToken<ArrayList<Moyen>>(){}.getType();
                        moyens = new Gson().fromJson(response.toString(), listType);
                        Log.d("list", "GetMoyenss count: "+moyens.size());
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
                    headers.put("Authorization", "Bearer " + JWTUtils.GetJWT(ChauffeurListMoyenView.this));
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
