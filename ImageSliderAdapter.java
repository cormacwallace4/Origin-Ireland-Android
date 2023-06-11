package com.cormac.origin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {

    private List<Integer> images;

    public ImageSliderAdapter(List<Integer> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
        return new ImageSliderViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        int imageResId = images.get(position);
        Glide.with(holder.itemView)
                .load(imageResId)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_slider_image);
        }
    }
}
