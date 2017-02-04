package com.highfive.highfive.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dan on 01.12.16.
 */

public class Profile {
    private String name;
    private String surname;
    private String uid;
    private String email;
    private String username;
    private double balance;
    private int positiveRating;
    private int negativeRating;
    private float rate;
    private boolean statusVIP;

    private String type;
    private List<String> comments;
    private List<Order> orderList;

    public Profile(String name, String surname) {
        this.name = name;
        this.surname = surname;
        rate = 0;
        comments = new LinkedList<>();
        orderList = new LinkedList<>();
        statusVIP = false;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public Profile(String email, String uid, String username, String balance, int negativeRating, int positiveRating,
                   String firstName, String secondName, String type) {
        this.email = email;
        this.uid = uid;

        this.username = username;
        this.name = firstName;
        this.surname = secondName;
        this.positiveRating = positiveRating;
        this.negativeRating = negativeRating;
        if (positiveRating != 0) {
            this.rate = (1 - (negativeRating / positiveRating)) * 100;
        } else {
            this.rate = 0;
        }
        this.balance = Double.parseDouble(balance);

        this.type = type;
        comments = new LinkedList<>();
        orderList = new LinkedList<>();
    }

    public void addOrder(Order order) {
        orderList.add(order);
    }

    public List<Order> getAllOrders() {
        return orderList;
    }

    public Order getOrderByInd(int ind) {
        return orderList.get(ind);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public boolean isStatusVIP() {
        return statusVIP;
    }

    public void setStatusVIP(boolean statusVIP) {
        this.statusVIP = statusVIP;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public List<String> getAllComments() {
        return comments;
    }

    public int getPositiveRating() {
        return positiveRating;
    }

    public void setPositiveRating(int positiveRating) {
        this.positiveRating = positiveRating;
    }

    public int getNegativeRating() {
        return negativeRating;
    }

    public void setNegativeRating(int negativeRating) {
        this.negativeRating = negativeRating;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
