package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;

public class chooseAddress extends AppCompatActivity {
    ImageView backpress;
    RecyclerView recyclerView;
    DatabaseReference dbref,cart_ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ArrayList<String> pic_items;
    delivery_viewHolder adapter;
    String dbkey;
    TextView name,add,phone;
    Button homeType,changeBtn;
    LinearLayout addlayout;
    CardView orderBtn;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(chooseAddress.this, Cart.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_address);
        backpress=findViewById(R.id.backpress);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        name=findViewById(R.id.name);
        add=findViewById(R.id.add);
        addlayout=findViewById(R.id.addlayout);
        homeType=findViewById(R.id.homeType);
        phone=findViewById(R.id.phone);
        orderBtn=findViewById(R.id.orderBtn);
        dbref= FirebaseDatabase.getInstance().getReference("USER_ADDRESS").child(user.getUid());
        dbref.child("CURRENT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    orderBtn.setVisibility(View.VISIBLE);
                    for( DataSnapshot snapshot1:snapshot.getChildren()){
                        dbkey=snapshot1.getKey();
                    }
                    getData(dbkey);
                }else
                    orderBtn.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cart_ref= FirebaseDatabase.getInstance().getReference("SHOPPING_BAG").child(user.getUid());
        cart_ref.child("CART").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    pic_items=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        pic_items.add(snapshot1.child("pic").getValue().toString());
                    }
                    adapter=new delivery_viewHolder(chooseAddress.this,pic_items);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        changeBtn=findViewById(R.id.changeBtn);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(chooseAddress.this,UserAddress.class);
                intent.putExtra("FROM","ORDER");
                startActivity(intent);
                finish();
            }
        });


        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(chooseAddress.this,paymentPage.class);
                intent.putExtra("ADD",add.getText().toString());
                intent.putExtra("PHONE",phone.getText().toString());
                intent.putExtra("NAME",name.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
    public void getData(String ky){
        dbref.child("ADDRESS").child(ky).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    addlayout.setVisibility(View.VISIBLE);
                    name.setText(snapshot.child("fullname").getValue().toString());
                    String ad = snapshot.child("add1").getValue().toString() + "\n" +
                            snapshot.child("add2").getValue().toString() + "\n" +
                            snapshot.child("city").getValue().toString() + "\n" +
                            snapshot.child("state").getValue().toString() + "-" +
                            snapshot.child("pincode").getValue().toString();

                    add.setText(ad);
                    phone.setText(snapshot.child("phone").getValue().toString());
                    if (snapshot.child("type").getValue().toString().equals("HOME")) {
                        homeType.setText("HOME");
                        homeType.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_baseline_home_24), null, null, null);
                    } else {
                        homeType.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_baseline_work_24), null, null, null);
                        homeType.setText("WORK");
                    }
                }else {
                    addlayout.setVisibility(GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}