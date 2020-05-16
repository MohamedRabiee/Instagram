package com.example.instagram.view.home.comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.model.Comments;

import java.util.List;

public class CommentAdaptor extends RecyclerView.Adapter<CommentHolder> {

    private List<Comments> comen;

    public CommentAdaptor(List<Comments> comen) {
        this.comen = comen;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_post, parent, false);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.bindView(comen.get(position));
    }

    @Override
    public int getItemCount() {
        return comen.size();
    }
}
