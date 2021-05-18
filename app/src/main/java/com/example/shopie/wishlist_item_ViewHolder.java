package com.example.shopie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class wishlist_item_ViewHolder extends RecyclerView.Adapter<wishlist_item_ViewHolder.myHolder>  {
    Context mcontext;
    ArrayList<String> keys;
    ArrayList<FirebaseWishlist> items;
    DatabaseReference dbref,cart_ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    int cost=0,prc=0,dsc=0,ttl=0,ITEM_PRESENT=0;
    FirebaseBill billings;

    public wishlist_item_ViewHolder(Context mcontext, ArrayList<String> keys, ArrayList<FirebaseWishlist> items,DatabaseReference dbref) {
        this.mcontext = mcontext;
        this.keys = keys;
        this.items = items;
        this.dbref = dbref;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new wishlist_item_ViewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.wishlist_item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, final int position) {
        Glide.with(mcontext)
                .load(items.get(position).getPic())
                .centerCrop().into(holder.pic);
        billings=new FirebaseBill();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        cart_ref= FirebaseDatabase.getInstance().getReference("SHOPPING_BAG").child(user.getUid());
        cart_ref.child("CART").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        if(items.get(position).getDbkey().equals(snapshot1.child("dbkey").getValue().toString())){
                            ITEM_PRESENT=1;
                        }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        cart_ref.child("BILL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    billings.setTotalamount(Integer.parseInt(snapshot.child("totalamount").getValue().toString()));
                    billings.setTotalprice(Integer.parseInt(snapshot.child("totalprice").getValue().toString()));
                    billings.setDiscount(Integer.parseInt(snapshot.child("discount").getValue().toString()));
                    billings.setCoupon(Integer.parseInt(snapshot.child("coupon").getValue().toString()));
                    billings.setShipping(Integer.parseInt(snapshot.child("shipping").getValue().toString()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        prc= Integer.parseInt(items.get(position).getPrice().toString());
        dsc= Integer.parseInt(items.get(position).getDiscount().toString());

        if(dsc==0)
            cost=prc;
        else
            cost= (prc * dsc)/100;
        ttl= prc - cost;
        String price_str=String.valueOf(ttl).substring(0,1)+","+String.valueOf(ttl).substring(1);

        holder.brand_name.setText(items.get(position).getBrand());
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
                intent.putExtra("CAT",items.get(position).getCat());
                intent.putExtra("KEY",items.get(position).getDbkey());
                mcontext.startActivity(intent);
            }
        });

        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbref.child(keys.get(position)).removeValue();
                notifyDataSetChanged();
            }
        });

        holder.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbref.child(keys.get(position)).removeValue();
                if(ITEM_PRESENT==1){
                    FirebaseWishlist wish_items = new FirebaseWishlist();

                    wish_items.setBrand(items.get(position).getBrand());
                    wish_items.setCat(items.get(position).getCat());
                    wish_items.setDbkey(items.get(position).getDbkey());
                    wish_items.setDiscount(items.get(position).getDiscount());
                    wish_items.setPrice(items.get(position).getPrice());
                    wish_items.setPic(items.get(position).getPic());
                    wish_items.setColor(items.get(position).getColor());
                    wish_items.setName(items.get(position).getName());
                    wish_items.setQty(items.get(position).getQty());
                    wish_items.setSize(items.get(position).getSize());
                    cart_ref.child("CART").push().setValue(wish_items);


                    FirebaseBill fbill = new FirebaseBill();
                    fbill.setCoupon(billings.getCoupon());
                    fbill.setShipping(billings.getShipping());
                    fbill.setDiscount(cost + billings.getDiscount());
                    fbill.setTotalprice(prc + billings.getTotalprice());
                    fbill.setTotalamount(ttl + billings.getTotalamount());
                    cart_ref.child("BILL").setValue(fbill);

                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class myHolder extends RecyclerView.ViewHolder{
        ImageButton removeItem;
        ImageView pic;
        TextView cartBtn,brand_name,price,actual_price,discount,strike_text;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            removeItem=itemView.findViewById(R.id.removeItem);
            pic=itemView.findViewById(R.id.pic);
            brand_name=itemView.findViewById(R.id.brand_name);
            price=itemView.findViewById(R.id.price);
            actual_price=itemView.findViewById(R.id.actual_price);
            discount=itemView.findViewById(R.id.discount);
            cartBtn=itemView.findViewById(R.id.cartBtn);
            strike_text=itemView.findViewById(R.id.strike_text);
        }
    }
}
