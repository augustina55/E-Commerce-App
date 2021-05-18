package com.example.shopie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchViewHolder extends RecyclerView.Adapter<SearchViewHolder.myHolder> {
    Context mcontext;
    ArrayList<String> keys,names;

    public SearchViewHolder(Context mcontext, ArrayList<String> keys, ArrayList<String> names) {
        this.mcontext = mcontext;
        this.keys = keys;
        this.names = names;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.search_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        holder.textView.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.textView);
        }
    }
}
