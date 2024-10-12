package com.example.transportenligne.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Global;
import com.example.transportenligne.Models.Chauffeur;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;
import com.example.transportenligne.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdminChauffeurList extends AppCompatActivity {
    ListView listView;
    AdminChauffeurListAdapter adapter;
   List<Chauffeur> chauffeurs;  boolean pendingOnly;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_chauffeur_list_edit);
        chauffeurs= new ArrayList<>();
        mQueue = Volley.newRequestQueue(this);
        var intent=getIntent();
         pendingOnly=intent.getBooleanExtra("pendingOnly",false);
        listView=findViewById(R.id.listviewchauffeurlistedit);
        listView.setOnItemClickListener((adapter, v, position, arg3) -> {
        });
        GetChauffeurs();

    }
    @Override
    public void onResume() {
        super.onResume();
    }
    private void GetChauffeurs() {
        String path;
        if(pendingOnly)
        {
            path= Global.ListChauffeurEtatComptePending;
        }
        else
            path= Global.ListChauffeurEtatCompteAccepted;
        mQueue.add(HttpRequest.arrayRequest(Request.Method.GET,path,null,this, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("AdminChauffeurList", "admin list response: " + response.toString());
                Type listType = new TypeToken<ArrayList<Chauffeur>>(){}.getType();
                chauffeurs = new Gson().fromJson(response.toString(), listType);
                Log.d("AdminChauffeurList", "GetChauffeurs count: "+chauffeurs.size());

                adapter=new AdminChauffeurListAdapter(AdminChauffeurList.this,chauffeurs);
                listView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                var count=adapter.getCount();
            }
            @Override
            public void onError(int statusCode,String message) {
                Log.e("AdminChauffeurList", "error getting chauffeurs "+statusCode+" "+message);
            }
        }));
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
