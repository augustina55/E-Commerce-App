package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersPage extends AppCompatActivity {
    ImageView backpress;
    Button shopBtn;
    RecyclerView recyclerview;
    DatabaseReference order_ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ArrayList<String> keys;
    ArrayList<FirebaseOrders> items;
    orderViewHolder adapter;
    LinearLayout EmptyLayout;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OrdersPage.this,Home.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_page);
        backpress=findViewById(R.id.backpress);
        EmptyLayout=findViewById(R.id.EmptyLayout);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        shopBtn=findViewById(R.id.shopBtn);
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerview=findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        order_ref= FirebaseDatabase.getInstance().getReference("ORDERS").child(user.getUid());
        order_ref.child("ORDER").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    EmptyLayout.setVisibility(View.GONE);
                    keys=new ArrayList<>();
                    items=new ArrayList<>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        keys.add(snapshot1.getKey().toString());
                        FirebaseOrders forders=snapshot1.getValue(FirebaseOrders.class);
                        items.add(forders);
                    }
                    adapter=new orderViewHolder(OrdersPage.this,keys,items,order_ref);
                    recyclerview.setAdapter(adapter);
                }else
                    EmptyLayout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}