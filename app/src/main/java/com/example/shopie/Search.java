package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    RecyclerView searchlistMen;
    SearchViewHolder Adapter;
    ArrayList<String> Men_keys,Men_names;
    DatabaseReference dbref_men;
    EditText SearchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        SearchField=findViewById(R.id.SearchField);

        searchlistMen=findViewById(R.id.searchlistMen);
        searchlistMen.setLayoutManager(new LinearLayoutManager(this));
        SearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(SearchField.getText().length()!=0){
                    FirebaseSearch(SearchField.getText().toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dbref_men=FirebaseDatabase.getInstance().getReference();


    }
    public void FirebaseSearch(final String s){
        dbref_men.child("MEN").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Men_keys=new ArrayList<>();
                    Men_names=new ArrayList<>();
                    int COUNT=0;
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        String clr=snapshot1.child("product details").child("color").getValue().toString();
                        String name=snapshot1.child("product details").child("name").getValue().toString();
                        String brand=snapshot1.child("product details").child("brand").getValue().toString();
                        String cat=snapshot1.child("product details").child("category").getValue().toString();
                        String desc=snapshot1.child("specifications").child("desc").getValue().toString();
                        if(s.toLowerCase().contains(cat.toLowerCase())){
                            Men_keys.add(snapshot1.getKey());
                            Men_names.add(cat);
                            COUNT++;
                        }else if(s.toLowerCase().contains(name.toLowerCase())){
                            Men_keys.add(snapshot1.getKey());
                            Men_names.add(name);
                            COUNT++;
                        }else if(s.toLowerCase().contains(brand.toLowerCase())){
                            Men_keys.add(snapshot1.getKey());
                            Men_names.add(brand);
                            COUNT++;
                        }else if(s.toLowerCase().contains(clr.toLowerCase())){
                            Men_keys.add(snapshot1.getKey());
                            Men_names.add(clr);
                            COUNT++;
                        }else if(s.toLowerCase().contains(desc.toLowerCase())){
                            Men_keys.add(snapshot1.getKey());
                            Men_names.add(desc);
                            COUNT++;
                        }


                        if(COUNT==10)
                            break;
                    }
                    Adapter=new SearchViewHolder(Search.this,Men_keys,Men_names);
                    searchlistMen.setAdapter(Adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}