package com.socify.app.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.socify.app.models.Chat;
import com.socify.app.utils.SocifyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

  public static final int MSG_TYPE_LEFT = 0;
  public static final int MSG_TYPE_RIGHT = 1;

  private Context mContext;
  private List<Chat> mChats;
  private String imageUrl;

  FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
  DatabaseReference reference;

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

    if (chat.getMessage().isEmpty()) {
      holder.show_message.setText(mContext.getResources().getString(R.string.the_message_has_been_unsent));
    } else {
      holder.show_message.setText(chat.getMessage());
    }

    if (position == mChats.size() - 1) { // check for last message
      if (chat.isSeen()) {
        holder.txt_seen.setText(mContext.getResources().getString(R.string.seen));
      } else {
        holder.txt_seen.setText(mContext.getResources().getString(R.string.delivered));
      }
    } else {
      holder.txt_seen.setVisibility(View.GONE);
    }

    if (chat.getHideFor() != null && chat.getHideFor().equals(fUser.getUid())) {
      holder.itemView.setVisibility(View.GONE);
      holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
    }

    holder.show_message.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.inflate(R.menu.message_menu);

        if (!chat.getSender().equals(fUser.getUid())) {
          popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
          popupMenu.getMenu().findItem(R.id.unsent).setVisible(false);
        }
        if (!chat.isEdited()) {
          popupMenu.getMenu().findItem(R.id.edit_history).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
              case R.id.edit:
                editMessage(chat.getId(), chat.getMessage());
                return true;
              case R.id.edit_history:
                showEditHistory(chat.getId());
                return true;
              case R.id.unsent:
                unsentMessage(chat.getId());
                return true;
              case R.id.remove_for_me:
                hideMessage(chat.getId(), chat.getSender(), chat.getReceiver());
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
    if (mChats.get(position).getSender().equals(fUser.getUid())) {
      return MSG_TYPE_RIGHT;
    } else {
      return MSG_TYPE_LEFT;
    }
  }

  private void editMessage(String messageId, String message) {
    EditText input = new EditText(mContext);
    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    input.setText(message);
    input.setLines(5);
    input.setMaxLines(10);
    input.setVerticalScrollBarEnabled(true);
    input.setMovementMethod(new ScrollingMovementMethod());
    input.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setTitle(mContext.getResources().getString(R.string.edit_message))
      .setView(input)
      .setPositiveButton(mContext.getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          reference = FirebaseDatabase.getInstance().getReference(Chat.CHATS_DB).child(messageId);

          reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              String oldMessage = snapshot.getValue(Chat.class).getMessage();

              HashMap<String, Object> hashMap = new HashMap<>();
              hashMap.put(SocifyUtils.EXTRA_MESSAGE, input.getText().toString());
              hashMap.put(SocifyUtils.EXTRA_EDITED, true);

              reference.child("editHistory").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot historySnapshot) {
                  ArrayList<String> editHistory = new ArrayList<>();
                  if (historySnapshot.exists()) {
                    editHistory = (ArrayList<String>) historySnapshot.getValue();
                  }
                  editHistory.add(oldMessage);

                  hashMap.put(SocifyUtils.EXTRA_EDIT_HISTORY, editHistory);

                  reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Edited successful", Toast.LENGTH_SHORT).show();
                      }
                    }
                  });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                  //
                }
              });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              //
            }
          });
        }
      })
      .setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.cancel();
        }
      })
      .show();
  }

  private void showEditHistory(String messageId) {
    reference = FirebaseDatabase.getInstance().getReference(Chat.CHATS_DB).child(messageId);

    reference.child("editHistory").addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        ArrayList<String> editHistory = new ArrayList<>();
        // nút menu xem lịch sử đã kiểm tra có tồn tại sẵn rồi, nên ta không cần check nữa, vì chắc chắc không null.
        editHistory = (ArrayList<String>) dataSnapshot.getValue();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.edit_history));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, editHistory);

        ListView listView = new ListView(mContext);
        listView.setAdapter(adapter);

        builder.setView(listView);

        builder.setPositiveButton(mContext.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

        builder.show();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void unsentMessage(String messageId) {
    reference = FirebaseDatabase.getInstance().getReference(Chat.CHATS_DB).child(messageId);

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put(SocifyUtils.EXTRA_MESSAGE, "");

    reference.updateChildren(hashMap);
  }

  private void hideMessage(String messageId, String sender, String receiver) {
    reference = FirebaseDatabase.getInstance().getReference(Chat.CHATS_DB).child(messageId);

    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        Chat chat = snapshot.getValue(Chat.class);

        if (chat.getHideFor() == null) {
          HashMap<String, Object> hashMap = new HashMap<>();
          hashMap.put(SocifyUtils.EXTRA_HIDE_FOR, fUser.getUid());

          reference.updateChildren(hashMap);
        } else {
          if (chat.getHideFor().equals(sender) || chat.getHideFor().equals(receiver)) {
            reference.removeValue(); // Xóa tin nhắn
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}
