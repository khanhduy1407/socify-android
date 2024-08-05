package com.socify.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.models.ChatList;
import com.socify.app.ui.adapters.MessageAdapter;
import com.socify.app.models.Chat;
import com.socify.app.models.User;
import com.socify.app.utils.SocifyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

  CircleImageView profile_image;
  TextView fullname;

  TextView txt_user_deleted;

  RelativeLayout bottomLayout;
  EditText text_send;
  ImageButton btn_send;

  MessageAdapter messageAdapter;
  List<Chat> mChats;

  RecyclerView recyclerView;

  FirebaseUser fUser;
  DatabaseReference reference;

  Intent intent;
  String userId;

  ValueEventListener seenListener;

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
        startActivity(new Intent(MessageActivity.this, MainChatActivity.class)
          .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
      }
    });

    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
    linearLayoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(linearLayoutManager);

    profile_image = findViewById(R.id.profile_image);
    fullname = findViewById(R.id.fullname);
    txt_user_deleted = findViewById(R.id.txt_user_deleted);
    bottomLayout = findViewById(R.id.bottom);
    text_send = findViewById(R.id.text_send);
    btn_send = findViewById(R.id.btn_send);

    intent = getIntent();
    userId = intent.getStringExtra(SocifyUtils.EXTRA_USER_ID);

    fUser = FirebaseAuth.getInstance().getCurrentUser();
    reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(userId);

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

        readMessages(fUser.getUid(), userId, user.getImageUrl());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
        if (user.isDeleted()) {
          txt_user_deleted.setVisibility(View.VISIBLE);
          bottomLayout.setVisibility(View.GONE);

          params.addRule(RelativeLayout.ABOVE, R.id.txt_user_deleted);
        } else {
          txt_user_deleted.setVisibility(View.GONE);
          bottomLayout.setVisibility(View.VISIBLE);

          params.addRule(RelativeLayout.ABOVE, R.id.bottom);
        }
        recyclerView.setLayoutParams(params);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });

    seenMessage(userId);
  }

  private void seenMessage(String userId) {
    reference = FirebaseDatabase.getInstance().getReference(Chat.CHATS_DB);
    seenListener = reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Chat chat = snapshot.getValue(Chat.class);
          if (chat.getReceiver().equals(fUser.getUid()) && chat.getSender().equals(userId)) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(SocifyUtils.EXTRA_SEEN, true);
            snapshot.getRef().updateChildren(hashMap);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void sendMessage(String sender, String receiver, String message) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    String messageId = reference.push().getKey();

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put(SocifyUtils.EXTRA_ID, messageId);
    hashMap.put(SocifyUtils.EXTRA_SENDER, sender);
    hashMap.put(SocifyUtils.EXTRA_RECEIVER, receiver);
    hashMap.put(SocifyUtils.EXTRA_MESSAGE, message);
    hashMap.put(SocifyUtils.EXTRA_SEEN, false);

    reference.child("Chats").child(messageId).setValue(hashMap);


    // add user to chat fragment
    final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference(ChatList.CHAT_LIST_DB)
      .child(fUser.getUid())
      .child(userId);

    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (!snapshot.exists()) {
          chatRef.child("id").setValue(userId);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });

    final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference(ChatList.CHAT_LIST_DB)
      .child(userId)
      .child(fUser.getUid());

    chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(!dataSnapshot.exists()){
          chatRefReceiver.child("id").setValue(fUser.getUid());
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void readMessages(String myId, String userId, String imageUrl) {
    mChats = new ArrayList<>();

    reference = FirebaseDatabase.getInstance().getReference(Chat.CHATS_DB);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mChats.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Chat chat = snapshot.getValue(Chat.class);
          if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
              chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
            mChats.add(chat);
          }
        }

        messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imageUrl);
        recyclerView.setAdapter(messageAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void status(String status) {
    reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(fUser.getUid());

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put(SocifyUtils.EXTRA_STATUS, status);

    reference.updateChildren(hashMap);
  }

  @Override
  protected void onResume() {
    super.onResume();
    status(SocifyUtils.STATUS_ONLINE);
  }

  @Override
  protected void onPause() {
    super.onPause();
    reference.removeEventListener(seenListener);
    status(SocifyUtils.STATUS_OFFLINE);
  }
}