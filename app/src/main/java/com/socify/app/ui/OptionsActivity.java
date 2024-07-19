package com.socify.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.socify.app.R;
import com.socify.app.SplashActivity;

public class OptionsActivity extends AppCompatActivity {

  TextView settings, logout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_options);

    settings = findViewById(R.id.settings);
    logout = findViewById(R.id.logout);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(getResources().getString(R.string.options));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    logout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(OptionsActivity.this, SplashActivity.class)
          .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
      }
    });
  }
}