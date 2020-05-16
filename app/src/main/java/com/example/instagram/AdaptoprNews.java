package com.example.instagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdaptoprNews extends RecyclerView.Adapter<AdaptoprNews.ViewHolder> {


    Context context;
    private int[] images;

    //this class contain items which in item_view_recyclerview (image view)
    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgnews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgnews=(ImageView) itemView.findViewById(R.id.imageView_on_itemview);
        }
    }

    public AdaptoprNews(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public AdaptoprNews.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_recyclerview_mainactivity , parent , false);
        ViewHolder viewHolder=new ViewHolder(layout);
        return viewHolder;
    }


// called when i scroll screen up and down
    @Override
    public void onBindViewHolder(@NonNull AdaptoprNews.ViewHolder holder, int position) {

        int image_id=images[position];
        holder.imgnews.setImageResource(image_id);

    }

    @Override
    public int getItemCount() {
        return images.length;
    }
}
