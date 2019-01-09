package com.codegreed_devs.i_gas.DashBoard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.codegreed_devs.i_gas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Home extends Fragment {

    CardView cardViewBuyGas, cardViewRefillGas, cardViewGasEquipments, cardViewBulkPurchase;
    String GasBurner = "";
    String GasGrill = "";
    Map<String, String> gasEquipments = new HashMap<String, String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return layout file
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Set toolbar title
        getActivity().setTitle("Home");


        cardViewBuyGas = getActivity().findViewById(R.id.cardViewBuyGas);
        cardViewBuyGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderDetails.class);
                startActivity(intent);

            }
        });

        cardViewRefillGas = getActivity().findViewById(R.id.cardViewRefill);
        cardViewRefillGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderDetails.class);
                startActivity(intent);
            }
        });

        cardViewGasEquipments = getActivity().findViewById(R.id.cardViewGasEquipments);
        cardViewGasEquipments.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                gasEquipments.remove(GasBurner, GasGrill);

                View checkBoxView = View.inflate(getActivity(), R.layout.checkboxgasequipments, null);
                CheckBox checkBoxGasBurner = checkBoxView.findViewById(R.id.checkboxGasBurner);
                CheckBox checkBoxGasGrill = checkBoxView.findViewById(R.id.checkboxGasGrill);

                checkBoxGasBurner.setText("Gas Burner");
                checkBoxGasGrill.setText("Gas Grill");


                checkBoxGasBurner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked){
                            GasBurner = "Gas Burner: Yes";
                            gasEquipments.put("Gas Burner", GasBurner);
                        }
                        if (!isChecked){
                            GasBurner = "Gas Burner: No";
                            gasEquipments.put("Gas Grill", GasBurner);
                        }

                    }
                });

                checkBoxGasGrill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            GasGrill = "Gas Grill: Yes";
                            gasEquipments.put("Gas Grill", GasGrill);
                        }
                        if (!isChecked){
                            GasGrill = "Gas Grill: No";
                            gasEquipments.put("Gas Grill", GasGrill);
                        }

                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Gas Equipments You Need")
                        .setView(checkBoxView)
                        .setCancelable(false)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference gasEquipmentRef = FirebaseDatabase.getInstance().getReference("Order Details");
                                gasEquipmentRef.child(clientId).setValue(gasEquipments);



                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();


            }
        });

        cardViewBulkPurchase = getActivity().findViewById(R.id.cardViewBulkPurchase);
        cardViewBulkPurchase.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), OrderDetails.class);
                startActivity(intent);

            }
        });

    }


}