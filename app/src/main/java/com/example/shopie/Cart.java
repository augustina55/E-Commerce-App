package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Cart extends AppCompatActivity {
    ArrayList<String> keys;
    ArrayList<FirebaseWishlist> items;
    DatabaseReference dbref,coupon_ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    RecyclerView recyclerView;
    ImageView bckPrss;
    CardView orderBtn;
    cart_viewHolder cart_adapter;
    LinearLayout totalLayout,recyclerLayout,EmptyLayout,layout5,CouLayout,receiptLayout,layout2,couponLayout;
    int price=0,total=0,prc=0,dsc=0,TotalCost=0,TotalDiscount=0,GrandTtl=0,COUNT=0;
    TextView totalprice_text,couponText;
    TextView shippingFee,itemCount,couponDiscount,totalMRP,totalDiscount,Grandtotal;
    Button shopBtn,couponCheck;
    EditText EditCoupon;
    public static int VAL;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Cart.this,Home.class));
        finish();
    }

    public void visibleLayouts(){
        layout2.setVisibility(View.VISIBLE);
        totalLayout.setVisibility(View.VISIBLE);
        recyclerLayout.setVisibility(View.VISIBLE);
        layout5.setVisibility(View.VISIBLE);
        couponText.setVisibility(View.VISIBLE);
        CouLayout.setVisibility(View.VISIBLE);
        receiptLayout.setVisibility(View.VISIBLE);
        orderBtn.setVisibility(View.VISIBLE);
    }

    public void goneLayouts(){
        orderBtn.setVisibility(View.GONE);
        EmptyLayout.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        totalLayout.setVisibility(View.GONE);
        recyclerLayout.setVisibility(View.GONE);
        layout5.setVisibility(View.GONE);
        couponText.setVisibility(View.GONE);
        CouLayout.setVisibility(View.GONE);
        receiptLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        bckPrss=findViewById(R.id.bckPrss);
        bckPrss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        shopBtn=findViewById(R.id.shopBtn);
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        totalLayout=findViewById(R.id.totalLayout);
        CouLayout=findViewById(R.id.CouLayout);
        couponCheck=findViewById(R.id.couponCheck);
        EditCoupon=findViewById(R.id.EditCoupon);
        couponLayout=findViewById(R.id.couponLayout);
        receiptLayout=findViewById(R.id.receiptLayout);
        couponText=findViewById(R.id.couponText);
        recyclerLayout=findViewById(R.id.recyclerLayout);
        EmptyLayout=findViewById(R.id.EmptyLayout);
        layout5=findViewById(R.id.layout5);
        layout2=findViewById(R.id.layout2);
        totalprice_text=findViewById(R.id.totalprice_text);
        shippingFee=findViewById(R.id.shippingFee);
        itemCount=findViewById(R.id.itemCount);
        couponDiscount=findViewById(R.id.couponDiscount);
        totalMRP=findViewById(R.id.totalMRP);
        totalDiscount=findViewById(R.id.totalDiscount);
        Grandtotal=findViewById(R.id.Grandtotal);
        orderBtn=findViewById(R.id.orderBtn);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        goneLayouts();

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbref= FirebaseDatabase.getInstance().getReference("SHOPPING_BAG").child(user.getUid());
        dbref.child("CART").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    visibleLayouts();
                    EmptyLayout.setVisibility(View.GONE);
                    items=new ArrayList<>();
                    recyclerView.removeAllViews();
                    keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        prc= Integer.parseInt(snapshot1.child("price").getValue().toString());
                        TotalCost+=prc;
                        dsc= Integer.parseInt(snapshot1.child("discount").getValue().toString());
                        price= (prc * dsc)/100;
                        TotalDiscount += (prc-price);

                        total += price;
                        FirebaseWishlist lists=snapshot1.getValue(FirebaseWishlist.class);
                        keys.add(snapshot1.getKey().toString());
                        items.add(lists);
                    }
                    cart_adapter=new cart_viewHolder(Cart.this,keys,items,dbref);
                    recyclerView.setAdapter(cart_adapter);
                    COUNT=keys.size();
                    itemCount.setText(COUNT+" ITEMS");
                }else {
                    goneLayouts();
                    EmptyLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        dbref.child("BILL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    total=Integer.parseInt(snapshot.child("totalamount").getValue().toString());
                    totalprice_text.setText(snapshot.child("totalamount").getValue().toString());
                    if(total<799 && COUNT==1)
                        shippingFee.setText("40");
                    else
                        shippingFee.setText(snapshot.child("shipping").getValue().toString());
                    couponDiscount.setText(snapshot.child("coupon").getValue().toString());
                    totalMRP.setText(snapshot.child("totalprice").getValue().toString());
                    totalDiscount.setText(snapshot.child("discount").getValue().toString());
                    Grandtotal.setText(snapshot.child("totalamount").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Cart.this,chooseAddress.class));
                finish();
            }
        });

        CouLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (couponLayout.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(CouLayout,
                            new AutoTransition());
                    couponLayout.setVisibility(View.GONE);
                }
                else {
                    TransitionManager.beginDelayedTransition(CouLayout,
                            new AutoTransition());
                    couponLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        coupon_ref=FirebaseDatabase.getInstance().getReference("COUPONS");

        couponCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(EditCoupon.getText()))
                    EditCoupon.setError("Enter Code");
                else {
                    coupon_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                int APPLY=0,CASHBACK=0;
                                boolean v=false;
                                String cc,ky;
                                for(DataSnapshot snapshot1:snapshot.getChildren()){
                                    ky=snapshot1.getKey().toString();
                                    cc=snapshot1.child("code").getValue().toString();
                                    CASHBACK= Integer.parseInt(snapshot1.child("coupon").getValue().toString());
                                    if(EditCoupon.getText().equals(cc)){
                                        APPLY=1;
                                        v=gtData(ky);
                                        break;
                                    }

                                }
                                if(APPLY==1){
                                    if(!v) {
                                        dbref.child("BILL").child("coupon").setValue(CASHBACK);
                                        Toast.makeText(Cart.this,"Coupon applied",Toast.LENGTH_SHORT).show();
                                    }else
                                        Toast.makeText(Cart.this,"Already Used",Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(Cart.this,"Invalid Coupon Code",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

    }
    public boolean gtData(String ky){

        coupon_ref.child(ky).child("APPLIED").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int COUNTER=0;
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        if(snapshot1.child("user").getValue().toString().equals(user.getUid())){
                            COUNTER=1;
                            break;
                        }
                    }
                    if(COUNTER==1)
                        VAL=1;
                    else
                        VAL=0;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if(VAL==1)
            return true;
        return false;
    }
}