package com.example.transportenligne;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.Course;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CourseListAdapter extends ArrayAdapter<Course> {
    Context context;
  public  List<Course> courses;

String mode;

    public CourseListAdapter(@NonNull Context context, @NonNull List<Course> objects,String mode) {
        super(context, 0);
        this.context=context;
        this.courses=objects;
        this.mode=mode;


    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View rowView=inflater.inflate(R.layout.course_list_edit, null,true);

        TextView chauffeurGPS = (TextView) rowView.findViewById(R.id.chauffeurGPS);
        TextView positionGPS = (TextView) rowView.findViewById(R.id.positionGPS);
        TextView destinationGPS = (TextView) rowView.findViewById(R.id.destinationGPS);
        TextView chauffeurname= (TextView) rowView.findViewById(R.id.chauffeurname);
        TextView model = (TextView) rowView.findViewById(R.id.model);
        TextView clientname = (TextView) rowView.findViewById(R.id.clientname);
        TextView avis = (TextView) rowView.findViewById(R.id.avis);
        TextView etat = (TextView) rowView.findViewById(R.id.etat);
        TextView inputposition = (TextView) rowView.findViewById(R.id.inputposition);
        TextView inputdestination = (TextView) rowView.findViewById(R.id.inputdestination);
        TextView FinishDateTime = (TextView) rowView.findViewById(R.id.FinishDateTime);
        TextView StartDateTime = (TextView) rowView.findViewById(R.id.StartDateTime);
        Button avisbutton = (Button) rowView.findViewById(R.id.avisbutton);
        Button cancelcourse = (Button) rowView.findViewById(R.id.cancelcourse);
        Button completecourse = (Button) rowView.findViewById(R.id.completecourse);

        chauffeurGPS.setText("GPS chauffeur: "+courses.get(position).chauffeurGPS);
        positionGPS.setText("GPS client: "+courses.get(position).positionGPS);
        destinationGPS.setText("GPS destination: "+courses.get(position).destinationGPS);
        chauffeurname.setText("Chauffeur: "+courses.get(position).chauffeurName);
        model.setText("Moyen: "+courses.get(position).moyenName);
        clientname.setText("Client: "+courses.get(position).clientName);
        avis.setText("Avis: "+courses.get(position).avis);
        etat.setText("Etat: "+courses.get(position).etat);
        inputposition.setText("Position Client: "+courses.get(position).inputposition);
        inputdestination.setText("Destination: "+courses.get(position).inputdestination);
        if(courses.get(position).FinishDateTime!=null)

        FinishDateTime.setText("Fin: "+courses.get(position).FinishDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        else         FinishDateTime.setText("Fin: ");

        StartDateTime.setText("Debut: "+courses.get(position).StartDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        completecourse.setOnClickListener(v -> {
          SetEtat("terminé",position,avisbutton,cancelcourse,completecourse);
        });
        cancelcourse.setOnClickListener(v -> {
            SetEtat("annulé",position,avisbutton,cancelcourse,completecourse);
        });
        avisbutton.setOnClickListener(v -> {
           var i=new Intent(context, Main_avis.class);
           i.putExtra("courseId",courses.get(position).id);
           context.startActivity(i);
        });
        if(mode.equals("admin"))
        {
            avisbutton.setVisibility(View.GONE);
            cancelcourse.setVisibility(View.GONE);
            completecourse.setVisibility(View.GONE);
        }
        if(mode.equals("chauffeur"))
        {
            cancelcourse.setVisibility(View.GONE);
            completecourse.setVisibility(View.GONE);
        }
        if(courses.get(position).etat.equals("terminé"))
        {
            cancelcourse.setVisibility(View.GONE);
            completecourse.setVisibility(View.GONE);
        }
        if(courses.get(position).etat.equals("pending"))
        {
            avisbutton.setVisibility(View.GONE);
        }


        return rowView;

    }



    @Override
    public int getCount()
    {
        int size = courses == null ? 0 : courses.size();

        Log.e("DD", "" + size);

        return size;
    }
    private void SetEtat(String etat, int position, Button avisbutton, Button cancelcourse, Button completecourse) {
        HttpRequest.mQueue=  Volley.newRequestQueue( context);
        String path=Global.Course+"/etat/"+courses.get(position).id+"/"+etat;
        HttpRequest.mQueue.add(HttpRequest.stringRequest(Request.Method.PATCH, path,null, context, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
            courses.get(position).etat=etat;
             avisbutton.setVisibility(View.VISIBLE);
             cancelcourse.setVisibility(View.GONE);
             completecourse.setVisibility(View.GONE);

            }
            @Override
            public void onError(int statusCode,String message) {
                Log.e("Refresh token ERROR", "error login "+statusCode+" "+message);
            }
        }));
    }
}
