package com.socify.app.notifications;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.socify.app.fcm.AccessToken;

// Fix for new version of Firebase Messaging: https://stackoverflow.com/a/72664319

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  @Override
  public void onNewToken(@NonNull String token) {
    super.onNewToken(token);

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    if (firebaseUser != null) {
      AccessToken accessToken = new AccessToken();

      final String token1 = accessToken.getAccessToken();
      updateToken(token1);
    }
  }

  private void updateToken(String refreshToken) {
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
    Token token = new Token(refreshToken);
    reference.child(firebaseUser.getUid()).setValue(token);
  }
}
