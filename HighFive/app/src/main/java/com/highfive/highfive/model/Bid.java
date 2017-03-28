package com.highfive.highfive.model;

/**
 * Created by dan on 08.03.17.
 */

public class Bid {
    private int price;
    private String bidCreatorId;

    public Bid(int price, String id) {
        this.price = price;
        this.bidCreatorId = id;
    }

    public String getBidCreatorId() {
        return bidCreatorId;
    }

    public void setBidCreatorId(String bidCreatorId) {
        this.bidCreatorId = bidCreatorId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
