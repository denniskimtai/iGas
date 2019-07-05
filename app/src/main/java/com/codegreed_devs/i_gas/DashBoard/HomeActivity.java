package com.codegreed_devs.i_gas.DashBoard;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.i_gas.BuildConfig;
import com.codegreed_devs.i_gas.R;
import com.codegreed_devs.i_gas.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView userName, userEmail;
    private ImageView userProfilePhoto;
    private Uri photoUrl;
    FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        //initialize shared preference
        sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID + "SHARED_PREF", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String mUserName = sharedPreferences.getString("ClientNames", "");
        String mUserEmail = sharedPreferences.getString("ClientEmail", "");
        photoUrl = mAuth.getCurrentUser().getPhotoUrl();

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        userProfilePhoto = navigationView.getHeaderView(0).findViewById(R.id.userProfilePhoto);

        userName.setText("Welcome " + mUserName);
        userEmail.setText(mUserEmail);

        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.userimg)
                .resize(100, 100)
                .centerCrop()
                .into(userProfilePhoto);

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

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    //Show alternate home screen for android versions below lollipop
                    fragment = new HomeAlternate();
                } else {

                    //Show cardview home screen
                    fragment = new Home();
                }

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
                String shareMessage = "https://play.google.com/store/apps/details?id=com.codegreed_devs.i_gas";
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

            case R.id.nav_contactus:
                alertDialog();
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                editor.clear();
                editor.apply();
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void alertDialog() {

        final  CharSequence[] contactUsOptions = {"Call", "Sms", "Email"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How do you wish to contact us?");
        builder.setItems(contactUsOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case 0:
                        //Place a call
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                        phoneIntent.setData(Uri.parse("tel:+254713989734"));
                        startActivity(phoneIntent);

                        break;

                    case 1:

                        //Send an sms
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setData(Uri.parse("sms:" + "+254726375757"));
                        startActivity(sendIntent);

                        break;

                    case 2:

                        //Send email
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:igaskenya@gmail.com"));
                        try {
                            startActivity(emailIntent);
                        } catch (ActivityNotFoundException e) {
                            //When there is no email app installed
                            Toast.makeText(HomeActivity.this, "No email app installed! Please install an email app and try again", Toast.LENGTH_SHORT).show();
                        }

                        break;

                }

                dialog.dismiss();
            }
        }).show();


    }
}
