package com.example.shopie;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class size_ViewHolder extends RecyclerView.Adapter<size_ViewHolder.myHolder> {
    public static int SIZE_KEY=0;

    public static int getSizeKey() {
        return SIZE_KEY;
    }
    Context mcontext;
    ArrayList<String> keys,size,qty;
    int SIZE_SELECTED;
    DatabaseReference dbref;

    public size_ViewHolder(Context mcontext, ArrayList<String> keys, ArrayList<String> size, ArrayList<String> qty,DatabaseReference dbref) {
        this.mcontext = mcontext;
        this.keys=keys;
        this.size = size;
        this.qty = qty;
        this.dbref=dbref;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new size_ViewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.size_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final myHolder holder, final int position) {
        holder.Text_size.setText(size.get(position));
        int qtyy=Integer.parseInt(qty.get(position));
        if( qtyy < 10 ) {
            holder.Text_qty.setVisibility(View.VISIBLE);
            holder.Text_qty.setText(qty.get(position) +" left");
        }else {
            holder.Text_qty.setVisibility(View.GONE);
        }
        holder.layout.setBackgroundResource(R.drawable.circle);
        holder.Text_size.setTextColor(Color.BLACK);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbref.child("size").setValue(size.get(position));
                SIZE_SELECTED=position;
                notifyDataSetChanged();
            }
        });
        if(SIZE_SELECTED==position) {
            holder.layout.setBackgroundResource(R.drawable.circle_red);
            holder.Text_size.setTextColor(Color.RED);
        }else{
            holder.layout.setBackgroundResource(R.drawable.circle);
            holder.Text_size.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return size.size();
    }

    public class myHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView Text_qty,Text_size;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            layout=itemView.findViewById(R.id.linearLayout);
            Text_qty=itemView.findViewById(R.id.qty);
            Text_size=itemView.findViewById(R.id.size);
        }
    }
}
