package com.socify.app.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.socify.app.models.ChatList;
import com.socify.app.ui.MessageActivity;
import com.socify.app.models.Chat;
import com.socify.app.models.User;
import com.socify.app.utils.SocifyUtils;

import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {

  private Context mContext;
  private List<User> mUsers;
  private boolean isChat;

  String theLastMessage;

  public UserChatAdapter(Context mContext, List<User> mUsers, boolean isChat) {
    this.mContext = mContext;
    this.mUsers = mUsers;
    this.isChat = isChat;
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

    if (isChat) {
      if (!user.isDeleted()) {
        if (user.getStatus() != null && user.getStatus().equals(SocifyUtils.STATUS_ONLINE)) {
          holder.img_status.setBackground(mContext.getDrawable(R.drawable.ic_status_online));
        } else {
          holder.img_status.setBackground(mContext.getDrawable(R.drawable.ic_status_offline));
        }
      } else {
        holder.img_status.setBackground(mContext.getDrawable(R.drawable.ic_status_deleted));
      }
      holder.img_status.setVisibility(View.VISIBLE);
    } else {
      holder.img_status.setVisibility(View.GONE);
    }

    if (isChat) {
      holder.username.setVisibility(View.GONE);
      holder.last_msg.setVisibility(View.VISIBLE);

      lastMessage(user.getId(), holder.last_msg);
    } else {
      holder.username.setVisibility(View.VISIBLE);
      holder.last_msg.setVisibility(View.GONE);

      holder.username.setText("@" + user.getUsername());
    }

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, MessageActivity.class);
        intent.putExtra(SocifyUtils.EXTRA_USER_ID, user.getId());
        mContext.startActivity(intent);
      }
    });

    if (isChat) {
      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          PopupMenu popupMenu = new PopupMenu(mContext, v);
          popupMenu.inflate(R.menu.chat_menu);

          popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
              switch (item.getItemId()) {
                case R.id.close_conversation:
                  hideChat(user.getId());
                  return true;
                default:
                  return false;
              }
            }
          });

          popupMenu.show();

          return false;
        }
      });
    }
  }

  @Override
  public int getItemCount() {
    return mUsers.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView profile_image, img_status, img_off;
    public TextView fullname, username, last_msg;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      profile_image = itemView.findViewById(R.id.profile_image);
      fullname = itemView.findViewById(R.id.fullname);
      username = itemView.findViewById(R.id.username);
      img_status = itemView.findViewById(R.id.img_status);
      last_msg = itemView.findViewById(R.id.last_msg);
    }
  }

  // check for last message
  private void lastMessage(String userId, TextView last_msg) {
    theLastMessage = "default";
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Chat.CHATS_DB);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Chat chat = snapshot.getValue(Chat.class);

          boolean isHidden = false;

          if (chat.getHideFor() != null) {
            String hideFor = chat.getHideFor();
            if (hideFor != null && hideFor.equals(firebaseUser.getUid())) {
              isHidden = true;
            }
          }

          if (isHidden) continue;

          if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
              chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
            if (chat.getSender().equals(firebaseUser.getUid())) {
              if (chat.getMessage().isEmpty()) {
                theLastMessage = mContext.getResources().getString(R.string.you) + ": " + mContext.getResources().getString(R.string.the_message_has_been_unsent);
              } else {
                theLastMessage = mContext.getResources().getString(R.string.you) + ": " + chat.getMessage();
              }
            } else {
              if (chat.getMessage().isEmpty()) {
                theLastMessage = mContext.getResources().getString(R.string.the_message_has_been_unsent);
              } else {
                theLastMessage = chat.getMessage();
              }
            }
          }
        }

        switch (theLastMessage) {
          case "default":
            last_msg.setText(mContext.getResources().getString(R.string.no_message));
            break;
          default:
            last_msg.setText(theLastMessage);
            break;
        }

        theLastMessage = "default";
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void hideChat(String userId) {
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance()
      .getReference(ChatList.CHAT_LIST_DB)
      .child(firebaseUser.getUid())
      .child(userId);

    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder
      .setTitle(mContext.getResources().getString(R.string.hide_chat))
      .setMessage(mContext.getResources().getString(R.string.hide_chat_message))
      .setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          reference.removeValue();
        }
      })
      .setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.cancel();
        }
      });
    builder.show();
  }
}
