package com.example.shopie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class childListView extends RecyclerView.Adapter<childListView.myHolder> {
    Context mcontetx;
    ArrayList<String> keys;

    public childListView(Context mcontetx, ArrayList<String> keys) {
        this.mcontetx = mcontetx;
        this.keys = keys;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new childListView.myHolder(LayoutInflater.from(mcontetx).inflate(R.layout.chil_list_view,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        holder.childnames.setText(keys.get(position));
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class myHolder  extends RecyclerView.ViewHolder {
        TextView childnames;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            childnames=itemView.findViewById(R.id.childnames);
        }
    }
}
