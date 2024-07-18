package com.socify.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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
import com.socify.app.ui.MainActivity;
import com.socify.app.ui.fragments.ProfileFragment;
import com.socify.app.ui.models.User;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

  private Context mContext;
  private List<User> mUsers;
  private boolean isFragment;

  private FirebaseUser firebaseUser;

  public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
    this.mContext = mContext;
    this.mUsers = mUsers;
    this.isFragment = isFragment;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
    return new UserAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    final User user = mUsers.get(i);

    viewHolder.btn_follow.setVisibility(View.VISIBLE);

    viewHolder.fullname.setText(user.getFullname());
    viewHolder.username.setText(user.getUsername());
    Glide.with(mContext).load(user.getImageUrl()).into(viewHolder.image_profile);
    isFollowing(user.getId(), viewHolder.btn_follow);

    if (user.getId().equals(firebaseUser.getUid())) {
      viewHolder.btn_follow.setVisibility(View.GONE);
    }

    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isFragment) {
          SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
          editor.putString("profileId", user.getId());
          editor.apply();

          ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
            new ProfileFragment()).commit();
        } else {
          Intent intent = new Intent(mContext, MainActivity.class);
          intent.putExtra("publisherId", user.getId());
          mContext.startActivity(intent);
        }
      }
    });

    viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (viewHolder.btn_follow.getText().toString().equals(mContext.getResources().getString(R.string.follow))) {
          FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
            .child("following").child(user.getId()).setValue(true);
          FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
            .child("followers").child(firebaseUser.getUid()).setValue(true);
          addNotification(user.getId());
        } else {
          FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
            .child("following").child(user.getId()).removeValue();
          FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
            .child("followers").child(firebaseUser.getUid()).removeValue();
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mUsers.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView fullname;
    public TextView username;
    public CircleImageView image_profile;
    public Button btn_follow;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      fullname = itemView.findViewById(R.id.fullname);
      username = itemView.findViewById(R.id.username);
      image_profile = itemView.findViewById(R.id.image_profile);
      btn_follow = itemView.findViewById(R.id.btn_follow);
    }
  }

  private void isFollowing(String userId, Button button) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
      .child("Follow").child(firebaseUser.getUid()).child("following");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.child(userId).exists()) {
          button.setText(R.string.following);
        } else {
          button.setText(R.string.follow);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void addNotification(String userId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("userId", firebaseUser.getUid());
    hashMap.put("text", mContext.getResources().getString(R.string.started_following_you));
    hashMap.put("postId", "");
    hashMap.put("post", false);

    reference.push().setValue(hashMap);
  }
}
