package com.example.transportenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;

public class Main_profilchauffeur extends AppCompatActivity {
    private Button buttonp;
    private EditText nomclientEdiText;
    private Button buttonenri;
    private ImageButton imageButtonret;
    private com.makeramen.roundedimageview.RoundedImageView RoundedImageView;
    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profilclien);

        buttonp = findViewById(R.id.buttonp);
        nomclientEdiText = findViewById(R.id.editTextPersonName);
        buttonenri = findViewById(R.id.buttonenri);
        imageButtonret = findViewById(R.id.fleshret);
        RoundedImageView = findViewById(R.id.profileImage);

        buttonp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
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
                // Récupère l'image à partir de l'URI
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // Affiche l'image sur l'ImageView
                RoundedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
