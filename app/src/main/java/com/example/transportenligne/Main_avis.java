package com.example.transportenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.Course;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class Main_avis extends AppCompatActivity {


    Button submitavis;
    EditText avistext;
    int courseid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avis);
        submitavis=findViewById(R.id.submitavis);
        avistext=findViewById(R.id.avistext);
        var intent=getIntent();
        courseid=intent.getIntExtra("courseId",0);
        submitavis.setOnClickListener(v -> {
            try {
                SetAvis(avistext.getText().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void SetAvis(String avis) throws JSONException {
        Course course=new Course();
        course.avis=avis;
        JSONObject requestdata=new JSONObject();
        requestdata.put("avis",avis);
        String path=Global.Course+"/avis/"+courseid;
        HttpRequest.mQueue=  Volley.newRequestQueue( Main_avis.this);
        HttpRequest.mQueue.add(HttpRequest.objectRequest(Request.Method.PATCH, path,requestdata, Main_avis.this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
       finish();
            }
            @Override
            public void onError(int statusCode,String message) {
                Log.e("avis ERROR", "error login "+statusCode+" "+message);
                Toast.makeText(Main_avis.this, "Erreur", Toast.LENGTH_SHORT).show();
            }
        }));
}}