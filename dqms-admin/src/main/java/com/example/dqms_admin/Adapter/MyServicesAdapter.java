package com.example.dqms_admin.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Interface.IAllDoctorsLoadListener;
import com.example.dqms_admin.Interface.IRecyclerItemSelectedListener;
import com.example.dqms_admin.Model.Services;
import com.example.dqms_admin.R;
import com.example.dqms_admin.ServicesActivity;

import java.util.ArrayList;
import java.util.List;

public class MyServicesAdapter extends RecyclerView.Adapter<MyServicesAdapter.MyViewHolder> {

    List<Services> servicesList;
    List<CardView> cardViewList;
    ServicesActivity activity;

    public MyServicesAdapter(List<Services> servicesList, ServicesActivity activity) {
        cardViewList = new ArrayList<>();
        this.servicesList = servicesList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity)
                .inflate(R.layout.layout_services, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.serviceName.setText(servicesList.get(position).getName());

        if(!cardViewList.contains(holder.serviceCard))
            cardViewList.add(holder.serviceCard);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for(CardView cardView:cardViewList){
                    cardView.setCardBackgroundColor(activity.getResources()
                            .getColor(R.color.white));
                }
                holder.serviceCard.setCardBackgroundColor(activity.getResources()
                        .getColor(R.color.turquoise));

                String selectedService = servicesList.get(pos).getName();
                Log.i("service adapter", "onItemSelectedListener: " + selectedService);
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView serviceName;
        CardView serviceCard;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener){
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceName = (TextView) itemView.findViewById(R.id.serviceTV);
            serviceCard = (CardView) itemView.findViewById(R.id.serviceCard);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
