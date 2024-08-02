package com.socify.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.socify.app.R;

import java.util.Locale;

public class LocaleHelper {

  public static void setLocale(Context context, String languageCode) {
    Locale locale = new Locale(languageCode);
    Locale.setDefault(locale);

    Resources res = context.getResources();
    Configuration config = new Configuration(res.getConfiguration());
    config.setLocale(locale);
    res.updateConfiguration(config, res.getDisplayMetrics());
  }

  public static void applyLocale(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String languageCode = prefs.getString(context.getString(R.string.pref_key_language), "en");
    setLocale(context, languageCode);
  }
}
