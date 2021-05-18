package com.example.shopie;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class orderViewHolder extends RecyclerView.Adapter<orderViewHolder.myHolder> {
    Context mcontext;
    ArrayList<String> keys;
    ArrayList<FirebaseOrders> lists;
    DatabaseReference dbref,db_ref2,rating_ref,rating_ref2;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public String name,picStr,brand,status,date,exchangeTxt;

    public orderViewHolder(Context mcontext, ArrayList<String> keys, ArrayList<FirebaseOrders> lists, DatabaseReference dbref) {
        this.mcontext = mcontext;
        this.keys = keys;
        this.lists = lists;
        this.dbref = dbref;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new orderViewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.order_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final myHolder holder, final int position) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db_ref2= FirebaseDatabase.getInstance().getReference(lists.get(position).getCat());
        db_ref2.child(lists.get(position).getDbkey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    brand=snapshot.child("product details").child("brand").getValue().toString();
                    name=snapshot.child("product details").child("name").getValue().toString();
                    picStr=snapshot.child("images").child("1").child("pic").getValue().toString();
                    holder.brandName.setText(brand);
                    holder.descText.setText(name);

                    Glide.with(mcontext)
                            .load(picStr)
                            .fitCenter().into(holder.pic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        holder.sizeText.setText("Size:"+lists.get(position).getSize());

        dbref.child("STATUS").child(keys.get(position)).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    date=snapshot.child("date").getValue().toString();
                    holder.statusText.setText(snapshot.child("status").getValue().toString());

                    status=snapshot.child("status").getValue().toString();
                    if(status.equals("Processing"))
                        holder.statusPic.setBackground(mcontext.getDrawable(R.drawable.processing));
                    else if(status.equals("Delivered")){
                        holder.exchangeLayout.setVisibility(View.VISIBLE);
                        holder.ratingLayout.setVisibility(View.VISIBLE);
                        holder.layoutReturn.setVisibility(View.VISIBLE);
                        holder.statusPic.setBackground(mcontext.getDrawable(R.drawable.delivered_pic));
                    }else if(status.equals("Cancelled"))
                        holder.statusPic.setBackground(mcontext.getDrawable(R.drawable.cancelled));
                    else if(status.equals("Return")) {
                        holder.statusPic.setBackground(mcontext.getDrawable(R.drawable.return_refund));
                        holder.exchangeLayout.setVisibility(View.VISIBLE);
                    }else if(status.equals("Refund")){
                        holder.statusPic.setBackground(mcontext.getDrawable(R.drawable.refund_money));
                        holder.exchangeLayout.setVisibility(View.VISIBLE);
                    }

                    exchangeTxt="Exchange return/refund on before " + AddDate(date);
                    holder.exchangetxt.setText(exchangeTxt);
                    holder.statusDate.setText(convertToDate(date));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext,OrderDetailedView.class);
                intent.putExtra("ORDERKEY",keys.get(position));
                intent.putExtra("CAT",lists.get(position).getCat());
                intent.putExtra("DBKEY",lists.get(position).getDbkey());
                intent.putExtra("SIZE",lists.get(position).getSize());
                intent.putExtra("STATUS",status);
                intent.putExtra("DATE",date);
                intent.putExtra("USER",lists.get(position).getName());
                intent.putExtra("PHONE",lists.get(position).getPhone());
                intent.putExtra("ADDRESS",lists.get(position).getAddress());
                int dd= Integer.parseInt(lists.get(position).getGrandDiscount()) + Integer.parseInt(lists.get(position).getShipping())
                        + Integer.parseInt(lists.get(position).getCoupon());
                intent.putExtra("DISCOUNT",lists.get(position).getGrandDiscount());
                intent.putExtra("PRICE",lists.get(position).getGrandtotal());
                intent.putExtra("EXCHANGE",exchangeTxt);
                mcontext.startActivity(intent);
            }
        });

        rating_ref2=FirebaseDatabase.getInstance().getReference("USER_REVIEWS");
        rating_ref=FirebaseDatabase.getInstance().getReference("REVIEWS").child(user.getUid());
        rating_ref.child(lists.get(position).getCat())
                .child(lists.get(position).getDbkey()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    float rat= Float.parseFloat(snapshot.child("rating").getValue().toString());
                    holder.ratingBar.setRating(rat);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM,yyy");
                LocalDateTime now = LocalDateTime.now();
                String date=dtf.format(now);

                rating_ref.child(lists.get(position).getCat())
                        .child(lists.get(position).getDbkey()).child("rating").setValue(rating);

                //user_reviews
                rating_ref2.child(lists.get(position).getCat())
                        .child(lists.get(position).getDbkey()).child(user.getUid())
                        .child("rating").setValue(rating);
                rating_ref2.child(lists.get(position).getCat())
                        .child(lists.get(position).getDbkey()).child(user.getUid())
                        .child("date").setValue(date);
            }
        });

        holder.edirRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext,EditReviews.class);
                intent.putExtra("CAT",lists.get(position).getCat());
                intent.putExtra("DBKEY",lists.get(position).getDbkey());
                mcontext.startActivity(intent);
            }
        });

    }

    public String AddDate(String sdate){
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = formatter.parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd MMM,yyyy");
        String strDate = dateFormat.format(date);
        try{
            c.setTime(formatter.parse(strDate));
        }catch(ParseException e){
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_MONTH, 15);
        int d=c.get(Calendar.DAY_OF_MONTH);
        int mm = c.get(Calendar.MONTH);
        String dd=String.valueOf(d) + " " + months.get(mm);
        return dd;
    }

    public String convertToDate(String sdate){
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        String [] dateParts = sdate.split("/");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];
        String dd="On "+ day +" "+months.get(Integer.parseInt(month)-1) +","+year;
        return dd;
    }


    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        ImageView statusPic,pic;
        TextView statusDate,statusText,brandName,descText,sizeText,exchangetxt,
                edirRating;
        Button returnBtn;
        RatingBar ratingBar;
        LinearLayout layoutReturn,ratingLayout,exchangeLayout;

        public myHolder(@NonNull View itemView) {
            super(itemView);
            statusPic=itemView.findViewById(R.id.statusPic);
            pic=itemView.findViewById(R.id.pic);
            statusDate=itemView.findViewById(R.id.statusDate);
            brandName=itemView.findViewById(R.id.brandName);
            descText=itemView.findViewById(R.id.descText);
            sizeText=itemView.findViewById(R.id.sizeText);
            exchangetxt=itemView.findViewById(R.id.exchangetxt);
            edirRating=itemView.findViewById(R.id.edirRating);
            returnBtn=itemView.findViewById(R.id.returnBtn);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            layoutReturn=itemView.findViewById(R.id.layoutReturn);
            ratingLayout=itemView.findViewById(R.id.ratingLayout);
            statusText=itemView.findViewById(R.id.statusText);
            exchangeLayout=itemView.findViewById(R.id.exchangeLayout);
        }
    }
}
