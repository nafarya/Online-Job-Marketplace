package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.highfive.highfive.R;
import com.highfive.highfive.services.auth.Authenticator;
import com.highfive.highfive.services.messaging.Chat;
import com.highfive.highfive.services.messaging.ChatHolder;

/**
 * Created by heat_wave on 12/4/16.
 */


public class ChatFragment extends Fragment {

    private RecyclerView chatView;
    private FirebaseRecyclerAdapter chatAdapter;
    private EditText message;
    private DatabaseReference ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        message = (EditText) v.findViewById(R.id.message_text);
        v.findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.push().setValue(new Chat(Authenticator.getCurrentUser().getEmail(),
                        Authenticator.getCurrentUser().getUid(), message.getText().toString()));
                message.setText("");
            }
        });
        chatView = (RecyclerView) v.findViewById(R.id.chat);
        chatView.setHasFixedSize(true);
        chatView.setLayoutManager(new LinearLayoutManager(getContext()));

        ref = FirebaseDatabase.getInstance().getReference();
        ref.limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
                    Chat msg = msgSnapshot.getValue(Chat.class);
                    Log.i("Chat", msg.getName() + ": " + msg.getText());
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("Chat", "The read failed: " + firebaseError.getDetails());
            }
        });

        chatAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(Chat.class, android.R.layout.two_line_list_item, ChatHolder.class, ref) {
            @Override
            public void populateViewHolder(ChatHolder chatMessageViewHolder, Chat chatMessage, int position) {
                chatMessageViewHolder.setName(chatMessage.getName());
                chatMessageViewHolder.setText(chatMessage.getText());
            }
        };
        chatView.setAdapter(chatAdapter);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatAdapter.cleanup();
    }
}