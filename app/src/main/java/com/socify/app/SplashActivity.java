package com.socify.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);

    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    startActivity(mainIntent);
    finish();
  }
}
