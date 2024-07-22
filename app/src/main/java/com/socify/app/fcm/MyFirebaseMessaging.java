package com.socify.app.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.socify.app.ui.MessageActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {

  @Override
  public void onMessageReceived(@NonNull RemoteMessage message) {
    super.onMessageReceived(message);

    String sent = message.getData().get("sent");

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    if (firebaseUser != null && sent.equals(firebaseUser.getUid())) {
      sendNotification(message);
    }
  }

  private void sendNotification(RemoteMessage message) {
    String user = message.getData().get("user");
    String icon = message.getData().get("icon");
    String title = message.getData().get("title");
    String body = message.getData().get("body");

    RemoteMessage.Notification notification = message.getNotification();
    int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
    Intent intent = new Intent(this, MessageActivity.class);
    Bundle bundle = new Bundle();
    bundle.putString("userId", user);
    intent.putExtras(bundle);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

    Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
      .setSmallIcon(Integer.parseInt(icon))
      .setContentTitle(title)
      .setContentText(body)
      .setAutoCancel(true)
      .setSound(defaultSound)
      .setContentIntent(pendingIntent);
    NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    int i = 0;
    if (j > 0) {
      i = j;
    }

    noti.notify(i, builder.build());
  }
}
