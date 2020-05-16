package com.example.instagram.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.view.auth.register.RegisterActivity;
import com.example.instagram.view.main.Home_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user == null)
                {
                   startActivity(new Intent( SplashActivity.this , RegisterActivity.class));
                }else{
                    startActivity(new Intent( SplashActivity.this , Home_Activity.class));

                }
            }
        }, 2000);
    }
}
