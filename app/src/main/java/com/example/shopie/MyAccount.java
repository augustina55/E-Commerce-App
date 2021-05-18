package com.example.shopie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class MyAccount extends AppCompatActivity {

    LinearLayout singOut;
    CardView cardAddress,ordersBtn;
    ImageView profileEdit,bckPrss;
    FirebaseAuth mAuth;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MyAccount.this,Home.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);
        mAuth=FirebaseAuth.getInstance();
        bckPrss=findViewById(R.id.bckPrss);
        bckPrss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cardAddress=findViewById(R.id.cardAddress);
        cardAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MyAccount.this,UserAddress.class);
                intent.putExtra("FROM","ACCOUNT");
                startActivity(intent);
                finish();
            }
        });
        ordersBtn=findViewById(R.id.ordersBtn);
        ordersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MyAccount.this,OrdersPage.class);
                startActivity(intent);
                finish();
            }
        });
        profileEdit=findViewById(R.id.profileEdit);
        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccount.this,UserProfile.class));
                finish();
            }
        });
        singOut=findViewById(R.id.singOut);
        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(MyAccount.this,Welcome.class));
                finish();
            }
        });
    }
}