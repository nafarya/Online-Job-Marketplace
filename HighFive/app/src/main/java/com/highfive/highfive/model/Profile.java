package com.highfive.highfive.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 01.12.16.
 */

public class Profile {
    private String email;

    @SerializedName("username")
    @Expose
    private String username;

    private double balance;
    private double positiveRating;
    private double negativeRating;
    private double neutralRating;
    private String avatar;
    private ArrayList<String> StudentOrderIdList;

    private String name;
    private String surname;
    private String uid;

    private double rate;
    private boolean statusVIP;
    private String description;

    private String type;
    private ArrayList<String> comments;
    private ArrayList<Order> orderList;

    public Profile(String name, String surname) {
        this.name = name;
        this.surname = surname;
        rate = 0;
        comments = new ArrayList<>();
        orderList = new ArrayList<>();
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

    public Profile(String email, String uid, String username, String balance, double negativeRating, double positiveRating,
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
        comments = new ArrayList<>();
        orderList = new ArrayList<>();
        StudentOrderIdList = new ArrayList<>();

    }

    public void addOrder(Order order) {
        orderList.add(order);
    }

    public ArrayList<Order> getAllOrders() {
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

    public double getRate() {
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

    public double getPositiveRating() {
        return positiveRating;
    }

    public void setPositiveRating(int positiveRating) {
        this.positiveRating = positiveRating;
    }

    public double getNegativeRating() {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Order> getStudentOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            if (uid.equals(orderList.get(i).getOrderCreatorId())) {
                orders.add(orderList.get(i));
            }
        }
        return orders;
    }

    public ArrayList<Order> getOrdersByFilter(String subjectId, String orderTypeId) {
        ArrayList<Order> orders = new ArrayList<>();
        boolean subjFlag = false;
        boolean typeFlag = false;
        for (int i = 0; i < orderList.size(); i++) {
            if (subjectId.equals("all") || orderList.get(i).getSubjectId().equals(subjectId)) {
                subjFlag = true;
            }
            if (orderTypeId.equals("all") || orderList.get(i).getType().equals(orderTypeId)) {
                typeFlag = true;
            }
            if (subjFlag && typeFlag) {
                orders.add(orderList.get(i));
            }
            subjFlag = false;
            typeFlag = false;
        }
        return orders;
    }

    public Order getOrderById(String id) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getOrderdId().equals(id)) {
                return orderList.get(i);
            }
        }
        return null;
    }

    public double getNeutralRating() {
        return neutralRating;
    }

    public void setNeutralRating(double neutralRating) {
        this.neutralRating = neutralRating;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ArrayList<String> getStudentOrderIdList() {
        return StudentOrderIdList;
    }

    public void setStudentOrderIdList(ArrayList<String> studentOrderIdList) {
        StudentOrderIdList = studentOrderIdList;
    }

    public void setOrderList(ArrayList<Order> list) {
        orderList = list;
    }

    public ArrayList<Order> getActiveOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getStatus().equals("Открыт")) {
                orders.add(orderList.get(i));
            }
        }
        return orders;
    }

    public ArrayList<Order> getCompletedOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getStatus().equals("Завершен")) {
                orders.add(orderList.get(i));
            }
        }
        return orders;
    }

    public double getProfileRating() {
        double sum = positiveRating + negativeRating + neutralRating;
        double wSum = positiveRating - negativeRating + 0.5*neutralRating;
        if (sum == 0) {
            return 0.0;
        }
        return (wSum / sum) * 5.0;
    }
}
