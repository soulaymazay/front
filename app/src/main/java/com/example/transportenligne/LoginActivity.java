package com.example.transportenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.LoginResponse;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);
        loginButton = (Button) findViewById(R.id.loginButton);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String Email = emailText.getText().toString().trim();
                String Password = passwordText.getText().toString().trim();


                if(VerifyInput(Email,Password)==false)
                {
                    return;
                }
                JSONObject requestData = new JSONObject();
                try {
                    requestData.put("username", Email);
                    requestData.put("password", Password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpRequest.mQueue=  Volley.newRequestQueue( LoginActivity.this);
                HttpRequest.mQueue.add(HttpRequest.objectRequest(Request.Method.POST, Global.Login,requestData, LoginActivity.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        var loginresponse=new Gson().fromJson(result, LoginResponse.class);
                        JWTUtils.SaveJWT(LoginActivity.this,loginresponse.token);
                        JWTUtils.SaveRefreshToken(LoginActivity.this,loginresponse.refresh_token);
                        var i = new Intent(getApplicationContext(), Introduction.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                        startActivity(i);
                        finish();
                    }
                    @Override
                    public void onError(int statusCode,String message) {
                        if(message!=null)
                        {
                            if(message.contains("{\"code\":401,\"message\":\"Invalid credentials.\"}"))
                            {
                                Toast.makeText(LoginActivity.this, "password or email incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Log.e("Refresh token ERROR", "error login "+statusCode+" "+message);
                    }
                }));

            }});
    }

    private boolean VerifyInput(String Email, String Password) {

        if (Email.isEmpty()) {
            emailText.setError("L'adresse e-mail est requise.");
            emailText.requestFocus();
            return false;
        }

        if (!Email.contains(".") || !Email.contains("@")) {
            emailText.setError("L'adresse e-mail n'est pas valide.");
            emailText.requestFocus();
            return false;
        }

        if (Password.isEmpty()) {
            passwordText.setError("Le mot de passe est requis.");
            passwordText.requestFocus();
            return false;
        }

        if (Password.length() < 6) {
            passwordText.setError("Le mot de passe doit comporter au moins 6 caractères.");
            passwordText.requestFocus();
            return false;
        }
        return true;


    }}