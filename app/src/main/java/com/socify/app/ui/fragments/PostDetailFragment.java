package com.socify.app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.adapters.PostAdapter;
import com.socify.app.models.Post;
import com.socify.app.utils.SocifyUtils;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {

  String postId;
  private RecyclerView recyclerView;
  private PostAdapter postAdapter;
  private List<Post> postList;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

    SharedPreferences preferences = getContext().getSharedPreferences(SocifyUtils.PREFS, Context.MODE_PRIVATE);
    postId = preferences.getString(SocifyUtils.EXTRA_POST_ID, "none");

    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(linearLayoutManager);

    postList = new ArrayList<>();
    postAdapter = new PostAdapter(getContext(), postList);
    recyclerView.setAdapter(postAdapter);

    readPost();

    return view;
  }

  private void readPost() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Post.POSTS_DB).child(postId);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        postList.clear();
        Post post = snapshot.getValue(Post.class);
        postList.add(post);

        postAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}