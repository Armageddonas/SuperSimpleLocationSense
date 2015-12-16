package com.zeuss_works.armageddonas.supersimplelocationsense;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String TAG = "LocationCheck";
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //region location declare
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                Log.v(TAG, "Change");
                refreshLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(getBaseContext(), "Miracle",
                        Toast.LENGTH_SHORT).show();
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(getBaseContext(), "Don't cry",
                        Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(getBaseContext(), "Cry",
                        Toast.LENGTH_SHORT).show();
            }
        };


        try {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 15, locationListener);
        } catch (SecurityException ex) {
            Toast.makeText(getBaseContext(), "Permission Problem",
                    Toast.LENGTH_SHORT).show();
            Log.v(TAG, "Permission problem");
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 15, locationListener);

        } catch (SecurityException ex) {
            Toast.makeText(getBaseContext(), "Permission Problem",
                    Toast.LENGTH_SHORT).show();
            Log.v(TAG, "Permission problem");
        }

        //endregion
    }

    public void refreshLocation(Location location) {
        Toast.makeText(getBaseContext(), "Updated",
                Toast.LENGTH_SHORT).show();

        mMap.clear();
        // Add a marker in yourlocation and move the camera
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //region Get address
        try {
            Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.isEmpty()) {
                mMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));
            } else {
                if (addresses.size() > 0) {
                    mMap.addMarker(new MarkerOptions().position(myLocation)
                            .title((addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName()))
                            .snippet(String.valueOf(location.getSpeed())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        //endregion
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
