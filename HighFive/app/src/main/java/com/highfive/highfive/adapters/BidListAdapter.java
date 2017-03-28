package com.highfive.highfive.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.Bid;

import java.util.List;

/**
 * Created by dan on 08.03.17.
 */

public class BidListAdapter extends RecyclerView.Adapter<BidListAdapter.ViewHolder> {

    List<Bid> bidList;

    public BidListAdapter(List<Bid> bidList) {
        this.bidList = bidList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bidlist_item, parent, false);
        return new BidListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id.setText("ID пользователя " + bidList.get(position).getBidCreatorId());
        holder.price.setText("Ставка " + String.valueOf(bidList.get(position).getPrice()) + "Руб.");
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.bidlist_item_id);
            price = (TextView) itemView.findViewById(R.id.bidlist_item_price);
        }
    }
}
