package com.example.transportenligne;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.Chauffeur;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChauffeurListView extends AppCompatActivity {
    ListView listView;
    ChauffeurListAdapter adapter;
   List<Chauffeur> chauffeurs;

        private void GetChauffeursData() {
                    String path = Global.ListChauffeurOnlineAndAccepted;
                    HttpRequest.mQueue = Volley.newRequestQueue(ChauffeurListView.this);
                    HttpRequest.mQueue.add(HttpRequest.arrayRequest(Request.Method.GET, path, null,
                            ChauffeurListView.this, new VolleyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d("chauffeur", "online chauffeur list: " + response.toString());
                            Type listType = new TypeToken<ArrayList<Chauffeur>>(){}.getType();
                            chauffeurs = new Gson().fromJson(response.toString(), listType);
                            adapter=new ChauffeurListAdapter(ChauffeurListView.this,chauffeurs);
                            listView.setAdapter(adapter);
                            adapter.clear();
                            adapter.addAll(chauffeurs);
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onError(int statusCode, String message) {
                            Log.e("chauffeur ERROR", "error getting online chauffeur list " + statusCode + " " + message);
                        }
                    }));

    }

    String positionText;String positionGPS;String destinationGPS;String destinationInput;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chauffeur_list);

        listView=findViewById(R.id.listview);
        var intent=getIntent();
       positionText= intent.getStringExtra("positionText");
       positionGPS= intent.getStringExtra("positionGPS");
       destinationGPS= intent.getStringExtra("destinationGPS");
       destinationInput= intent.getStringExtra("destinationInput");
       chauffeurs=new ArrayList<Chauffeur>() ;
        GetChauffeursData();

        listView.setOnItemClickListener((adapter, v, position, arg3) -> {
            Intent intent1 = new Intent(ChauffeurListView.this, ChauffeurListMoyenView.class);
            var id=chauffeurs.get(position).user.id;
            intent1.putExtra("id",id);
            intent1.putExtra("positionText",positionText);
            intent1.putExtra("positionGPS",positionGPS);
            intent1.putExtra("destinationGPS",destinationGPS);
            intent1.putExtra("destinationInput",destinationInput);
            startActivity(intent1);

        });
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
