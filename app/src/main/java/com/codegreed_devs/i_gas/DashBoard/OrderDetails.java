package com.codegreed_devs.i_gas.DashBoard;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.i_gas.R;
import com.google.firebase.database.FirebaseDatabase;

public class OrderDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView mnumberOfCylinders, mGasBrand;
    Button orderNow;
    String gasBrand = "";
    String gasSize = "";
    String gasType = "";
    String numberOfCylinders = "";

    private Spinner spinner;
    private static final String[] spinnerItems = {"1 Cylinder", "2 Cylinders", "3 Cylinders", "4 Cylinders", "5 or more Cylinders"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderDetails.this,R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mnumberOfCylinders = findViewById(R.id.numberOfCylinders);
        mGasBrand = findViewById(R.id.gasBrand);

        final String key = FirebaseDatabase.getInstance().getReference("Order Details").push().getKey();

        orderNow = findViewById(R.id.orderNow);
        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                gasBrand = mGasBrand.getText().toString().trim();

                //MOVED CODE TO POST CLIENT ORDER DETAILS TO MAPS ACTIVITY
                if (validate())
                {

                    //Alert dialog message
                    String message = "Gas Size : " + gasSize + "\n";
                    message += "Gas Type : " + gasType + "\n";
                    message += "Number of Cylinders : " + numberOfCylinders + "\n";
                    message += "Gas Brand : " + gasBrand + "\n";

                    //show order details preview
                    final AlertDialog.Builder alert = new AlertDialog.Builder(OrderDetails.this);
                    alert.setTitle("Please Confirm Order!");
                    alert.setMessage(message);
                    alert.setCancelable(false);
                    alert.setPositiveButton("Confirm order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //go to FindVendorsActivity
                            Intent intent = new Intent(OrderDetails.this, FindVendorsActivity.class);
                            intent.putExtra("Unique_Key", key);
                            intent.putExtra("gasSize", gasSize);
                            intent.putExtra("gasType", gasType);
                            intent.putExtra("numberOfCylinders", numberOfCylinders);
                            intent.putExtra("GasBrand", gasBrand);
                            startActivity(intent);
                            finish();

                        }
                    });
                    alert.setNegativeButton("Edit Order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();

                }

            }
        });
    }

    //Spinner selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                numberOfCylinders = "1 Cylinder";
                break;

            case 1:
                numberOfCylinders = "2 Cylinders";
                break;

            case 2:
                numberOfCylinders = "3 Cylinders";
                break;

            case 3:
                numberOfCylinders = "4 Cylinders";
                break;

            case 4:
                numberOfCylinders = "5 or more Cylinders";
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void gasSize(View view){
        //Check if radiobutton is checked
        boolean isChecked = ((RadioButton) view ).isChecked();
        //check which radio button is checked
        switch (view.getId()) {
            case R.id.gas3kg:
                if (isChecked) {
                    gasSize = "3kg gas cylinder";
                }
                break;

            case R.id.gas6kg:
                if (isChecked) {
                    gasSize = "6kg gas cylinder";
                }
                break;

            case R.id.gas13kg:
                if (isChecked) {
                    gasSize = "13kg gas cylinder";
                }
                break;

            case R.id.gas225kg:
                if (isChecked) {
                    gasSize = "22.5kg gas cylinder";
                }
                break;

            case R.id.gas50kg:
                if (isChecked) {
                    gasSize = "50kg gas cylinder";
                }
                break;
        }
    }

    public void gasType(View view) {
        //Check if radiobutton is checked
        boolean isChecked = ((RadioButton) view ).isChecked();
        //check with radio button is checked
        switch (view.getId()) {
            case R.id.completeGas:
                if (isChecked)
                    gasType = "Complete Gas";
                break;

            case R.id.cylinderOnly:
                if (isChecked)
                    gasType = "Cylinder Only";
                break;

        }

    }

    private boolean validate() {

        if (TextUtils.isEmpty(gasSize))
        {
            Toast.makeText(this, "Choose gas size", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(gasType))
        {
            Toast.makeText(this, "Choose gas type", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(numberOfCylinders))
        {
            Toast.makeText(this, "Choose number of cylinders", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(gasBrand))
        {
            Toast.makeText(this, "Enter gas brand", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

}
