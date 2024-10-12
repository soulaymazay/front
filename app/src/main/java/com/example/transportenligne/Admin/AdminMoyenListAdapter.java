package com.example.transportenligne.Admin;

import android.content.Context;
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
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.Moyen;
import com.example.transportenligne.Models.VolleyCallback;
import com.example.transportenligne.R;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminMoyenListAdapter extends ArrayAdapter<Moyen> {
    Context context;
    public  List<Moyen> moyens;

    public AdminMoyenListAdapter(@NonNull Context context, @NonNull List<Moyen> objects) {
        super(context, 0, objects);
        this.context=context;
        this.moyens=objects;
    }
    private void UpdateMoyenEtat(String etat, int position) {
        String path=Global.Moyen+"/"+moyens.get(position).id+"/"+etat;
        HttpRequest.mQueue = Volley.newRequestQueue(context);
        HttpRequest.mQueue.add(HttpRequest.stringRequest(Request.Method.PATCH, path, null, context, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {

                moyens.remove(position);
                notifyDataSetChanged();
            }
            @Override
            public void onError(int statusCode, String message) {
                Log.e("State ERROR", "error changing state " + statusCode + " " + message);
            }
        }));

    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View rowView=inflater.inflate(R.layout.admin_moyen_list_adaoter, null,true);
        TextView nommoyen = (TextView) rowView.findViewById(R.id.nommoyen);
        ImageView imagemoyen = (ImageView) rowView.findViewById(R.id.imagemoyen);
        TextView marquemoyen = (TextView) rowView.findViewById(R.id.marquemoyen);
        TextView couleurmoyen = (TextView) rowView.findViewById(R.id.couleurmoyen);
        TextView annemoyen = (TextView) rowView.findViewById(R.id.annemoyen);
        TextView etatmoyen = (TextView) rowView.findViewById(R.id.etatmoyen);

        Button acceptbutton=(Button)rowView.findViewById(R.id.AccepterMoyen);
        acceptbutton.setOnClickListener(view1 -> {
            UpdateMoyenEtat("Accepted",position);
        });
        Button rejectbutton=(Button)rowView.findViewById(R.id.RefuserMoyen);
        rejectbutton.setOnClickListener(view1 -> {
            UpdateMoyenEtat("Rejected",position);
        });

        etatmoyen.setText("Etat :"+moyens.get(position).etat);
        nommoyen.setText("Type: "+moyens.get(position).nom);
        marquemoyen.setText("Marque: "+moyens.get(position).marque);
        couleurmoyen.setText("Couleur: "+moyens.get(position).couleur);
        annemoyen.setText("Annee: "+moyens.get(position).annee);
        if(moyens.get(position).image!=null) {

            byte[] decodedString = Base64.decode(moyens.get(position).image,Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            var stringimage= new String(decodedString, StandardCharsets.UTF_8);
            imagemoyen.setImageBitmap(decodedByte);}
        return rowView;

    }


}
