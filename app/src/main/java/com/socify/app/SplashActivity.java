package com.socify.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.socify.app.helpers.LocaleHelper;
import com.socify.app.ui.LoginActivity;
import com.socify.app.ui.MainActivity;
import com.socify.app.ui.MainChatActivity;

public class SplashActivity extends AppCompatActivity {

  FirebaseUser firebaseUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    LocaleHelper.applyLocale(SplashActivity.this);

    super.onCreate(savedInstanceState);

    Intent mainIntent;

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    // redirect to home page if user is not null
    if (firebaseUser != null) {
      mainIntent = new Intent(this, MainActivity.class);
    } else {
      mainIntent = new Intent(this, LoginActivity.class);
    }

    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(mainIntent);
    finish();
  }
}
