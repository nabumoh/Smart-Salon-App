package com.nadeem.fadesalon;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
// class handles the splash screen.
public class SplashTiming extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //function for Timing Splash
        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    sleep(2000);
                } catch (Exception e) {
                } finally {
                    Intent intent = new Intent(SplashTiming.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        };
        thread.start();
    }
}
