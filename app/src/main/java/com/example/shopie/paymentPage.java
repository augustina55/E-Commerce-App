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
import android.view.View;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class paymentPage extends AppCompatActivity {
    offerViewHolder offerAdapter;
    RecyclerView offerRecyclerView;
    TextView textShowMore;
    ArrayList<String> list,order_keys,order_cats,order_size,order_qty;
    CardView orderBtn;
    DatabaseReference dbref,Bill_ref,order_ref,cart_ref;
    int OFFER_CLICKED=0;
    LinearLayout cashLayout,onlineLayout;
    String pay_type="NULL";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ImageView backpress;
    TextView shippingFee,couponDiscount,totalMRP,totalDiscount,Grandtotal;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(paymentPage.this,chooseAddress.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_page);
        textShowMore=findViewById(R.id.textShowMore);
        backpress=findViewById(R.id.backpress);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        shippingFee=findViewById(R.id.shippingFee);
        couponDiscount=findViewById(R.id.couponDiscount);
        totalMRP=findViewById(R.id.totalMRP);
        totalDiscount=findViewById(R.id.totalDiscount);
        Grandtotal=findViewById(R.id.Grandtotal);
        orderBtn=findViewById(R.id.orderBtn);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        offerRecyclerView=findViewById(R.id.offerRecyclerView);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbref= FirebaseDatabase.getInstance().getReference("OFFERS");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list=new ArrayList<>();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                        list.add(snapshot1.child("offer").getValue().toString());

                    offerAdapter=new offerViewHolder(paymentPage.this,list,"0");
                    offerRecyclerView.setAdapter(offerAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        order_ref= FirebaseDatabase.getInstance().getReference("ORDERS").child(user.getUid());
        cart_ref= FirebaseDatabase.getInstance().getReference("SHOPPING_BAG").child(user.getUid());
        Bill_ref= FirebaseDatabase.getInstance().getReference("SHOPPING_BAG").child(user.getUid());
        Bill_ref.child("BILL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
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

        cart_ref.child("CART").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    order_keys = new ArrayList<>();
                    order_cats=new ArrayList<>();
                    order_qty = new ArrayList<>();
                    order_size=new ArrayList<>();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        order_keys.add(snapshot1.child("dbkey").getValue().toString());
                        order_cats.add(snapshot1.child("cat").getValue().toString());
                        order_qty.add(snapshot1.child("qty").getValue().toString());
                        order_size.add(snapshot1.child("size").getValue().toString());
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        textShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OFFER_CLICKED++;
                if(OFFER_CLICKED==1){
                    textShowMore.setText("Show Less");
                    offerAdapter=new offerViewHolder(paymentPage.this,list,"1");
                }else{
                    textShowMore.setText("Show More");
                    offerAdapter=new offerViewHolder(paymentPage.this,list,"0");
                }
                offerRecyclerView.setAdapter(offerAdapter);
            }
        });

        onlineLayout=findViewById(R.id.onlineLayout);
        cashLayout=findViewById(R.id.cashLayout);
        cashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashLayout.setBackground(getResources().getDrawable(R.drawable.selectd_bg));
                onlineLayout.setBackground(getResources().getDrawable(R.drawable.plain_bg));
                pay_type="CASH";
                orderBtn.setVisibility(View.VISIBLE);
            }
        });
        onlineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashLayout.setBackground(getResources().getDrawable(R.drawable.plain_bg));
                onlineLayout.setBackground(getResources().getDrawable(R.drawable.selectd_bg));
                pay_type="ONLINE";
                orderBtn.setVisibility(View.VISIBLE);
            }
        });




        orderBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDateTime now = LocalDateTime.now();
                String date=dtf.format(now);
                if(pay_type.equals("CASH")){
                    for(int i=0;i<order_cats.size();i++){
                        FirebaseOrders forders=new FirebaseOrders();
                        forders.setCat(order_cats.get(i));
                        forders.setDbkey(order_keys.get(i));
                        forders.setQty(order_qty.get(i));
                        forders.setSize(order_size.get(i));
                        forders.setGrandDiscount(totalDiscount.getText().toString());
                        forders.setGrandtotal(Grandtotal.getText().toString());
                        forders.setCoupon(couponDiscount.getText().toString());
                        forders.setShipping(shippingFee.getText().toString());
                        forders.setPaymentMode(pay_type);
                        forders.setOrderdate(date);
                        forders.setDelivery_date(getDate());
                        forders.setName(getIntent().getStringExtra("NAME"));
                        forders.setPhone(getIntent().getStringExtra("PHONE"));
                        forders.setAddress(getIntent().getStringExtra("ADD"));

                        String ky=order_ref.push().getKey();
                        order_ref.child("ORDER").child(ky).setValue(forders);
                        //status
                        FirebaseOrderStatus fstatus=new FirebaseOrderStatus();
                        fstatus.setDate(date);
                        fstatus.setStatus("Processing");
                        fstatus.setLocation("NULL");
                        order_ref.child("STATUS").child(ky).setValue(fstatus);
                    }
                    cart_ref.removeValue();
                    startActivity(new Intent(paymentPage.this,AckPage.class));
                    finish();
                }else if(pay_type.equals("ONLINE")){
                    //Online Payment

                }else
                    Toast.makeText(paymentPage.this,"Please selecte payment method",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getDate(){
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date);
        try{
            c.setTime(formatter.parse(strDate));
        }catch(ParseException e){
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_MONTH, 7);
        int d=c.get(Calendar.DAY_OF_MONTH);
        int mm = c.get(Calendar.MONTH);
        String dd=String.valueOf(d) + " " + months.get(mm);

        return dd;
    }
}