package com.example.transportenligne;

import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.Moyen;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChauffeurListMoyenEdit extends AppCompatActivity {
    ListView listView;
    MoyenListAdapter adapter;
   List<Moyen> moyens;
Button addButton;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chauffeur_list_moyen_edit1);
        moyens=new ArrayList<Moyen>();
        addButton=findViewById(R.id.addMoyen);
        mQueue = Volley.newRequestQueue(this);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChauffeurListMoyenEdit.this, MoyenCreate.class);
            startActivity(intent);
        });
        listView=findViewById(R.id.listview);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {

                                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
            }
        });
        GetMoyens();

    }
    @Override
    public void onResume() {
        super.onResume();

       GetMoyens();
    }
    private void GetMoyens() {
        String path=Global.GetMoyen+"/"+JWTUtils.GetId(ChauffeurListMoyenEdit.this);
        mQueue.add(HttpRequest.arrayRequest(Request.Method.GET,path,null,this, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("moyen", "moyen list response est la suivante: " + response.toString());
                Type listType = new TypeToken<ArrayList<Moyen>>(){}.getType();
                moyens = new Gson().fromJson(response.toString(), listType);
                Log.d("list", "GetMoyens: users count: "+moyens.size());
                adapter=new MoyenListAdapter(ChauffeurListMoyenEdit.this,moyens);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(int statusCode,String message) {
                Log.e("MOYEN ERROR", "error getting users "+statusCode+" "+message);
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
