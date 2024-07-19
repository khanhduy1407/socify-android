package com.socify.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.CommentsActivity;
import com.socify.app.ui.FollowersActivity;
import com.socify.app.ui.fragments.PostDetailFragment;
import com.socify.app.ui.fragments.ProfileFragment;
import com.socify.app.ui.models.Post;
import com.socify.app.ui.models.User;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

  public Context mContext;
  public List<Post> mPosts;

  private FirebaseUser firebaseUser;

  public PostAdapter(Context mContext, List<Post> mPosts) {
    this.mContext = mContext;
    this.mPosts = mPosts;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
    return new PostAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    Post post = mPosts.get(position);

    Glide.with(mContext).load(post.getPostImage())
      .apply(new RequestOptions().placeholder(R.drawable.img_placeholder))
      .into(holder.post_image);

    if (post.getDescription().equals("") || post.getDescription().isEmpty()) {
      holder.description.setVisibility(View.GONE);
    } else {
      holder.description.setVisibility(View.VISIBLE);
      holder.description.setText(post.getDescription());
    }

    publisherInfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher());
    isLiked(post.getPostId(), holder.like);
    nrLikes(holder.likes, post.getPostId());
    getComments(post.getPostId(), holder.comments);
    isSaved(post.getPostId(), holder.save);

    holder.image_profile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileId", post.getPublisher());
        editor.apply();

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
          new ProfileFragment()).commit();
      }
    });

    holder.publisher.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileId", post.getPublisher());
        editor.apply();

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
          new ProfileFragment()).commit();
      }
    });

    holder.username.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileId", post.getPublisher());
        editor.apply();

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
          new ProfileFragment()).commit();
      }
    });

    holder.post_image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("postId", post.getPostId());
        editor.apply();

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
          new PostDetailFragment()).commit();
      }
    });

    holder.like.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (holder.like.getTag().equals("like")) {
          FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
            .child(firebaseUser.getUid()).setValue(true);
          addNotification(post.getPublisher(), post.getPostId());
        } else {
          FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
            .child(firebaseUser.getUid()).removeValue();
        }
      }
    });

    holder.likes.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, FollowersActivity.class);
        intent.putExtra("id", post.getPostId());
        intent.putExtra("title", mContext.getResources().getString(R.string.likes));
        intent.putExtra("tag", "likes");
        mContext.startActivity(intent);
      }
    });

    holder.comment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra("postId", post.getPostId());
        intent.putExtra("publisherId", post.getPublisher());
        mContext.startActivity(intent);
      }
    });

    holder.comments.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra("postId", post.getPostId());
        intent.putExtra("publisherId", post.getPublisher());
        mContext.startActivity(intent);
      }
    });

    holder.save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (holder.save.getTag().equals("save")) {
          FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
            .child(post.getPostId()).setValue(true);
        } else {
          FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
            .child(post.getPostId()).removeValue();
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mPosts.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView image_profile, post_image, like, comment, save;
    public TextView publisher, username, description, likes, comments;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      image_profile = itemView.findViewById(R.id.image_profile);
      post_image = itemView.findViewById(R.id.post_image);
      like = itemView.findViewById(R.id.like);
      comment = itemView.findViewById(R.id.comment);
      save = itemView.findViewById(R.id.save);
      publisher = itemView.findViewById(R.id.publisher);
      username = itemView.findViewById(R.id.username);
      description = itemView.findViewById(R.id.description);
      likes = itemView.findViewById(R.id.likes);
      comments = itemView.findViewById(R.id.comments);
    }
  }

  private void isLiked(String postId, ImageView imageView) {
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
      .child("Likes")
      .child(postId);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.child(firebaseUser.getUid()).exists()) {
          imageView.setImageResource(R.drawable.ic_liked);
          imageView.setTag("liked");
        } else {
          imageView.setImageResource(R.drawable.ic_thumb_up);
          imageView.setTag("like");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void nrLikes(final TextView likes, String postId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
      .child(postId);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        likes.setText(String.valueOf(snapshot.getChildrenCount()));
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void getComments(String postId, final TextView comments) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        comments.setText("" + snapshot.getChildrenCount());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        Glide.with(mContext).load(user.getImageUrl()).into(image_profile);
        username.setText("@" + user.getUsername());
        publisher.setText(user.getFullname());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void isSaved(final String postId, ImageView imageView) {
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
      .child(firebaseUser.getUid());

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.child(postId).exists()) {
          imageView.setImageResource(R.drawable.ic_save_black);
          imageView.setTag("saved");
        } else {
          imageView.setImageResource(R.drawable.ic_save);
          imageView.setTag("save");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void addNotification(String userId, String postId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("userId", firebaseUser.getUid());
    hashMap.put("text", mContext.getResources().getString(R.string.liked_your_post));
    hashMap.put("postId", postId);
    hashMap.put("post", true);

    reference.push().setValue(hashMap);
  }
}
