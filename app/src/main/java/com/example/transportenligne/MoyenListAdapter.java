package com.example.transportenligne;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.transportenligne.Models.Moyen;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MoyenListAdapter extends ArrayAdapter<Moyen> {
    Context context;
  public  List<Moyen> moyens;

    public MoyenListAdapter(@NonNull Context context, @NonNull List<Moyen> objects) {
        super(context, 0, objects);
        this.context=context;
        this.moyens=objects;


    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View rowView=inflater.inflate(R.layout.moyen_list_edit, null,true);
        TextView nommoyen = (TextView) rowView.findViewById(R.id.nommoyen);
        ImageView imagemoyen = (ImageView) rowView.findViewById(R.id.imagemoyen);
        TextView marquemoyen = (TextView) rowView.findViewById(R.id.marquemoyen);
        TextView couleurmoyen = (TextView) rowView.findViewById(R.id.couleurmoyen);
        TextView annemoyen = (TextView) rowView.findViewById(R.id.annemoyen);
        TextView moyenetat = (TextView) rowView.findViewById(R.id.moyenetat);
        // Set state
     //   checkBox.setChecked(checkboxes[position]);

        // Register listener

        nommoyen.setText("Type: "+moyens.get(position).nom);
        marquemoyen.setText("Marque: "+moyens.get(position).marque);
        couleurmoyen.setText("Couleur: "+moyens.get(position).couleur);
        annemoyen.setText("Annee: "+moyens.get(position).annee);
        moyenetat.setText("Etat: "+moyens.get(position).etat);
   //     users.get(position).image="data:image/jpg;base64," +users.get(position).image;
        if(moyens.get(position).image!=null) {

            byte[] decodedString = Base64.decode(moyens.get(position).image,Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        var stringimage= new String(decodedString, StandardCharsets.UTF_8);
        imagemoyen.setImageBitmap(decodedByte);}

        return rowView;

    }
}
