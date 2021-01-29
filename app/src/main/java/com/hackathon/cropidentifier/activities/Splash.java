package com.hackathon.cropidentifier.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.hackathon.cropidentifier.R;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.Fade;
        setContentView(R.layout.activity_splash);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this,Login.class));
            }
        },1000);

    }

    @Override
    protected void onPause() {
        super.onPause();

        finishAffinity();
    }
}