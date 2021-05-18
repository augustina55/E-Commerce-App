package com.example.shopie;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class cart_viewHolder extends RecyclerView.Adapter<cart_viewHolder.myHolder> {
    Context mcontext;
    ArrayList<String> keys;
    ArrayList<FirebaseWishlist> items;
    DatabaseReference dbref,db_wishlist,cart_ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseBill billings;
    int prc=0,cost=0,dsc=0,ttl=0;

    public cart_viewHolder(Context mcontext, ArrayList<String> keys, ArrayList<FirebaseWishlist> items,DatabaseReference dbref) {
        this.mcontext = mcontext;
        this.keys = keys;
        this.items = items;
        this.dbref=dbref;
    }

    @NonNull
    @Override
    public cart_viewHolder.myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new cart_viewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.cart_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final cart_viewHolder.myHolder holder, final int position) {
        billings=new FirebaseBill();

        holder.actual_price.setPaintFlags(holder.actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.strike_text.setPaintFlags(holder.strike_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        cart_ref=FirebaseDatabase.getInstance().getReference("SHOPPING_BAG").child(user.getUid());
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
        db_wishlist= FirebaseDatabase.getInstance().getReference("WISHLIST").child(user.getUid());
        Glide.with(mcontext)
                .load(items.get(position).getPic())
                .centerCrop().into(holder.pic);

        prc= Integer.parseInt(items.get(position).getPrice().toString());
        dsc= Integer.parseInt(items.get(position).getDiscount().toString());

        if(dsc==0)
            cost=prc;
        else
            cost= (prc * dsc)/100;
        ttl = prc - cost;
        String price_str=String.valueOf(ttl).substring(0,1)+","+String.valueOf(ttl).substring(1);
        holder.brand_name.setText(items.get(position).getBrand());
        holder.item_name.setText(items.get(position).getName());
        holder.color.setText(items.get(position).getColor());
        holder.price.setText(price_str);
        holder.actual_price.setPaintFlags(holder.actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.strike_text.setPaintFlags(holder.strike_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        String actualprice_str=String.valueOf(prc).substring(0,1)+","+String.valueOf(prc).substring(1);
        holder.actual_price.setText(actualprice_str);
        String dd=String.valueOf(dsc) + "% OFF";
        holder.discount.setText(dd);
        holder.size.setText("Size:"+items.get(position).getSize());
        holder.qty.setText("Qty:"+items.get(position).getQty());

        holder.delivery.setText(getDate());

        holder.wishlist_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbref.child("CART").child(keys.get(position)).removeValue();
                FirebaseBill fbill = new FirebaseBill();
                fbill.setCoupon(billings.getCoupon());
                fbill.setShipping(billings.getShipping());
                fbill.setDiscount(cost - billings.getDiscount());
                fbill.setTotalprice(prc - billings.getTotalprice());
                fbill.setTotalamount(ttl - billings.getTotalamount());
                cart_ref.child("BILL").setValue(fbill);

                //move to wishlist
                FirebaseWishlist wish_items=new FirebaseWishlist();
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
                db_wishlist.push().setValue(wish_items);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogView = LayoutInflater.from(mcontext).inflate(R.layout.bottomsheet_remove, null);
                final BottomSheetDialog dialog = new BottomSheetDialog(mcontext);
                dialog.setContentView(dialogView);
                ImageView closeBtn=dialogView.findViewById(R.id.closeBtn);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                ImageView pic=dialogView.findViewById(R.id.pic);
                Glide.with(dialogView)
                        .load(items.get(position).getPic())
                        .centerCrop().into(pic);

                TextView wishlist=dialogView.findViewById(R.id.wishlist);
                wishlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbref.child("CART").child(keys.get(position)).removeValue();
                        FirebaseBill fbill = new FirebaseBill();
                        fbill.setCoupon(billings.getCoupon());
                        fbill.setShipping(billings.getShipping());
                        fbill.setDiscount(billings.getDiscount() - cost);
                        fbill.setTotalprice(billings.getTotalprice() - prc);
                        fbill.setTotalamount(billings.getTotalamount() - ttl);
                        cart_ref.child("BILL").setValue(fbill);

                        //move to wishlist
                        FirebaseWishlist wish_items=new FirebaseWishlist();
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
                        db_wishlist.push().setValue(wish_items);
                        notifyDataSetChanged();
                    }
                });

                TextView remove=dialogView.findViewById(R.id.remove);
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbref.child("CART").child(keys.get(position)).removeValue();
                        FirebaseBill fbill = new FirebaseBill();
                        fbill.setCoupon(billings.getCoupon());
                        fbill.setShipping(billings.getShipping());
                        fbill.setDiscount(billings.getDiscount() - cost);
                        fbill.setTotalprice(billings.getTotalprice() - prc);
                        fbill.setTotalamount(billings.getTotalamount() - ttl);
                        cart_ref.child("BILL").setValue(fbill);
                        dialog.cancel();
                        notifyDataSetChanged();
                    }
                });

                dialog.show();
            }
        });

    }

    public String getDate(){
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date);
        try{
            c.setTime(formatter.parse(strDate));
        }catch(ParseException e){
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_MONTH, 7);
        int d=c.get(Calendar.DAY_OF_MONTH);
        int mm = c.get(Calendar.MONTH);
        String dd=String.valueOf(d) + " " + months.get(mm);

        return dd;
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView brand_name,item_name,color,size,qty,delivery,price,actual_price,discount,strike_text,remove,wishlist_text;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            pic=itemView.findViewById(R.id.pic);
            brand_name=itemView.findViewById(R.id.brand_name);
            item_name=itemView.findViewById(R.id.item_name);
            color=itemView.findViewById(R.id.item_color);
            size=itemView.findViewById(R.id.text_size);
            qty=itemView.findViewById(R.id.text_qty);
            delivery=itemView.findViewById(R.id.delievry);
            price=itemView.findViewById(R.id.price);
            actual_price=itemView.findViewById(R.id.actual_price);
            discount=itemView.findViewById(R.id.discount);
            strike_text=itemView.findViewById(R.id.strike_text);
            wishlist_text=itemView.findViewById(R.id.wishlist);
            remove=itemView.findViewById(R.id.removeItem);
        }
    }
}
