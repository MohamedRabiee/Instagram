package com.example.instagram.view.home.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.example.instagram.utils.OnLikeClicked;

import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostHolder> {

    private List<Post> posts;
    private OnLikeClicked onLikeClicked;

    public PostAdaptor(List<Post> posts, OnLikeClicked onLikeClicked) {
        this.posts = posts;
        this.onLikeClicked = onLikeClicked;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, final int position) {
        holder.bindView(posts.get(position));
        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onLikeClicked.onLikeClicked(position);
            }
        });

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment =  holder.edtComment.getText().toString();
                onLikeClicked.onCommentAdd(position , comment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
