package com.example.dqms_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Model.ServicesOffered;
import com.example.dqms_admin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyServicesOfferedAdapter extends RecyclerView.Adapter<MyServicesOfferedAdapter.ViewHolder> {

    ArrayList<ServicesOffered> list;
    Context context;

    public MyServicesOfferedAdapter(ArrayList<ServicesOffered> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ServicesOffered model = list.get(position);

        Picasso.get().load(model.getServiceImage()).placeholder(R.drawable.dental_crown).into(holder.itemImage);

        holder.itemDescription.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemDescription;
        ImageView itemImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemDescription = itemView.findViewById(R.id.itemDescription);

            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}
