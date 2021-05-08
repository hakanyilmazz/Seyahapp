package com.hakanyilmazz.seyahapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hakanyilmazz.seyahapp.R;

public class SplashActivity extends AppCompatActivity {

    private static boolean isAppStarted = false;

    private final long millisInFuture = 2000;
    private final long countDownInterval = 1000;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        if (isAppStarted) {
            startApplication();
        } else {
            isAppStarted = true;
            startApplicationWithTimer();
        }
    }

    private void startApplicationWithTimer() {
        final CountDownTimer timer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                startApplication();
            }
        };

        timer.start();
    }

    private void startApplication() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Intent intentToActivity = null;

        if (currentUser != null) {
            intentToActivity = new Intent(SplashActivity.this,
                    PlacesActivity.class);
        } else {
            intentToActivity = new Intent(SplashActivity.this,
                    LoginActivity.class);
        }

        startActivity(intentToActivity);
        finish();
    }

}