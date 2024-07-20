package com.socify.app.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.AddStoryActivity;
import com.socify.app.ui.StoryActivity;
import com.socify.app.ui.models.Story;
import com.socify.app.ui.models.User;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

  private Context mContext;
  private List<Story> mStories;

  public StoryAdapter(Context mContext, List<Story> mStories) {
    this.mContext = mContext;
    this.mStories = mStories;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == 0) {
      View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent, false);
      return new StoryAdapter.ViewHolder(view);
    } else {
      View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false);
      return new StoryAdapter.ViewHolder(view);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Story story = mStories.get(position);

    userInfo(holder, story.getUserId(), position);

    if (holder.getAdapterPosition() != 0) {
      seenStory(holder, story.getUserId());
    }

    if (holder.getAdapterPosition() == 0) {
      myStory(holder.add_story_text, holder.story_plus, false);
    }

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (holder.getAdapterPosition() == 0) {
          myStory(holder.add_story_text, holder.story_plus, true);
        } else {
          Intent intent = new Intent(mContext, StoryActivity.class);
          intent.putExtra("userId", story.getUserId());
          mContext.startActivity(intent);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mStories.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView story_photo, story_plus, story_photo_seen;
    public TextView story_username, add_story_text;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      story_photo = itemView.findViewById(R.id.story_photo);
      story_plus = itemView.findViewById(R.id.story_plus);
      story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
      story_username = itemView.findViewById(R.id.story_username);
      add_story_text = itemView.findViewById(R.id.add_story_text);
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return 0;
    }
    return 1;
  }

  private void userInfo(final ViewHolder viewHolder, final String userId, final int pos) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        Glide.with(mContext).load(user.getImageUrl()).into(viewHolder.story_photo);
        if (pos != 0) {
          Glide.with(mContext).load(user.getImageUrl()).into(viewHolder.story_photo_seen);
          viewHolder.story_username.setText("@"+user.getUsername());
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void myStory(final TextView textView, final ImageView imageView, final boolean click) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stories")
      .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        int count = 0;
        long time_current = System.currentTimeMillis();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Story story = snapshot.getValue(Story.class);
          if (time_current > story.getTimeStart() && time_current < story.getTimeEnd()) {
            count++;
          }
        }

        if (click) {
          if (count > 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.view_story),
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  Intent intent = new Intent(mContext, StoryActivity.class);
                  intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                  mContext.startActivity(intent);
                  dialog.dismiss();
                }
              });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.add_story),
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  Intent intent = new Intent(mContext, AddStoryActivity.class);
                  mContext.startActivity(intent);
                  dialog.dismiss();
                }
              });
            alertDialog.show();
          } else {
            Intent intent = new Intent(mContext, AddStoryActivity.class);
            mContext.startActivity(intent);
          }
        } else {
          if (count > 0) {
            textView.setText(mContext.getResources().getString(R.string.my_story));
            imageView.setVisibility(View.GONE);
          } else {
            textView.setText(mContext.getResources().getString(R.string.add_story));
            imageView.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  private void seenStory(final ViewHolder viewHolder, String userId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stories")
      .child(userId);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        int i = 0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          if (!snapshot.child("views")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .exists() && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeEnd()) {
            i++;
          }
        }

        if (i > 0) {
          viewHolder.story_photo.setVisibility(View.VISIBLE);
          viewHolder.story_photo_seen.setVisibility(View.GONE);
        } else {
          viewHolder.story_photo.setVisibility(View.GONE);
          viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}
