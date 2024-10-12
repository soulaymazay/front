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
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.Moyen;
import com.example.transportenligne.Models.VolleyCallback;
import com.example.transportenligne.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdminChauffeurListMoyenEdit extends AppCompatActivity {
    ListView listView;
    AdminMoyenListAdapter adapter;
   List<Moyen> moyens;
    private RequestQueue mQueue;
    private int chauffeurId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_chauffeur_list_moyen_edit);
        moyens=new ArrayList<Moyen>();
        mQueue = Volley.newRequestQueue(this);
      var intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e("admin list moyens intents", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }
      chauffeurId=intent.getIntExtra("id",0);
        listView=findViewById(R.id.listview);


        listView.setOnItemClickListener((adapter, v, position, arg3) -> {
        });
        GetMoyensById();

    }
    @Override
    public void onResume() {
        super.onResume();

        GetMoyensById();
    }
    private void GetMoyensById() {
        String path;
        if(chauffeurId==-1)
        {
            path=Global.Moyen+"/etat/Accepted";
        }
        else if(chauffeurId==0)
        {
             path=Global.Moyen+"/etat/pending";
        }
        else
         path= Global.GetMoyen+"/"+chauffeurId;
        mQueue.add(HttpRequest.arrayRequest(Request.Method.GET,path,null,this, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("moyen", "moyen list response est la suivante: " + response.toString());
                Type listType = new TypeToken<ArrayList<Moyen>>(){}.getType();
                moyens = new Gson().fromJson(response.toString(), listType);
                Log.d("list", "GetMoyens: users count: "+moyens.size());
                adapter=new AdminMoyenListAdapter(AdminChauffeurListMoyenEdit.this,moyens);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(int statusCode,String message) {
                Log.e("MOYEN ERROR", "error getting moyens "+statusCode+" "+message);
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
