package com.example.kane.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, JsonReceivedCommand {

    private GoogleMap mGoogleMap;
    private Marker markerFinish;
    private MarkerOptions markerOptionsFinish;

    private double finishLat;
    private double finishLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        mGoogleMap = map;
        String address = "Bavorov";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + address + "&key=AIzaSyC2lGEulxqVNmD45HnLSQ0rg05wq7qUZjc";
        new RetrieveJsonTask().execute(new RetrieveJsonParam(url, this));


        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                LatLng finish = new LatLng(point.latitude, point.longitude);
                if(markerFinish == null){
                    markerOptionsFinish = new MarkerOptions().position(finish);
                    markerFinish = mGoogleMap.addMarker(markerOptionsFinish);
                    finishLat = point.latitude;
                    finishLon = point.longitude;
                } else {
                    markerFinish.remove();
                    markerOptionsFinish = new MarkerOptions().position(finish);
                    markerFinish = mGoogleMap.addMarker(markerOptionsFinish);
                    finishLat = point.latitude;
                    finishLon = point.longitude;
                }

            }
        });
    }


    public void jsonReceived(JSONObject object)
    {
        double lat = 0, lng = 0;

        try {
            JSONObject jsonLocation = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            lat = jsonLocation.getDouble("lat");
            lng = jsonLocation.getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng sydney = new LatLng(lat, lng);
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
