package com.example.transportenligne.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Global;
import com.example.transportenligne.Models.Chauffeur;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;
import com.example.transportenligne.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminChauffeurListAdapter extends ArrayAdapter<Chauffeur> {
    Context context;
  public  List<Chauffeur> chauffeurs;
    public AdminChauffeurListAdapter(@NonNull Context context, @NonNull List<Chauffeur> objects) {
        super(context, 0);
        this.context=context;
        this.chauffeurs=objects;
    }
    public void UpdateChauffeurEtatCompte(String etat, int position)  {
        var chauffeur=new Chauffeur();
        chauffeur.etatcompte=etat;
        JSONObject requestData=new JSONObject();
        try {
            requestData.put("etatcompte",etat);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        var path= Global.ChauffeurAPI+"/"+chauffeurs.get(position).user.id;

        HttpRequest.mQueue = Volley.newRequestQueue(context);
        HttpRequest.mQueue.add(HttpRequest.stringRequest(Request.Method.PUT,path, requestData, context, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                chauffeurs.get(position).etatcompte=etat;
                chauffeurs.remove(position);
                notifyDataSetChanged();
            }
            @Override
            public void onError(int statusCode, String message) {
                Log.e("State ERROR", "error changing state " + statusCode + " " + message);
            }
        }));

    }
    @Override
    public int getCount()
    {
        int size = chauffeurs == null ? 0 : chauffeurs.size();

        Log.e("DD", "" + size);

        return size;
    }
    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        View rowView=inflater.inflate(R.layout.admin_chauffeur_list_adapter, null,true);
        TextView etat = rowView.findViewById(R.id.etatclient);
        ImageView imageuser = rowView.findViewById(R.id.imageuser);
        TextView email = rowView.findViewById(R.id.emailuser);
        TextView username = rowView.findViewById(R.id.nomuser);
        TextView numpermis = rowView.findViewById(R.id.numpermis);
        @SuppressLint("MissingInflatedId") Button acceptbutton= rowView.findViewById(R.id.acceptchauffeur);
        acceptbutton.setOnClickListener(view1 -> {
            UpdateChauffeurEtatCompte("accepted",position);
        });

        Button rejectbutton=(Button)rowView.findViewById(R.id.rejectchauffeur);
        rejectbutton.setOnClickListener(view1 -> {
            UpdateChauffeurEtatCompte("rejected",position);
        });

        etat.setText("Etat: "+ chauffeurs.get(position).etat);
        email.setText("email: "+ chauffeurs.get(position).user.email);
        username.setText("username: "+ chauffeurs.get(position).user.name);
        numpermis.setText("permis: "+ chauffeurs.get(position).numpermis);
        if(chauffeurs.get(position).user.image!=null) {
            byte[] decodedString = Base64.decode(chauffeurs.get(position).user.image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            var stringimage = new String(decodedString, StandardCharsets.UTF_8);
            imageuser.setImageBitmap(decodedByte);
        }

        return rowView;

    }

}
