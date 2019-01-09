package com.codegreed_devs.i_gas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codegreed_devs.i_gas.DashBoard.HomeActivity;

public class OrderConfirmationActivity extends AppCompatActivity {
    private Button orderAgain;
    private TextView finishedPurchasing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        orderAgain = findViewById(R.id.orderAgain);
        orderAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderConfirmationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        finishedPurchasing = findViewById(R.id.finishedPurchasing);
        finishedPurchasing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
