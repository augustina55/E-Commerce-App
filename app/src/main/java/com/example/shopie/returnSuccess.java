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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class returnSuccess extends AppCompatActivity {
    RecyclerView horizontalRecycler,horizontalRecycler2;
    DatabaseReference dbref,dbref2;
    ArrayList<String> keys,keys2;
    ArrayList<FirebaseItems> items,items2;
    item_ViewHolder adapter;
    Button btnShop;
    ImageView pic;
    TextView txtOrder;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(returnSuccess.this,Home.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_success);
        btnShop=findViewById(R.id.btnShop);
        txtOrder=findViewById(R.id.txtOrder);
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pic=findViewById(R.id.pic);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPic=getIntent().getStringExtra("PIC");
                if(strPic.equals("RETURN")) {
                    txtOrder.setText("Return/refund requested \n we will get back to you soon");
                    pic.setBackground(getResources().getDrawable(R.drawable.return_refund));
                }else {
                    txtOrder.setText("Ordered Cancelled \n we will get back to you soon");
                    pic.setBackground(getResources().getDrawable(R.drawable.cancelled));
                }
            }
        });
        horizontalRecycler=findViewById(R.id.horizontalRecycler);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.HORIZONTAL,false);
        horizontalRecycler.setLayoutManager(gridLayoutManager);
        dbref= FirebaseDatabase.getInstance().getReference("MEN");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    keys=new ArrayList<>();
                    items=new ArrayList<>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        keys.add(snapshot1.getKey().toString());
                        FirebaseItems list = new FirebaseItems();
                        list.setPic(snapshot1.child("images").child("1").child("pic").getValue().toString());
                        list.setName(snapshot1.child("product details").child("name").getValue().toString());
                        list.setBrand(snapshot1.child("product details").child("brand").getValue().toString());
                        list.setDiscount(snapshot1.child("product details").child("discount").getValue().toString());
                        list.setPrice(snapshot1.child("product details").child("price").getValue().toString());
                        items.add(list);
                    }
                    adapter=new item_ViewHolder(returnSuccess.this,keys,items,"MEN");
                    horizontalRecycler.setAdapter(adapter);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        horizontalRecycler2=findViewById(R.id.horizontalRecycler2);
        GridLayoutManager gridLayoutManager1=new GridLayoutManager(this,1,GridLayoutManager.HORIZONTAL,false);
        horizontalRecycler2.setLayoutManager(gridLayoutManager1);
        dbref2= FirebaseDatabase.getInstance().getReference("WOMEN");
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    keys2=new ArrayList<>();
                    items2=new ArrayList<>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        keys2.add(snapshot1.getKey().toString());
                        FirebaseItems list = new FirebaseItems();
                        list.setPic(snapshot1.child("images").child("1").child("pic").getValue().toString());
                        list.setName(snapshot1.child("product details").child("name").getValue().toString());
                        list.setBrand(snapshot1.child("product details").child("brand").getValue().toString());
                        list.setDiscount(snapshot1.child("product details").child("discount").getValue().toString());
                        list.setPrice(snapshot1.child("product details").child("price").getValue().toString());
                        items2.add(list);
                    }
                    adapter=new item_ViewHolder(returnSuccess.this,keys2,items2,"WOMEN");
                    horizontalRecycler2.setAdapter(adapter);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}