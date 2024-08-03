package com.socify.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocifyPattern {

  public static final String USERNAME = "^[a-zA-Z0-9_]+$";
  public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

  private static Pattern pattern(String regex) {
    return Pattern.compile(regex);
  }

  public static boolean isValid(String string, final String regex) {
    if (string == null) {
      return false;
    }
    Matcher matcher = pattern(regex).matcher(string);
    return matcher.matches();
  }

}
