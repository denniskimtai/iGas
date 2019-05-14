package com.codegreed_devs.i_gas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderHistoryAdapter extends BaseAdapter {
    Context c;
    ArrayList<com.codegreed_devs.i_gas.OrderHistoryModel> orderHistoryModelArrayList;

    //Constructor
    public OrderHistoryAdapter(Context c, ArrayList<com.codegreed_devs.i_gas.OrderHistoryModel> orderHistoryModelArrayList){
        this.c = c;
        this.orderHistoryModelArrayList = orderHistoryModelArrayList;
    }


    @Override
    public int getCount() {
        return orderHistoryModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderHistoryModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.orderhistorymodel, parent,false);
        }

        TextView gasSize = convertView.findViewById(R.id.gasSize);
        TextView orderStatus = convertView.findViewById(R.id.orderStatus);

        final com.codegreed_devs.i_gas.OrderHistoryModel o = (com.codegreed_devs.i_gas.OrderHistoryModel) this.getItem(position);

        //Set details
        gasSize.setText(o.getGasSize());
        orderStatus.setText(o.getOrderStatus());

        //On click listener
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open order history details activity
                openOrderHistoryDetailsActivity(o.getGasBrand(), o.getGasSize(), o.getGasType(), o.getNumberOfCylinders(), o.getOrderId(), o.getOrderStatus());

            }
        });

        return convertView;
    }

    //Open order history details activity
    private void openOrderHistoryDetailsActivity(String...details) {
        Intent i=new Intent(c, com.codegreed_devs.i_gas.OrderHistoryDetails.class);
        i.putExtra("GAS_BRAND",details[0]);
        i.putExtra("GAS_SIZE",details[1]);
        i.putExtra("GAS_TYPE",details[2]);
        i.putExtra("NUMBER_OF_CYLINDERS",details[3]);
        i.putExtra("ORDER_ID",details[4]);
        i.putExtra("ORDER_STATUS",details[5]);

        c.startActivity(i);
    }
}
