package com.socify.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socify.app.R;
import com.socify.app.ui.models.User;

import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {

  private Context mContext;
  private List<User> mUsers;

  public UserChatAdapter(Context mContext, List<User> mUsers) {
    this.mContext = mContext;
    this.mUsers = mUsers;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.user_chat_item, parent, false);
    return new UserChatAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    User user = mUsers.get(position);
    holder.fullname.setText(user.getFullname());
    Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);
  }

  @Override
  public int getItemCount() {
    return mUsers.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView profile_image;
    public TextView fullname;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      profile_image = itemView.findViewById(R.id.profile_image);
      fullname = itemView.findViewById(R.id.fullname);
    }
  }
}
