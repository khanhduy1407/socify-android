package com.socify.app.ui.adapters;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.fragments.PostDetailFragment;
import com.socify.app.ui.fragments.ProfileFragment;
import com.socify.app.models.Notification;
import com.socify.app.models.Post;
import com.socify.app.models.User;
import com.socify.app.utils.SocifyUtils;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

  private Context mContext;
  private List<Notification> mNotifications;

  public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
    this.mContext = mContext;
    this.mNotifications = mNotifications;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
    return new NotificationAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Notification notification = mNotifications.get(position);

    holder.text.setText(notification.getText());

    getUserInfo(holder.image_profile, holder.username, notification.getUserId());

    if (notification.isPost()) {
      holder.post_image.setVisibility(View.VISIBLE);
      getPostImage(holder.post_image, notification.getPostId());
    } else {
      holder.post_image.setVisibility(View.GONE);
    }

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SocifyUtils.PREFS, Context.MODE_PRIVATE).edit();
        if (notification.isPost()) {
          editor.putString(SocifyUtils.EXTRA_POST_ID, notification.getPostId());
          editor.apply();

          ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new PostDetailFragment()).commit();
        } else {
          editor.putString(SocifyUtils.EXTRA_PROFILE_ID, notification.getUserId());
          editor.apply();

          ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new ProfileFragment()).commit();
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mNotifications.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView image_profile, post_image;
    public TextView username, text;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      image_profile = itemView.findViewById(R.id.image_profile);
      post_image = itemView.findViewById(R.id.post_image);
      username = itemView.findViewById(R.id.username);
      text = itemView.findViewById(R.id.comment);
    }
  }

  private void getUserInfo(final ImageView imageView, final TextView username, String publisherId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(publisherId);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        Glide.with(mContext).load(user.getImageUrl()).into(imageView);
        username.setText("@"+user.getUsername());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void getPostImage(final ImageView imageView, final String postId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Post.POSTS_DB).child(postId);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        Post post = snapshot.getValue(Post.class);
        Glide.with(mContext).load(post.getPostImage()).into(imageView);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}
