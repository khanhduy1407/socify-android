package com.socify.app.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socify.app.R;
import com.socify.app.ui.fragments.PostDetailFragment;
import com.socify.app.models.Post;
import com.socify.app.utils.SocifyUtils;

import java.util.List;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ViewHolder> {

  private Context mContext;
  private List<Post> mPosts;

  public MyPhotoAdapter(Context mContext, List<Post> mPosts) {
    this.mContext = mContext;
    this.mPosts = mPosts;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.photos_item, parent, false);
    return new MyPhotoAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final Post post = mPosts.get(position);

    Glide.with(mContext).load(post.getPostImage()).into(holder.post_image);

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
  }

  @Override
  public int getItemCount() {
    return mPosts.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView post_image;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      post_image = itemView.findViewById(R.id.post_image);
    }
  }
}
