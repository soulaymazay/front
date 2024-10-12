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
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.User;
import com.example.transportenligne.Models.VolleyCallback;
import com.example.transportenligne.Models.VolleyMultipartRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientSignup extends AppCompatActivity {

    private EditText mFullNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mVfpasswordEditText;
    private Button connectButton;    private Button btnimprofil;

    private ImageView imageprofil;    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.printf("test1");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscrit_client);
        Log.d("oncreate", "onCreate: created MainActivity_inscrit_client");

        connectButton= (Button)findViewById(R.id.ConnectButton);
        mFullNameEditText = (EditText)findViewById(R.id.T1);
        mEmailEditText =(EditText) findViewById(R.id.T2);
        mPasswordEditText = (EditText)findViewById(R.id.mp1);
        mVfpasswordEditText = (EditText)findViewById(R.id.mp2);
        btnimprofil = findViewById(R.id.buttonChoose);

        btnimprofil.setOnClickListener(v -> showImageChooser());

        imageprofil = findViewById(R.id.imageView);
        imageprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            SignUp();

            }});

    }

    private void SignUp() {

        // Récupérer les valeurs des champs
        System.out.printf("succsees");
        String mFullName = mFullNameEditText.getText().toString().trim();
        String mEmail = mEmailEditText.getText().toString().trim();
        String mPassword = mPasswordEditText.getText().toString().trim();
        String vfpassword = mVfpasswordEditText.getText().toString().trim();

        // Vérifier si les champs sont valides
        if (mFullName.isEmpty()) {
            mFullNameEditText.setError("Le nom complet est requis.");
            mFullNameEditText.requestFocus();
            return;
        }
        if (mEmail.isEmpty()) {
            mEmailEditText.setError("L'adresse e-mail est requise.");
            mEmailEditText.requestFocus();
            return;
        }
        if (!mEmail.contains(".") || !mEmail.contains("@")) {
            mEmailEditText.setError("L'adresse e-mail n'est pas valide.");
            mEmailEditText.requestFocus();
            return;
        }
        if (mPassword.isEmpty()) {
            mPasswordEditText.setError("Le mot de passe est requis.");
            mPasswordEditText.requestFocus();
            return;
        }
        if (mPassword.length() < 6) {
            mPasswordEditText.setError("Le mot de passe doit comporter au moins 6 caractères.");
            mPasswordEditText.requestFocus();
            return;
        }
        if (!mPassword.equals(vfpassword)) {
            // Les mots de passe ne correspondent pas, afficher un message d'erreur
            mVfpasswordEditText.setError("Les mots de passe ne correspondent pas");
            mVfpasswordEditText.requestFocus();
            return;
        }
        User user = new User();
        user.email = mEmailEditText.getText().toString().trim();
        user.username = mFullNameEditText.getText().toString().trim();
        user.password = mPasswordEditText.getText().toString().trim();
        HashMap<String, String> map = new HashMap<>();
        var data = new Gson().toJson(user);
        map.put("data", data);
        Map<String, VolleyMultipartRequest.DataPart> fileparams = new HashMap<>();
        fileparams.put("image", new VolleyMultipartRequest.DataPart("user.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), imageprofil.getDrawable()), "image/jpeg"));
        HttpRequest.mQueue = Volley.newRequestQueue(ClientSignup.this);

        HttpRequest.mQueue.add(HttpRequest.multipartRequest(Request.Method.POST, Global.addClient, map, fileparams, ClientSignup.this, new VolleyCallback() {


            @Override
            public void onSuccess(String result) {
                Log.d("MyApp", "response est la suivante: " + result.toString());
                Toast.makeText(ClientSignup.this, "Inscription réussie!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }

            @SuppressLint("SuspiciousIndentation")
            public void onError(int statusCode, String message) {

                if (message.contains("email exists"))
                    Toast.makeText(ClientSignup.this, "Email existe", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ClientSignup.this, "Inscription echoué!", Toast.LENGTH_SHORT).show();
            }
        }));
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