package com.example.geofencing;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

public class MainActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {
    private ArrayList<Place> savedList;
    ToggleButton toggle;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private GridAdapter gridAdapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSaved;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private PlacesClient placesClient;
    private GeofencingProvider geofencingProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggle = findViewById(R.id.toggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                GeofenceActionProvider.Statics.setEnabled(b);
                Toast.makeText(MainActivity.this,b ? "Fencing enabled" : "Fencing disabled",Toast.LENGTH_SHORT).show();
                if (b) {
                    Intent i = new Intent(MainActivity.this,MapViewActivity.class);
                    i.putExtra(MapViewActivity.INTENT_LOCATION_KEY,savedList);
                    startActivity(i);
                }
            }
        });

        Places.initialize(this, "AIzaSyB45KFBO3F2YujEWyq216k0jMqqX_n1le0");
        savedList = new ArrayList<>();

        geofencingProvider = new LocalGeofencing(this);

        pref = getApplicationContext().getSharedPreferences("preferences", 0);
        editor = pref.edit();
        placesClient = com.google.android.libraries.places.api.Places.createClient(getApplicationContext());

        recyclerView = findViewById(R.id.places_recycler_view);
        ((EditText) findViewById(R.id.place_search)).addTextChangedListener(filterTextWatcher);
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();
        gridAdapter = new GridAdapter(this, savedList);
        recyclerViewSaved = findViewById(R.id.saved_recycler_view);
        recyclerViewSaved.setLayoutManager((new LinearLayoutManager(this)));
        gridAdapter.setClickListener(new GridAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                geofencingProvider.removeFence(savedList.get(position));
                editor.remove(savedList.get(position).getName());
                editor.apply();
                savedList.remove(savedList.get(position));
                gridAdapter.notifyDataSetChanged();
            }
        });
        recyclerViewSaved.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        GeofenceActionProvider.Statics.setAudioService((AudioManager)getSystemService(AUDIO_SERVICE));

        Log.d("Keys" , pref.getAll().toString());
        Map<String, ?> keys = pref.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            String placeId = String.valueOf(entry.getValue());

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    savedList.add(place);
                    gridAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                    }
                }
            });
        }

        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},0);
    }
    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (recyclerView.getVisibility() == View.GONE) {recyclerView.setVisibility(View.VISIBLE);}
            } else {
                if (recyclerView.getVisibility() == View.VISIBLE) {recyclerView.setVisibility(View.GONE);}
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    };

    @Override
    public void click(Place place) {
        Toast.makeText(this, place.getAddress()+", "+place.getLatLng().latitude+place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
        savedList.add(place);
        gridAdapter.savedList = savedList;
        gridAdapter.notifyDataSetChanged();
        mAutoCompleteAdapter.notifyDataSetChanged();
        editor.putString(place.getName() , place.getId());
        editor.apply();

        geofencingProvider.addFence(place);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
