package com.codegreed_devs.i_gas.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.codegreed_devs.i_gas.DashBoard.HomeActivity;
import com.codegreed_devs.i_gas.R;

public class RegistrationActivity extends AppCompatActivity {
    Button signupButton;
    CheckBox checkBoxTerms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        signupButton = findViewById(R.id.signupButton);
        signupButton.setEnabled(false);
        signupButton.setBackgroundColor(Color.GRAY);
        signupButton.setText("Please accept terms and conditions");
        signupButton.setTextSize(12);

        checkBoxTerms = findViewById(R.id.checkboxTerms);
        checkBoxTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    signupButton.setEnabled(true);
                    signupButton.setBackgroundColor(Color.parseColor("#689F38"));
                    signupButton.setText("Sign Up");
                    signupButton.setTextSize(17);
                    signupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToMain_Activity();
                        }
                    });
                } else {
                    signupButton.setEnabled(false);
                    signupButton.setBackgroundColor(Color.GRAY);
                    signupButton.setText("Please accept terms and conditions");
                    signupButton.setTextSize(12);
                }
            }
        });




    }

    private void goToMainActivity() {
        Intent homeIntent = new Intent(RegistrationActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    public void goToMain_Activity() {
        Intent mainActivity = new Intent(RegistrationActivity.this, HomeActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivity);
        finish();
    }

    private void goToLogInActivity() {
        Intent loginIntent = new Intent(RegistrationActivity.this,LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }
}
