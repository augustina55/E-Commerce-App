package com.example.shopie;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CancelOrder extends AppCompatActivity {
    RadioGroup radioGroup;
    CardView SubmitBtn;
    RadioButton radioButton;
    ImageView pic,backpress;
    TextView brandName,descText,sizeText;
    EditText details;
    DatabaseReference order_ref,order_ref2;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CancelOrder.this,OrdersPage.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_order);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        details=findViewById(R.id.details);
        backpress=findViewById(R.id.backpress);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        radioGroup=findViewById(R.id.radioGroup);
        SubmitBtn=findViewById(R.id.SubmitBtn);
        order_ref= FirebaseDatabase.getInstance().getReference("ORDERS").child(user.getUid());
        order_ref2= FirebaseDatabase.getInstance().getReference("ORDERS_STATUS").child(user.getUid());
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                int selectedId=0;
                selectedId=radioGroup.getCheckedRadioButtonId();
                radioButton=(RadioButton)findViewById(selectedId);
                if(selectedId==0)
                    Toast.makeText(CancelOrder.this,"Select reason",Toast.LENGTH_SHORT).show();
                else{
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM,yyy");
                    LocalDateTime now = LocalDateTime.now();
                    String date=dtf.format(now);

                    FirebaseOrderStatus fstatus=new FirebaseOrderStatus();
                    fstatus.setDate(date);
                    fstatus.setStatus("Cancelled");
                    fstatus.setLocation("NULL");
                    order_ref.child("STATUS")
                            .child(getIntent().getStringExtra("ORDERKEY").toString())
                            .setValue(fstatus);
                    Toast.makeText(CancelOrder.this,"Order Cancelled",Toast.LENGTH_SHORT).show();
                    order_ref2.child(getIntent().getStringExtra("ORDERKEY").toString()).setValue("Cancelled");
                    Intent intent=new Intent(CancelOrder.this,returnSuccess.class);
                    intent.putExtra("PIC","CANCEL");
                    startActivity(intent);
                }

            }
        });
        pic=findViewById(R.id.pic);
        Glide.with(this)
                .load(getIntent().getStringExtra("PIC"))
                .fitCenter().into(pic);

        brandName=findViewById(R.id.brandName);
        brandName.setText(getIntent().getStringExtra("BRAND"));

        descText=findViewById(R.id.descText);
        descText.setText(getIntent().getStringExtra("NAME"));

        sizeText=findViewById(R.id.sizeText);
        sizeText.setText("Size: " +getIntent().getStringExtra("SIZE").toString());
    }
}