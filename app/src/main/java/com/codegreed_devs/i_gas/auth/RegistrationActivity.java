package com.codegreed_devs.i_gas.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.i_gas.BuildConfig;
import com.codegreed_devs.i_gas.DashBoard.Home;
import com.codegreed_devs.i_gas.DashBoard.HomeActivity;
import com.codegreed_devs.i_gas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "FCMToken";
    private DatabaseReference mDatabaseRef;
    private SharedPreferences sharedPref;
    private Button signupButton;
    private CheckBox checkBoxTerms;
    private EditText regPassword, confirm_password, regEmail, regFullName, regPhoneNumber, regLocation;
    private TextView alreadyMember;
    private ProgressDialog progressDialog;
    private String mFullName, mEmail, mPhoneNumber, mLocation, mPassword, mConfirmPassword;

    //Firebase object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        sharedPref = getApplicationContext().getSharedPreferences(BuildConfig.APPLICATION_ID + "SHARED_PREF", Context.MODE_PRIVATE);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        //Initialize objects
        regEmail = findViewById(R.id.regEmail);
        regFullName = findViewById(R.id.name);
        regPhoneNumber = findViewById(R.id.phone);
        regLocation = findViewById(R.id.location);
        regPassword = findViewById(R.id.reg_password);
        confirm_password = findViewById(R.id.reg_confirm_password);
        signupButton = findViewById(R.id.signupButton);
        progressDialog = new ProgressDialog(this);

        //Initialize firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        signupButton = findViewById(R.id.signupButton);
        signupButton.setEnabled(false);
        signupButton.setBackgroundColor(Color.GRAY);
        signupButton.setText("Sign Up");
        signupButton.setTextSize(17);

        checkBoxTerms = findViewById(R.id.checkboxTerms);
        checkBoxTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    signupButton.setEnabled(true);
                    signupButton.setBackgroundColor(Color.parseColor("#689F38"));
                    signupButton.setText("Sign Up");
                    signupButton.setTextSize(17);
                } else {
                    signupButton.setEnabled(false);
                    signupButton.setBackgroundColor(Color.GRAY);
                    signupButton.setText("Please accept terms and conditions");
                    signupButton.setTextSize(12);
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    registerUser();
            }
        });

        alreadyMember  =findViewById(R.id.alreadyMember);
        alreadyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    //Firebase authentication to register new users
    private void registerUser(){

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //Create new user
        firebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Check if registering was successful
                        if (task.isSuccessful()) {

                            //Write to database
                            final String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            DatabaseReference regActivityRef = FirebaseDatabase.getInstance().getReference("clients");
                            RegistrationActivityClass registrationActivity = new RegistrationActivityClass(clientId,mFullName,mEmail,mPhoneNumber,mLocation);

                            regActivityRef.child(clientId).setValue(registrationActivity).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        sendFCMTokenToDatabase(clientId);
                                        Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if (task.getException() != null)
                                    {
                                        Log.e("DATABASE ERROR", task.getException().getMessage());
                                    }
                                    progressDialog.dismiss();
                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registration failed! Please check your internet connection or password and try again", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }



    private boolean validate() {

        mFullName = regFullName.getText().toString().trim();
        mEmail = regEmail.getText().toString().trim();
        mPhoneNumber = regPhoneNumber.getText().toString().trim();
        mLocation = regLocation.getText().toString().trim();
        mPassword = regPassword.getText().toString().trim();
        mConfirmPassword = confirm_password.getText().toString().trim();

        if (mFullName.isEmpty())
        {
            regFullName.setError("Enter full names!");
            return false;
        }
        else if (mEmail.isEmpty())
        {
            regEmail.setError("Enter valid email!");
            return false;
        }
        else if (mPhoneNumber.isEmpty())
        {
            regFullName.setError("Enter valid phone number!");
            return false;
        }
        else if (mLocation.isEmpty())
        {
            regFullName.setError("Enter location!");
            return false;
        }
        else if (mPassword.isEmpty())
        {
            regFullName.setError("Enter password!");
            return false;
        }
        else if (mPassword.length() < 6)
        {
            regFullName.setError("Password must be 6 characters or more!");
            return false;
        }
        else if (!mConfirmPassword.equals(mPassword))
        {
            confirm_password.setError("Passwords don't match");
            return false;
        }

        return true;
    }

    private void sendFCMTokenToDatabase(final String clientId) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    mDatabaseRef.child("clients").child(clientId).child("fcm_token")
                            .setValue(task.getResult().getToken())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful() && task.getException() != null)
                                        Log.e(TAG, task.getException().getMessage());
                                }
                            });
                } else if (task.getException() != null) {
                    Log.e(TAG, task.getException().getMessage());
                }
            }
        });
    }
}
