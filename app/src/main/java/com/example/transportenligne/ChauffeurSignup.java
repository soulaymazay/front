package com.example.transportenligne;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.AppHelper;
import com.example.transportenligne.Models.Chauffeur;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;
import com.example.transportenligne.Models.VolleyMultipartRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChauffeurSignup extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Constante pour identifier la demande de sélection d'image

    private EditText mPermisEdiText;
    private EditText mFullNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mVfpasswordEditText;
    private EditText mageEditText;
    private Button mSignupButton, btnimprofil;
    private ImageView imageprofil;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscrit_infochauffeur);

        mPermisEdiText = findViewById(R.id.numPermis);
        mFullNameEditText = findViewById(R.id.nomChauffeur);
        mEmailEditText = findViewById(R.id.emailChauffeur);
        mPasswordEditText = findViewById(R.id.password);
        mVfpasswordEditText = findViewById(R.id.passwordVerify);
        mageEditText = findViewById(R.id.agechauffeur);
        mSignupButton = findViewById(R.id.loginButton);
        btnimprofil = findViewById(R.id.buttonChoose);
        imageprofil = findViewById(R.id.imageView);
        imageprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        mSignupButton.setOnClickListener(view -> {
            VerifyInputs();
            JSONObject requestData = new JSONObject();
            var numpermis=mPermisEdiText.getText().toString().trim();
            var username=mFullNameEditText.getText().toString().trim();
            var password= mPasswordEditText.getText().toString().trim();
            var email= mEmailEditText.getText().toString().trim();
            var age= mageEditText.getText().toString().trim();

            Chauffeur chauffeur=new Chauffeur();
            chauffeur.email=email;
            chauffeur.username=username;
            chauffeur.numpermis=numpermis;
            chauffeur.password=password;

            HashMap<String,String> map=new HashMap<>();
            var data=new Gson().toJson(chauffeur);
            map.put("data",data);
            Map<String, VolleyMultipartRequest.DataPart> fileparams = new HashMap<>();
            fileparams.put("image", new VolleyMultipartRequest.DataPart("moyen.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), imageprofil.getDrawable()), "image/jpeg"));
            HttpRequest.mQueue = Volley.newRequestQueue(this);

            HttpRequest.mQueue.add(HttpRequest.multipartRequest(Request.Method.POST, Global.ChauffeurAPI,map,fileparams, ChauffeurSignup.this, new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("MyApp","response est la suivante: "+result.toString());
                    Toast.makeText(ChauffeurSignup.this, "Inscription réussie!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                @SuppressLint("SuspiciousIndentation")
                public void onError(int statusCode, String message) {

                    if(message.contains("email exists"))
                        Toast.makeText(ChauffeurSignup.this, "Email existe", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ChauffeurSignup.this, "Inscription echoué!", Toast.LENGTH_SHORT).show();
                }
            }));
        });;
    }




    private void VerifyInputs() {
        // Récupérer les valeurs des champs
        String fullName = mFullNameEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String vfpassword = mVfpasswordEditText.getText().toString().trim();

        // Vérifier si les champs sont valides
        String ageString = mageEditText.getText().toString().trim();

        if (!ageString.isEmpty()) {
            int age = Integer.parseInt(ageString);

            if (age >= 18 && age <= 55) {
                // L'âge est valide
                Toast.makeText(getApplicationContext(), "l'âge est valide", Toast.LENGTH_SHORT).show();
                // Autres actions à effectuer lorsque l'âge est valide

            } else {
                // L'âge est invalide
                Toast.makeText(getApplicationContext(), "L'âge doit être compris entre 18 ans et 55 ans.", Toast.LENGTH_SHORT).show();
                // Autres actions à effectuer lorsque l'âge est invalide
            }
        } else {
            // Champ d'âge vide
            Toast.makeText(getApplicationContext(), "Veuillez saisir votre âge.", Toast.LENGTH_SHORT).show();
            // Autres actions à effectuer lorsque le champ d'âge est vide
        }

        if (fullName.isEmpty()) {
            mFullNameEditText.setError("Le nom complet est requis.");
            mFullNameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            mEmailEditText.setError("L'adresse e-mail est requise.");
            mEmailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mPasswordEditText.setError("Le mot de passe est requis.");
            mPasswordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mPasswordEditText.setError("Le mot de passe doit comporter au moins 6 caractères.");
            mPasswordEditText.requestFocus();
            return;
        }

        if (!password.equals(vfpassword)) {
            // Les mots de passe ne correspondent pas, afficher un message d'erreur
            mVfpasswordEditText.setError("Les mots de passe ne correspondent pas");
            mVfpasswordEditText.requestFocus();
        }
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageprofil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}