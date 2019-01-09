package com.codegreed_devs.i_gas.DashBoard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codegreed_devs.i_gas.R;

public class AboutUs extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Return layout file
        return inflater.inflate(R.layout.fragment_aboutus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Set title of toolbar
        getActivity().setTitle("About Us");

        Intent drive = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1F1x0oG0BgIVO1gROhI3vMpdEnBz0syu2/view?usp=sharing"));
        startActivity(drive);
    }
}
