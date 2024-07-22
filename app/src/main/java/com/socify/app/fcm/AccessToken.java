package com.socify.app.fcm;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {

  private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

  public String getAccessToken() {
    try {
      String jsonString = "{\n" +
        "  \"type\": \"service_account\",\n" +
        "  \"project_id\": \"socify-bef22\",\n" +
        "  \"private_key_id\": \"44c5e4da4d5e81037703d3a54792fc60da719f16\",\n" +
        "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDA6dKlHecvgNTE\\nkiDdlvfc1nKHRFiOLBkGdxkjpXCkigGG1A48LKIRt9AqE7/UNQZgk8zrzN2ld0An\\nl2kKpYn1Ten6/aIT8ZckBAAc+hOFb5p2IgKVwMdIesKmnWWxo51pIRGg+1k9x6nN\\nztT0E460MiCwRYlx3fGnuuglSLyqlbA3YSeRwTShyE1xjcqT4JmAHhmRx1XoNGMn\\nPwG5JrtHwRKGzAFB3wU33R4jJWbdeOMgL2TUUca8yh/W7B0xSgDdIp6hsIy/APJy\\nW0iuObm8ZTXYSSdT9N/o5WqtYlfl3JpRpEfuiwJktVtFjv7C10Mhtm0REajvI3zN\\n5NDeODj5AgMBAAECggEAJ+2dfDByw7nCEAeAxuxy0+IM5WNngtGswlJPQToCVQP9\\naeclJR5F0y3852CcETOCZOXigaGu9Ir4m9kyVBJMOhoMxjbN35ve4QT08wptlO4z\\nBaVnbJBwjtddx3eHb1uCfppqGCw93dQgGKlpGTWdET3Y1zMlTgyWclV0rBbgedvS\\njahAYzxqAOgRZtDcdA83p04qQlBowyO8b6HwI7XCU2hvjYVgbBLligRtDcVnCK0Q\\nY7IZzXKJC4r5yoTVgiryFb5Orfa1hscTdO7lbcjFpvFX0hFoXTaNVWBi2tjmVseW\\nQBof5a2eISLgseVNtOIliK4Gx2i1VTJGKK0lnnr/awKBgQDkC0loj6PYZByv2HUV\\nqZm+c49CSu/wfWKqfO7VKAgygxG+/UkWx3xqGhvDj2mEokUV1G2ZZZVEBtkiOeNC\\nXtt+ZekGRfWHDr9vrukWQ5N7bY8W10NLaBTa+9GqdHberKGxe8ekkkS8MxV1g7Rp\\nibMYjWdfDrUefDf4F8lDRYFmdwKBgQDYkAcjIPANOVGf08ReWN49YDl3+DH+Wh3o\\nzYXTBakip2YXV9vAUTLf3xa+D7D1xcYj7TRaQGNKI1ZNjwTo64FK8Drcb9KfcgVb\\n2PuTGTgL3cnZTIwovaXS+J0ctD1BWOZqWxZXegb1qKojoGR0xwBbVL59aqJH2T8K\\n7RjghWyIDwKBgFH5+LacxDJ6spFIB7b00+7RzdLv61cRS9GrvZNko6W81jlJBegp\\naAEgR5DCUhU63Ajf51I4YTMmHxIdqyNlxBOpAN6fcYbOd2TnhKTbP/4jwUddtLQ7\\nwxuBYLqcyUp81ldd/sObTRg2aOVopgc2V+pgkqNqPxOpZdlKe7AyITU7AoGBAMIE\\nNwC3z5hpU8FW3cSQ5XojLRlYGKIBZm5dQpmNprvPMV2UbuHbXTLIEP3x3N4zc5JY\\nPuDkco/HHNibWxC5fLoAgxtea1pBsp9Yw/pDS70F1IKDW8ZcMaXS6h7/MynKVZPP\\nSzLPtlB77QHB/n3Ik38/avG9iOnJrftB81mi71rtAoGBAJWegfVVH0Qjw+lyiBgG\\nBOjAieclb8y+kkdUQ+jhWg56VnMKBPqmq+DOqKArww0WeuuqvPmUoY06TAeVjamb\\n4yCzr37so30Oki4FTSrNq8wj9CkHsDMCK1e/DIUPjARA3/pgClrUwRJAndESqD81\\nK44+uOYsxkyVKqiedCN2ImZS\\n-----END PRIVATE KEY-----\\n\",\n" +
        "  \"client_email\": \"firebase-adminsdk-jydos@socify-bef22.iam.gserviceaccount.com\",\n" +
        "  \"client_id\": \"109443088995502134267\",\n" +
        "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
        "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
        "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
        "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-jydos%40socify-bef22.iam.gserviceaccount.com\",\n" +
        "  \"universe_domain\": \"googleapis.com\"\n" +
        "}\n";

      InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

      GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
        .createScoped(Lists.newArrayList(firebaseMessagingScope));

      googleCredentials.refresh();

      return googleCredentials.getAccessToken().getTokenValue();
    } catch (IOException e) {
      Log.e("error", "" + e.getMessage());
      return "";
    }
  }
}
