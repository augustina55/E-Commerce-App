package com.example.shopie;

import android.content.Context;
import android.os.Build;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class listViewHolder extends RecyclerView.Adapter<listViewHolder.myHolder> {
    Context mcontetx;
    ArrayList<String> keys;
    DatabaseReference dbref;
    ArrayList<String> childKey;
    childListView adapter;

    public listViewHolder(Context mcontetx, ArrayList<String> keys,DatabaseReference dbref) {
        this.mcontetx = mcontetx;
        this.keys = keys;
        this.dbref=dbref;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myHolder(LayoutInflater.from(mcontetx).inflate(R.layout.list_view,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull final myHolder holder, int position) {
        holder.heading.setText(keys.get(position));
        holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (holder.hiddenView.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(holder.cardView,
                            new AutoTransition());
                    holder.hiddenView.setVisibility(View.GONE);
                    holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
                }
                else {
                    TransitionManager.beginDelayedTransition(holder.cardView,
                            new AutoTransition());
                    holder.hiddenView.setVisibility(View.VISIBLE);
                    holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
            }
        });
        holder.childRecyclerview.setLayoutManager(new LinearLayoutManager(mcontetx));
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    childKey=new ArrayList<>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        childKey.add(snapshot1.getKey().toString());
                    }
                    adapter=new childListView(mcontetx,childKey);
                    holder.childRecyclerview.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class myHolder extends RecyclerView.ViewHolder{
        TextView heading;
        LinearLayout hiddenView;
        CardView cardView;
        ImageView arrow;
        RecyclerView childRecyclerview;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            childRecyclerview=itemView.findViewById(R.id.childRecyclerview);
            heading=(TextView)itemView.findViewById(R.id.heading);
            hiddenView=(LinearLayout)itemView.findViewById(R.id.hidden_view);
            cardView=(CardView)itemView.findViewById(R.id.base_cardview);
            arrow=(ImageView)itemView.findViewById(R.id.arrow_button);
        }
    }
}
