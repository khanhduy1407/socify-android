package com.socify.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.socify.app.models.Notification;
import com.socify.app.ui.adapters.CommentAdapter;
import com.socify.app.models.Comment;
import com.socify.app.models.User;
import com.socify.app.utils.SocifyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

  private RecyclerView recyclerView;
  private CommentAdapter commentAdapter;
  private List<Comment> commentList;

  EditText add_comment;
  ImageView image_profile;
  TextView post;

  String postId;
  String publisherId;

  FirebaseUser firebaseUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comments);

    Intent intent = getIntent();
    postId = intent.getStringExtra(SocifyUtils.EXTRA_POST_ID);
    publisherId = intent.getStringExtra(SocifyUtils.EXTRA_PUBLISHER_ID);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(Comment.COMMENTS_DB);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    commentList = new ArrayList<>();
    commentAdapter = new CommentAdapter(this, commentList, postId);
    recyclerView.setAdapter(commentAdapter);

    readComments();

    add_comment = findViewById(R.id.add_comment);
    image_profile = findViewById(R.id.image_profile);
    post = findViewById(R.id.post);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    post.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (add_comment.getText().toString().equals("")) {
          Toast.makeText(CommentsActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
        } else {
          addComment();
        }
      }
    });

    getImage();
  }

  private void readComments() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Comment.COMMENTS_DB).child(postId);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        commentList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Comment comment = snapshot.getValue(Comment.class);
          commentList.add(comment);
        }

        commentAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void addComment() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Comment.COMMENTS_DB).child(postId);

    String commentId = reference.push().getKey();

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put(SocifyUtils.EXTRA_COMMENT_ID, commentId);
    hashMap.put(SocifyUtils.EXTRA_COMMENT, add_comment.getText().toString());
    hashMap.put(SocifyUtils.EXTRA_PUBLISHER, firebaseUser.getUid());

    reference.child(commentId).setValue(hashMap);
    addNotification();
    add_comment.setText("");
  }

  private void getImage() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(firebaseUser.getUid());

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        Glide.with(getApplicationContext()).load(user.getImageUrl()).into(image_profile);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void addNotification() {
    DatabaseReference reference = FirebaseDatabase.getInstance()
      .getReference(Notification.NOTIFICATIONS_DB)
      .child(publisherId);

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put(SocifyUtils.EXTRA_USER_ID, firebaseUser.getUid());
    hashMap.put(SocifyUtils.EXTRA_TEXT, getApplicationContext().getResources().getString(R.string.commented) + " " + add_comment.getText().toString());
    hashMap.put(SocifyUtils.EXTRA_POST_ID, postId);
    hashMap.put(SocifyUtils.EXTRA_POST, true);

    reference.push().setValue(hashMap);
  }
}