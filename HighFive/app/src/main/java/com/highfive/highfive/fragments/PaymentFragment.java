package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.highfive.highfive.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dan on 15.04.17.
 */

public class PaymentFragment extends Fragment{

    @InjectView(R.id.webView)           WebView webView;

    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_webview, container, false);
        ButterKnife.inject(this, v);
        Bundle bundle = getArguments();
        uid = bundle.getString("uid");
        webView.loadUrl("https://yareshu.ru/api/payment/form/" + uid);
        return v;
    }
}

