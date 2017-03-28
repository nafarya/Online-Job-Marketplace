package com.highfive.highfive.model;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dan on 02.12.16.
 */

public class Order {
    private String orderdId;
    private String theme;
    private String description;
    private Date date;
    private String orderCreatorId;
    private List<Bid> bidlist;

    public Order(String orderId, String themeId, String description) {
        this.orderdId = orderId;
        this.theme = themeId;
        this.description = description;
        bidlist = new ArrayList<>();
    }

    public void addBid(String id, int price) {
        Bid bid = new Bid(price, id);
        bidlist.add(bid);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOrderdId() {
        return orderdId;
    }

    public void setOrderdId(String orderdId) {
        this.orderdId = orderdId;
    }

    public String getOrderCreatorId() {
        return orderCreatorId;
    }

    public void setOrderCreatorId(String orderCreatorId) {
        this.orderCreatorId = orderCreatorId;
    }
}
