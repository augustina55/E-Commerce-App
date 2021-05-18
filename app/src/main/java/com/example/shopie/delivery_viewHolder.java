package com.example.shopie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class delivery_viewHolder extends RecyclerView.Adapter<delivery_viewHolder.myHolder>  {
    Context mcontext;
    ArrayList<String> pic;

    public delivery_viewHolder(Context mcontext, ArrayList<String> pic) {
        this.mcontext = mcontext;
        this.pic = pic;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new delivery_viewHolder.myHolder(LayoutInflater.from(mcontext).inflate(R.layout.delivery_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        Glide.with(mcontext)
                .load(pic.get(position))
                .centerCrop().into(holder.pic);
        holder.deliveryDate.setText(getDate());

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
        int yy=c.get(Calendar.YEAR);
        String dd=String.valueOf(d) + " " + months.get(mm) + " " + String.valueOf(yy);

        return dd;
    }

    @Override
    public int getItemCount() {
        return pic.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView deliveryDate;
        public myHolder(@NonNull View itemView) {
            super(itemView);
            deliveryDate=itemView.findViewById(R.id.deliveryDate);
            pic=itemView.findViewById(R.id.pic);
        }
    }
}
