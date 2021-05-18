package com.example.shopie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class item_ViewHolder extends RecyclerView.Adapter<item_ViewHolder.myHolder>  {
    Context mcontext;
    ArrayList<String> keys;
    ArrayList<FirebaseItems> items;
    String db;

    public item_ViewHolder(Context mcontext, ArrayList<String> keys, ArrayList<FirebaseItems> items,String db) {
        this.mcontext = mcontext;
        this.keys = keys;
        this.items = items;
        this.db = db;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new item_ViewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, final int position) {
        Glide.with(mcontext)
                .load(items.get(position).getPic())
                .centerCrop().into(holder.pic);

        int prc= Integer.parseInt(items.get(position).getPrice().toString());
        int dsc= Integer.parseInt(items.get(position).getDiscount().toString());
        int cost=0;
        if(dsc==0)
            cost=0;
        else
            cost= (prc * dsc)/100;
        int ttl= Math.abs(prc - cost);
        String price_str;
        if(ttl>999)
            price_str=String.valueOf(ttl).substring(0,1)+","+String.valueOf(ttl).substring(1);
        else
            price_str=String.valueOf(ttl);

        holder.brand_name.setText(items.get(position).getBrand());
        holder.item_name.setText(items.get(position).getName());
        holder.price.setText(price_str);
        holder.actual_price.setPaintFlags(holder.actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.strike_text.setPaintFlags(holder.strike_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        String actualprice_str=String.valueOf(prc).substring(0,1)+","+String.valueOf(prc).substring(1);
        holder.actual_price.setText(actualprice_str);
        String dd=String.valueOf(dsc) + "% OFF";
        holder.discount.setText(dd);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext,product.class);
                intent.putExtra("CAT",db);
                intent.putExtra("KEY",keys.get(position));
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
        TextView brand_name,item_name,price,actual_price,discount,strike_text;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            pic=itemView.findViewById(R.id.pic);
            brand_name=itemView.findViewById(R.id.brand_name);
            item_name=itemView.findViewById(R.id.item_name);
            price=itemView.findViewById(R.id.price);
            actual_price=itemView.findViewById(R.id.actual_price);
            discount=itemView.findViewById(R.id.discount);
            strike_text=itemView.findViewById(R.id.strike_text);
        }
    }
}
