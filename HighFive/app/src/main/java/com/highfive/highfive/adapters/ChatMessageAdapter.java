package com.highfive.highfive.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.Message;
import com.highfive.highfive.model.Profile;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dan on 22.04.17.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private List<Message> messages;
    private Context context;
    private Profile profile;

    public ChatMessageAdapter(List<Message> messages, Context context, Profile profile) {
        this.messages = messages;
        this.context = context;
        this.profile = profile;
    }

    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
        return new ChatMessageAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatMessageAdapter.ViewHolder holder, int position) {
        holder.text.setText(messages.get(position).getText());
        holder.time.setText(messages.get(position).getTime());
        if (messages.get(position).getUser().equals(profile.getUid())) {
            holder.username.setText(profile.getUsername());
            Picasso.with(context).load("https://yareshu.ru/" + profile.getAvatar()).into(holder.avatar);
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView username;
        private TextView time;
        private TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.chat_message_avatar);
            username = (TextView) itemView.findViewById(R.id.chat_message_username);
            time = (TextView) itemView.findViewById(R.id.chat_message_time);
            text = (TextView) itemView.findViewById(R.id.chat_message_text);
        }
    }
}
