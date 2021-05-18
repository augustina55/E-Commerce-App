package com.example.shopie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewViewHolder extends RecyclerView.Adapter<ReviewViewHolder.myHolder> {
    Context mcontext;
    ArrayList<FirebaseReviews> items;
    int count;

    public ReviewViewHolder(Context mcontext, ArrayList<FirebaseReviews> items,int count) {
        this.mcontext = mcontext;
        this.items = items;
        this.count=count;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.review_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        holder.ratingtext.setText(String.valueOf(items.get(position).getRating()));
        holder.userReview.setText(items.get(position).getReview());
        holder.date.setText(items.get(position).getDate());
        holder.username.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(count==5)
           return items.size()-(items.size()-5);
        else
            return items.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        TextView ratingtext,userReview,username,date;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            ratingtext=itemView.findViewById(R.id.ratingtext);
            userReview=itemView.findViewById(R.id.userReview);
            username=itemView.findViewById(R.id.username);
            date=itemView.findViewById(R.id.date);
        }
    }
}
