package com.highfive.highfive.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.ProfileComment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dan on 01.12.16.
 */

public class ProfileCommentsAdapter extends RecyclerView.Adapter<ProfileCommentsAdapter.ViewHolder> {

    private List<ProfileComment> comments;
    private List<Profile> profileList;
    private Context context;

    public ProfileCommentsAdapter(List<ProfileComment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_comment_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(comments.get(position).getText());

        if (profileList != null) {
            Picasso.with(context).load("https://yareshu.ru/" + profileList.get(position).getAvatar()).into(holder.avatar);
            holder.username.setText(profileList.get(position).getUsername());
            holder.ratingBar.setRating((float)profileList.get(position).getProfileRating());
        }
        if (comments.get(position).getRate() == -1) {
            holder.ratingImage.setImageResource(R.drawable.ic_thumb_down_black_36dp);
        }
        if (comments.get(position).getRate() == 1) {
            holder.ratingImage.setImageResource(R.drawable.ic_thumb_up_black_36dp);
        }
        if (comments.get(position).getRate() == 0) {
            holder.ratingImage.setImageResource(R.drawable.ic_thumbs_up_down_black_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setProfileList(List<Profile> profileList) {
        this.profileList = profileList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private RatingBar ratingBar;
        private ImageView ratingImage;
        private TextView username;
        private TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.profile_comment_avatar);
            ratingBar = (RatingBar) itemView.findViewById(R.id.profile_comment_rating_bar);
            ratingImage = (ImageView) itemView.findViewById(R.id.profile_comment_rating_image);
            username = (TextView) itemView.findViewById(R.id.profile_comment_username);
            text = (TextView) itemView.findViewById(R.id.profile_comment_text);
        }

    }
}
