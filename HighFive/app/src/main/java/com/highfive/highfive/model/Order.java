package com.highfive.highfive.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dan on 02.12.16.
 */

public class Order implements Parcelable {
    @SerializedName("id")
    private String orderdId;

    @SerializedName("theme")
    private String theme;

    @SerializedName("description")
    private String description;

    @SerializedName("deadline")
    private String deadLine;

    @SerializedName("creator")
    private String orderCreatorId;

    @SerializedName("subject")
    private String subjectId;

    @SerializedName("status")
    private String status;

    @SerializedName("type")
    private String type;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("offer")
    private String offer;

    @SerializedName("title")
    private String title;

    public void setBidsIds(List<String> bidsIds) {
        this.bidsIds = bidsIds;
    }

    @SerializedName("bids")
    private List<String> bidsIds = new ArrayList<>();

    private ArrayList<Bid> bidlist = new ArrayList<>();

    public Order() {
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

    public int getNumOfBids() {
        return bidsIds.size();
        //return bidArraySize;
    }

    public void setBidArraySize(int bidArraySize) {
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
