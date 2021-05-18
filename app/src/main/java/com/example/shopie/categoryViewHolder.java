package com.example.shopie;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class categoryViewHolder extends RecyclerView.Adapter<categoryViewHolder.myHolder>  {
    Context mcontext;
    ArrayList<String> keys,imgs;
    String db,type;

    public categoryViewHolder(Context mcontext, ArrayList<String> keys, ArrayList<String> imgs,String db,String type) {
        this.mcontext = mcontext;
        this.keys = keys;
        this.imgs = imgs;
        this.db=db;
        this.type=type;
    }

    @NonNull
    @Override
    public categoryViewHolder.myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new categoryViewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.cat_viewholder,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull categoryViewHolder.myHolder holder, final int position) {
        Glide.with(mcontext)
                .load(imgs.get(position))
                .fitCenter().into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext,itemsPage.class);
                intent.putExtra("CAT",db); //Men or women
                intent.putExtra("TYPE",type);
                intent.putExtra("DBKEY",keys.get(position));
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class myHolder extends RecyclerView.ViewHolder{
        ImageView pic;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            pic=itemView.findViewById(R.id.pic);
        }
    }
}
