package com.example.kane.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, JsonReceivedCommand {

    private GoogleMap mGoogleMap;
    private Marker mFinishMarker;
    private double mFinishLat;
    private double mFinishLon;
    private int mPrice = 0;

    LinearLayout l1;
    LinearLayout l2;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );

        ll = (LinearLayout) findViewById(R.id.id_main_ll);

        l1 = new LinearLayout(getApplicationContext());
        l1.setOrientation(LinearLayout.HORIZONTAL);
        l1.setWeightSum(0);
        l1.setGravity(Gravity.CENTER);
        ll.addView(l1,params);

        l2 = new LinearLayout(getApplicationContext());
        l2.setOrientation(LinearLayout.HORIZONTAL);
        l2.setWeightSum(0);
        l2.setGravity(Gravity.CENTER);
        ll.addView(l2,params);

        SetUpMenu();
    }


    public void SetUpMenu(){
        TextView tx1 = new TextView(getApplicationContext());
        tx1.setText("DRIVE");
        tx1.setTextSize(20);
        tx1.setTextColor(Color.parseColor("#FFFFFF"));
        tx1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFinishMarker == null) {
                    Toast.makeText(getApplicationContext(), "Please select location", Toast.LENGTH_SHORT).show();
                } else {
                    RemoveViewsFromMenu();
                    SetUpDrive();
                }
            }
        });
        l1.addView(tx1);

        TextView tx2 = new TextView(getApplicationContext());
        tx2.setText("HOP ON");
        tx2.setTextSize(20);
        tx2.setTextColor(Color.parseColor("#FFFFFF"));
        tx2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFinishMarker == null) {
                    Toast.makeText(getApplicationContext(), "Please select location", Toast.LENGTH_SHORT).show();
                } else {
                    RemoveViewsFromMenu();
                }
            }
        });
        l2.addView(tx2);
    }

    public void RemoveViewsFromMenu(){
        l1.removeAllViews();
        l2.removeAllViews();
    }

    public void SetUpDrive(){

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );

        params.setMargins(40,60,40,60);

        final TextView tx3 = new TextView(getApplicationContext());
        tx3.setText("0 kč");
        tx3.setTextSize(20);
        tx3.setTextColor(Color.parseColor("#FFFFFF"));
        tx3.setGravity(Gravity.CENTER);

        Button bplus = new Button(getApplicationContext());
        bplus.setText("+");
        bplus.setBackgroundColor(Color.parseColor("#FFFFFF"));
        bplus.setTextColor(Color.parseColor("#000000"));
        bplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrice = mPrice +5;
                if(mPrice < 0)  {
                    mPrice = 0;
                }
                tx3.setText(mPrice + " kč");
            }
        });


        Button bminus = new Button(getApplicationContext());
        bminus.setText("-");
        bminus.setBackgroundColor(Color.parseColor("#FFFFFF"));
        bminus.setTextColor(Color.parseColor("#000000"));
        bminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrice = mPrice -5;
                if(mPrice < 0)  {
                    mPrice = 0;
                }
                tx3.setText(mPrice + " kč");
            }
        });

        l1.addView(bminus, params);
        l1.addView(tx3, params2);
        l1.addView(bplus, params);


        TextView tx4 = new TextView(getApplicationContext());
        tx4.setText("PLACE OFFER");
        tx4.setTextSize(20);
        tx4.setTextColor(Color.parseColor("#FFFFFF"));
        tx4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveViewsFromMenu();
                ll.removeAllViews();
                Waiting();
            }
        });
        l2.addView(tx4);
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

    public void Waiting() {
        TextView tx5 = new TextView(getApplicationContext());
        tx5.setText("WAITING");
        tx5.setTextSize(30);
        tx5.setTextColor(Color.parseColor("#FFFFFF"));
        tx5.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        ll.addView(tx5, params);
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
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mFinishLat = latLng.latitude;
                mFinishLon = latLng.longitude;
                LatLng finish = new LatLng(latLng.latitude, latLng.longitude);
                MarkerOptions markerOptions = new MarkerOptions().position(finish);
                if(mFinishMarker != null) {
                    mFinishMarker.remove();
                }
                mFinishMarker = mGoogleMap.addMarker(markerOptions);
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

        if ( mFinishMarker != null )
            mFinishMarker.remove();
        mFinishMarker = mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void doMySearch(String query)
    {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + query + "&key=AIzaSyC2lGEulxqVNmD45HnLSQ0rg05wq7qUZjc";
        new RetrieveJsonTask().execute(new RetrieveJsonParam(url, this));
    }
}
