package com.myapp.mytest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.myapp.mytest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FabVisibilityListener {
    ActivityMainBinding binding;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FloatingActionButton fab;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fab = binding.fab;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        drawerLayout = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = binding.navigationDrawer;
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new reportsFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.reports)
                replaceFragment(new reportsFragment());
            else if (itemId == R.id.analytics)
                replaceFragment(new analyticsFragment());
            else if (itemId == R.id.accounts)
                replaceFragment(new accountsFragment());
            return true;
        });

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        });

        DB = new DBHelper(this);

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settings) {

        } else if (itemId == R.id.backup) {

        } else if (itemId == R.id.delete) {

        } else if (itemId == R.id.rate) {

        } else if (itemId == R.id.help) {

        } else if (itemId == R.id.feedback) {

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setFabVisibility(int visibility) {
        fab.setVisibility(visibility);
    }
}