package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class UserAddress extends AppCompatActivity {

    LinearLayout addAdress;
    RecyclerView recyclerView;
    DatabaseReference dbref;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ArrayList<String> keys;
    ArrayList<FirebaseAddress> fadd;
    AddressViewHolder adapter;
    ImageView backprss;
    TextView addCount;
    String FROM;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(FROM.equals("ACCOUNT"))
             startActivity(new Intent(UserAddress.this,MyAccount.class));
        else
            startActivity(new Intent(UserAddress.this,chooseAddress.class));
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_address);
        FROM=getIntent().getStringExtra("FROM");

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        addAdress=findViewById(R.id.addAdress);
        addCount=findViewById(R.id.addCount);
        addAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserAddress.this,AddAddress.class);
                intent.putExtra("EDIT","NO");
                intent.putExtra("FROM",FROM);
                startActivity(intent);
                finish();
            }
        });
        backprss=findViewById(R.id.backprss);
        backprss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbref= FirebaseDatabase.getInstance().getReference("USER_ADDRESS").child(user.getUid());
        dbref.child("ADDRESS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    fadd=new ArrayList<>();
                    keys=new ArrayList<>();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String ky=snapshot.getKey();
                        FirebaseAddress address=dataSnapshot.getValue(FirebaseAddress.class);
                        fadd.add(address);
                        keys.add(ky);
                    }
                    adapter=new AddressViewHolder(UserAddress.this,keys,fadd,dbref,FROM);
                    recyclerView.setAdapter(adapter);
                    addCount.setText(keys.size() + " Saved address");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}