package com.codegreed_devs.i_gas.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.i_gas.DashBoard.Home;
import com.codegreed_devs.i_gas.DashBoard.HomeActivity;
import com.codegreed_devs.i_gas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private Button signupButton;
    private CheckBox checkBoxTerms;
    private EditText regPassword, confirm_password, regEmail, regFullName, regPhoneNumber, regLocation;
    private TextView alreadyMember;
    private ProgressDialog progressDialog;

    //Firebase object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Initialize objects
        regPassword = findViewById(R.id.reg_password);
        regEmail = findViewById(R.id.regEmail);
        regFullName = findViewById(R.id.name);
        regPhoneNumber = findViewById(R.id.phone);
        regLocation = findViewById(R.id.location);
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
                confirmPassword();
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

        //get email and password
        final String Email = regEmail.getText().toString().trim();
        String Password = regPassword.getText().toString().trim();

        //Check if emails are empty
        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(RegistrationActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Password)) {

            Toast.makeText(RegistrationActivity.this, "Password field cannot be empty ", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //Create new user
        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Check if registering was successful
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();

                            Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            String mFullName = regFullName.getText().toString().trim();
                            String mEmail = regEmail.getText().toString().trim();
                            String mPhoneNumber = regPhoneNumber.getText().toString().trim();
                            String mLocation = regLocation.getText().toString().trim();

                            //Write to database
                            String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference regActivityRef = FirebaseDatabase.getInstance().getReference("Client Registration Details");
                            RegistrationActivityClass registrationActivity = new RegistrationActivityClass(mFullName,mEmail,mPhoneNumber,mLocation);
                            regActivityRef.child(clientId).setValue(registrationActivity);

                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);



                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registration failed! Please check your internet connection or password and try again", Toast.LENGTH_SHORT).show();
                            return;
                        }


                    }
                });
    }



    private void confirmPassword() {

        regPassword = findViewById(R.id.reg_password);
        confirm_password = findViewById(R.id.reg_confirm_password);

        if (!regPassword.equals(confirm_password)){
            Toast.makeText(this, "Password does not match try again", Toast.LENGTH_SHORT).show();
            signupButton.setEnabled(false
            );
            return;

        }
    }

}
