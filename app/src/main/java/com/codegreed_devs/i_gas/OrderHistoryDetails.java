package com.codegreed_devs.i_gas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class OrderHistoryDetails extends AppCompatActivity {
    private TextView TxtViewGasBrand, TxtViewGasSize, TxtViewGasType, TxtViewnumberOfcylinders, TxtVieworderId, TxtViewOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_details);

        TxtViewGasBrand = findViewById(R.id.gasBrand);
        TxtViewGasSize = findViewById(R.id.gasSize);
        TxtViewGasType = findViewById(R.id.gasType);
        TxtViewnumberOfcylinders = findViewById(R.id.numberOfCylinders);
        TxtVieworderId = findViewById(R.id.orderId);
        TxtViewOrderStatus = findViewById(R.id.orderStatus);

        //GET INTENT AND RECEIVE DATA
        Intent i = this.getIntent();

        String GasBrand = i.getExtras().getString("GAS_BRAND");
        String GasSize = i.getExtras().getString("GAS_SIZE");
        String GasType = i.getExtras().getString("GAS_TYPE");
        String NumberOfCylinders = i.getExtras().getString("NUMBER_OF_CYLINDERS");
        String OrderId = i.getExtras().getString("ORDER_ID");
        String OrderStatus = i.getExtras().getString("ORDER_STATUS");

        TxtViewGasBrand.setText(GasBrand);
        TxtViewGasSize.setText(GasSize);
        TxtViewGasType.setText(GasType);
        TxtViewnumberOfcylinders.setText(NumberOfCylinders);
        TxtVieworderId.setText(OrderId);
        TxtViewOrderStatus.setText(OrderStatus);

    }
}
