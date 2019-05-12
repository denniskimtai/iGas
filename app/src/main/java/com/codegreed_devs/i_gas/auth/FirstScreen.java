package com.codegreed_devs.i_gas.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.codegreed_devs.i_gas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FirstScreen extends Activity {

    //Duration of wait
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        //handler to start menu activity and close splash screen after 1000ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent to start login activity
                Intent intent = new Intent(FirstScreen.this, LoginActivity.class);
                FirstScreen.this.startActivity(intent);
                FirstScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
