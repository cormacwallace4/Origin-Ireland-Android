package com.cormac.origin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.Toast;

public class DiscoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Find the button in the layout
        Button backButton = findViewById(R.id.back_button);
        Button saveLocationButton = findViewById(R.id.save_button);

        // Set an onClickListener on the button
        backButton.setOnClickListener(v -> {
            // Finish the current activity
            finish();
        });

        saveLocationButton.setOnClickListener(v -> {
            HistoricalLocation location = (HistoricalLocation) getIntent().getSerializableExtra("location");
            boolean locationSaved = HistoricalLocations.addLocation(DiscoverActivity.this, location);

            if (locationSaved) {
                Toast.makeText(DiscoverActivity.this, "Location saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DiscoverActivity.this, "Location already saved", Toast.LENGTH_SHORT).show();
            }
        });

        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        // Find the Directions button in the layout
        Button directionsButton = findViewById(R.id.directions_button);

        directionsButton.setOnClickListener(v -> {
            HistoricalLocation location = (HistoricalLocation) getIntent().getSerializableExtra("location");
            if (location != null) {
                // Get the latitude and longitude of the location
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(DiscoverActivity.this, "Please install Google Maps", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Get the location from intent extras
        Intent intent = getIntent();
        HistoricalLocation location = (HistoricalLocation) intent.getSerializableExtra("location");


        // Display the description of the clicked location
        if (location != null) {
            TextView locationDescriptionView = findViewById(R.id.location_description);
            locationDescriptionView.setText(location.getDescription());

            // Set up the image slider
            ViewPager2 viewPager = findViewById(R.id.image_slider);
            ImageSliderAdapter adapter = new ImageSliderAdapter(location.getImages());
            viewPager.setAdapter(adapter);
        }
    }
}
