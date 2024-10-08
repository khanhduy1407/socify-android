package com.socify.app.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.models.Comment;
import com.socify.app.models.Notification;
import com.socify.app.ui.CommentsActivity;
import com.socify.app.ui.FollowersActivity;
import com.socify.app.ui.fragments.PostDetailFragment;
import com.socify.app.ui.fragments.ProfileFragment;
import com.socify.app.models.Post;
import com.socify.app.models.User;
import com.socify.app.utils.SocifyUtils;

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
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SocifyUtils.PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(SocifyUtils.EXTRA_PROFILE_ID, post.getPublisher());
        editor.apply();

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
          new ProfileFragment()).commit();
      }
    });

    holder.publisher.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SocifyUtils.PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(SocifyUtils.EXTRA_PROFILE_ID, post.getPublisher());
        editor.apply();

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
          new ProfileFragment()).commit();
      }
    });

    holder.username.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SocifyUtils.PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(SocifyUtils.EXTRA_PROFILE_ID, post.getPublisher());
        editor.apply();

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
          new ProfileFragment()).commit();
      }
    });

    holder.post_image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SocifyUtils.PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(SocifyUtils.EXTRA_POST_ID, post.getPostId());
        editor.apply();

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
          new PostDetailFragment()).commit();
      }
    });

    holder.like.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (holder.like.getTag().equals(SocifyUtils.TAG_LIKE)) {
          FirebaseDatabase.getInstance().getReference().child(Post.LIKES_DB).child(post.getPostId())
            .child(firebaseUser.getUid()).setValue(true);
          addNotification(post.getPublisher(), post.getPostId());
        } else {
          FirebaseDatabase.getInstance().getReference().child(Post.LIKES_DB).child(post.getPostId())
            .child(firebaseUser.getUid()).removeValue();
        }
      }
    });

    holder.likes.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, FollowersActivity.class);
        intent.putExtra(SocifyUtils.EXTRA_ID, post.getPostId());
        intent.putExtra(SocifyUtils.EXTRA_TITLE, mContext.getResources().getString(R.string.likes));
        intent.putExtra(SocifyUtils.EXTRA_TAG, SocifyUtils.TAG_LIKES);
        mContext.startActivity(intent);
      }
    });

    holder.comment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra(SocifyUtils.EXTRA_POST_ID, post.getPostId());
        intent.putExtra(SocifyUtils.EXTRA_PUBLISHER_ID, post.getPublisher());
        mContext.startActivity(intent);
      }
    });

    holder.comments.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra(SocifyUtils.EXTRA_POST_ID, post.getPostId());
        intent.putExtra(SocifyUtils.EXTRA_PUBLISHER_ID, post.getPublisher());
        mContext.startActivity(intent);
      }
    });

    holder.save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (holder.save.getTag().equals(SocifyUtils.TAG_SAVE)) {
          FirebaseDatabase.getInstance().getReference().child(Post.SAVES_DB).child(firebaseUser.getUid())
            .child(post.getPostId()).setValue(true);
        } else {
          FirebaseDatabase.getInstance().getReference().child(Post.SAVES_DB).child(firebaseUser.getUid())
            .child(post.getPostId()).removeValue();
        }
      }
    });

    holder.more.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.inflate(R.menu.post_menu);

        if (!post.getPublisher().equals(firebaseUser.getUid())) {
          popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
          popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
              case R.id.edit:
                editPost(post.getPostId());
                return true;
              case R.id.delete:
                FirebaseDatabase.getInstance().getReference(Post.POSTS_DB)
                  .child(post.getPostId()).removeValue()
                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                      }
                    }
                  });
                return true;
              case R.id.report:
                Toast.makeText(mContext, "Report clicked!", Toast.LENGTH_SHORT).show();
                return true;
              default:
                return false;
            }
          }
        });
        popupMenu.show();
      }
    });
  }

  @Override
  public int getItemCount() {
    return mPosts.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView image_profile, post_image, like, comment, save, more;
    public TextView publisher, username, description, likes, comments;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      image_profile = itemView.findViewById(R.id.image_profile);
      post_image = itemView.findViewById(R.id.post_image);
      like = itemView.findViewById(R.id.like);
      comment = itemView.findViewById(R.id.comment);
      save = itemView.findViewById(R.id.save);
      more = itemView.findViewById(R.id.more);
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
      .child(Post.LIKES_DB)
      .child(postId);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.child(firebaseUser.getUid()).exists()) {
          imageView.setImageResource(R.drawable.ic_liked);
          imageView.setTag(SocifyUtils.TAG_LIKED);
        } else {
          imageView.setImageResource(R.drawable.ic_thumb_up);
          imageView.setTag(SocifyUtils.TAG_LIKE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void nrLikes(final TextView likes, String postId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Post.LIKES_DB)
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
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Comment.COMMENTS_DB).child(postId);

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
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(userId);

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

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Post.SAVES_DB)
      .child(firebaseUser.getUid());

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.child(postId).exists()) {
          imageView.setImageResource(R.drawable.ic_save_black);
          imageView.setTag(SocifyUtils.TAG_SAVED);
        } else {
          imageView.setImageResource(R.drawable.ic_save);
          imageView.setTag(SocifyUtils.TAG_SAVE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void addNotification(String userId, String postId) {
    DatabaseReference reference = FirebaseDatabase.getInstance()
      .getReference(Notification.NOTIFICATIONS_DB)
      .child(userId);

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put(SocifyUtils.EXTRA_USER_ID, firebaseUser.getUid());
    hashMap.put(SocifyUtils.EXTRA_TEXT, mContext.getResources().getString(R.string.liked_your_post));
    hashMap.put(SocifyUtils.EXTRA_POST_ID, postId);
    hashMap.put(SocifyUtils.EXTRA_POST, true);

    reference.push().setValue(hashMap);
  }

  private void editPost(String postId) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
    alertDialog.setTitle(mContext.getResources().getString(R.string.edit_post));

    EditText editText = new EditText(mContext);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.MATCH_PARENT
    );
    editText.setLayoutParams(lp);
    alertDialog.setView(editText);

    getText(postId, editText);

    alertDialog.setPositiveButton(mContext.getResources().getString(R.string.edit),
      new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          HashMap<String, Object> hashMap = new HashMap<>();
          hashMap.put(SocifyUtils.EXTRA_DESCRIPTION, editText.getText().toString());

          FirebaseDatabase.getInstance().getReference(Post.POSTS_DB)
            .child(postId).updateChildren(hashMap);
        }
      });
    alertDialog.setNegativeButton(mContext.getResources().getString(R.string.cancel),
      new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });
    alertDialog.show();
  }

  private void getText(String postId, final EditText editText) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Post.POSTS_DB)
      .child(postId);
    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        editText.setText(snapshot.getValue(Post.class).getDescription());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}
