package com.example.instagram.view.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.instagram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class Home_Activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);

        bottomNavigationView=findViewById(R.id.bottomNavigation);
        NavController navController = Navigation.findNavController(this , R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView , navController);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : Objects.requireNonNull(getSupportFragmentManager()
                .getPrimaryNavigationFragment()).getChildFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
