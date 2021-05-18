package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class category_view extends AppCompatActivity {

    LinearLayout bestDeals,offers,top_offers,Discount;
    TextView catnames;
    RecyclerView recyclerViewDeals,recyclerViewTopBrands,recyclerViewTopoffers,recyclerViewDiscount;
    DatabaseReference db_ref;
    categoryViewHolder adapter;
    ArrayList<String> deals_keys,deals_imgs,top_keys,top_imgs,offers_imgs,offers_keys,
                        discount_keys,discount_imgs;

    String DB_CAT;
    ImageView imageView4;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(category_view.this,Home.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_view);
        imageView4=findViewById(R.id.imageView4);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        catnames=findViewById(R.id.catnames);
        catnames.setText(getIntent().getStringExtra("CAT"));
        ImageView searchImg=findViewById(R.id.searchImg);
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(category_view.this,Search.class));
                finish();
            }
        });
        bestDeals=findViewById(R.id.bestDeals);
        bestDeals.setVisibility(View.GONE);
        offers=findViewById(R.id.offers);
        offers.setVisibility(View.GONE);
        top_offers=findViewById(R.id.top_offers);
        top_offers.setVisibility(View.GONE);
        Discount=findViewById(R.id.Discount);
        Discount.setVisibility(View.GONE);

        db_ref= FirebaseDatabase.getInstance().getReference("Images");

        DB_CAT=getIntent().getStringExtra("CAT").toString();

        recyclerViewDiscount=findViewById(R.id.recyclerViewDiscount);
        GridLayoutManager gridLayoutManager3=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerViewDiscount.setLayoutManager(gridLayoutManager3);
        db_ref.child("discount").child(DB_CAT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    discount_imgs=new ArrayList<>();
                    discount_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        discount_keys.add(snapshot1.getKey().toString());
                        discount_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(category_view.this,discount_keys,discount_imgs,DB_CAT,"discount");
                    recyclerViewDiscount.setAdapter(adapter);
                    Discount.setVisibility(View.VISIBLE);
                }else
                    Discount.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //best deals
        recyclerViewDeals=findViewById(R.id.recyclerViewDeals);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerViewDeals.setLayoutManager(gridLayoutManager);


        db_ref.child("Best_Deals").child(DB_CAT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    deals_imgs=new ArrayList<>();
                    deals_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        deals_keys.add(snapshot1.getKey().toString());
                        deals_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(category_view.this,deals_keys,deals_imgs,DB_CAT,"category");
                    recyclerViewDeals.setAdapter(adapter);
                    bestDeals.setVisibility(View.VISIBLE);
                }else
                    bestDeals.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerViewTopBrands=findViewById(R.id.recyclerViewTopBrands);
        GridLayoutManager gridLayoutManager1=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerViewTopBrands.setLayoutManager(gridLayoutManager1);
        db_ref.child("Top_Brands").child(DB_CAT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    top_imgs=new ArrayList<>();
                    top_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        top_keys.add(snapshot1.getKey().toString());
                        top_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(category_view.this,top_keys,top_imgs,DB_CAT,"brand");
                    recyclerViewTopBrands.setAdapter(adapter);
                    offers.setVisibility(View.VISIBLE);
                }else
                    offers.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerViewTopoffers=findViewById(R.id.recyclerViewTopoffers);
        GridLayoutManager gridLayoutManager2=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerViewTopoffers.setLayoutManager(gridLayoutManager2);
        db_ref.child("offers").child(DB_CAT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    offers_imgs=new ArrayList<>();
                    offers_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        offers_keys.add(snapshot1.getKey().toString());
                        offers_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(category_view.this,offers_keys,offers_imgs,DB_CAT,"price");
                    recyclerViewTopoffers.setAdapter(adapter);
                    top_offers.setVisibility(View.VISIBLE);
                }else
                    top_offers.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
}