package com.socify.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.models.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

  CircleImageView profile_image;
  TextView fullname;

  EditText text_send;
  ImageButton btn_send;

  FirebaseUser fUser;
  DatabaseReference reference;

  Intent intent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    profile_image = findViewById(R.id.profile_image);
    fullname = findViewById(R.id.fullname);
    text_send = findViewById(R.id.text_send);
    btn_send = findViewById(R.id.btn_send);

    intent = getIntent();
    final String userId = intent.getStringExtra("userId");

    fUser = FirebaseAuth.getInstance().getCurrentUser();
    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

    btn_send.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String msg = text_send.getText().toString();
        if (!msg.equals("")) {
          sendMessage(fUser.getUid(), userId, msg);
        } else {
          // do nothing
        }
        text_send.setText("");
      }
    });

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        fullname.setText(user.getFullname());
        Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void sendMessage(String sender, String receiver, String message) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("sender", sender);
    hashMap.put("receiver", receiver);
    hashMap.put("message", message);

    reference.child("Chats").push().setValue(hashMap);
  }
}