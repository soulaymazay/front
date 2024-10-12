package com.example.transportenligne.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.transportenligne.AvisList;
import com.example.transportenligne.ChauffeurDashboard;
import com.example.transportenligne.JWTUtils;
import com.example.transportenligne.Introduction;
import com.example.transportenligne.R;

public class AdminDashboard extends AppCompatActivity {
    Button gererclient, gererchauffeur,gereracceptedmoyens,gererpendingchauffeur,logoutadmin,avisadmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admindashboard);
        gererclient = findViewById(R.id.gererclient);
        gereracceptedmoyens = findViewById(R.id.gereracceptedmoyens);
        gererpendingchauffeur = findViewById(R.id.gererpendingchauffeur);
        logoutadmin = findViewById(R.id.logoutadmin);
        gererchauffeur = findViewById(R.id.gererchauffeur);
        avisadmin = findViewById(R.id.avisadmin);
        var i= getIntent();
        var role=i.getStringExtra("role");
        Toast.makeText(this, role, Toast.LENGTH_SHORT).show();
        var gererpendingmoyens = findViewById(R.id.gererpendingmoyens);
        gererclient.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminClientList.class);
            startActivity(intent);
        });
        gererchauffeur.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminChauffeurList.class);
            startActivity(intent);
        });
        gererpendingchauffeur.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminChauffeurList.class);
            intent.putExtra("pendingOnly",true);
            startActivity(intent);
        });
        gererpendingmoyens.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminChauffeurListMoyenEdit.class);
            startActivity(intent);
        });
        gereracceptedmoyens.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminChauffeurListMoyenEdit.class);
            intent.putExtra("id",-1);
            startActivity(intent);
        });
        avisadmin.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AvisList.class);
            intent.putExtra("mode","admin");
            startActivity(intent);
        });
        logoutadmin.setOnClickListener(view -> {
            JWTUtils.Logout(AdminDashboard.this);
        });
    }
}