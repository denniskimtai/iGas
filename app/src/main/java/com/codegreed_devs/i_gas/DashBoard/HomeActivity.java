package com.codegreed_devs.i_gas.DashBoard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codegreed_devs.i_gas.R;
import com.codegreed_devs.i_gas.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Display home screen as the first screen when its loaded
        displaySelectedScreen(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //Call method when each item is clicked by passing itemId of item clicked
        displaySelectedScreen(item.getItemId());

        return true;
    }

    private void displaySelectedScreen(int ItemId) {
        //Fragment object
        Fragment fragment = null;

        //Initialize clicked fragment objects
        switch (ItemId) {
            case R.id.nav_home:
                fragment = new Home();
                break;

            case R.id.nav_profile:
                fragment = new Profile();
                break;

            case R.id.nav_orderhistory:
                fragment = new OrderHistory();
                break;

            case R.id.nav_aboutus:
                Intent aboutUs = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1F1x0oG0BgIVO1gROhI3vMpdEnBz0syu2/view?usp=sharing"));
                startActivity(aboutUs);
                break;

            case R.id.nav_share:
                Intent navShare = new Intent(Intent.ACTION_SEND);
                navShare.setType("text/plain");
                String shareMessage = "Google PlayStore link";
                navShare.putExtra(Intent.EXTRA_SUBJECT, "Follow this link to download iGas app");
                navShare.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(navShare, "Share Via"));
                break;

            case R.id.nav_rateus:
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                startActivity(rateIntent);
                break;

            case R.id.nav_faqs:
                Intent faqs = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1vqpfn1_vGo93JH8mv7b5GNQF_LYPtHJR/view?usp=sharing"));
                startActivity(faqs);
                break;

            case R.id.nav_terms:
                Intent terms = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1vqpfn1_vGo93JH8mv7b5GNQF_LYPtHJR/view?usp=sharing"));
                startActivity(terms);
                break;

            case R.id.nav_privacy_policy:
                Intent privacy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1vqpfn1_vGo93JH8mv7b5GNQF_LYPtHJR/view?usp=sharing"));
                startActivity(privacy);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent  = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        //Replace the fragments
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.contentFrame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
