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
import com.example.transportenligne.Models.Client;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;
import com.example.transportenligne.R;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminClientListAdapter extends ArrayAdapter<Client> {
    Context context;
  public  List<Client> users;

    public AdminClientListAdapter(@NonNull Context context, @NonNull List<Client> objects) {
        super(context, 0, objects);
        this.context=context;
        this.users =objects;


    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View rowView=inflater.inflate(R.layout.client_list_edit, null,true);
        TextView etat = (TextView) rowView.findViewById(R.id.etatclient);
        ImageView imageuser = (ImageView) rowView.findViewById(R.id.imageuser);
        TextView email = (TextView) rowView.findViewById(R.id.emailuser);
        TextView username = (TextView) rowView.findViewById(R.id.nomuser);
        Button delete=(Button) rowView.findViewById(R.id.delete);
        delete.setVisibility(View.VISIBLE);

        delete.setOnClickListener(view1 -> {
            HttpRequest.mQueue = Volley.newRequestQueue(context);
            HttpRequest.mQueue.add(HttpRequest.objectRequest(Request.Method.DELETE, Global.addClient+"/"+users.get(position).user.id, null, context, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.d("user", "user deleted");
                    remove(users.get(position));
                    notifyDataSetChanged();
                }
                @Override
                public void onError(int statusCode, String message) {
                    Log.e("State ERROR", "error getting state " + statusCode + " " + message);
                }
            }));

        });
        etat.setText("Etat: "+ users.get(position).etat);
        email.setText("email: "+ users.get(position).user.email);
        username.setText("username: "+ users.get(position).user.name);
   //     users.get(position).image="data:image/jpg;base64," +users.get(position).image;
        if(users.get(position).user.image!=null) {

            byte[] decodedString = Base64.decode(users.get(position).user.image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            var stringimage = new String(decodedString, StandardCharsets.UTF_8);
            imageuser.setImageBitmap(decodedByte);
        }
        return rowView;

    }
}
