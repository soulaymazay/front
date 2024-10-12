package com.example.transportenligne;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.transportenligne.Models.Course;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientChauffeurPosition extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = ClientChauffeurPosition.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    private final LatLng defaultLocation = new LatLng(35.5024, 11.0457);
    private GoogleMap map;
    private TextView DestinationGPS;
    private String positionGPS;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng clientDestinationGPS;
    private LatLng clientPositionGPS;
    Button AcceptButton;
    Button RefuseButton;
    MarkerOptions destinationMarker=new MarkerOptions().title("Votre destination");
    MarkerOptions clientMarker=new MarkerOptions().title("Votre destination");
    private Course course;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientchauffeurposition);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        AcceptButton=findViewById(R.id.Accepter);
        RefuseButton=findViewById(R.id.Refuser);


        var intent=getIntent();
        var jsonmessage=intent.getStringExtra("message");
        Log.d(TAG, "onCreate: "+jsonmessage);
        Gson gson=new Gson();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                if(!json.toString().equals("{}"))
                    return LocalDateTime.parse(json.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                return null;
            }).create();

        }
        course = gson.fromJson(jsonmessage.toString(), Course.class);
        var latlong=course.destinationGPS.split(",");
        if(!course.destinationGPS.isBlank())
            clientDestinationGPS =new LatLng(Double.parseDouble(latlong[0]),Double.parseDouble(latlong[1]));
        var clientlatlong= course.positionGPS.split(",");
        var clientlat=Double.parseDouble(clientlatlong[0]);
        var clientlng=Double.parseDouble(clientlatlong[1]);
        clientPositionGPS =new LatLng(clientlat,clientlng);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        AcceptButton.setOnClickListener(v -> {
            WebSocketMessage wsm=new WebSocketMessage();
            wsm.type="ClientRequestAccepted";
            wsm.clientId=course.client;
            wsm.chauffeurId=course.chauffeur;
            wsm.courseId=course.id;
            wsm.chauffeurPosition=lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude();
            var jsonmessage12 =new Gson().toJson(wsm);
            Intent responseintent = new Intent(ClientChauffeurPosition.this, WebsocketConnector.class);
            responseintent.putExtra("message", jsonmessage12);
            startService(responseintent);
            Toast.makeText(ClientChauffeurPosition.this, "Demande Accepté", Toast.LENGTH_SHORT).show();
            Intent homeintent=new Intent(ClientChauffeurPosition.this, ChauffeurDashboard.class);
            startActivity(homeintent);
        });
        RefuseButton.setOnClickListener(v -> {
            WebSocketMessage wsm=new WebSocketMessage();

            wsm.type="ClientRequestRefused";
            wsm.clientId=course.client;
            wsm.chauffeurId=course.chauffeur;
            wsm.courseId=course.id;

            var jsonmessage1 =new Gson().toJson(wsm);
            Intent responseintent = new Intent(ClientChauffeurPosition.this, WebsocketConnector.class);

            responseintent.putExtra("message", jsonmessage1);
            startService(responseintent);
            Toast.makeText(ClientChauffeurPosition.this, "Demande Refusé", Toast.LENGTH_SHORT).show();

            Intent homeintent=new Intent(ClientChauffeurPosition.this, ChauffeurDashboard.class);
            startActivity(homeintent);
        });
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {


            }
        });
        enableMyLocation();
        getDeviceLocation();
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                destinationMarker=setMarker(clientDestinationGPS,"Destination");
                clientMarker=setMarker(clientPositionGPS,"Client");
                map.addMarker(destinationMarker).showInfoWindow();
                map.addMarker(clientMarker).showInfoWindow();
                ShowAllMarkers();
            }
        });

    }

    private void ShowAllMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(clientMarker.getPosition());
        builder.include(destinationMarker.getPosition());
        if(lastKnownLocation!=null)
            builder.include(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()));
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding =300; // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        map.animateCamera(cu);

    }

    private MarkerOptions setMarker(LatLng latLng,String title) {

        MarkerOptions marker=new MarkerOptions().position(latLng).title(title);
        return marker;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDeviceLocation() {

        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 15));
                            positionGPS=(lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude());
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, 15));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }



    private void drawRoute() {
        // Check location access permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle lack of permissions
            return;
        }

        // Create a GeoApiContext object with your Google Maps API key
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyCq_hap5AaVWAT7Jcr3bh3vN0DcYcTXrTE")
                .build();

        // Perform a route request between your current location and the destination
        DirectionsResult directionsResult;
        try {
            directionsResult = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING) // Choose the travel mode (DRIVING, WALKING, TRANSIT, etc.)
                    .origin(String.valueOf(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))) // Your current location
                    .destination(String.valueOf(new LatLng(clientDestinationGPS.latitude, clientDestinationGPS.longitude))) // The specified destination
                    .await();

            if (directionsResult.routes.length > 0) {
                // Get the first available route
                com.google.maps.model.DirectionsRoute route = directionsResult.routes[0];

                // Clear any existing markers and polylines on the map
                map.clear();

                // Add a marker for the destination
                MarkerOptions destinationMarkerOptions = new MarkerOptions()
                        .position(clientDestinationGPS)
                        .title("Destination");
                map.addMarker(destinationMarkerOptions);

                // Draw the polyline for the route
                PolylineOptions polylineOptions = new PolylineOptions();
                for (com.google.maps.model.LatLng point : route.overviewPolyline.decodePath()) {
                    polylineOptions.add(new LatLng(point.lat, point.lng));
                }
                polylineOptions.color(Color.BLUE); // Set the color of the polyline
                polylineOptions.width(10); // Set the width of the polyline
                map.addPolyline(polylineOptions);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
