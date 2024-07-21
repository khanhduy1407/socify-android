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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.socify.app.R;
import com.socify.app.ui.models.Chat;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

  public static final int MSG_TYPE_LEFT = 0;
  public static final int MSG_TYPE_RIGHT = 1;

  private Context mContext;
  private List<Chat> mChats;
  private String imageUrl;

  FirebaseUser fUser;

  public MessageAdapter(Context mContext, List<Chat> mChats, String imageUrl) {
    this.mContext = mContext;
    this.mChats = mChats;
    this.imageUrl = imageUrl;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view;
    if (viewType == MSG_TYPE_RIGHT) {
      view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
    } else {
      view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
    }
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Chat chat = mChats.get(position);

    Glide.with(mContext).load(imageUrl).into(holder.profile_image);
    holder.show_message.setText(chat.getMessage());

    if (position == mChats.size() - 1) { // check for last message
      if (chat.isSeen()) {
        holder.txt_seen.setText(mContext.getResources().getString(R.string.seen));
      } else {
        holder.txt_seen.setText(mContext.getResources().getString(R.string.delivered));
      }
    } else {
      holder.txt_seen.setVisibility(View.GONE);
    }
  }

  @Override
  public int getItemCount() {
    return mChats.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView profile_image;
    public TextView show_message, txt_seen;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      profile_image = itemView.findViewById(R.id.profile_image);
      show_message = itemView.findViewById(R.id.show_message);
      txt_seen = itemView.findViewById(R.id.txt_seen);
    }
  }

  @Override
  public int getItemViewType(int position) {
    fUser = FirebaseAuth.getInstance().getCurrentUser();
    if (mChats.get(position).getSender().equals(fUser.getUid())) {
      return MSG_TYPE_RIGHT;
    } else {
      return MSG_TYPE_LEFT;
    }
  }
}
