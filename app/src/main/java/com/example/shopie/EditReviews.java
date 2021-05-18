package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditReviews extends AppCompatActivity {

    CardView orderBtn;
    RatingBar rating;
    FirebaseAuth mAuth;
    DatabaseReference dbref,rating_ref,rating_ref2;
    FirebaseUserProfile fuser;
    EditText reviewEdit;
    ImageView backpress;
    String user,fullname,Imageurl;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditReviews.this,OrdersPage.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reviews);
        mAuth=FirebaseAuth.getInstance();
        backpress=findViewById(R.id.backpress);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        user = mAuth.getCurrentUser().getUid();
        rating=findViewById(R.id.rating);
        reviewEdit=findViewById(R.id.reviewEdit);
        dbref= FirebaseDatabase.getInstance().getReference("USER_PROFILE");
        dbref.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    fullname=snapshot.child("firstname").getValue().toString() +", "+
                            snapshot.child("lastname").getValue().toString();
                    Imageurl = snapshot.child("imageurl").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        rating_ref=FirebaseDatabase.getInstance().getReference("REVIEWS").child(user);

        rating_ref2=FirebaseDatabase.getInstance().getReference("USER_REVIEWS");
        rating_ref2.child(getIntent().getStringExtra("CAT").toString())
                .child(getIntent().getStringExtra("DBKEY").toString())
                .child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    rating.setRating(Float.parseFloat(snapshot.child("rating").getValue().toString()));
                    reviewEdit.setText(snapshot.child("review").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        orderBtn=findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM,yyy");
                LocalDateTime now = LocalDateTime.now();
                String date=dtf.format(now);

                rating_ref.child(getIntent().getStringExtra("CAT").toString())
                        .child(getIntent().getStringExtra("DBKEY").toString())
                        .child("rating").setValue(rating.getRating());

                //user Rating
                FirebaseReviews freview=new FirebaseReviews();
                freview.setDate(date);
                freview.setName(fullname);
                freview.setRating(rating.getRating());
                if(reviewEdit.getText().length()==0)
                    freview.setReview("NULL");
                else
                    freview.setReview(reviewEdit.getText().toString());

                rating_ref2.child(getIntent().getStringExtra("CAT").toString())
                        .child(getIntent().getStringExtra("DBKEY").toString())
                        .child(user).setValue(freview);

                Toast.makeText(EditReviews.this,"Thank you for your feedback",Toast.LENGTH_SHORT).show();
            }
        });
    }
}