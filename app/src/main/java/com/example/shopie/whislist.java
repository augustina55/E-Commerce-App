package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class whislist extends AppCompatActivity {

    RecyclerView recyclerWhislist;
    ArrayList<String> keys;
    ArrayList<FirebaseWishlist> items;
    DatabaseReference dbref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    LinearLayout TextNotFound;
    Button shopBtn;
    wishlist_item_ViewHolder wish_adapter;
    ImageView bckPrss;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(whislist.this,Home.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whislist);
        shopBtn=findViewById(R.id.shopBtn);
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(whislist.this,Home.class));
                finish();
            }
        });
        bckPrss=findViewById(R.id.bckPrss);
        bckPrss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextNotFound=findViewById(R.id.TextNotFound);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        recyclerWhislist=findViewById(R.id.recyclerWhislist);
        GridLayoutManager gridLayoutManager1=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerWhislist.setLayoutManager(gridLayoutManager1);
        dbref= FirebaseDatabase.getInstance().getReference("WISHLIST").child(user.getUid());
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    recyclerWhislist.removeAllViews();
                    items=new ArrayList<>();
                    keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        FirebaseWishlist lists=snapshot1.getValue(FirebaseWishlist.class);
                        keys.add(snapshot1.getKey().toString());
                        items.add(lists);
                    }
                    wish_adapter=new wishlist_item_ViewHolder(whislist.this,keys,items,dbref);
                    recyclerWhislist.setAdapter(wish_adapter);
                }else{
                    recyclerWhislist.removeAllViews();
                    TextNotFound.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}