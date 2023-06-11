package com.cormac.origin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_INTRO_SHOWN = "introShown";

    private GoogleMap map;
    private List<HistoricalLocation> locations;
    private HashMap<Marker, HistoricalLocation> markerLocationMap = new HashMap<>();
    private AutoCompleteTextView locationSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Check if the intro dialog has been shown before
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean introShown = prefs.getBoolean(PREF_INTRO_SHOWN, false);

        if (!introShown) {
            // Show the intro dialog
            IntroDialogFragment introDialog = new IntroDialogFragment();
            introDialog.show(getSupportFragmentManager(), "IntroDialog");

            // Set the flag in SharedPreferences to indicate that the intro dialog has been shown
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_INTRO_SHOWN, true);
            editor.apply();
        }


        Button savedLocationsButton = findViewById(R.id.btn_saved_locations);
        savedLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent savedLocationsIntent = new Intent(MainActivity.this, SavedLocationsActivity.class);
                startActivity(savedLocationsIntent);
            }
        });

        Button guideButton = findViewById(R.id.btn_guide);
        guideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntroDialogFragment introDialog = new IntroDialogFragment();
                introDialog.show(getSupportFragmentManager(), "IntroDialog");
            }
        });

        locationSearch = findViewById(R.id.location_search);

        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationSearch.setText("");
            }
        });

        locationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLocations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        locations = HistoricalLocations.getDefaultLocations();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LatLng ireland = new LatLng(53.9, -8.0);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ireland, 6.45f));

        addLocationPins();

        map.setInfoWindowAdapter(this);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                HistoricalLocation location = markerLocationMap.get(marker);
                if (location != null) {
                    Intent discoverIntent = new Intent(MainActivity.this, DiscoverActivity.class);
                    discoverIntent.putExtra("location", location);
                    startActivity(discoverIntent);
                }
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(locationSearch.getWindowToken(), 0);
            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                float zoom = map.getCameraPosition().zoom;
                updateMarkerVisibility(zoom);
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = AppCompatResources.getDrawable(this, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void updateMarkerVisibility(float zoom) {
        for (Marker marker : markerLocationMap.keySet()) {
            if (zoom > 7) {  // The zoom level at which the location name should appear
                marker.setIcon(createIcon(markerLocationMap.get(marker).getName()));
            } else {
                marker.setIcon(bitmapDescriptorFromVector(R.drawable.baseline_place_24));
            }
        }
    }



    private void addLocationPins() {
        if (map != null && locations != null) {
            for (HistoricalLocation location : locations) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Create a custom bitmap with the location name
                BitmapDescriptor icon = createIcon(location.getName());

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(icon)
                        .title(location.getName())
                        .snippet(location.getSubtitle());

                Marker marker = map.addMarker(markerOptions);
                markerLocationMap.put(marker, location);
            }
        }
    }


    private BitmapDescriptor createIcon(String locationName) {
        // Create a paint for the text stroke
        Paint strokePaint = new Paint();
        strokePaint.setColor(Color.WHITE); // White stroke
        strokePaint.setTextSize(16); // Text Size
        strokePaint.setStyle(Paint.Style.STROKE); // Stroke style
        strokePaint.setStrokeWidth(5); // Stroke width
        strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
        strokePaint.setAntiAlias(true);

        // Create a paint for the text fill
        Paint fillPaint = new Paint();
        fillPaint.setColor(Color.BLACK); // Black fill
        fillPaint.setTextSize(16); // Text Size
        fillPaint.setStyle(Paint.Style.FILL); // Fill style
        strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
        fillPaint.setAntiAlias(true);

        // Get the pin icon
        Drawable vectorDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_place_24);
        if (vectorDrawable == null) {
            return null;
        }
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap pinBitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasPin = new Canvas(pinBitmap);
        vectorDrawable.draw(canvasPin);

        // Create a bitmap for combining the pin and text. The width is the larger of the pin and text, and the height is the sum of the pin and text.
        Bitmap combinedBitmap = Bitmap.createBitmap(Math.max(pinBitmap.getWidth(), (int) strokePaint.measureText(locationName)), pinBitmap.getHeight() + 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinedBitmap);

        // Draw the pin and text on the bitmap. The pin is at the bottom, and the text is centered above it.
        canvas.drawBitmap(pinBitmap, (combinedBitmap.getWidth() - pinBitmap.getWidth()) / 2, 50, null);
        canvas.drawText(locationName, (combinedBitmap.getWidth() - strokePaint.measureText(locationName)) / 2, 50, strokePaint);
        canvas.drawText(locationName, (combinedBitmap.getWidth() - fillPaint.measureText(locationName)) / 2, 50, fillPaint);

        // Convert the bitmap to a BitmapDescriptor for use as a marker icon
        return BitmapDescriptorFactory.fromBitmap(combinedBitmap);
    }




    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);

        TextView titleTextView = view.findViewById(R.id.title);
        TextView subtitleTextView = view.findViewById(R.id.subtitle);
        Button discoverButton = view.findViewById(R.id.btn_discover);

        titleTextView.setText(marker.getTitle());
        subtitleTextView.setText(marker.getSnippet());

        return view;
    }

    private void filterLocations(String query) {
        for (Marker marker : markerLocationMap.keySet()) {
            HistoricalLocation location = markerLocationMap.get(marker);
            if (location != null) {
                String name = location.getName().toLowerCase();
                marker.setVisible(name.contains(query.toLowerCase()));
            }
        }
    }
}
