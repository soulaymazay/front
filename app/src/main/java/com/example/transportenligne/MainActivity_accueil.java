package com.example.transportenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity_accueil extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton radioButton,radioButton2;
    Button button1,button2,button3 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        radioButton=(RadioButton) findViewById(R.id.radio3);
        radioButton2=(RadioButton) findViewById(R.id.radio4);
        radioGroup=findViewById(R.id.radiogroup1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selected);
                Intent intent = new Intent(MainActivity_accueil.this, ChauffeurListMoyenViewGuest.class);

                startActivity(intent);
            }
        });
        button2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity_accueil.this, LoginActivity.class);
            startActivity(intent);
        });
        button3.setOnClickListener(view -> {
            if(radioButton.isChecked()){
                Intent intent = new Intent(MainActivity_accueil.this, ClientSignup.class);
                startActivity(intent);
            }
            else if (radioButton2.isChecked()){
                Intent intent = new Intent(MainActivity_accueil.this, ChauffeurSignup.class);
                startActivity(intent);
            }
        });

    }
}