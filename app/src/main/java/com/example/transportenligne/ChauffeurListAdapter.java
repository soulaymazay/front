package com.example.transportenligne;

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

import com.example.transportenligne.Models.Chauffeur;
import com.example.transportenligne.Models.Moyen;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChauffeurListAdapter extends ArrayAdapter<Chauffeur> {
    Context context;
  public  List<Chauffeur> chauffeurs;


    public ChauffeurListAdapter(@NonNull Context context, @NonNull List<Chauffeur> objects) {
        super(context, 0);
        this.context=context;
        this.chauffeurs=objects;


    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View rowView=inflater.inflate(R.layout.item_chauff, null,true);
        TextView nomchauff = (TextView) rowView.findViewById(R.id.nomchauff);
        ImageView imageprofil = (ImageView) rowView.findViewById(R.id.imageprofil);

        // Set state
     //   checkBox.setChecked(checkboxes[position]);

        // Register listener

        nomchauff.setText(chauffeurs.get(position).user.name);
        if(chauffeurs.get(position).image!=null) {
            byte[] decodedString = Base64.decode(chauffeurs.get(position).image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            var stringimage = new String(decodedString, StandardCharsets.UTF_8);
            imageprofil.setImageBitmap(decodedByte);
        }

        return rowView;

    }
}
