package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.ChatMessageAdapter;
import com.highfive.highfive.model.Message;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.responseModels.NameValuePairs;
import com.highfive.highfive.responseModels.Obj;
import com.highfive.highfive.responseModels.Values;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatFragment extends Fragment {

    @InjectView(R.id.chat)                  RecyclerView chatRv;
    @InjectView(R.id.message_text)          EditText messageText;
    @InjectView(R.id.send_message_button)   ImageButton sendMessage;
    @InjectView(R.id.chat_attach_file)      ImageButton attachFile;

    private Socket socket;
    private String chatToken;
    private String authToken;
    private Order order;
    private List<Message> messages;
    private ChatMessageAdapter adapter;
    private Profile profile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.inject(this, v);

        chatRv.setAdapter(adapter);

        authToken = HighFiveHttpClient.getTokenCookie().getValue();
        Bundle bundle = getArguments();
        order = bundle.getParcelable("order");
        chatToken = order.getChatToken();

        Gson gson = new Gson();

        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.path = "/api/socket.io";
        opts.query = "key=" + authToken + ":" + chatToken;


        try {
            socket = IO.socket("https://yareshu.ru", opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.emit("history", 0);
                //socket.disconnect();
            }

        }).on("message", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                String s = gson.toJson(args[0]);
                Type objType = new TypeToken<Obj>(){}.getType();
                Obj obj = gson.fromJson(s, objType);
                NameValuePairs tmp = obj.getNameValuePairs();
                Message msg = new Message();
                msg.setId(tmp.getId());
                msg.setUser(tmp.getUser());
                msg.setText(tmp.getText());
                msg.setTime(tmp.getTime());
                msg.setUser(tmp.getUser());
                messages.add(msg);
                adapter.setMessages(messages);
                FragmentActivity act = getActivity();
                if (act != null) {
                    act.runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                chatRv.scrollToPosition(messages.size() - 1);
                    });

                }

            }
        }).on("history", args -> {
            String s = gson.toJson(args[1]);
            Type collectionType = new TypeToken<Values<Collection<Obj>>>(){}.getType();
            Values<Collection<Obj>> tmp = gson.fromJson(s, collectionType);
            List<Obj> list = new ArrayList<Obj>(tmp.getValues());
            for (int i = 0; i < list.size(); i++) {
                Message msg = new Message();
                msg.setId(list.get(i).getNameValuePairs().getId());
                msg.setOrder(list.get(i).getNameValuePairs().getOrder());
                msg.setText(list.get(i).getNameValuePairs().getText());
                msg.setTime(list.get(i).getNameValuePairs().getTime());
                msg.setUser(list.get(i).getNameValuePairs().getUser());
                messages.add(msg);
            }
            Collections.reverse(messages);
            adapter.setMessages(messages);
            FragmentActivity act = getActivity();
            if (act != null) {
                act.runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    chatRv.scrollToPosition(messages.size() - 1);
                });
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}
                int x = 0;
        });
        socket.connect();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("message", messageText.getText());
                messageText.setText("");
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);

        messages = new ArrayList<Message>();
        adapter = new ChatMessageAdapter(messages, getContext(), profile);
    }
}