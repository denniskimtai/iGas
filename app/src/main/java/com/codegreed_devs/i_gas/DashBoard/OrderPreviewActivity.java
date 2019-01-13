package com.codegreed_devs.i_gas.DashBoard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.codegreed_devs.i_gas.ClientMapsActivity;
import com.codegreed_devs.i_gas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderPreviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RadioButton gas3kg, gas13kg, gas16kg, completeGas, cylinderOnly;
    private TextView gasBrandText;

    private Spinner spinner;
    private static final String[] spinnerItems = {"1 Cylinder", "2 Cylinders", "3 Cylinders", "4 Cylinders", "5 or more Cylinders"};

    private Button confirmOrderButton;

    private String gasSize = "";
    private String gasTypeString = "";
    private String numberOfCylinders = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_preview);

        //spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderPreviewActivity.this,R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        gas3kg = findViewById(R.id.gas3kg);
        gas13kg = findViewById(R.id.gas13kg);
        gas16kg = findViewById(R.id.gas16kg);

        completeGas = findViewById(R.id.completeGas);
        cylinderOnly = findViewById(R.id.cylinderOnly);

        gasBrandText = findViewById(R.id.gasBrand);
         spinner = findViewById(R.id.spinner);

         confirmOrderButton = findViewById(R.id.confirmOrder);


        //get unique key of order
        Intent uniqueKeyIntent = getIntent();
        final String unique_key = uniqueKeyIntent.getExtras().getString("Unique_Key");

        //get current user id
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference orderPreviewRef = FirebaseDatabase.getInstance().getReference("Order Details").child(userId).child(unique_key);

        //get child values

        Intent GasBrandIntent = getIntent();
        String GasBrand = GasBrandIntent.getExtras().getString("GasBrand");

        Intent GasTypeIntent = getIntent();
        String GasType = GasTypeIntent.getExtras().getString("gasType");

        Intent NumberOfCylindersIntent = getIntent();
        String NumberOfCylinders = NumberOfCylindersIntent.getExtras().getString("numberOfCylinders");

        Intent GasSizeIntent = getIntent();
        String GasSize = GasSizeIntent.getExtras().getString("gasSize");



                    //set text of gas brand
                    gasBrandText.setText(GasBrand);

                    //set checked for gas type
                    switch (GasType) {
                        case "Cylinder Only":
                            cylinderOnly.setChecked(true);
                            gasTypeString = "Cylinder Only";
                            break;

                        case "Complete Gas":
                            completeGas.setChecked(true);
                            gasTypeString = "Complete Gas";
                            break;
                    }

                    //set for number of cylinders
                    switch (NumberOfCylinders) {
                        case "1 Cylinder":
                            spinner.setSelection(0);
                            break;

                        case "2 Cylinders":
                            spinner.setSelection(1);
                            break;

                        case "3 Cylinders":
                            spinner.setSelection(2);
                            break;

                        case "4 Cylinders":
                            spinner.setSelection(3);
                            break;

                        case "5 or more Cylinders":
                            spinner.setSelection(4);
                            break;
                    }

                    //display in activity for client to confirm order
                    switch (GasSize) {
                        case "3kg gas cylinder":
                            gas3kg.setChecked(true);
                            gasSize = "3kg gas cylinder";
                            break;

                        case "13kg gas cylinder":
                            gas13kg.setChecked(true);
                            gasSize = "13kg gas cylinder";
                            break;

                        case "16kg gas cylinder":
                            gas16kg.setChecked(true);
                            gasSize = "16kg gas cylinder";
                            break;
                    }


        //onclick listerner to listen for change and update data in db when clicked
        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get text of gasBrand
                String updateGasBrand = gasBrandText.getText().toString();

                Intent intent = new Intent(OrderPreviewActivity.this, ClientMapsActivity.class);
                intent.putExtra("Unique_Key", unique_key);
                intent.putExtra("gasSize", gasSize);
                intent.putExtra("gasType", gasTypeString);
                intent.putExtra("numberOfCylinders", numberOfCylinders);
                intent.putExtra("GasBrand", updateGasBrand);
                startActivity(intent);

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
        //check with radio button is checked
        switch (view.getId()) {
            case R.id.gas3kg:
                if (isChecked) {
                    gasSize = "3kg gas cylinder";
                }
                break;

            case R.id.gas13kg:
                if (isChecked) {
                    gasSize = "13kg gas cylinder";
                }
                break;

            case R.id.gas16kg:
                if (isChecked) {
                    gasSize = "16kg gas cylinder";
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
                    gasTypeString = "Complete Gas";
                break;

            case R.id.cylinderOnly:
                if (isChecked)
                    gasTypeString = "Cylinder Only";
                break;

        }

    }

}

