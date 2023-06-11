package com.cormac.origin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LocationListAdapter extends ArrayAdapter<HistoricalLocation> {

    public LocationListAdapter(Context context, List<HistoricalLocation> locations) {
        super(context, 0, locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView locationNameTextView = convertView.findViewById(R.id.text_view_location_name);
        Button deleteButton = convertView.findViewById(R.id.button_delete);

        HistoricalLocation location = getItem(position);

        if (location != null) {
            locationNameTextView.setText(location.toString());

            deleteButton.setOnClickListener(v -> {
                // Delete location from your data source here
                HistoricalLocations.deleteLocation(getContext(), location);
                // Remove location from the adapter
                remove(location);
                // Notify adapter about data changes (so the list can be updated)
                notifyDataSetChanged();
            });
        }

        return convertView;
    }

    public void setData(List<HistoricalLocation> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }
}
