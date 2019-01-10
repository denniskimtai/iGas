package com.codegreed_devs.i_gas.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextView noAccount;
    private EditText loginEmail, loginPassword;
    private Button sign_in_btn;
    private CheckBox checkBoxShowPassword;
    private ProgressDialog progressDialog;
    private DatabaseReference rootRef;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //Define firebaseauth object
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        loginPassword = findViewById(R.id.login_password);
        checkBoxShowPassword = findViewById(R.id.checkboxShowPassword);
        progressDialog = new ProgressDialog(this);
        noAccount = findViewById(R.id.noAccount);

        //Show password if user chooses
        showPassword();

        //Initialize firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        //initialize shared preference
        sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID + "SHARED_PREF", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Check is user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            //user is already logged in go to homeActivity
            finish();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);

        }

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

    }

    //Check credentials to login
    private void loginUser(){

        //Get email and password from user
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        //Check if email and password is empty
        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Enter Email Address");
            return;
        }

        if (TextUtils.isEmpty(password)){
            loginEmail.setError("Enter Password");
            return;
        }

        //Display progress dialog
        progressDialog.setMessage("Signing in Please wait...");

        //login user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        //Check if task is successful or not
                        if (task.isSuccessful()){
                            //Save user details to shared preference
                            saveUserDetails(firebaseAuth.getCurrentUser().getUid());
                        }
                        else if (task.getException() != null)
                        {
                            Toast.makeText(LoginActivity.this, "Couldn't log in", Toast.LENGTH_SHORT).show();
                            Log.e("AUTH ERROR", task.getException().getMessage());
                        }
                    }
                });

    }

    private void saveUserDetails(final String uid) {

        rootRef.child("clients").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                editor.putString("ClientId", uid);
                editor.putString("ClientNames", dataSnapshot.child("clientNames").getValue(String.class));
                editor.putString("ClientEmail", dataSnapshot.child("clientEmail").getValue(String.class));
                editor.putString("ClientPhoneNumber", dataSnapshot.child("regPhoneNumber").getValue(String.class));
                editor.putString("ClientLocation", dataSnapshot.child("regLocation").getValue(String.class));

                editor.apply();

                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showPassword(){

        checkBoxShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });

    }



}
