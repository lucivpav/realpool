package com.example.kane.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, JsonReceivedCommand {

    private GoogleMap mGoogleMap;
    private Marker mFinishMarker;
    private int mPrice = 0;

    LinearLayout l1;
    LinearLayout l2;
    LinearLayout ll;

    Intent mServiceIntent;

    private enum JsonTask {FindLocation, FindRoute};
    private JsonTask mJsonTask;
    private LatLng mCurPos;
    private LatLng mDestination;
    private FusedLocationProviderClient mFusedLocationClient;


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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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


        findViewById(R.id.id_navigation_ll).setVisibility(LinearLayout.GONE);

        mServiceIntent = new Intent(this, RSSPullService.class);
        //mServiceIntent.setData(Uri.parse(dataUrl));

        startService(mServiceIntent);

        SetUpMenu();


    }

    private void SetupNavigation(String text)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );

        ll = (LinearLayout) findViewById(R.id.id_navigation_ll);
        ll.setVisibility(LinearLayout.VISIBLE);
        TextView tx = new TextView(getApplicationContext());
        tx.setText(Html.fromHtml(text));
        tx.setTextSize(20);
        tx.setLayoutParams(params);
        tx.setTextColor(Color.parseColor("#FFFFFF"));
        tx.setPadding(10,10,10,10);
        tx.setGravity(Gravity.CENTER);

        ll.addView(tx);
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
                    setupRoute();
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
                    SetUpHopOn();
                }
            }
        });
        l2.addView(tx2);
    }

    public void RemoveViewsFromMenu(){
        l1.removeAllViews();
        l2.removeAllViews();
    }

    public void SetUpHopOn(){

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
                //Debugging:
                //Waiting();
                ShowOffer("Alex", BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.default_user), 3);
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

    public void ShowOffer(final String user, Bitmap bitmap, int review) {

        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                2.0f
        );
        l1.setLayoutParams(params3);
        ImageView view = new ImageView(getApplicationContext());
        view.setImageBitmap(bitmap);
        view.setImageResource(android.R.mipmap.sym_def_app_icon);
        l1.addView(view);

        l2.setOrientation(LinearLayout.VERTICAL);

        TextView tx6 = new TextView(getApplicationContext());
        TextView tx7 = new TextView(getApplicationContext());

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );

        if(review == 1) {
            tx6.setText(user + "  *");
        } else if (review == 2) {
            tx6.setText(user + "  **");
        } else  if(review == 3) {
            tx6.setText(user + "  ***");
        } else  {
            Toast.makeText(getApplicationContext(),"Invalid review", Toast.LENGTH_SHORT).show();
            tx6.setText(user);
        }

        tx7.setText("wants to pick you up");

        tx6.setTextColor(Color.parseColor("#FFFFFF"));
        tx7.setTextColor(Color.parseColor("#FFFFFF"));
        tx6.setTextSize(22);
        tx7.setTextSize(18);
        tx6.setGravity(Gravity.BOTTOM);
        tx7.setGravity(Gravity.LEFT);

        l2.addView(tx6, params2);
        l2.addView(tx7, params2);

        ll.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "Declined", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "Accepted", Toast.LENGTH_SHORT).show();
                ShowDriver(user);
            }
            public void onSwipeBottom() {
            }

        });

    }

    public void ShowDriver(String driver)  {
        RemoveViewsFromMenu();
        ll.removeAllViews();
        TextView tx8 = new TextView(getApplicationContext());
        tx8.setText("DRIVING WITH " + driver);
        tx8.setTextSize(25);
        tx8.setTextColor(Color.parseColor("#FFFFFF"));
        tx8.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        ll.addView(tx8, params);
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
                        Boolean prevPosWasNull = mCurPos == null;
                        mCurPos = new LatLng(location.latitude, location.longitude);
                        if ( prevPosWasNull )
                            mapZoomAtPosition(mCurPos);
                    }
                });



        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mDestination = new LatLng(latLng.latitude, latLng.longitude);
                MarkerOptions markerOptions = new MarkerOptions().position(mDestination);
                if(mFinishMarker != null) {
                    mFinishMarker.remove();
                }
                mFinishMarker = mGoogleMap.addMarker(markerOptions);
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

        mDestination = new LatLng(lat, lng);

        if ( mFinishMarker != null )
            mFinishMarker.remove();
        mFinishMarker = mGoogleMap.addMarker(new MarkerOptions().position(mDestination).title("Marker"));
        mapZoomAtPosition(mDestination);
    }

    private void setupRoute()
    {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + mCurPos.latitude + "," + mCurPos.longitude
                + "&destination=" + mDestination.latitude + "," + mDestination.longitude
                + "&key=AIzaSyC2lGEulxqVNmD45HnLSQ0rg05wq7qUZjc";
        mJsonTask = JsonTask.FindRoute;
        new RetrieveJsonTask().execute(new RetrieveJsonParam(url, this));
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
                if ( i == 0 ) {
                    polylineOptions.add(start);
                    String instructions = steps.getJSONObject(i).getString("html_instructions");
                    SetupNavigation(instructions);
                }
                polylineOptions.add(end);
            }
            Polyline line = mGoogleMap.addPolyline(polylineOptions);
            mapZoomAtPosition(mCurPos);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void mapZoomAtPosition(LatLng position)
    {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(17).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
