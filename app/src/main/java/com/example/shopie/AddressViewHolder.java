package com.example.shopie;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AddressViewHolder extends RecyclerView.Adapter<AddressViewHolder.myHolder>  {
    DatabaseReference dbref;
    Context mcontext;
    ArrayList<String> keys;
    ArrayList<FirebaseAddress> fadd;
    String FROM;

    public AddressViewHolder(Context mcontext, ArrayList<String> keys, ArrayList<FirebaseAddress> fadd, DatabaseReference dbref,String FROM) {
        this.mcontext = mcontext;
        this.keys = keys;
        this.fadd = fadd;
        this.dbref=dbref;
        this.FROM=FROM;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myHolder(LayoutInflater.from(mcontext).inflate(R.layout.address_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, final int position) {
        holder.textName.setText(fadd.get(position).getFullname());
        holder.textPhone.setText(fadd.get(position).getPhone());
        holder.textAdd1.setText(fadd.get(position).getAdd1());
        holder.textAdd2.setText(fadd.get(position).getAdd2());
        holder.textType.setText(fadd.get(position).getType());

        if(FROM.equals("ACCOUNT"))
            holder.radioBtn.setVisibility(View.GONE);
        else
            holder.radioBtn.setVisibility(View.VISIBLE);

        holder.radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbref.child("CURRENT").child("key").setValue(keys.get(position));
                Toast.makeText(mcontext,"Address Selected!",Toast.LENGTH_SHORT).show();
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(mcontext,v);
                popupMenu.getMenuInflater().inflate(R.menu.menu2,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.nav_delete:
                                dbref.child("ADDRESS").child(keys.get(position)).removeValue();
                                Toast.makeText(mcontext,"Deleted!",Toast.LENGTH_LONG).show();
                                break;
                            case R.id.nav_edit:
                                Intent intent=new Intent(mcontext, AddAddress.class);
                                intent.putExtra("EDIT","YES");
                                intent.putExtra("NAME",fadd.get(position).getFullname());
                                intent.putExtra("PHONE",fadd.get(position).getPhone());
                                intent.putExtra("ADD1",fadd.get(position).getAdd1());
                                intent.putExtra("ADD2",fadd.get(position).getAdd2());
                                intent.putExtra("TYPE",fadd.get(position).getType());
                                intent.putExtra("CITY",fadd.get(position).getCity());
                                intent.putExtra("STATE",fadd.get(position).getState());
                                intent.putExtra("PIN",fadd.get(position).getPincode());
                                intent.putExtra("KEY",keys.get(position));
                                mcontext.startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return fadd.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        ImageView more;
        RadioButton radioBtn;
        TextView textName,textAdd1,textAdd2,textPhone,textType;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            more=itemView.findViewById(R.id.imgaeMore);
            textName=itemView.findViewById(R.id.textName);
            textPhone=itemView.findViewById(R.id.textPhone);
            textAdd1=itemView.findViewById(R.id.textAdd1);
            textAdd2=itemView.findViewById(R.id.textAdd2);
            textType=itemView.findViewById(R.id.textType);
            radioBtn=itemView.findViewById(R.id.radioBtn);
        }
    }
}
