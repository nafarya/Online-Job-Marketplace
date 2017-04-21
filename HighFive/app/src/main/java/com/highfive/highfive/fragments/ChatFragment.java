package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.ChatMessageAdapter;
import com.highfive.highfive.model.Message;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.responseModels.NameValuePairs;
import com.highfive.highfive.responseModels.Obj;
import com.highfive.highfive.responseModels.Values;
import com.highfive.highfive.util.HighFiveHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatFragment extends Fragment {

    @InjectView(R.id.chat)          RecyclerView chatRv;

    private Socket socket;
    private String chatToken = "d9d357eaa641ccb8f7a699296749e8f8";
    private String authToken;
    private Order order;
    private List<Message> messages;
    private ChatMessageAdapter adapter;

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

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                int x = 0;

            }
        }).on("history", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                String s = gson.toJson(args[1]);
                try {
                    JSONArray array = new JSONArray(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Type collectionType = new TypeToken<Values<Collection<Obj>>>(){}.getType();
                Values<Collection<Obj>> wtf = gson.fromJson(s, collectionType);
                List<Obj> list = new ArrayList<Obj>(wtf.getValues());
                for (int i = 0; i < list.size(); i++) {
                    Message msg = new Message();
                    msg.setId(list.get(i).getNameValuePairs().getId());
                    msg.setOrder(list.get(i).getNameValuePairs().getOrder());
                    msg.setText(list.get(i).getNameValuePairs().getText());
                    msg.setTime(list.get(i).getNameValuePairs().getTime());
                    msg.setUser(list.get(i).getNameValuePairs().getUser());
                    messages.add(msg);
                }

                adapter.setMessages(messages);
                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}
                int x = 0;
        });
        socket.connect();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messages = new ArrayList<Message>();
        adapter = new ChatMessageAdapter(messages);
    }
}