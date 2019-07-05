package com.codegreed_devs.i_gas.DashBoard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codegreed_devs.i_gas.DashBoard.Home;
import com.codegreed_devs.i_gas.DashBoard.HomeActivity;
import com.codegreed_devs.i_gas.DashBoard.VendorModel;
import com.codegreed_devs.i_gas.DashBoard.VendorsListAdapter;
import com.codegreed_devs.i_gas.OrderConfirmationActivity;
import com.codegreed_devs.i_gas.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindVendorsActivity extends FragmentActivity{

    public static final String FCM_LEGACY_API_TOKEN = "AIzaSyAaslzBTkkRgiPqNIriqewsvZhqZ64lL2Q";
    public static final String FCM_LEGACY_URL = "https://fcm.googleapis.com/fcm/send";
    public static final int HTTP_RESPONSE_OK = 200;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private DatabaseReference rootRef;
    private ProgressBar loadVendors;
    private VendorsListAdapter adapter;
    private ArrayList<VendorModel> vendors;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_vendors);

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //initialize views

        loadVendors = (ProgressBar) findViewById(R.id.load_vendors);

        //initialize other variables
        rootRef = FirebaseDatabase.getInstance().getReference();
        vendors = new ArrayList<VendorModel>();
        adapter = new VendorsListAdapter(this, vendors);



        //method call
        getClosestVendors();



    }


    private void getClosestVendors() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat
                    .requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);

        LocationServices
                .getFusedLocationProviderClient(this)
                .getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location deliveryLocation) {
                        //Add vendors location as child of vendors available
                        rootRef.child("vendors")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (final DataSnapshot ds : dataSnapshot.getChildren())
                                        {
                                            final String vendor_id = ds.getKey();
                                            GeoFire geoFire = new GeoFire(rootRef.child("vendors").child(vendor_id));
                                            geoFire.getLocation("business_location", new LocationCallback() {
                                                @Override
                                                public void onLocationResult(String key, GeoLocation location) {
                                                    if (location != null)
                                                    {
                                                        Location vendorLocation = new Location(vendor_id);
                                                        vendorLocation.setLatitude(location.latitude);
                                                        vendorLocation.setLongitude(location.longitude);
                                                        if (deliveryLocation.distanceTo(vendorLocation) < 1000)
                                                        {
                                                            //save distance from vendor to customer
                                                            int vendorDistance = (int) deliveryLocation.distanceTo(vendorLocation);

                                                            VendorModel vendor = new VendorModel(vendor_id,
                                                                    ds.child("business_name").getValue(String.class),
                                                                    ds.child("business_address").getValue(String.class),
                                                                    ds.child("fcm_token").getValue(String.class),
                                                                    vendorDistance);
                                                            vendors.add(vendor);
                                                        }

                                                        //initialize views
                                                        ListView vendorsList = (ListView) findViewById(R.id.vendors_list);

                                                        //update ui
                                                        vendorsList.setAdapter(adapter);

                                                        //handle item clicks
                                                        vendorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                confirmOrder(vendors.get(i));
                                                            }
                                                        });

                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        adapter.notifyDataSetChanged();
                                        loadVendors.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        loadVendors.setVisibility(View.GONE);
                                        Toast.makeText(FindVendorsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FindVendorsActivity.this, "There was a problem getting your location." +
                        "\nCheck your GPS settings", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void confirmOrder(final VendorModel vendor){

        String confirm = "Are you sure you want to order from :\n";
        confirm += vendor.getVendorName() + "\n";
        confirm += vendor.getVendorAddress() + "\n";

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm Order");
        alert.setMessage(confirm);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveOrderDetails(vendor.getVendorId(), vendor.getFcmToken());
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
                //dismiss dialog
            }
        });
        alert.show();
    }

    private void saveOrderDetails(final String vendorId, final String vendorFCMToken){

        loadVendors.setVisibility(View.VISIBLE);

        //get Strings from orderDetails activity

        final String Unique_Key = getIntent().getExtras().getString("Unique_Key");
        String gasSize = getIntent().getExtras().getString("gasSize");
        String gasType = getIntent().getExtras().getString("gasType");
        String numberOfCylinders = getIntent().getExtras().getString("numberOfCylinders");
        String GasBrand = getIntent().getExtras().getString("GasBrand");

        //save vendorFoundId under order details of client
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("orderId", Unique_Key);
        keyMap.put("clientId", userId);
        keyMap.put("gasSize", gasSize);
        keyMap.put("gasType", gasType);
        keyMap.put("mnumberOfCylinders", numberOfCylinders);
        keyMap.put("gasBrand", GasBrand);
        keyMap.put("vendorId", vendorId);
        keyMap.put("orderStatus", "waiting");

        //MOVED THIS FROM PREVIOUS ACTIVITY
        rootRef.child("Order Details").child(userId).child(Unique_Key).setValue(keyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful() && task.getException() != null)
                {
                    Log.e("DATABASE ERROR", task.getException().getMessage());
                    loadVendors.setVisibility(View.GONE);
                }
            }
        });

        rootRef.child("Order Details").child(vendorId).child(Unique_Key).setValue(keyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful() && vendorFCMToken != null)
                {
                    sendFCMNotification(vendorFCMToken, Unique_Key, vendorId);
                }
                else if (task.isSuccessful() && vendorFCMToken == null)
                {
                    loadVendors.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Order Sent!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), OrderConfirmationActivity.class));
                }
                else if (task.getException() != null)
                {
                    loadVendors.setVisibility(View.GONE);
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

                        if (response_code == HTTP_RESPONSE_OK)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadVendors.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Vendor notified!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), OrderConfirmationActivity.class));
                                }
                            });
                        }

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            getClosestVendors();
        }
    }
}
