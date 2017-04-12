package com.highfive.highfive.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by dan on 08.03.17.
 */

public class Bid implements Parcelable {
    private double price;
    private String bidCreatorId;
    private String orderId;
    private String createdAt;
    private String updatedAt;
    private String bidId;
    private ArrayList<BidComment> bidComments;

    public void addBidComment(BidComment comment) {
        bidComments.add(comment);
    }

    public ArrayList<BidComment> getBidComments() {
        return bidComments;
    }

    public void setBidComments(ArrayList<BidComment> bidComments) {
        this.bidComments = bidComments;
    }


    public Bid(double price, String id) {
        this.price = price;
        this.bidCreatorId = id;
        bidComments = new ArrayList<>();
    }

    public String getBidCreatorId() {
        return bidCreatorId;
    }

    public void setBidCreatorId(String bidCreatorId) {
        this.bidCreatorId = bidCreatorId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(price);
        dest.writeString(bidCreatorId);
        dest.writeString(orderId);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(bidId);
        dest.writeList(bidComments);
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
    public Bid(Parcel in) {
        price = in.readDouble();
        bidCreatorId = in.readString();
        orderId = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        bidId= in.readString();
        bidComments = in.readArrayList(BidComment.class.getClassLoader());
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }
}
