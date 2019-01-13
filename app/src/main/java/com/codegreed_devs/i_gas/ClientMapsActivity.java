package com.codegreed_devs.i_gas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codegreed_devs.i_gas.DashBoard.HomeActivity;
import com.codegreed_devs.i_gas.DashBoard.OrderDetails;
import com.codegreed_devs.i_gas.DashBoard.OrderDetailsClass;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static final String FCM_LEGACY_API_TOKEN = "AIzaSyAaslzBTkkRgiPqNIriqewsvZhqZ64lL2Q";
    public static final String FCM_LEGACY_URL = "https://fcm.googleapis.com/fcm/send";
    public static final int HTTP_RESPONSE_OK = 200;
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
        if (mapFragment != null)
        {
            mapFragment.onCreate(null);
            mapFragment.onResume();
            mapFragment.getMapAsync(this);
        }

        orderButton = findViewById(R.id.order_button);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (orderButton.getText().toString().trim())
                {
                    case "Order now":
                        //Change text on button
                        orderButton.setText("Finding a vendor...");
                        orderButton.setEnabled(false);
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
                        getClosestvendor();
                        //Send client location to vendor
                        deliveryLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                        getClosestvendor();
                        break;
                    case "Done!":
                        Intent intent = new Intent(ClientMapsActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

            }
        });


    }

    // starting radius to search closest vendor
    private int radius = 1;
    private boolean vendorFound = false;
    private String vendorFoundId;

    private void getClosestvendor() {

        //Get current location of client
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        //Add vendors location as child of vendors available
        DatabaseReference vendorLocation = FirebaseDatabase.getInstance().getReference().child("Available Vendors");

        GeoFire geoFire = new GeoFire(vendorLocation);

        //Query geofire for closest vendor around the clients location
        GeoQuery geoQuery  = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            //Called when vendor is found within the radius and key stores vendorId and location
            public void onKeyEntered(String key, GeoLocation location) {
                //Check if there is any vendor found
                if (!vendorFound) {
                    vendorFound = true;
                    vendorFoundId = key;

                    //Add new marker to new updated position
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.latitude, location.longitude))
                            .title("Vendor location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 11));

                    alertVendorFound(vendorFoundId);
                }
            }

            @Override
            public void onKeyExited(String key) {
                orderButton.setEnabled(true);
                orderButton.setText(getResources().getString(R.string.order_now));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                orderButton.setEnabled(true);
                orderButton.setText(getResources().getString(R.string.order_now));
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
                Log.e("GEOQUERY ERROR", error.getMessage());
                orderButton.setEnabled(true);
                orderButton.setText(getResources().getString(R.string.order_now));
            }
        });

    }

    private void alertVendorFound(final String vendorFoundId) {

        FirebaseDatabase.getInstance().getReference("vendors").child(vendorFoundId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String vendorName = dataSnapshot.child("business_name").getValue(String.class);
                String vendorEmail = dataSnapshot.child("business_email").getValue(String.class);
                String vendorAddress = dataSnapshot.child("business_address").getValue(String.class);
                final String vendorFCMToken = dataSnapshot.child("fcm_token").getValue(String.class);

                String message = "Vendor Name : " + vendorName + "\n";
                message += "Vendor Email : " + vendorEmail + "\n";
                message += "Vendor Address : " + vendorAddress;

                if (vendorAddress != null)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ClientMapsActivity.this);
                    alert.setTitle("Vendor Found");
                    alert.setMessage(message);
                    alert.setCancelable(false);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveOrderDetails(vendorFoundId, vendorFCMToken);
                        }
                    });
                    alert.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveOrderDetails(final String vendorFoundId, final String vendorFCMToken){

        //get Strings from orderDetails activity
        Intent intentKey = getIntent();

        final String Unique_Key = intentKey.getExtras().getString("Unique_Key");

        Intent gasSizeInt = getIntent();
        String gasSize = gasSizeInt.getExtras().getString("gasSize");

        Intent gasTypeInt = getIntent();
        String gasType = gasTypeInt.getExtras().getString("gasType");

        Intent numberOfCylindersInt = getIntent();
        String numberOfCylinders = numberOfCylindersInt.getExtras().getString("numberOfCylinders");

        Intent GasBrandInt = getIntent();
        String GasBrand = GasBrandInt.getExtras().getString("GasBrand");

        //save vendorFoundId under order details of client
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference orderDetailsRef = FirebaseDatabase
                .getInstance().getReference("Order Details").child(vendorFoundId).child(Unique_Key);

        final Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("orderId", Unique_Key);
        keyMap.put("clientId", userId);
        keyMap.put("gasSize", gasSize);
        keyMap.put("gasType", gasType);
        keyMap.put("mnumberOfCylinders", numberOfCylinders);
        keyMap.put("gasBrand", GasBrand);
        keyMap.put("vendorId", vendorFoundId);
        keyMap.put("orderStatus", "waiting");

        //MOVED THIS FROM PREVIOUS ACTIVITY
        FirebaseDatabase
                .getInstance()
                .getReference("Order Details")
                .child(userId).child(Unique_Key).setValue(keyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful() && task.getException() != null)
                {
                    Log.e("DATABASE ERROR", task.getException().getMessage());
                }
            }
        });

        orderDetailsRef.setValue(keyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    orderButton.setText("Done!");
                    sendFCMNotification(vendorFCMToken, Unique_Key, vendorFoundId);
                }
                else if (task.getException() != null)
                {
                    orderButton.setEnabled(true);
                    Log.e("DATABASE ERROR", task.getException().getMessage());
                }
            }
        });
    }

    private void sendFCMNotification(String vendorFCMToken, String orderId, String vendorId){
        if (vendorFCMToken != null)
        {
            String jsonData = "{";
            jsonData += "\"to\":";
            jsonData += "\"" + vendorFCMToken + "\",";
            jsonData += "\"notification\":";
            jsonData += "{";
            jsonData += "\"title\":";
            jsonData += "\"New Order\",";
            jsonData += "\"body\":";
            jsonData += "\"You have a new order\"";
            jsonData += "},";
            jsonData += "\"restricted_package_name\":";
            jsonData += "\"codegreed_devs.com.igasvendor\",";
            jsonData += "\"data\":{";
            jsonData += "\"order_id\":";
            jsonData += "\"" + orderId + "\",";
            jsonData += "\"vendor_id\":";
            jsonData += "\"" + vendorId + "\"";
            jsonData += "}";
            jsonData += "}";

            try {

                JSONObject jsonObject = new JSONObject(jsonData);

                Log.e("FCM JSON", jsonObject.toString());

                post(jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("OKHTTP ERROR", e.getMessage());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {

                        int response_code = response.code();

                        Log.e("HTTP RESPONSE CODE", String.valueOf(response_code));

                        if (response_code == HTTP_RESPONSE_OK)
                        {

                            final String responseMessage = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Log.e("FCM RESPONSE", responseMessage);

                                    try {

                                        JSONObject jsonObject = new JSONObject(responseMessage);

                                        int success = jsonObject.getInt("success");

                                        if (success != 0)
                                        {
                                            Toast.makeText(ClientMapsActivity.this, "Vendor notified!", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("NOTIFICATION STATUS", "VENDOR FCM TOKEN ABSENT");
        }
    }

    private void post(String jsonPayload, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, jsonPayload);

        Request request = new Request.Builder()
                .url(FCM_LEGACY_URL)
                .addHeader("Content-Type","application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "key=" + FCM_LEGACY_API_TOKEN)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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
                if (error != null)
                    Log.e("GEOFIRE ERROR", error.getMessage());
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        finish();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            buildApiClient();
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }
}
