package com.example.transportenligne;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.navigation.NavigationView;

public class MainActivitydashbord_client extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitydashbord_chauffeur);

        DrawerLayout drawerLayout = findViewById(R.id.drawerlayaout);
        findViewById(R.id.imagemenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.menuavis) {
                    // Start the ParametreActivity
                    Intent intent = new Intent(MainActivitydashbord_client.this, Main_avis.class);
                    startActivity(intent);
                } else if (itemId == R.id.menuinfo) {
                    // Start the AvisActivity
                    Intent intent = new Intent(MainActivitydashbord_client.this, test2.class);
                    startActivity(intent);
                }

                // Close the drawer after selecting an item
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}
