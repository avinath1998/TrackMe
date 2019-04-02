package com.productions.crackdown.bookingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.productions.crackdown.bookingapp.Views.HomeViewFragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentLayout;
    private static final String ROOT_TAG_STACK = "root_fragment";
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        fragmentLayout = findViewById(R.id.main_fragment_placeholder);
        startHomeFragment();
    }

    /*
        Starting the home fragment
     */
    private void startHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_placeholder, new HomeViewFragment()).addToBackStack(ROOT_TAG_STACK).commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount()>1)
            fragmentManager.popBackStack();
    }
}
