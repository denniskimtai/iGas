package com.codegreed_devs.i_gas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codegreed_devs.i_gas.DashBoard.OrderDetails;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ClientMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button orderButton;
    private LatLng deliveryLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        orderButton = findViewById(R.id.order_button);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change text on button
                orderButton.setText("Finding a vendor...");
                //get client id
                String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                //Create database reference for request
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("clientRequest");

                //Location for client
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(clientId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
                    //Send client location to vendor
                deliveryLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                getClosestvendor();

            }
        });


    }

    // starting radius to search closest vendor
    private int radius = 1;
    private boolean vendorFound = false;
    private String vendorFoundId;


    private void getClosestvendor() {
        //Add vendors location as child of vendors available
        DatabaseReference vendorLocation = FirebaseDatabase.getInstance().getReference().child("Available Vendors");

        GeoFire geoFire = new GeoFire(vendorLocation);

        //Query geofire for closest vendor around the clients location
        GeoQuery geoQuery  = geoFire.queryAtLocation(new GeoLocation(deliveryLocation.latitude, deliveryLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            //Called when vendor is found within the radius and key stores vendorId and location
            public void onKeyEntered(String key, GeoLocation location) {
                //Check if there is any vendor found
                if (!vendorFound) {
                    vendorFound = true;
                    vendorFoundId = key;

                    //Save order details under vendorfound id
//                    DatabaseReference availebleDriverRef = FirebaseDatabase.getInstance().getReference().child("Available vendors").child(vendorFoundId);
//                    HashMap map = new HashMap();
//                    map.put();

                    String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference("Order Details").child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            FirebaseDatabase.getInstance().getReference("Available Vendors").child(vendorFoundId).child("Client Order Details").setValue(dataSnapshot.getValue());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //Go to orderdetails activity to receive order details
                    Intent intent = new Intent(ClientMapsActivity.this, OrderConfirmationActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!vendorFound) {
                    radius++;
                    getClosestvendor();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        buildApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildApiClient(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }


    //Get changed location
    @Override
    public void onLocationChanged(Location location) {
        if (getApplicationContext() !=null) {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //Clear previous markers
            mMap.clear();
            //Add new marker to new updated position
            mMap.addMarker(new MarkerOptions().position(latLng).title("Delivery location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }

        //Save clients updated location
        String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer Locations");

        //Save using geofire
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(clientId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });


    }



    //When map is connected to specified location
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //Update location after every second
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //Trigger refreshment of location
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        //remove users location from database if he minimize or exit app
        //Save clients updated location
        String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer Locations");

        //Remove location
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(clientId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        finish();

    }
}
