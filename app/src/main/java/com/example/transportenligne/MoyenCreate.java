package com.example.transportenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.AppHelper;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.Moyen;
import com.example.transportenligne.Models.VolleyCallback;
import com.example.transportenligne.Models.VolleyMultipartRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MoyenCreate extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button buttonChoose;
    private ImageView imageView;

    private EditText mMarqueEdiText;
    private EditText mModeleEditText;
    private EditText mAnneeEditText;
    private EditText coleurText;
    private Button mSignupButton;
RadioButton moto;
RadioButton voiture;    private RequestQueue mQueue;

    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moyentransportform);
        mQueue = Volley.newRequestQueue(this);
        buttonChoose = findViewById(R.id.buttonChoose);
        imageView = findViewById(R.id.imageView);
        moto=findViewById(R.id.moto);
        coleurText=findViewById(R.id.couleur);
        voiture=findViewById(R.id.voiture);
        mMarqueEdiText = findViewById(R.id.numPermis);
        mModeleEditText = findViewById(R.id.nomChauffeur);
        mAnneeEditText = findViewById(R.id.emailChauffeur);
        mSignupButton = findViewById(R.id.loginButton);
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });




        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String marque = mMarqueEdiText.getText().toString().trim();
                String modele = mModeleEditText.getText().toString().trim();
                String annee = mAnneeEditText.getText().toString().trim();
                String type;
                String couleur=coleurText.getText().toString().trim();
                boolean motochecked=moto.isChecked();
                boolean voiturechecked=voiture.isChecked();
                if(motochecked==false && voiturechecked==false) {
                    moto.setError("Choisir moyen");
                    moto.requestFocus();
                    return;
                }
                else
                {
                    if(motochecked)
                    {
                        type="Moto";
                    }
                    else type="Voiture";
                }
                if (marque.isEmpty()) {
                    mMarqueEdiText.setError("La marque complète est requise.");
                    mMarqueEdiText.requestFocus();
                    return;
                }
                if (modele.isEmpty()) {
                    mModeleEditText.setError("Le modèle est requis.");
                    mModeleEditText.requestFocus();
                    return;
                }

                if (annee.isEmpty()) {
                    mAnneeEditText.setError("L'année est requise.");
                    mAnneeEditText.requestFocus();
                    return;
                }
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int selectedYear;
                try {
                    selectedYear = Integer.parseInt(annee);
                } catch (NumberFormatException e) {
                    mAnneeEditText.setError("L'année doit être composée uniquement de chiffres.");
                    mAnneeEditText.requestFocus();
                    return;
                }
                if (selectedYear > currentYear) {
                    mAnneeEditText.setError("L'année ne doit pas être dans le futur.");
                    mAnneeEditText.requestFocus();
                    return;
                }
                Moyen moyen=new Moyen(0,type,marque,couleur,annee,modele,"","");
                moyen.userId=Integer.parseInt(JWTUtils.GetId(MoyenCreate.this));
                HashMap<String,String> map=new HashMap<>();
                var data=new Gson().toJson(moyen);
                map.put("data",data);
                Map<String, VolleyMultipartRequest.DataPart> fileparams = new HashMap<>();
                fileparams.put("image", new VolleyMultipartRequest.DataPart("moyen.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), imageView.getDrawable()), "image/jpeg"));
                mQueue.add(HttpRequest.multipartRequest(Request.Method.POST, Global.Moyen,map,fileparams, MoyenCreate.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("moyen", "moyen response est la suivante: " + result.toString());
                        finish();
                    }
                    public void onError(int statusCode,String message) {
                        Log.e("MOYEN ERROR", "error adding moyen"+statusCode+" "+message);
                    }
                }));
        }});
    }
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choisir une image"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            Uri filePath = data.getData();
            try {
                // Récupère l'image à partir de l'URI
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // Affiche l'image sur l'ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
