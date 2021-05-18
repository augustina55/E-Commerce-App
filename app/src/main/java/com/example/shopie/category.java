package com.example.shopie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class category extends AppCompatActivity {

    CardView cardMen,cardWomen,Cardkid;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        cardMen=findViewById(R.id.cardMen);
        cardMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(category.this,category_view.class);
                intent.putExtra("CAT","MEN");
                startActivity(intent);
            }
        });
        cardWomen=findViewById(R.id.cardWomen);
        cardWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(category.this,category_view.class);
                intent.putExtra("CAT","WOMEN");
                startActivity(intent);
            }
        });

        Cardkid=findViewById(R.id.Cardkid);
        Cardkid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(category.this,kids.class);
                intent.putExtra("CAT","KIDS");
                startActivity(intent);
            }
        });
    }
}