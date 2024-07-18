package com.socify.app.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.EditProfileActivity;
import com.socify.app.ui.adapters.MyPhotoAdapter;
import com.socify.app.ui.models.Post;
import com.socify.app.ui.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

  ImageView image_profile, options;
  TextView posts, followers, following, username, fullname, bio;
  Button edit_profile;
  ImageButton my_photos, saved_photos;

  private List<String> mySaves;

  RecyclerView recyclerView;
  MyPhotoAdapter myPhotoAdapter;
  List<Post> postList;

  RecyclerView recyclerView_saves;
  MyPhotoAdapter myPhotoAdapter_saves;
  List<Post> postList_saves;

  FirebaseUser firebaseUser;
  String profileId;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
    profileId = prefs.getString("profileId", "none");

    image_profile = view.findViewById(R.id.image_profile);
    options = view.findViewById(R.id.options);
    posts = view.findViewById(R.id.posts);
    followers = view.findViewById(R.id.followers);
    following = view.findViewById(R.id.following);
    username = view.findViewById(R.id.username);
    fullname = view.findViewById(R.id.fullname);
    bio = view.findViewById(R.id.bio);
    edit_profile = view.findViewById(R.id.edit_profile);
    my_photos = view.findViewById(R.id.my_photos);
    saved_photos = view.findViewById(R.id.saved_photos);

    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
    recyclerView.setLayoutManager(linearLayoutManager);
    postList = new ArrayList<>();
    myPhotoAdapter = new MyPhotoAdapter(getContext(), postList);
    recyclerView.setAdapter(myPhotoAdapter);

    recyclerView_saves = view.findViewById(R.id.recycler_view_saves);
    recyclerView_saves.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
    recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
    postList_saves = new ArrayList<>();
    myPhotoAdapter_saves = new MyPhotoAdapter(getContext(), postList_saves);
    recyclerView_saves.setAdapter(myPhotoAdapter_saves);

    recyclerView.setVisibility(View.VISIBLE);
    recyclerView_saves.setVisibility(View.GONE);

    userInfo();
    getFollowers();
    getNrPosts();
    myPhotos();
    mySaves();

    if (profileId.equals(firebaseUser.getUid())) {
      edit_profile.setText(getContext().getResources().getString(R.string.edit_profile));
    } else {
      checkFollow();
      saved_photos.setVisibility(View.GONE);
    }

    edit_profile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String btn = edit_profile.getText().toString();

        if (btn.equals(getContext().getResources().getString(R.string.edit_profile))) {
          startActivity(new Intent(getContext(), EditProfileActivity.class));
        } else if (btn.equals(getContext().getResources().getString(R.string.follow))) {
          // nút theo dõi
          FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
            .child("following").child(profileId).setValue(true);
          FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
            .child("followers").child(firebaseUser.getUid()).setValue(true);
          addNotification();
        } else if (btn.equals(getContext().getResources().getString(R.string.following))) {
          // nút đang theo dõi
          FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
            .child("following").child(profileId).removeValue();
          FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
            .child("followers").child(firebaseUser.getUid()).removeValue();
        }
      }
    });

    my_photos.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);
      }
    });

    saved_photos.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        recyclerView.setVisibility(View.GONE);
        recyclerView_saves.setVisibility(View.VISIBLE);
      }
    });

    return view;
  }

  private void userInfo() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (getContext() == null) {
          return;
        }

        User user = snapshot.getValue(User.class);

        Glide.with(getContext()).load(user.getImageUrl()).into(image_profile);
        username.setText("@"+user.getUsername());
        fullname.setText(user.getFullname());
        bio.setText(user.getBio());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void checkFollow() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
      .child("Follow").child(firebaseUser.getUid()).child("following");

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.child(profileId).exists()) {
          edit_profile.setText(getContext().getResources().getString(R.string.following));
        } else {
          edit_profile.setText(getContext().getResources().getString(R.string.follow));
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void getFollowers() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
      .child("Follow").child(profileId).child("followers");

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        followers.setText(""+dataSnapshot.getChildrenCount());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });


    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
      .child("Follow").child(profileId).child("following");

    reference1.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        following.setText(""+dataSnapshot.getChildrenCount());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void getNrPosts() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        int i = 0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Post post = snapshot.getValue(Post.class);
          if (post.getPublisher().equals(profileId)) {
            i++;
          }
        }

        posts.setText(""+i);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void myPhotos() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        postList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Post post = snapshot.getValue(Post.class);
          if (post.getPublisher().equals(profileId)) {
            postList.add(post);
          }
        }
        Collections.reverse(postList);
        myPhotoAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void mySaves() {
    mySaves = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
      .child(firebaseUser.getUid());

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          mySaves.add(snapshot.getKey());
        }

        readSaves();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void readSaves() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        postList_saves.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Post post = snapshot.getValue(Post.class);

          for (String id : mySaves) {
            if (post.getPostId().equals(id)) {
              postList_saves.add(post);
            }
          }
        }
        myPhotoAdapter_saves.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void addNotification() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("userId", firebaseUser.getUid());
    hashMap.put("text", getContext().getResources().getString(R.string.started_following_you));
    hashMap.put("postId", "");
    hashMap.put("post", false);

    reference.push().setValue(hashMap);
  }
}