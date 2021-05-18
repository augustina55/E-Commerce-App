package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class itemsPage extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference dbref;
    item_ViewHolder adapter;

    ArrayList<String> imgs,keys;
    ArrayList<FirebaseItems> items;
    TextView textNotfound;
    String TYPE,DB_KEY,CAT;
    ImageView imageView4;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(itemsPage.this,Home.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_page);
        ImageView searchImg=findViewById(R.id.searchImg);
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(itemsPage.this,Search.class));
                finish();
            }
        });
        imageView4=findViewById(R.id.imageView4);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        textNotfound=findViewById(R.id.textNotfound);
        textNotfound.setVisibility(View.INVISIBLE);
        recyclerView=findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager1=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager1);
        CAT=getIntent().getStringExtra("CAT").toString();
        TYPE=getIntent().getStringExtra("TYPE").toString();
        DB_KEY=getIntent().getStringExtra("DBKEY").toString();

        dbref= FirebaseDatabase.getInstance().getReference(CAT);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    imgs=new ArrayList<>();
                    keys=new ArrayList<>();
                    items=new ArrayList<>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        if(TYPE.equals("discount")){
                            int dsc= Integer.parseInt(snapshot1.child("product details").child("discount").getValue().toString());
                            if(dsc >= Integer.parseInt(DB_KEY)){
                                keys.add(snapshot1.getKey().toString());
                                FirebaseItems list = new FirebaseItems();
                                list.setPic(snapshot1.child("images").child("1").child("pic").getValue().toString());
                                list.setName(snapshot1.child("product details").child("name").getValue().toString());
                                list.setBrand(snapshot1.child("product details").child("brand").getValue().toString());
                                list.setDiscount(snapshot1.child("product details").child("discount").getValue().toString());
                                list.setPrice(snapshot1.child("product details").child("price").getValue().toString());
                                items.add(list);
                            }
                        }else if(TYPE.equals("price")){
                            int prc= Integer.parseInt(snapshot1.child("product details").child("price").getValue().toString());
                            int dsc= Integer.parseInt(snapshot1.child("product details").child("discount").getValue().toString());
                            int cost= (prc * dsc)/100;
                            if(cost <= Integer.parseInt(DB_KEY)) {
                                keys.add(snapshot1.getKey().toString());
                                FirebaseItems list = new FirebaseItems();
                                list.setPic(snapshot1.child("images").child("1").child("pic").getValue().toString());
                                list.setName(snapshot1.child("product details").child("name").getValue().toString());
                                list.setBrand(snapshot1.child("product details").child("brand").getValue().toString());
                                list.setDiscount(snapshot1.child("product details").child("discount").getValue().toString());
                                list.setPrice(snapshot1.child("product details").child("price").getValue().toString());
                                items.add(list);
                            }
                        }else
                        if(snapshot1.child("product details").child(TYPE).getValue().toString().equals(DB_KEY)) {
                            keys.add(snapshot1.getKey().toString());
                            FirebaseItems list = new FirebaseItems();
                            list.setPic(snapshot1.child("images").child("1").child("pic").getValue().toString());
                            list.setName(snapshot1.child("product details").child("name").getValue().toString());
                            list.setBrand(snapshot1.child("product details").child("brand").getValue().toString());
                            list.setDiscount(snapshot1.child("product details").child("discount").getValue().toString());
                            list.setPrice(snapshot1.child("product details").child("price").getValue().toString());
                            items.add(list);
                        }
                    }
                    adapter=new item_ViewHolder(itemsPage.this,keys,items,CAT);
                    recyclerView.setAdapter(adapter);
                }else
                    textNotfound.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}