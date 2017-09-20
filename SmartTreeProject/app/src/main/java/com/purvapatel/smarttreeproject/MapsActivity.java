package com.purvapatel.smarttreeproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.purvapatel.smarttreeproject.Modules.DirectionFinder;
import com.purvapatel.smarttreeproject.Modules.DirectionFinderListener;
import com.purvapatel.smarttreeproject.Modules.GPSTracker;
import com.purvapatel.smarttreeproject.Modules.Route;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Purva Patel on 9/12/2017.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private String origin;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;


    double txtlatitide, txtlongitude;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    // GPSTracker class
             GPSTracker gps;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_maps);
         // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
         mapFragment.getMapAsync(this);

         try {
             if (ActivityCompat.checkSelfPermission(this, mPermission)
                 != MockPackageManager.PERMISSION_GRANTED) {

                 ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

             // If any permission above not allowed by user, this condition will
            //execute every time, else your else part will work
             }
         } catch (Exception e) {
             e.printStackTrace();
         }

         btnFindPath = (Button) findViewById(R.id.btnFindPath);
         etDestination = (EditText) findViewById(R.id.etDestination);
         etOrigin = (EditText) findViewById(R.id.etOrigin);

         // to get latitude and longitude of the current location
         gps = new GPSTracker(this);

         // check if GPS enabled
         if(gps.canGetLocation()){

             // get latitude
             double latitude = gps.getLatitude();
             // get longitude
             double longitude = gps.getLongitude();

             txtlatitide = latitude;
             txtlongitude = longitude;

             // get street address form the latitude and longitude.
             Geocoder geocoder;
             geocoder = new Geocoder(this, Locale.getDefault());

             try {
                 List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                 if(addresses.size() > 0) {
                     etOrigin.setText("" + addresses.get(0).getAddressLine(0));
                     origin = addresses.get(0).getAddressLine(0);
                 }
             }catch (Exception e){}



         }else{
             // can't get location
             // GPS or Network is not enabled
             // Ask user to enable GPS/network in settings
             gps.showSettingsAlert();
         }


         // when user clicks on Find Path button.
         btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
             sendRequest();
             }
         });
     }

     private void sendRequest() {
         //String origin = origin.toString();
         String destination = etDestination.getText().toString();

         // if original address is empty.
         if (origin.isEmpty()) {
             Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
             return;
         }
         // fi destination address is empty.
        if (destination.isEmpty()) {
             Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
             return;
         }

         try {
             // find the direction
             new DirectionFinder(this, origin, destination).execute();
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }
    }

     @Override
     public void onMapReady(GoogleMap googleMap) {
         mMap = googleMap;
         // put the marker when map is ready.
         LatLng hcmus = new LatLng(txtlatitide, txtlongitude);
         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
         originMarkers.add(mMap.addMarker(new MarkerOptions()
                                .title(""+origin)
                                .position(hcmus)));

         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             // TODO: Consider calling
             //    ActivityCompat#requestPermissions
             // here to request the missing permissions, and then overriding
             //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
             //                                          int[] grantResults)
             // to handle the case where the user grants the permission. See the documentation
             // for ActivityCompat#requestPermissions for more details.
            return;
         }
         mMap.setMyLocationEnabled(true);
     }


    @Override
    public void onDirectionFinderStart() {
        // progress bar during the map loading time.
        progressDialog = ProgressDialog.show(this, "Please wait.", "Finding direction..!", true);

         if (originMarkers != null) {
             for (Marker marker : originMarkers) {
                 marker.remove();
             }
         }

         if (destinationMarkers != null) {
             for (Marker marker : destinationMarkers) {
                marker.remove();
             }
         }

         if (polylinePaths != null) {
             for (Polyline polyline:polylinePaths ) {
                 polyline.remove();
             }
         }
     }

     @Override
     public void onDirectionFinderSuccess(List<Route> routes) {
         progressDialog.dismiss();
         polylinePaths = new ArrayList<>();
         originMarkers = new ArrayList<>();
         destinationMarkers = new ArrayList<>();

         for (Route route : routes) {
             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
             // set duration.
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
             // set distance
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

             // add market on original address.
             originMarkers.add(mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                                         .title(route.startAddress)
                     .position(route.startLocation)));

             // add marker on destination address.
             destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                                        .title(route.endAddress)
                 .position(route.endLocation)));

             // set the polyline between two locations.
             PolylineOptions polylineOptions = new PolylineOptions().
                     geodesic(true).
                     color(Color.RED).
                     width(10);

             for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

             polylinePaths.add(mMap.addPolyline(polylineOptions));
         }
     }

}



