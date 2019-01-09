package com.codegreed_devs.i_gas.DashBoard;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codegreed_devs.i_gas.ClientMapsActivity;
import com.codegreed_devs.i_gas.R;
import com.codegreed_devs.i_gas.auth.RegistrationActivityClass;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends Fragment {

    CardView cardViewBuyGas, cardViewRefillGas, cardViewGasEquipments, cardViewBulkPurchase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return layout file
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Set toolbar title
        getActivity().setTitle("Home");


        cardViewBuyGas = getActivity().findViewById(R.id.cardViewBuyGas);
        cardViewBuyGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderDetails.class);
                startActivity(intent);

            }
        });

        cardViewRefillGas = getActivity().findViewById(R.id.cardViewRefill);
        cardViewRefillGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderDetails.class);
                startActivity(intent);
            }
        });

        cardViewGasEquipments = getActivity().findViewById(R.id.cardViewGasEquipments);
        cardViewGasEquipments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cardViewBulkPurchase = getActivity().findViewById(R.id.cardViewBulkPurchase);
        cardViewBulkPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //Save details of registration
        Intent i = getActivity().getIntent();

        String mFullName = i.getStringExtra("Full_Name");
        String mEmail = i.getStringExtra("Email");
        String mPhoneNumber = i.getStringExtra("Phone_Number");
        String mLocation = i.getStringExtra("Location");



    }


}