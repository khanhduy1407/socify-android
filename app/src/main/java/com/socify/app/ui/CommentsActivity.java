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

public class CommentsActivity extends AppCompatActivity {

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

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("Comments");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    add_comment = findViewById(R.id.add_comment);
    image_profile = findViewById(R.id.image_profile);
    post = findViewById(R.id.post);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    Intent intent = getIntent();
    postId = intent.getStringExtra("postId");
    publisherId = intent.getStringExtra("publisherId");

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

  private void addComment() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("comment", add_comment.getText().toString());
    hashMap.put("publisher", firebaseUser.getUid());

    reference.push().setValue(hashMap);
    add_comment.setText("");
  }

  private void getImage() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

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
}