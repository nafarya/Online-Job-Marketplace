package com.highfive.highfive.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;
import com.squareup.picasso.Picasso;

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
    private List<Profile> profileList;
    private Context context;

    public BidListAdapter(List<Bid> bidList, OnItemClickListener listener, Context context) {
        this.bidList = bidList;
        this.listener = listener;
        this.context = context;
    }

    public void setProfileList(List<Profile> list) {
        profileList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bid_list_item, parent, false);
        return new BidListAdapter.ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (profileList != null) {
            Picasso.with(context).load("https://yareshu.ru/" + profileList.get(position).getAvatar()).into(holder.avatar);
            holder.username.setText(profileList.get(position).getUsername());
            holder.ratingBar.setRating((float)profileList.get(position).getProfileRating());
        } else {
            holder.username.setText(bidList.get(position).getBidCreatorId());
        }
        holder.offer.setText((String.valueOf((int)bidList.get(position).getOffer())) + " Р");

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
        TextView username;
        TextView offer;
        TextView numOfComments;
        RatingBar ratingBar;
        Button button;
        OnItemClickListener listener;
        ImageView avatar;


        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.bidlist_item_username);
            offer = (TextView) itemView.findViewById(R.id.bidlist_item_price);
            numOfComments = (TextView) itemView.findViewById(R.id.bidList_item_num_of_comments);
            avatar = (ImageView) itemView.findViewById(R.id.bid_list_item_profile);
            this.listener = listener;
            itemView.setOnClickListener(this);


            button = (Button) itemView.findViewById(R.id.bidlist_item_button);
            Type profileType = new TypeToken<Profile>(){}.getType();
            Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
            if (profile.getType().equals("teacher")) {
                button.setVisibility(View.GONE);
            }

            ratingBar = (RatingBar) itemView.findViewById(R.id.bidlist_item_rating_bar);
            LayerDrawable drawable = (LayerDrawable) ratingBar.getProgressDrawable();
            Drawable progress = drawable.getDrawable(2);
            DrawableCompat.setTint(progress, context.getResources().getColor(R.color.gold));
            progress = drawable.getDrawable(1);
            DrawableCompat.setTintMode(progress, PorterDuff.Mode.DST_ATOP);
            DrawableCompat.setTint(progress, context.getResources().getColor(R.color.gold));
            DrawableCompat.setTintMode(progress, PorterDuff.Mode.SRC_ATOP);
            DrawableCompat.setTint(progress, context.getResources().getColor(R.color.darkGrey));
            progress = drawable.getDrawable(0);
            DrawableCompat.setTint(progress, context.getResources().getColor(R.color.darkGrey));
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }
}
