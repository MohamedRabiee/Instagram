package com.example.instagram.view.home.comment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.model.Comments;

public class CommentHolder extends RecyclerView.ViewHolder {

    private ImageView comment_imgview;
    private TextView comment_txtAccountName , comment_txtview;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);
        initView();
    }

    private void initView()
    {
        comment_imgview = itemView.findViewById(R.id.comment_account_imageView);
        comment_txtAccountName = itemView.findViewById(R.id.comment_account_name_textView);
        comment_txtview = itemView.findViewById(R.id.comment_account_textView);

    }


    void bindView(Comments post) {
//        Picasso.get()
//                .load(post.getUserImage())
//                .placeholder(R.drawable.img_placeholder)
//                .into(comment_imgview);

 //       comment_txtAccountName.setText(post.setUserName());
        comment_txtview.setText(post.getCommentData());
    }
}
