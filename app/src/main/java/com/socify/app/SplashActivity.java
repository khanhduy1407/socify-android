package com.socify.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

  FirebaseUser firebaseUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
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
