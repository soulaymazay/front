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
import com.example.transportenligne.Models.Course;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AvisList extends AppCompatActivity {
    ListView listView;
    CourseListAdapter adapter;
   List<Course> courses;
   String mode;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_list_course);
        courses=new ArrayList<Course>();
        mQueue = Volley.newRequestQueue(this);
        var intent=getIntent();
        mode=intent.getStringExtra("mode");
        listView=findViewById(R.id.listview);
        GetCourses();

    }
    @Override
    public void onResume() {
        super.onResume();

       GetCourses();
    }
    private void GetCourses() {
        String path = null;
        if(mode.equals("admin"))
        {
            path=Global.Course;
        }
        else if(mode.equals("client"))
        {
            path=Global.Course+"/client/"+JWTUtils.GetId(this);
        }
        else if(mode.equals("chauffeur"))
        {
            path=Global.Course+"/chauffeur/"+JWTUtils.GetId(this);
        }
        else if(mode.equals("moyen"))
        {var intent=getIntent();
            int moyenid=intent.getIntExtra("moyen",0);
            path=Global.Course+"/moyen/"+moyenid;
        }
        mQueue.add(HttpRequest.arrayRequest(Request.Method.GET,path,null,this, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("course", "course list response est la suivante: " + response.toString());
                Type listType = new TypeToken<ArrayList<Course>>(){}.getType();
                Gson gson=new Gson();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                     gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                         return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                }).create();

            }
                courses = gson.fromJson(response.toString(), listType);
                Log.d("list", "GetCourses: users count: "+courses.size());
                adapter=new CourseListAdapter(AvisList.this,courses,mode);
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
