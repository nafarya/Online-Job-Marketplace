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

import java.util.List;

import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * Created by dan on 22.04.17.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private List<Message> messages;
    private Context context;
    private Profile profile;
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;

    public ChatMessageAdapter(List<Message> messages, Context context, Profile profile) {
        this.messages = messages;
        this.context = context;
        this.profile = profile;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getUser().equals(profile.getUid())) {
            return MY_MESSAGE;
        } else {
            return OTHER_MESSAGE;
        }
    }

    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == MY_MESSAGE) {
             v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_2, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_1, parent, false);
        }
        return new ChatMessageAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatMessageAdapter.ViewHolder holder, int position) {
//        holder.text.setText(messages.get(position).getText());
//        holder.time.setText(messages.get(position).getTime());
//        if (messages.get(position).getUser().equals(profile.getUid())) {
//            holder.username.setText(profile.getUsername());
//            //Picasso.with(context).load("https://yareshu.ru/" + profile.getAvatar()).into(holder.avatar);
//        } else {
//
//        }
//        holder.tvMessage.setMaxWidth(80);
//        holder.tvMessage.setMaxLines(2);
//        holder.filename.setMaxWidth(80);
//        holder.filename.setMaxLines(2);

        holder.ivImage.setVisibility(View.GONE);
        holder.tvMessage.setVisibility(View.VISIBLE);
        holder.tvMessage.setText(messages.get(position).getText());
        holder.tvTime.setText(messages.get(position).getTime());


        if (messages.get(position).getMyFile() == null) {
            holder.filename.setVisibility(View.GONE);
        } else {
            holder.filename.setVisibility(View.VISIBLE);
        }

        if (messages.get(position).getText().equals("")) {
            //holder.filename.setText("https://yareshu.ru");
            if (messages.get(position).getMyFile() != null) {
                holder.tvMessage.setText(messages.get(position).getMyFile().getName());
                holder.filename.setText("https://yareshu.ru/uploads/" + messages.get(position).getMyFile().getPath());
                holder.filename.setMaxLines(2);
            }
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

        TextView tvMessage, tvTime;
        ImageView ivImage;
        ChatMessageView chatMessageView;
        TextView filename;

        public ViewHolder(View itemView) {
            super(itemView);
            chatMessageView = (ChatMessageView) itemView.findViewById(R.id.chatMessageView);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            filename = (TextView) itemView.findViewById(R.id.tv_file_message);
        }
    }
}
