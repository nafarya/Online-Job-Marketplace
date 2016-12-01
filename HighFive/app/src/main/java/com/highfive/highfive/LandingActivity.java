package com.highfive.highfive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.highfive.highfive.fragments.HelpFragment;
import com.highfive.highfive.fragments.OrderListFragment;
import com.highfive.highfive.fragments.ProfileFragment;
import com.highfive.highfive.services.auth.Authenticator;

public class LandingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String TAG = "Landing";
    public static final int LOGIN_SCREEN_REQUEST_CODE = 11;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);



        if (Authenticator.isLoginNeeded()) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER)
                            .build(), LOGIN_SCREEN_REQUEST_CODE);
        }

        Fragment fragment = new OrderListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_profile_fragment) {
            fragment = new ProfileFragment();
        } else if (id == R.id.nav_order_list_fragment) {
            fragment = new OrderListFragment();
        } else if (id == R.id.nav_help_fragment) {
            fragment = new HelpFragment();
        } else {
            System.exit(0);
        }



        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
