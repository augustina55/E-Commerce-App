package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;
import java.util.List;

public class product extends AppCompatActivity {
    private List<SliderItem> mSliderItems;
    DatabaseReference dbref,dbref2,dbref3,wish_dbref,cart_ref;
    String CAT,DB_KEY,category,pic_url;
    SliderView sliderView;
    RecyclerView sizeRecyclerview,horizontalRecycler;
    ArrayList<String> size_keys,size,size_qty;
    size_ViewHolder size_adapter;
    TextView brand_name,name,price,actual_price,discount,strikeOut,taxText;
    TextView material,size_fit,product_details,rating,color,ratingtext,wishlistText,cart_text;
    item_ViewHolder adapter;
    ArrayList<String> imgs,keys;
    ArrayList<FirebaseItems> items;
    LinearLayout ratingLayout,ratingVisibility,wishlistBtn,cartBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ImageView wishlistImg,imageView8,favImg;
    int SELECTED_SIZE=0;
    int dsc,prc,cost,ttl,BILLING=0;
    FirebaseBill billings;

    ReviewViewHolder review_adaptr;
    TextView ratingCount;
    ArrayList<FirebaseReviews> review_items;
    DatabaseReference review_db;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);
        wishlistBtn=findViewById(R.id.wishlistBtn);
        wishlistText=findViewById(R.id.wishlistText);

        taxText=findViewById(R.id.taxText);
        favImg=findViewById(R.id.favImg);
        favImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(product.this,whislist.class));
                finish();
            }
        });
        imageView8=findViewById(R.id.imageView8);
        imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(product.this,Cart.class));
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        CAT=getIntent().getStringExtra("CAT").toString();
        DB_KEY=getIntent().getStringExtra("KEY").toString();
        cartBtn=findViewById(R.id.cartBtn);
        ratingtext=findViewById(R.id.ratingtext);
        ratingCount=findViewById(R.id.ratingCount);
        material=findViewById(R.id.material);
        size_fit=findViewById(R.id.size_fit);
        product_details=findViewById(R.id.product_details);
        color=findViewById(R.id.color);
        brand_name=findViewById(R.id.brand_name);
        name=findViewById(R.id.name);
        price=findViewById(R.id.price);
        actual_price=findViewById(R.id.actual_price);
        discount=findViewById(R.id.discount);
        strikeOut=findViewById(R.id.strikeOut);
        strikeOut.setPaintFlags(strikeOut.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        actual_price.setPaintFlags(actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        dbref= FirebaseDatabase.getInstance().getReference().child(CAT).child(DB_KEY);

        wishlistImg=findViewById(R.id.wishlistImg);
        wish_dbref=FirebaseDatabase.getInstance().getReference("WISHLIST").child(user.getUid());
        wish_dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        if(snapshot1.child("cat").getValue().toString().equals(CAT)){
                            if(snapshot1.child("dbkey").getValue().toString().equals(DB_KEY)){
                                wishlistText.setText("WISHLISTED");
                                wishlistImg.setBackgroundResource(R.drawable.ic_baseline_favorite_red);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        cart_text=findViewById(R.id.cart_text);
        cart_ref=FirebaseDatabase.getInstance().getReference("SHOPPING_BAG").child(user.getUid());
        cart_ref.child("CART").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        if(snapshot1.child("cat").getValue().toString().equals(CAT)){
                            if(snapshot1.child("dbkey").getValue().toString().equals(DB_KEY)){
                                cart_text.setText("GO TO BAG");
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        billings=new FirebaseBill();
        cart_ref.child("BILL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    BILLING=1;
                    billings.setTotalamount(Integer.parseInt(snapshot.child("totalamount").getValue().toString()));
                    billings.setTotalprice(Integer.parseInt(snapshot.child("totalprice").getValue().toString()));
                    billings.setDiscount(Integer.parseInt(snapshot.child("discount").getValue().toString()));
                    billings.setCoupon(Integer.parseInt(snapshot.child("coupon").getValue().toString()));
                    billings.setShipping(Integer.parseInt(snapshot.child("shipping").getValue().toString()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        sliderView = findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();



        dbref3= FirebaseDatabase.getInstance().getReference().child("SELECTED_SIZE").child(CAT).child(DB_KEY);
        dbref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    SELECTED_SIZE= Integer.parseInt(snapshot.child("size").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dbref.child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mSliderItems = new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        if(snapshot1.getKey().equals("1"))
                            pic_url=snapshot1.child("pic").getValue().toString();

                        SliderItem itms=new SliderItem();
                        itms.setImageUrl(snapshot1.child("pic").getValue().toString());
                        mSliderItems.add(itms);
                    }
                    SliderAdapterExample adapter = new SliderAdapterExample(product.this,mSliderItems);
                    sliderView.setSliderAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Size
        sizeRecyclerview=findViewById(R.id.sizeRecyclerview);

        dbref.child("product details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    color.setText(snapshot.child("color").getValue().toString());
                    name.setText(snapshot.child("name").getValue().toString());
                    category=snapshot.child("category").getValue().toString();
                    brand_name.setText(snapshot.child("brand").getValue().toString());
                    prc= Integer.parseInt(snapshot.child("price").getValue().toString());
                    dsc= Integer.parseInt(snapshot.child("discount").getValue().toString());
                    if(dsc==0)
                        cost=0;
                    else
                        cost= (prc * dsc)/100;

                    ttl = prc - cost;
                    String price_str=String.valueOf(ttl).substring(0,1)+","+String.valueOf(ttl).substring(1);
                    price.setText(price_str);
                    String s=snapshot.child("price").getValue().toString();
                    String actual_str=s.substring(0,1)+","+s.substring(1);
                    actual_price.setText(actual_str);

                    String dd="(" + snapshot.child("discount").getValue().toString() +"% OFF)";
                    discount.setText(dd);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        dbref.child("specifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    material.setText(snapshot.child("Material & Care").getValue().toString());
                    size_fit.setText(snapshot.child("Size & Fit").getValue().toString());
                    product_details.setText(snapshot.child("desc").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //rating
        final TextView reviewMore=findViewById(R.id.reviewMore);
        ratingVisibility=findViewById(R.id.ratingVisibility);
        ratingLayout=findViewById(R.id.ratingLayout);
        final RecyclerView RatingRecyclerview=findViewById(R.id.RatingRecyclerview);
        RatingRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        review_db=FirebaseDatabase.getInstance().getReference("USER_REVIEWS");
        review_db.child(CAT).child(DB_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ratingVisibility.setVisibility(View.VISIBLE);
                    ratingLayout.setVisibility(View.VISIBLE);
                    float Total_rating=0.0f;
                    review_items=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        FirebaseReviews freview=snapshot1.getValue(FirebaseReviews.class);
                        review_items.add(freview);
                        Total_rating += Float.parseFloat(snapshot.child("rating").getValue().toString());
                    }
                    review_adaptr=new ReviewViewHolder(product.this,review_items,5);
                    RatingRecyclerview.setAdapter(review_adaptr);
                    ratingtext.setText(String.valueOf(Total_rating));
                    ratingCount.setText(String.valueOf(Total_rating));
                }else {
                    ratingVisibility.setVisibility(View.GONE);
                    ratingLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reviewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reviewMore.getText().equals("View All")){
                    review_adaptr=new ReviewViewHolder(product.this,review_items,0);

                }else
                    review_adaptr=new ReviewViewHolder(product.this,review_items,5);

                RatingRecyclerview.setAdapter(review_adaptr);
            }
        });
        //suggestions
        horizontalRecycler=findViewById(R.id.horizontalRecycler);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.HORIZONTAL,false);
        horizontalRecycler.setLayoutManager(gridLayoutManager);
        dbref2=FirebaseDatabase.getInstance().getReference(CAT);
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int COUNTER=0;
                    imgs=new ArrayList<>();
                    keys=new ArrayList<>();
                    items=new ArrayList<>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        if(snapshot1.child("product details").child("brand").getValue().toString().equals(brand_name.getText())){
                            keys.add(snapshot1.getKey().toString());
                            FirebaseItems list = new FirebaseItems();
                            list.setPic(snapshot1.child("images").child("1").child("pic").getValue().toString());
                            list.setName(snapshot1.child("product details").child("name").getValue().toString());
                            list.setBrand(snapshot1.child("product details").child("brand").getValue().toString());
                            list.setDiscount(snapshot1.child("product details").child("discount").getValue().toString());
                            list.setPrice(snapshot1.child("product details").child("price").getValue().toString());
                            items.add(list);
                            COUNTER++;
                        }else if(snapshot1.child("product details").child("category").getValue().toString().equals(category)){
                            keys.add(snapshot1.getKey().toString());
                            FirebaseItems list = new FirebaseItems();
                            list.setPic(snapshot1.child("images").child("1").child("pic").getValue().toString());
                            list.setName(snapshot1.child("product details").child("name").getValue().toString());
                            list.setBrand(snapshot1.child("product details").child("brand").getValue().toString());
                            list.setDiscount(snapshot1.child("product details").child("discount").getValue().toString());
                            list.setPrice(snapshot1.child("product details").child("price").getValue().toString());
                            items.add(list);
                            COUNTER++;
                        }
                        if(COUNTER==15)
                            break;
                    }
                    adapter=new item_ViewHolder(product.this,keys,items,CAT);
                    horizontalRecycler.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wishlistText.getText().equals("WISHLIST")){
                    FirebaseWishlist wish_items=new FirebaseWishlist();
                    wish_items.setBrand(brand_name.getText().toString().trim());
                    wish_items.setCat(CAT);
                    wish_items.setDbkey(DB_KEY);
                    wish_items.setDiscount(String.valueOf(dsc));
                    wish_items.setPrice(String.valueOf(prc));
                    wish_items.setPic(pic_url);
                    wish_items.setColor(color.getText().toString().trim());
                    wish_items.setName(name.getText().toString().trim());
                    wish_items.setQty("0");
                    wish_items.setSize("0");
                    wish_dbref.push().setValue(wish_items);
                }else
                    Toast.makeText(product.this,"Item already added!",Toast.LENGTH_SHORT).show();

            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart_text.getText().equals("ADD TO BAG")){
                    if(SELECTED_SIZE==0){
                        final View dialogView = getLayoutInflater().inflate(R.layout.bottom_size, null);
                        final BottomSheetDialog dialog = new BottomSheetDialog(product.this);
                        dialog.setContentView(dialogView);
                        final RecyclerView sizeRecyclerview=dialogView.findViewById(R.id.sizeRecyclerview);
                        GridLayoutManager gridLayoutManager1=new GridLayoutManager(dialog.getContext(),7,GridLayoutManager.VERTICAL,false);
                        sizeRecyclerview.setLayoutManager(gridLayoutManager1);
                        dbref.child("size").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    size_keys=new ArrayList<>();
                                    size=new ArrayList<>();
                                    size_qty=new ArrayList<>();
                                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                                        size_keys.add(snapshot1.getKey());
                                        size.add(snapshot1.child("size").getValue().toString());
                                        size_qty.add(snapshot1.child("qty").getValue().toString());
                                    }
                                    size_adapter=new size_ViewHolder(product.this,size_keys,size,size_qty,dbref3);
                                    sizeRecyclerview.setAdapter(size_adapter);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        TextView sizeChart=dialogView.findViewById(R.id.sizeChart);

                        Button btnDone=dialogView.findViewById(R.id.btnDone);
                        btnDone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(cart_text.getText().equals("ADD TO BAG")){
                                    if(SELECTED_SIZE > 0) {
                                        FirebaseWishlist wish_items = new FirebaseWishlist();
                                        wish_items.setBrand(brand_name.getText().toString().trim());
                                        wish_items.setCat(CAT);
                                        wish_items.setDbkey(DB_KEY);
                                        wish_items.setDiscount(String.valueOf(dsc));
                                        wish_items.setPrice(String.valueOf(prc));
                                        wish_items.setPic(pic_url);
                                        wish_items.setColor(color.getText().toString().trim());
                                        wish_items.setName(name.getText().toString().trim());
                                        wish_items.setQty("1");
                                        wish_items.setSize(String.valueOf(SELECTED_SIZE));
                                        cart_ref.child("CART").push().setValue(wish_items);
                                        //total Update
                                        FirebaseBill fbill = new FirebaseBill();
                                        if(BILLING==1) {
                                            fbill.setCoupon(billings.getCoupon());
                                            fbill.setShipping(billings.getShipping());
                                            fbill.setDiscount(cost + billings.getDiscount());
                                            fbill.setTotalprice(prc + billings.getTotalprice());
                                            fbill.setTotalamount(ttl + billings.getTotalamount());
                                            cart_ref.child("BILL").setValue(fbill);
                                        }else{
                                            fbill.setCoupon(0);
                                            fbill.setShipping(0);
                                            fbill.setDiscount(cost);
                                            fbill.setTotalprice(prc);
                                            fbill.setTotalamount(ttl);
                                            cart_ref.child("BILL").setValue(fbill);
                                        }
                                        Toast.makeText(product.this,"Item Added!",Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }else
                                        Toast.makeText(product.this,"Select your size",Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(product.this,"Already in cart",Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog.show();
                    }
                }else{
                    startActivity(new Intent(product.this,Cart.class));
                    finish();
                }

            }
        });

    }
}