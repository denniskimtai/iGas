package com.codegreed_devs.i_gas.DashBoard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codegreed_devs.i_gas.R;

import java.util.ArrayList;

public class VendorsListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<VendorModel> vendors;

    public VendorsListAdapter(Context context, ArrayList<VendorModel> vendors) {
        this.context = context;
        this.vendors = vendors;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return vendors.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.vendor_list_item, viewGroup, false);
        TextView name = (TextView) view.findViewById(R.id.vendor_name);
        TextView address = (TextView) view.findViewById(R.id.vendor_address);
        TextView vendorDistance = view.findViewById(R.id.vendor_distance);
        name.setText(vendors.get(i).getVendorName());
        address.setText(vendors.get(i).getVendorAddress());
        vendorDistance.setText(vendors.get(i).getVendorDistance() + " km away");
        return view;
    }

}
