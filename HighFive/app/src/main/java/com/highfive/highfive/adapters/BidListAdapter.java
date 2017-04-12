package com.highfive.highfive.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by dan on 08.03.17.
 */

public class BidListAdapter extends RecyclerView.Adapter<BidListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int item);
    }

    private List<Bid> bidList;
    private OnItemClickListener listener;

    public BidListAdapter(List<Bid> bidList, OnItemClickListener listener) {
        this.bidList = bidList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bid_list_item, parent, false);
        return new BidListAdapter.ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id.setText("UserID: " + bidList.get(position).getBidCreatorId());
        holder.price.setText((String.valueOf((int)bidList.get(position).getPrice())) + " Р");

        int numOfComments = bidList.get(position).getBidComments().size();
        String comments = " ";
        if (numOfComments == 1) {
            comments += "Комментарий";
        }
        if (numOfComments > 1 && numOfComments < 5) {
            comments += "Комментария";
        }
        if (comments.equals(" ")) {
            comments += "Комментариев";
        }
        holder.numOfComments.setText(String.valueOf(numOfComments) + comments);
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView id;
        TextView price;
        TextView numOfComments;
        RatingBar ratingBar;
        Button button;
        OnItemClickListener listener;


        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.bidlist_item_id);
            price = (TextView) itemView.findViewById(R.id.bidlist_item_price);
            numOfComments = (TextView) itemView.findViewById(R.id.bidList_item_num_of_comments);
            this.listener = listener;
            itemView.setOnClickListener(this);


            button = (Button) itemView.findViewById(R.id.bidlist_item_button);
            Type profileType = new TypeToken<Profile>(){}.getType();
            Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
            if (profile.getType().equals("teacher")) {
                button.setVisibility(View.GONE);
            }

            ratingBar = (RatingBar) itemView.findViewById(R.id.bidlist_item_rating_bar);
            Drawable progress = ratingBar.getProgressDrawable();
            DrawableCompat.setTint(progress, Color.rgb(255,215,79));
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }
}
