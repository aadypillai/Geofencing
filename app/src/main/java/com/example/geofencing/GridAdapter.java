package com.example.geofencing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.CustomViewHolder> {
    public ArrayList<com.google.android.libraries.places.api.model.Place> savedList = new ArrayList<>();
    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private final PlacesClient placesClient;
    private ItemClickListener mClickListener;
    public GridAdapter(Context context , ArrayList<com.google.android.libraries.places.api.model.Place> places)    {
        savedList = places;
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
    }
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(view);
    }

    public void onBindViewHolder(@NonNull CustomViewHolder holder, int i) {
        Place place = savedList.get(i);
        holder.place_area.setText(place.getName());
        holder.place_address.setText(place.getAddress());

    }

    @Override
    public int getItemCount() {
        return savedList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView place_area;
        TextView place_address;

        public CustomViewHolder (View view) {
            super(view);
            this.place_area = view.findViewById(R.id.place_area);
            this.place_address = view.findViewById(R.id.place_address);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }


}
