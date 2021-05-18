package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    static final float END_SCALE = 0.7f;
    LinearLayout contentView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuClick,menpic,womenpic,kidPic;
    ImageView favourites,imageView3;
    private long backpressTime;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    DatabaseReference db_ref;
    categoryViewHolder adapter;
    ArrayList<String> men_imgs,men_keys,women_imgs,women_keys,top_imgs,top_keys,topwomen_imgs,topwomen_keys;
    LinearLayout offers;

    RecyclerView recyclerViewMEN,recyclerViewWOMEN,recyclerViewTopBrands,recyclerViewTopBrandsWomen;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            if(backpressTime+2000 > System.currentTimeMillis()){
                super.onBackPressed();
                return;
            }else{
                Toast.makeText(Home.this,"Press back again to exit!", Toast.LENGTH_SHORT).show();
            }
            backpressTime=System.currentTimeMillis();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        menuClick=(ImageView)findViewById(R.id.menu_icon);
        offers=findViewById(R.id.offers);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //navigation
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        navigationView=(NavigationView)findViewById(R.id.navigation);
        navigationDrawer();
        contentView=(LinearLayout)findViewById(R.id.contentv);

        menpic=findViewById(R.id.menpic);
        menpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,category_view.class);
                intent.putExtra("CAT","MEN");
                startActivity(intent);
            }
        });

        womenpic=findViewById(R.id.womenpic);
        womenpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,category_view.class);
                intent.putExtra("CAT","WOMEN");
                startActivity(intent);
            }
        });
        kidPic=findViewById(R.id.kidPic);
        kidPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,kids.class);
                intent.putExtra("CAT","KIDS");
                startActivity(intent);
            }
        });
        favourites=findViewById(R.id.favourites);
        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,whislist.class));
                finish();
            }
        });
        imageView3=findViewById(R.id.imageView3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Cart.class));
                finish();
            }
        });

        db_ref= FirebaseDatabase.getInstance().getReference("Images");
        recyclerViewMEN=findViewById(R.id.recyclerViewMEN);
        GridLayoutManager gridLayoutManager3=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerViewMEN.setLayoutManager(gridLayoutManager3);
        db_ref.child("home").child("bag").child("MEN").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    men_imgs=new ArrayList<>();
                    men_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        men_keys.add(snapshot1.getKey().toString());
                        men_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(Home.this,men_keys,men_imgs,"MEN","category");
                    recyclerViewMEN.setAdapter(adapter);
                    recyclerViewMEN.setVisibility(View.VISIBLE);
                }else
                    recyclerViewMEN.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerViewWOMEN=findViewById(R.id.recyclerViewWOMEN);
        GridLayoutManager gridLayoutManager2=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerViewWOMEN.setLayoutManager(gridLayoutManager2);
        db_ref.child("home").child("bag").child("WOMEN").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    women_imgs=new ArrayList<>();
                    women_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        women_keys.add(snapshot1.getKey().toString());
                        women_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(Home.this,women_keys,women_imgs,"WOMEN","category");
                    recyclerViewWOMEN.setAdapter(adapter);
                    recyclerViewWOMEN.setVisibility(View.VISIBLE);
                }else
                    recyclerViewWOMEN.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerViewTopBrands=findViewById(R.id.recyclerViewTopBrands);
        recyclerViewTopBrandsWomen=findViewById(R.id.recyclerViewTopBrandsWomen);
        GridLayoutManager gridLayoutManager1=new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false);
        recyclerViewTopBrands.setLayoutManager(gridLayoutManager1);
        recyclerViewTopBrandsWomen.setLayoutManager(gridLayoutManager);
        db_ref.child("home").child("Top_Brands").child("MEN").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    top_imgs=new ArrayList<>();
                    top_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        top_keys.add(snapshot1.getKey().toString());
                        top_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(Home.this,top_keys,top_imgs,"MEN","brand");
                    recyclerViewTopBrands.setAdapter(adapter);
                    offers.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        db_ref.child("home").child("Top_Brands").child("WOMEN").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    topwomen_imgs=new ArrayList<>();
                    topwomen_keys=new ArrayList<>();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        topwomen_keys.add(snapshot1.getKey().toString());
                        topwomen_imgs.add(snapshot1.child("imageurl").getValue().toString());
                    }
                    adapter=new categoryViewHolder(Home.this,topwomen_imgs,topwomen_imgs,"WOMEN","brand");
                    recyclerViewTopBrandsWomen.setAdapter(adapter);
                    offers.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ImageView searchImg=findViewById(R.id.searchImg);
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Search.class));
                finish();
            }
        });

    }
    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        menuClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateDrawer();


    }

    private void animateDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorTransparent));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaleOffset=slideOffset*(1-END_SCALE);
                final float offsetScale=1-diffScaleOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xoffset=drawerView.getWidth() * slideOffset;
                final float xOffsetDiff=contentView.getWidth() * diffScaleOffset / 2;
                final float xTranslation = xoffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_account:
                startActivity(new Intent(Home.this,MyAccount.class));
                finish();
                break;
            case R.id.nav_cat:
                startActivity(new Intent(Home.this,category.class));
                finish();
                break;
            case R.id.nav_whislist:
                startActivity(new Intent(Home.this,whislist.class));
                finish();
                break;
            case R.id.nav_cart:
                startActivity(new Intent(Home.this,Cart.class));
                finish();
                break;
            case R.id.nav_orders:
                startActivity(new Intent(Home.this,OrdersPage.class));
                finish();
                break;
        }
        return true;
    }

}