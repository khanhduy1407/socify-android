package com.socify.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.adapters.UserAdapter;
import com.socify.app.ui.models.User;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

  String id;
  String title;
  String tag;

  List<String> idList;

  RecyclerView recyclerView;
  UserAdapter userAdapter;
  List<User> userList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_followers);

    Intent intent = getIntent();
    id = intent.getStringExtra("id");
    title = intent.getStringExtra("title");
    tag = intent.getStringExtra("tag");

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(title);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    userList = new ArrayList<>();
    userAdapter = new UserAdapter(this, userList, false);
    recyclerView.setAdapter(userAdapter);

    idList = new ArrayList<>();

    switch (tag) {
      case "likes":
        getLikes();
        break;
      case "following":
        getFollowing();
        break;
      case "followers":
        getFollowers();
        break;
      case "story_views":
        getStoryViews();
        break;
    }
  }

  private void getLikes() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
      .child(id);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        idList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          idList.add(snapshot.getKey());
        }
        showUsers();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void getFollowing() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
      .child(id).child("following");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        idList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          idList.add(snapshot.getKey());
        }
        showUsers();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void getFollowers() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
      .child(id).child("followers");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        idList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          idList.add(snapshot.getKey());
        }
        showUsers();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void getStoryViews() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stories")
      .child(id).child(getIntent().getStringExtra("storyId")).child("views");
    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        idList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          idList.add(snapshot.getKey());
        }
        showUsers();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void showUsers() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        userList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          User user = snapshot.getValue(User.class);
          for (String id : idList) {
            if (user.getId().equals(id)) {
              userList.add(user);
            }
          }
        }
        userAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}