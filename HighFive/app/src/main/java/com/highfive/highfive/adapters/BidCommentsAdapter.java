package com.highfive.highfive.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.BidComment;

import java.util.List;

/**
 * Created by dan on 12.04.17.
 */

public class BidCommentsAdapter extends RecyclerView.Adapter<BidCommentsAdapter.ViewHolder>{

    private List<BidComment> comments;
    private String creatorId;

    public BidCommentsAdapter(List<BidComment> comments, String creatorId) {
        this.comments = comments;
        this.creatorId = creatorId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bid_list_comments_item, parent, false);
        return new BidCommentsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(comments.get(position).getText());
        holder.time.setText(comments.get(position).getCreatedAt());

        if (comments.get(position).getOwner().equals("student")) {
            holder.ownerType.setText("Студент");
        } else {
            holder.ownerType.setText("Преподаватель");
        }
        if (comments.get(position).get_id().equals(creatorId)) {
            holder.creatorMarker.setText("Автор заказа");
        } else {
            holder.creatorMarker.setText("Левый чувак");
            holder.creatorMarker.setBackgroundColor(Color.parseColor("#ADFF2F"));
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView time;
        TextView ownerType;
        TextView creatorMarker;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.bid_list_comment_item_comment);
            time = (TextView) itemView.findViewById(R.id.bid_list_comment_item_time);
            ownerType = (TextView) itemView.findViewById(R.id.bid_list_comment_item_owner_type);
            creatorMarker = (TextView) itemView.findViewById(R.id.bid_list_comment_item_creator_marker);
        }

    }
}
