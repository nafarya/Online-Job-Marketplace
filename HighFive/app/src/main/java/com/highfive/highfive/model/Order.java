package com.highfive.highfive.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dan on 02.12.16.
 */

public class Order implements Parcelable {
    private String orderdId;
    private String theme;
    private String description;
    private Date date;
    private String deadLine;
    private String orderCreatorId;
    private ArrayList<Bid> bidlist = new ArrayList<>();
    private String subjectId;
    private String status;
    private String type;
    private String offer;
    private String title;

    public Order() {}

    public Order(String orderId, String themeId) {
        this.orderdId = orderId;
        this.theme = themeId;
    }

    public Order(String orderId, String themeId, String description) {
        this.orderdId = orderId;
        this.theme = themeId;
        this.description = description;
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

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getStatus() {
        if (status.equals("active")) {
            return "Открыт";
        } else {
            return "Завершен";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderdId);
        dest.writeString(theme);
        dest.writeString(description);

        //dest.writeString(date);

        dest.writeString(orderCreatorId);
        dest.writeList(bidlist);
        dest.writeString(subjectId);
        dest.writeString(status);
        dest.writeString(type);
        dest.writeString(offer);
        dest.writeString(deadLine);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Bid createFromParcel(Parcel in) {
            return new Bid(in);
        }

        public Bid[] newArray(int size) {
            return new Bid[size];
        }
    };

    // "De-parcel object
    public Order(Parcel in) {
        orderdId = in.readString();
        theme = in.readString();
        description = in.readString();
        orderCreatorId  = in.readString();
        bidlist = in.readArrayList(Bid.class.getClassLoader());
        subjectId = in.readString();
        status = in.readString();
        type = in.readString();
        offer = in.readString();
        deadLine = in.readString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getdeadLine() {
        String time = deadLine.substring(0, 4) + "." + deadLine.substring(5, 7) + "." +
                deadLine.substring(8, 10) + " " + deadLine.substring(11, 16);
        return time;
    }

    public void setdeadLine(String deadLine) {
        this.deadLine = deadLine;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
