package com.socify.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.socify.app.R;
import com.socify.app.SplashActivity;
import com.socify.app.helpers.LocaleHelper;

public class OptionsActivity extends AppCompatActivity {

  private static final int REQUEST_CODE_SETTINGS = 2002;

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

    settings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(OptionsActivity.this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_SETTINGS && resultCode == RESULT_OK) {
      LocaleHelper.applyLocale(OptionsActivity.this);
      recreate();
    }
  }
}