package com.example.transportenligne;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.transportenligne.Models.HttpRequest;
import com.example.transportenligne.Models.VolleyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class ClientDashboard extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = ClientDashboard.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final LatLng defaultLocation = new LatLng(35.5024, 11.0457);
    private GoogleMap map;
    private EditText DestinationInput;
    private TextView DestinationGPS;
    private EditText positionText;
    private Button searchButton;
    private Button positionButton;
    private Button SelectButton;
    private Button navigationDrawer;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng destinationLatLng;
    private DrawerLayout drawerLayout;  NavigationView navigationView;
    private String positionGPS;
    MarkerOptions marker=new MarkerOptions().title("Votre destination");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location_demo);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        drawerLayout = findViewById(R.id.drawerLayoutClient);
        DestinationInput = findViewById(R.id.DestinationInput);
        DestinationGPS=findViewById(R.id.DestinationGPS);
        searchButton = findViewById(R.id.SearchButton);
        positionButton = findViewById(R.id.PositionButton);
        positionText = findViewById(R.id.PositionText);

        findViewById(R.id.imagemenu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
         navigationView = findViewById(R.id.navigationView);
        GetProfilePicture();
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        SelectButton = findViewById(R.id.SelectButton);
        SelectButton.setOnClickListener(v -> selectChauffeur());
        positionButton.setOnClickListener(v -> getDeviceLocation());
        searchButton.setOnClickListener(v -> {
            String locationName = DestinationInput.getText().toString();
            if (!locationName.isEmpty()) {
                searchLocation(locationName);
            } else {
                Toast.makeText(ClientDashboard.this, "Please enter a location", Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void GetProfilePicture() {
        String path=Global.apiUrl+"/user/getimage/"+JWTUtils.GetId(this);
        HttpRequest.mQueue=  Volley.newRequestQueue( ClientDashboard.this);
        HttpRequest.mQueue.add(HttpRequest.stringRequest(Request.Method.GET, path,null, ClientDashboard.this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                View hView = navigationView.getHeaderView(0);
                ImageView image=(ImageView) hView.findViewById(R.id.userprofilepicture);
                if(result!=null) {

                    byte[] decodedString = Base64.decode(result,Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                var stringimage= new String(decodedString, StandardCharsets.UTF_8);
                image.setImageBitmap(decodedByte);}
            }
            @Override
            public void onError(int statusCode,String message) {
                Log.e("avis ERROR", "error profile "+statusCode+" "+message);
                Toast.makeText(ClientDashboard.this, "Erreur", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(latLng -> {
            DestinationGPS.setText(latLng.latitude+","+latLng.longitude);
            setMarker(latLng);

        });
        enableMyLocation();
        getDeviceLocation();
    }
private void selectChauffeur(){
Intent intent=new Intent(this, ChauffeurListView.class);
var positionstring=positionText.getText().toString();;
intent.putExtra("positionText",positionstring);
intent.putExtra("positionGPS",positionGPS);
intent.putExtra("destinationGPS",DestinationGPS.getText());
intent.putExtra("destinationInput",DestinationInput.getText().toString());
startActivity(intent);
}
    private void setMarker(LatLng latLng) {
        map.clear();
        marker.position(latLng);
        map.addMarker(marker);
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

    private void searchLocation(String locationName) {
        locationName=locationName+" Tunisia";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(locationName);
                map.addMarker(markerOptions);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                // Store the destination coordinates
                destinationLatLng = latLng;
                setMarker(latLng);
                // Draw the route
                drawRoute();
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    .destination(String.valueOf(new LatLng(destinationLatLng.latitude, destinationLatLng.longitude))) // The specified destination
                    .await();

            if (directionsResult.routes.length > 0) {
                // Get the first available route
                com.google.maps.model.DirectionsRoute route = directionsResult.routes[0];

                // Clear any existing markers and polylines on the map
                map.clear();

                // Add a marker for the destination
                MarkerOptions destinationMarkerOptions = new MarkerOptions()
                        .position(destinationLatLng)
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

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == R.id.menuavis) {
            // Start the ParametreActivity
            Intent intent = new Intent(ClientDashboard.this, AvisList.class);
            intent.putExtra("mode","client");
            startActivity(intent);
        } else if (itemId == R.id.menuinfo) {
            // Start the AvisActivity
            Intent intent = new Intent(ClientDashboard.this, Main_infoapp.class);
            startActivity(intent);
        }

        else if (itemId == R.id.logout) {
            JWTUtils.Logout(ClientDashboard.this);
        }
        // Close the drawer after selecting an item
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
