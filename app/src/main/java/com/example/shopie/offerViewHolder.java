package com.example.shopie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class offerViewHolder extends RecyclerView.Adapter<offerViewHolder.myHolder>  {
    Context mcontext;
    ArrayList<String> list;
    String db;

    public offerViewHolder(Context mcontext, ArrayList<String> list,String db) {
        this.mcontext = mcontext;
        this.list = list;
        this.db=db;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new offerViewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.offer_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        holder.textOffer.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        if(db.equals("1"))
             return list.size();
        return 1;
    }

    public class myHolder extends RecyclerView.ViewHolder {
        TextView textOffer;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            textOffer=itemView.findViewById(R.id.textOffer);
        }
    }
}
