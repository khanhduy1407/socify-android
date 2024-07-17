package com.socify.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socify.app.R;
import com.socify.app.ui.models.Post;

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
    Post post = mPosts.get(position);

    Glide.with(mContext).load(post.getPostImage()).into(holder.post_image);
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
