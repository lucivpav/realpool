package com.example.kane.myapplication;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, JsonReceivedCommand {

    private GoogleMap mGoogleMap;
    private Marker mFinishMarker;
    private enum JsonTask {FindLocation, FindRoute};
    private JsonTask mJsonTask;
    private LatLng mCurPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mymenu, menu);


        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        mCurPos = new LatLng(location.latitude, location.longitude);
                    }
                });
    }

    public void jsonReceived(JSONObject object)
    {
        if ( mJsonTask == JsonTask.FindLocation )
            setJsonLocation(object);
        else if ( mJsonTask == JsonTask.FindRoute )
            setJsonRoute(object);
    }

    private void doMySearch(String query)
    {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + query + "&key=AIzaSyC2lGEulxqVNmD45HnLSQ0rg05wq7qUZjc";
        mJsonTask = JsonTask.FindLocation;
        new RetrieveJsonTask().execute(new RetrieveJsonParam(url, this));
    }

    private void setJsonLocation(JSONObject object)
    {
        double lat = 0, lng = 0;

        try {
            JSONObject jsonLocation = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            lat = jsonLocation.getDouble("lat");
            lng = jsonLocation.getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng destination = new LatLng(lat, lng);

        if ( mFinishMarker != null )
            mFinishMarker.remove();
        mFinishMarker = mGoogleMap.addMarker(new MarkerOptions().position(destination).title("Marker"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(destination));

        // set route

        /*String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + mCurPos.latitude + "," + mCurPos.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&key=AIzaSyC2lGEulxqVNmD45HnLSQ0rg05wq7qUZjc";
        mJsonTask = JsonTask.FindRoute;
        new RetrieveJsonTask().execute(new RetrieveJsonParam(url, this));*/
    }

    private void setJsonRoute(JSONObject object)
    {
        try {
            JSONArray steps = object.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                                           .getJSONObject(0).getJSONArray("steps");
            PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.RED);
            for ( int i = 0 ; i < steps.length() ; i++ )
            {
                JSONObject startLocation = steps.getJSONObject(i).getJSONObject("start_location");
                JSONObject endLocation = steps.getJSONObject(i).getJSONObject("end_location");
                LatLng start = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));
                LatLng end = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));
                if ( i == 0 )
                    polylineOptions.add(start);
                polylineOptions.add(end);
            }
            Polyline line = mGoogleMap.addPolyline(polylineOptions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
