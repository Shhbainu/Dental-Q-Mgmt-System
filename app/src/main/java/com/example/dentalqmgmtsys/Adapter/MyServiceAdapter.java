package com.example.dentalqmgmtsys.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Interface.IRecyclerItemSelectedListener;
import com.example.dentalqmgmtsys.Models.Service;
import com.example.dentalqmgmtsys.R;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;


public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.MyViewHolder> {

    Context context;
    List<Service> serviceList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_services,parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.serviceTV.setText(serviceList.get(i).getName());

        if(!cardViewList.contains(holder.serviceCard))
            cardViewList.add(holder.serviceCard);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //Set white background for all card that is not selected
                for(CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));

                //Set turquoise background if card is selected
                holder.serviceCard.setCardBackgroundColor(context.getResources()
                        .getColor(R.color.turquoise));

                //Send Broadcast to Add Appointment Activity to enable next button
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_SERVICE, serviceList.get(pos));
                intent.putExtra(Common.KEY_STEP, 1);
                localBroadcastManager.sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView serviceTV;
        CardView serviceCard;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            serviceTV = (TextView)itemView.findViewById(R.id.serviceTV);
            serviceCard = (CardView)itemView.findViewById(R.id.serviceCard);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
