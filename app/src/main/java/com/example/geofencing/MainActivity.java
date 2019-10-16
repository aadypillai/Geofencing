package com.example.geofencing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

//import com.zaaibo.drive.R;
//import com.zaaibo.drive.adapter.PlacesAutoCompleteAdapter;


public class MainActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {
    ToggleButton toggle;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggle = findViewById(R.id.toggle);
        //toggle.setOnClickListener(this);
        Places.initialize(this, "AIzaSyB45KFBO3F2YujEWyq216k0jMqqX_n1le0");

        recyclerView = findViewById(R.id.places_recycler_view);
        ((EditText) findViewById(R.id.place_search)).addTextChangedListener(filterTextWatcher);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();

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
        Log.d("Henlo" , "Inside click!");
        Toast.makeText(this, place.getAddress()+", "+place.getLatLng().latitude+place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
    }

/*    @Override
    public void onClick(View v) {
        switch (v.getId())  {
            case R.id.toggle:
                //TURN SILENT MODE ON OR OFF
        }
    }

*/
}
