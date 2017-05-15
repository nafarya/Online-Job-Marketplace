package com.highfive.highfive.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.BidComment;
import com.highfive.highfive.model.Profile;

import java.util.List;

/**
 * Created by dan on 12.04.17.
 */

public class BidCommentsAdapter extends RecyclerView.Adapter<BidCommentsAdapter.ViewHolder>{

    public void setComments(List<BidComment> comments) {
        this.comments = comments;
    }

    private List<BidComment> comments;
    private String creatorId;
    private List<Profile> commentProfiles;

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
        if (commentProfiles != null) {
            holder.username.setText(getUsername(comments.get(position).get_id()));
        } else {
            holder.username.setVisibility(View.GONE);
        }
        if (comments.get(position).get_id().equals(creatorId)) {
            holder.creatorMarker.setText("Автор заказа");
        } else {
            holder.creatorMarker.setBackgroundColor(Color.parseColor("#ADFF2F"));
            if (comments.get(position).getOwner().equals("student")) {
                holder.creatorMarker.setText("Студент");
            } else {
                holder.creatorMarker.setText("Преподаватель");
            }

        }
    }

    private String getUsername(String id) {
        for (int i = 0; i < commentProfiles.size(); i++) {
            if (commentProfiles.get(i).getUid().equals(id)) {
                return commentProfiles.get(i).getUsername();
            }

        }
        return "error";
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setCommentProfiles(List<Profile> commentProfiles) {
        this.commentProfiles = commentProfiles;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView time;
        TextView username;
        TextView creatorMarker;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.bid_list_comment_item_comment);
            time = (TextView) itemView.findViewById(R.id.bid_list_comment_item_time);
            username = (TextView) itemView.findViewById(R.id.bid_list_comment_item_username);
            creatorMarker = (TextView) itemView.findViewById(R.id.bid_list_comment_item_creator_marker);
        }

    }
}
