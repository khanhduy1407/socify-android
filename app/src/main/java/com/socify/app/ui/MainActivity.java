package com.socify.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.socify.app.R;
import com.socify.app.helpers.LocaleHelper;
import com.socify.app.ui.fragments.HomeFragment;
import com.socify.app.ui.fragments.NotificationsFragment;
import com.socify.app.ui.fragments.ProfileFragment;
import com.socify.app.ui.fragments.SearchFragment;
import com.socify.app.utils.SocifyUtils;

public class MainActivity extends AppCompatActivity {

  BottomNavigationView bottomNavigationView;
  Fragment selectedFragment = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    LocaleHelper.applyLocale(MainActivity.this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    bottomNavigationView = findViewById(R.id.bottom_navigation);

    bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    Bundle intent = getIntent().getExtras();
    if (intent != null) {
      String publisher = intent.getString(SocifyUtils.EXTRA_PUBLISHER_ID);

      SharedPreferences.Editor editor = getSharedPreferences(SocifyUtils.PREFS, MODE_PRIVATE).edit();
      editor.putString(SocifyUtils.EXTRA_PROFILE_ID, publisher);
      editor.apply();

      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
    } else {
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
  }

  private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
    new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
          case R.id.nav_home:
            selectedFragment = new HomeFragment();
            break;
          case R.id.nav_search:
            selectedFragment = new SearchFragment();
            break;
          case R.id.nav_add_post:
            selectedFragment = null;
            startActivity(new Intent(MainActivity.this, PostActivity.class));
            break;
          case R.id.nav_notifications:
            selectedFragment = new NotificationsFragment();
            break;
          case R.id.nav_profile:
            SharedPreferences.Editor editor = getSharedPreferences(SocifyUtils.PREFS, MODE_PRIVATE).edit();
            editor.putString(SocifyUtils.EXTRA_PROFILE_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
            editor.apply();
            selectedFragment = new ProfileFragment();
            break;
        }

        if (selectedFragment != null) {
          getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }

        return true;
      }
    };
}