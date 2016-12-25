package com.highfive.highfive.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by heat_wave on 25.12.16.
 */

public class HighFiveHttpClient {
    private static final String BASE_URL = "http://95.213.191.61/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static PersistentCookieStore cookieStore;

    public static void initCookieStore(Context context) {
        if (cookieStore == null) {
            cookieStore = new PersistentCookieStore(context);
            client.setCookieStore(cookieStore);
        }
    }

    public static void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie);
    }

    public static void clearCookies() {
        cookieStore.clear();
    }

    public static Cookie getCookie(String name) {
        for (Cookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}