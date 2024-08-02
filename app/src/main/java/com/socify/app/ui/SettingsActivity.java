package com.socify.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.socify.app.R;
import com.socify.app.helpers.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);

    if (savedInstanceState == null) {
      getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.settings, new SettingsFragment())
        .commit();
    }

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    overridePendingTransition(0, 0);
  }

  @Override
  public void finish() {
    setResult(RESULT_OK);
    super.finish();
  }

  public static class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
      setPreferencesFromResource(R.xml.pref_settings_general, rootKey);

      sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
          if (key.equals(getActivity().getResources().getString(R.string.pref_key_language))) {
            if (getActivity() != null) {
              LocaleHelper.applyLocale(getActivity());
              getActivity().recreate();
            }
          }
        }
      };

      SharedPreferences preferenceManager = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
      preferenceManager.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      SharedPreferences preferenceManager = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
      preferenceManager.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
  }
}
