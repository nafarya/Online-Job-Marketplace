package com.highfive.highfive.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

/**
 * Created by heat_wave on 25.12.16.
 */

public class HighFiveHttpClient {

    private static final String BASE_URL = "http://95.213.191.61/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static PersistentCookieStore cookieStore;

    public static void initCookieStore(Context context) {
        if (cookieStore == null) {
            cookieStore = new PersistentCookieStore(context);
            client.setCookieStore(cookieStore);
            Cookie token = getTokenCookie();
            if (token != null) {
                client.addHeader("api-token", token.getValue());
            }
        }
        client.setLoggingEnabled(true);
    }

    public static void addUidCookie(String uid) {
        BasicClientCookie cookie = new BasicClientCookie("id", uid);
        cookieStore.addCookie(cookie);
    }

    public static void addTokenCookie(String token) {
        BasicClientCookie cookie = new BasicClientCookie("token", token);
        client.addHeader("api-token", token);
        cookieStore.addCookie(cookie);
    }

    public static Cookie getUidCookie() {
        return getCookie("id");
    }

    public static Cookie getTokenCookie() {
        return getCookie("token");
    }

    public static void clearCookies() {
        cookieStore.clear();
    }

    private static Cookie getCookie(String name) {
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

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}