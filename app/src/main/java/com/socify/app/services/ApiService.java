package com.socify.app.services;

import retrofit2.http.Header;

public interface ApiService {
  @Header(
    {
      "Content-Type: application/json",
      "Authorization:key="
    }
  )
}
