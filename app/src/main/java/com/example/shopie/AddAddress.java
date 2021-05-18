package com.example.shopie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddAddress extends AppCompatActivity {

    double lat=0.0,lng=0.0;
    Button locationBtn;
    ImageView backpress;
    EditText editName,editPhone,editCity,editState,editPin,editAdd1,editAdd2;
    Button homeType,workType,saveBTN;
    String type="NULL",key="NULL";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference dbref;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(AddAddress.this,UserAddress.class);
        intent.putExtra("FROM",getIntent().getStringExtra("FROM"));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbref = FirebaseDatabase.getInstance().getReference().child("USER_ADDRESS").child(user.getUid());

        setContentView(R.layout.add_address);
        locationBtn=findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoLocation geoLocation = new GeoLocation(getApplicationContext());

                Location l = geoLocation.getLocation();

                if (l != null) {
                    lat = l.getLatitude();
                    lng = l.getLongitude();

                    Geocoder geocoder = new Geocoder(AddAddress.this, Locale.getDefault());

                    List<Address> addresses  = null;
                    try {
                        addresses = geocoder.getFromLocation(l.getLatitude(),l.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String zip = addresses.get(0).getPostalCode();
                    String country = addresses.get(0).getCountryName();

                    editCity.setText(city);
                    editPin.setText(zip);
                    editState.setText(state);
                 }
            }
        });

        backpress=findViewById(R.id.backpress);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editName=findViewById(R.id.editName);
        editPhone=findViewById(R.id.editPhone);
        editPin=findViewById(R.id.editPin);
        editCity=findViewById(R.id.editCity);
        editState=findViewById(R.id.editState);
        editAdd1=findViewById(R.id.editAdd1);
        editAdd2=findViewById(R.id.editAdd2);

        homeType=findViewById(R.id.homeType);
        homeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="HOME";
                homeType.setBackground(getResources().getDrawable(R.drawable.button2));
                workType.setBackground(getResources().getDrawable(R.drawable.button1));
            }
        });
        workType=findViewById(R.id.workType);
        workType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="WORK";
                homeType.setBackground(getResources().getDrawable(R.drawable.button1));
                workType.setBackground(getResources().getDrawable(R.drawable.button2));
            }
        });

        saveBTN=findViewById(R.id.saveBTN);

        String edit=getIntent().getStringExtra("EDIT");
        if(edit.equals("YES")){
            editName.setText(getIntent().getStringExtra("NAME"));
            editPhone.setText(getIntent().getStringExtra("PHONE"));
            editCity.setText(getIntent().getStringExtra("CITY"));
            editState.setText(getIntent().getStringExtra("STATE"));
            editPin.setText(getIntent().getStringExtra("PIN"));
            editAdd1.setText(getIntent().getStringExtra("ADD1"));
            editAdd2.setText(getIntent().getStringExtra("ADD2"));
            if(getIntent().getStringExtra("TYPE").equals("HOME")){
                type=getIntent().getStringExtra("TYPE");
                homeType.setBackground(getResources().getDrawable(R.drawable.button2));
                workType.setBackground(getResources().getDrawable(R.drawable.button1));
            }else{
                type=getIntent().getStringExtra("TYPE");
                homeType.setBackground(getResources().getDrawable(R.drawable.button1));
                workType.setBackground(getResources().getDrawable(R.drawable.button2));
            }
            key=getIntent().getStringExtra("KEY");
        }
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editName.getText().toString().isEmpty())
                    editName.setError("Please enter name");
                else if(editPhone.getText().toString().isEmpty())
                    editPhone.setError("Please enter phone");
                else if(editCity.getText().toString().isEmpty())
                    editCity.setError("Please enter city");
                else if(editPin.getText().toString().isEmpty())
                    editPin.setError("Please enter pincode");
                else if(editState.getText().toString().isEmpty())
                    editState.setError("Please enter state");
                else if(editAdd1.getText().toString().isEmpty())
                    editAdd1.setError("Please enter valid address");
                else if(editAdd2.getText().toString().isEmpty())
                    editAdd2.setError("Please enter valid address");
                else if(type.equals("NULL"))
                    Toast.makeText(AddAddress.this,"Please select address type",Toast.LENGTH_SHORT).show();
                else
                    SaveAddress();
            }
        });
    }
    private void SaveAddress(){
        FirebaseAddress fadd=new FirebaseAddress();
        fadd.setFullname(editName.getText().toString().trim());
        fadd.setPhone(editPhone.getText().toString().trim());
        fadd.setPincode(editPhone.getText().toString().trim());
        fadd.setCity(editCity.getText().toString().trim());
        fadd.setState(editState.getText().toString().trim());
        fadd.setAdd1(editAdd1.getText().toString().trim());
        fadd.setAdd2(editAdd2.getText().toString().trim());
        fadd.setType(type);
        if(key.equals("NULL")) {
            String ky= String.valueOf(dbref.push().getKey());
            dbref.child("ADDRESS").child(ky).setValue(fadd);
            dbref.child("CURRENT").child(ky).child("key").setValue(ky);
        }else
            dbref.child("ADDRESS").child(key).setValue(fadd);
        Toast.makeText(AddAddress.this,"Address added!",Toast.LENGTH_SHORT).show();

    }
}
