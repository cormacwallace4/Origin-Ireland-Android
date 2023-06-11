package com.cormac.origin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class SavedLocationsActivity extends AppCompatActivity {
    ListView listView;
    List<HistoricalLocation> savedLocations;

    LocationListAdapter adapter;  // Declare adapter here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Set navigation bar color to transparent and adjust system UI visibility
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        listView = (ListView) findViewById(R.id.listView);  // Assuming you have a ListView with the id "listView"
        savedLocations = HistoricalLocations.getLocations(this);

        // Initialize adapter here
        adapter = new LocationListAdapter(this, savedLocations);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            HistoricalLocation location = savedLocations.get(position);
            if(location != null) {
                Intent intent = new Intent(SavedLocationsActivity.this, DiscoverActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });


        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Dismiss the activity and return to the previous view
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        savedLocations = HistoricalLocations.getLocations(this);
        adapter.setData(savedLocations);
        adapter.notifyDataSetChanged();
    }

}
