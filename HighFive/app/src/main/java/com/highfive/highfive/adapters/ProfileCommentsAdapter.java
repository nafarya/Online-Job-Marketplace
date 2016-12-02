package com.highfive.highfive.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highfive.highfive.R;

import java.util.List;

/**
 * Created by dan on 01.12.16.
 */

public class ProfileCommentsAdapter extends RecyclerView.Adapter<ProfileCommentsAdapter.ViewHolder> {

    List<String> comments;

    public ProfileCommentsAdapter(List<String> comments) {
        this.comments = comments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_comment_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.comment.setText(comments.get(position));
    }

    @Override
    public int getItemCount() {
        Log.i("asdasdasd", comments.size() + "");
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView comment;

        public ViewHolder(View itemView) {
            super(itemView);
            comment = (TextView) itemView.findViewById(R.id.profile_comment_list_item_id);
        }

    }
}
