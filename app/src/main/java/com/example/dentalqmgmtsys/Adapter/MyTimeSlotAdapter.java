package com.example.dentalqmgmtsys.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.util.ULocale;
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
import com.example.dentalqmgmtsys.Models.TimeSlot;
import com.example.dentalqmgmtsys.R;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context) {

        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.timeSlotTV.setText(new StringBuilder(Common.convertTimeSlotToString(i)));
        if(timeSlotList.size() == 0) //If all position is available, just show
        {
            //If all time slot is empty, all card is enable
            holder.timeSlotCard.setEnabled(true);

            holder.timeSlotDescriptionTV.setText("Available");
            holder.timeSlotDescriptionTV.setTextColor(context.getResources()
                    .getColor(android.R.color.black));
            holder.timeSlotTV.setTextColor(context.getResources()
                    .getColor(android.R.color.black));
            holder.timeSlotCard.setCardBackgroundColor(context.getResources()
                    .getColor(android.R.color.white));
        }
        else // if there is position that is full
        {
            for(TimeSlot slotValue:timeSlotList){
                //Loop all time slot
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if(slot == i)// If slot == position
                {
                    holder.timeSlotCard.setEnabled(false);
                    //Setting tag for all time slot when full
                    holder.timeSlotCard.setTag(Common.DISABLE_TAG);
                    holder.timeSlotCard.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.darker_gray));
                    holder.timeSlotDescriptionTV.setText("Full");
                    holder.timeSlotDescriptionTV.setTextColor(context.getResources()
                            .getColor(android.R.color.black));
                    holder.timeSlotTV.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                }
            }
        }

        //Add all card to list
        if(!cardViewList.contains(holder.timeSlotCard))
            cardViewList.add(holder.timeSlotCard);

        //Check if card time slot is available
            holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int pos) {
                    //Loop all card in card list
                    for (CardView cardView : cardViewList) {
                        if (cardView.getTag() == null) // Only available card time slot
                            cardView.setCardBackgroundColor(context.getResources()
                                    .getColor(android.R.color.white));
                    }
                    //Selected card will be change
                    holder.timeSlotCard.setCardBackgroundColor(context.getResources()
                            .getColor(R.color.turquoise));

                    //After selecting, will send broadcast to enable the next button
                    Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                    intent.putExtra(Common.KEY_TIME_SLOT, i); //Put an index to the time that is selected
                    intent.putExtra(Common.KEY_STEP, 2);
                    localBroadcastManager.sendBroadcast(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView timeSlotTV, timeSlotDescriptionTV;
        CardView timeSlotCard;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSlotCard = (CardView) itemView.findViewById(R.id.timeSlotCard);
            timeSlotTV = (TextView) itemView.findViewById(R.id.timeSlotTV);
            timeSlotDescriptionTV = (TextView) itemView.findViewById(R.id.timeSlotDescriptionTV);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());

        }
    }
}
