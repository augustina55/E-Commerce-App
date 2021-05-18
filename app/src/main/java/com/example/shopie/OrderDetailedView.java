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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailedView extends AppCompatActivity {
    RecyclerView horizontalRecycler,horizontalRecycler2;
    DatabaseReference dbref,dbref2,details_ref;
    ArrayList<String> keys,keys2;
    ArrayList<FirebaseItems> items,items2;
    item_ViewHolder adapter;

    Button returnBtn;
    ImageView pic,statusPic,backpress;
    TextView brand_name,nameTxt,sizeTxt,statusText,statusDate,exchangetxt,username,phoneNumber,fullAddress;
    TextView discountTxt,priceTxt;

    public String imageurl,TXTname,brandTXT;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OrderDetailedView.this,OrdersPage.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detailed_view);
        backpress=findViewById(R.id.backpress);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        returnBtn=findViewById(R.id.returnBtn);
        pic=findViewById(R.id.pic);
        brand_name=findViewById(R.id.brand_name);
        nameTxt=findViewById(R.id.nameTxt);
        String cat=getIntent().getStringExtra("CAT").toString();
        String dbkey=getIntent().getStringExtra("DBKEY").toString();

        details_ref=FirebaseDatabase.getInstance().getReference(cat).child(dbkey);
        details_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    imageurl=snapshot.child("images").child("1").child("pic").getValue().toString();
                    brandTXT=snapshot.child("product details").child("brand").getValue().toString();
                    TXTname=snapshot.child("product details").child("name").getValue().toString();
                    Glide.with(OrderDetailedView.this)
                            .load(imageurl)
                            .fitCenter().into(pic);
                    brand_name.setText(brandTXT);
                    nameTxt.setText(TXTname);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        sizeTxt=findViewById(R.id.sizeTxt);
        sizeTxt.setText(getIntent().getStringExtra("SIZE"));

        statusPic=findViewById(R.id.statusPic);
        String status=getIntent().getStringExtra("STATUS");
        if(status.equals("Processing")) {
            returnBtn.setText("CANCEL ORDER");
            statusPic.setBackground(getResources().getDrawable(R.drawable.processing));
        }else if(status.equals("Delivered")) {
            returnBtn.setText("REFUND OR ADD RETURN");
            statusPic.setBackground(getResources().getDrawable(R.drawable.delivered_pic));
        }else if(status.equals("Cancelled")) {
            returnBtn.setVisibility(View.GONE);
            statusPic.setBackground(getResources().getDrawable(R.drawable.cancelled));
        }else if(status.equals("Return")) {
            returnBtn.setVisibility(View.GONE);
            statusPic.setBackground(getResources().getDrawable(R.drawable.return_refund));
        }else if(status.equals("Refund")) {
            returnBtn.setVisibility(View.GONE);
            statusPic.setBackground(getResources().getDrawable(R.drawable.refund_money));
        }
        statusText=findViewById(R.id.statusText);
        statusText.setText(status);
        statusDate=findViewById(R.id.statusDate);
        statusDate.setText(getIntent().getStringExtra("DATE"));

        exchangetxt=findViewById(R.id.exchangetxt);
        exchangetxt.setText(getIntent().getStringExtra("EXCHANGE"));


        username=findViewById(R.id.username);
        username.setText(getIntent().getStringExtra("USER"));
        phoneNumber=findViewById(R.id.phoneNumber);
        phoneNumber.setText(getIntent().getStringExtra("PHONE"));
        fullAddress=findViewById(R.id.fullAddress);
        fullAddress.setText(getIntent().getStringExtra("ADDRESS"));

        discountTxt=findViewById(R.id.discountTxt);
        int d=Integer.parseInt(getIntent().getStringExtra("DISCOUNT").toString());
        String discount_str;
        if(d>999)
            discount_str=String.valueOf(d).substring(0,1)+","+String.valueOf(d).substring(1);
        else
            discount_str= String.valueOf(d);

            discountTxt.setText("\u20B9" + discount_str);
        int p=Integer.parseInt(getIntent().getStringExtra("PRICE").toString());

        priceTxt=findViewById(R.id.priceTxt);
        String price_str;
        if(p>999)
             price_str=String.valueOf(p).substring(0,1)+","+String.valueOf(p).substring(1);
        else
            price_str= String.valueOf(p);
        priceTxt.setText("\u20B9" + price_str);



        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(returnBtn.getText().equals("REFUND OR ADD RETURN")){
                    Intent intent=new Intent(OrderDetailedView.this,ReturnItem.class);
                    intent.putExtra("ORDERKEY",getIntent().getStringExtra("ORDERKEY"));
                    intent.putExtra("PIC",imageurl);
                    intent.putExtra("BRAND",brandTXT);
                    intent.putExtra("NAME",TXTname);
                    intent.putExtra("SIZE",sizeTxt.getText());
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(OrderDetailedView.this,CancelOrder.class);
                    intent.putExtra("ORDERKEY",getIntent().getStringExtra("ORDERKEY"));
                    intent.putExtra("PIC",imageurl);
                    intent.putExtra("BRAND",brandTXT);
                    intent.putExtra("NAME",TXTname);
                    intent.putExtra("SIZE",sizeTxt.getText());
                    startActivity(intent);
                    finish();
                }
            }
        });

        //Show Items
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
                    adapter=new item_ViewHolder(OrderDetailedView.this,keys,items,"MEN");
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
                    adapter=new item_ViewHolder(OrderDetailedView.this,keys2,items2,"WOMEN");
                    horizontalRecycler2.setAdapter(adapter);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}