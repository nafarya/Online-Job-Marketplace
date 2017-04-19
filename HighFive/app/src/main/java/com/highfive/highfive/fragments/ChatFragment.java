package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highfive.highfive.R;
import com.highfive.highfive.util.HighFiveHttpClient;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatFragment extends Fragment {

    private Socket socket;
    private String chatToken;
    private String authToken = "d9d357eaa641ccb8f7a699296749e8f8";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        authToken = HighFiveHttpClient.getTokenCookie().getValue();
        Bundle bundle = getArguments();
        //chatToken = bundle.getString("orderToken");

        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.query = "key=" + authToken + ":" + chatToken;

        try {
            socket = IO.socket("https://yareshu.ru/api/socket.io", opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.emit("history");
                //socket.disconnect();
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                int x = 0;

            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}
                int x = 0;
        });
        socket.connect();

        return v;
    }

}