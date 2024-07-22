package com.socify.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.MainActivity;
import com.socify.app.ui.MainChatActivity;
import com.socify.app.ui.PostActivity;
import com.socify.app.ui.adapters.PostAdapter;
import com.socify.app.ui.adapters.StoryAdapter;
import com.socify.app.ui.models.Post;
import com.socify.app.ui.models.Story;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

  ImageView add_post, open_chat;

  private RecyclerView recyclerView;
  private PostAdapter postAdapter;
  private List<Post> postList;

  private RecyclerView recyclerView_story;
  private StoryAdapter storyAdapter;
  private List<Story> storyList;

  private List<String> followingList;

  ProgressBar progressBar;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);

    add_post = view.findViewById(R.id.add_post);
    open_chat = view.findViewById(R.id.open_chat);

    add_post.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getContext(), PostActivity.class));
      }
    });

    open_chat.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getContext(), MainChatActivity.class));
      }
    });

    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    linearLayoutManager.setReverseLayout(true);
    linearLayoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(linearLayoutManager);
    postList = new ArrayList<>();
    postAdapter = new PostAdapter(getContext(), postList);
    recyclerView.setAdapter(postAdapter);

    recyclerView_story = view.findViewById(R.id.recycler_view_story);
    recyclerView_story.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
      LinearLayoutManager.HORIZONTAL, false);
    recyclerView_story.setLayoutManager(linearLayoutManager1);
    storyList = new ArrayList<>();
    storyAdapter = new StoryAdapter(getContext(), storyList);
    recyclerView_story.setAdapter(storyAdapter);

    progressBar = view.findViewById(R.id.progress_circular);

    checkFollowing();

    return view;
  }

  private void checkFollowing() {
    followingList = new ArrayList<>();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("following");

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        followingList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          followingList.add(snapshot.getKey());
        }

        readPosts();
        readStories();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void readPosts() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        postList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Post post = snapshot.getValue(Post.class);
          for (String id : followingList) {
            if (post.getPublisher().equals(id)) {
              postList.add(post);
            }
          }
        }

        postAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void readStories() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stories");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        long time_current = System.currentTimeMillis();
        storyList.clear();
        storyList.add(new Story("", FirebaseAuth.getInstance().getCurrentUser().getUid(),
          "", 0, 0));
        for (String id : followingList) {
          int countStory = 0;
          Story story = null;
          for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
            story = snapshot.getValue(Story.class);
            if (time_current > story.getTimeStart() && time_current < story.getTimeEnd()) {
              countStory++;
            }
          }
          if (countStory > 0) {
            storyList.add(story);
          }
        }
        storyAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}