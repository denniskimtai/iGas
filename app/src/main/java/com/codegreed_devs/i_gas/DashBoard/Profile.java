package com.codegreed_devs.i_gas.DashBoard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.i_gas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {

    private EditText profileFullName, profileEmail, profilePhoneNumber, profileLocation;
    ProgressDialog progressDialog;
    Button updateProfile;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return layout file
        return inflater.inflate(R.layout.fragment_profile,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");

        //Initialize views
        profileFullName = getActivity().findViewById(R.id.profileFullName);
        profileEmail = getActivity().findViewById(R.id.profileEmail);
        profilePhoneNumber = getActivity().findViewById(R.id.profilePhoneNumber);
        profileLocation = getActivity().findViewById(R.id.profileLocation);
        updateProfile = getActivity().findViewById(R.id.updateProfile);
        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("Fetching information! Please wait...");
        progressDialog.show();

        //get current user id
        String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Database reference
        final DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("Client Registration Details").child(clientId);

        //Fetch from database
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    //Set text in the edit text
                    profileFullName.setText(dataSnapshot.child("regFullName").getValue(String.class));
                    profileEmail.setText(dataSnapshot.child("regEmail").getValue(String.class));
                    profilePhoneNumber.setText(dataSnapshot.child("regPhoneNumber").getValue(String.class));
                    profileLocation.setText(dataSnapshot.child("regLocation").getValue(String.class));


                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save updates
                Map<String, String> profileMap = new HashMap<String, String>();
                profileMap.put("regFullName", profileFullName.getText().toString());
                profileMap.put("regEmail", profileEmail.getText().toString());
                profileMap.put("regPhoneNumber", profilePhoneNumber.getText().toString());
                profileMap.put("regLocation", profileLocation.getText().toString());

                profileRef.setValue(profileMap);

                Toast.makeText(getActivity(), "Update Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }
}
