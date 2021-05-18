package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class kids extends AppCompatActivity {

    RecyclerView recyclerViewTopBrands,recyclerViewFavBrands,recyclerViewListView;
    LinearLayout topBrands,FavBrands;
    DatabaseReference db_ref;
    TextView catnames;
    categoryViewHolder adapter;
    ArrayList<String> brands_keys,brands_imgs,fav_keys,fav_imgs,listView_keys;
    listViewHolder listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kids);
        topBrands=findViewById(R.id.topBrands);
        topBrands.setVisibility(View.GONE);
        FavBrands=findViewById(R.id.FavBrands);
        FavBrands.setVisibility(View.GONE);

        db_ref= FirebaseDatabase.getInstance().getReference("Images");

        catnames=findViewById(R.id.catnames);
        catnames.setText(getIntent().getStringExtra("CAT"));

        recyclerViewTopBrands=findViewById(R.id.recyclerViewTopBrands);
        GridLayoutManager gridLayoutManager1=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerViewTopBrands.setLayoutManager(gridLayoutManager1);
        db_ref.child("Top_Brands").child(getIntent().getStringExtra("CAT").toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    brands_imgs=new ArrayList<>();
                    brands_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        brands_keys.add(snapshot1.getKey().toString());
                        brands_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(kids.this,brands_keys,brands_imgs,getIntent().getStringExtra("CAT").toString(),"brand");
                    recyclerViewTopBrands.setAdapter(adapter);
                    topBrands.setVisibility(View.VISIBLE);
                }else
                    topBrands.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerViewFavBrands=findViewById(R.id.recyclerViewFavBrands);
        recyclerViewFavBrands.setLayoutManager(new LinearLayoutManager(kids.this,LinearLayoutManager.HORIZONTAL,false));
        db_ref.child("Fav_Brands").child(getIntent().getStringExtra("CAT").toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    fav_imgs=new ArrayList<>();
                    fav_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        fav_keys.add(snapshot1.getKey().toString());
                        fav_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(kids.this,fav_keys,fav_imgs,getIntent().getStringExtra("CAT").toString(),"brand");
                    recyclerViewFavBrands.setAdapter(adapter);
                    FavBrands.setVisibility(View.VISIBLE);
                }else
                    FavBrands.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //kids listView
        recyclerViewListView=findViewById(R.id.recyclerViewListView);
        recyclerViewListView.setLayoutManager(new LinearLayoutManager(this));


    }
}