package com.codegreed_devs.i_gas.DashBoard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.i_gas.BuildConfig;
import com.codegreed_devs.i_gas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private ProgressDialog progressDialog;
    private Button updateProfile;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

        //initialize shared preference
        sharedPreferences = getContext().getSharedPreferences(BuildConfig.APPLICATION_ID + "SharedPreference", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //get current user id
        String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Database reference
        final DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("clients").child(clientId);


        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Updating information! Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                //save updates
                Map<String, String> profileMap = new HashMap<String, String>();
                profileMap.put("clientId", sharedPreferences.getString("ClientId", ""));
                profileMap.put("clientName", profileFullName.getText().toString().trim());
                profileMap.put("clientEmail", profileEmail.getText().toString().trim());
                profileMap.put("regPhoneNumber", profilePhoneNumber.getText().toString().trim());
                profileMap.put("regLocation", profileLocation.getText().toString().trim());
                profileMap.put("fcm_token", sharedPreferences.getString("FCMToken", ""));

                profileRef.setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getActivity(), "Update Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else if (task.getException() != null)
                        {
                            Log.e("DATABASE ERROR", task.getException().getMessage());
                        }
                    }
                });

            }
        });

    }
}
