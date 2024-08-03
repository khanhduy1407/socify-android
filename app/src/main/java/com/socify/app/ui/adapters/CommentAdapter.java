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
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.socify.app.utils.SocifyUtils;
import com.socify.app.ui.MainActivity;
import com.socify.app.models.Comment;
import com.socify.app.models.User;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

  private Context mContext;
  private List<Comment> mComments;
  private String postId;

  FirebaseUser firebaseUser;

  public CommentAdapter(Context mContext, List<Comment> mComments, String postId) {
    this.mContext = mContext;
    this.mComments = mComments;
    this.postId = postId;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
    return new CommentAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    Comment comment = mComments.get(position);

    holder.comment.setText(comment.getComment());
    getUserInfo(holder.image_profile, holder.fullname, comment.getPublisher());

    holder.image_profile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(SocifyUtils.EXTRA_PUBLISHER_ID, comment.getPublisher());
        mContext.startActivity(intent);
      }
    });

    holder.fullname.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(SocifyUtils.EXTRA_PUBLISHER_ID, comment.getPublisher());
        mContext.startActivity(intent);
      }
    });

    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (comment.getPublisher().equals(firebaseUser.getUid())) {
          AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
          alertDialog.setTitle(mContext.getResources().getString(R.string.do_you_want_to_delete));
          alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mContext.getResources().getString(R.string.no),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            });
          alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.yes),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference(Comment.COMMENTS_DB)
                  .child(postId).child(comment.getCommentId())
                  .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                      }
                    }
                  });
                dialog.dismiss();
              }
            });
          alertDialog.show();
        }
        return true;
      }
    });
  }

  @Override
  public int getItemCount() {
    return mComments.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView image_profile;
    public TextView fullname, comment;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      image_profile = itemView.findViewById(R.id.image_profile);
      fullname = itemView.findViewById(R.id.fullname);
      comment = itemView.findViewById(R.id.comment);
    }
  }

  private void getUserInfo(final ImageView imageView, final TextView fullname, String publisherId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(publisherId);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        Glide.with(mContext).load(user.getImageUrl()).into(imageView);
        fullname.setText(user.getFullname());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}
