package com.example.transportenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class test extends AppCompatActivity {
    EditText nom,moyen;
    Button add;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        nom=findViewById(R.id.nom);
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),test.class);
                i.putExtra("nom",nom.getText().toString());
                i.putExtra("moyen.",moyen.getText().toString());
                startActivity(i);
            }
        });
    }
}