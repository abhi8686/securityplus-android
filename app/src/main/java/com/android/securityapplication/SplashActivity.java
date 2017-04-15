package com.android.securityapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(preferences.getString("key",null)==null)
        {
            preferences.edit().putBoolean("firstrun",true).apply();
            startActivity(new Intent(this,LandingActivity.class));
            finish();
        }
        else {
//            if(preferences.getBoolean("active", false) == true)
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            else
//                startActivity(new Intent(getApplicationContext(), PaymentActivity.class));
            finish();

        }
    }
}
