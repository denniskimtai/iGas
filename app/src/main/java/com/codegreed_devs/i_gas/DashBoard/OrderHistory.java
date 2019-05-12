package com.codegreed_devs.i_gas.DashBoard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.codegreed_devs.i_gas.OrderHistoryAdapter;
import com.codegreed_devs.i_gas.OrderHistoryModel;
import com.codegreed_devs.i_gas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderHistory extends Fragment {

    DatabaseReference db;
    OrderHistoryAdapter adapter;
    ListView listView;
    ArrayList<OrderHistoryModel> orderHistoryModelArrayList;
    ProgressDialog loadOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Return layout file
        return inflater.inflate(R.layout.fragment_orderhistory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Order History");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        listView = getActivity().findViewById(R.id.orderHistorylistView);

        //INITIALIZE FIREBASE DB
        db= FirebaseDatabase.getInstance().getReference().child("Order Details").child(userId);
        orderHistoryModelArrayList = new ArrayList<OrderHistoryModel>();

        //ADAPTER
        adapter = new OrderHistoryAdapter(getActivity(), orderHistoryModelArrayList);
        listView.setAdapter(adapter);

        loadOrders = new ProgressDialog(getActivity());

        //METHOD CALL
        getRecords();
    }

    private void getRecords() {

        loadOrders.setMessage("Getting Orders...");
        loadOrders.setCancelable(false);
        loadOrders.show();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderHistoryModelArrayList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    orderHistoryModelArrayList.add(new OrderHistoryModel(ds.child("orderId").getValue(String.class),
                            ds.child("gasBrand").getValue(String.class),
                            ds.child("gasSize").getValue(String.class),
                            ds.child("gasType").getValue(String.class),
                            ds.child("mnumberOfCylinders").getValue(String.class),
                            ds.child("orderStatus").getValue(String.class)));

                }
                loadOrders.dismiss();
                adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadOrders.dismiss();
                Toast.makeText(getActivity(), "Could not fetch your order history", Toast.LENGTH_SHORT).show();

            }
        });


    }
}
