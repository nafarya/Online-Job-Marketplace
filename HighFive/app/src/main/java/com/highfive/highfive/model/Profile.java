package com.highfive.highfive.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 01.12.16.
 */

public class Profile {
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("balance")
    @Expose
    private double balance;

    @SerializedName("rating")
    @Expose
    private Rating rating;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("secondName")
    @Expose
    private String secondName;

    @SerializedName("id")
    @Expose
    private String uid;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("orders")
    @Expose
    private ArrayList<String> studentOrderIdList;

    @SerializedName("reviews")
    @Expose
    private ArrayList<ProfileComment> comments;

    private int activeOrders;

    public Profile(){
        studentOrderIdList = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public Profile(String firstName, String secondName) {
        this.firstName = firstName;
        this.secondName = secondName;
        comments = new ArrayList<>();
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
        return type.toLowerCase();
    }

    public Profile(String email, String uid, String username, String balance,
                   String firstName, String secondName, String type) {
        this.email = email;
        this.uid = uid;

        this.username = username;
        this.firstName = firstName;
        this.secondName = secondName;
        this.balance = Double.parseDouble(balance);

        this.type = type;
        comments = new ArrayList<>();

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void addComment(ProfileComment comment) {
        comments.add(comment);
    }

    public List<ProfileComment> getAllComments() {
        return comments;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ArrayList<String> getStudentOrderIdList() {
        return studentOrderIdList;
    }

    public void setStudentOrderIdList(ArrayList<String> studentOrderIdList) {
        this.studentOrderIdList = studentOrderIdList;
    }

    public double getProfileRating() {
        double sum = rating.getPositive() + rating.getNegative() + rating.getNeutral();
        double wSum = rating.getPositive() - rating.getNegative() + 0.5* rating.getNeutral();
        if (sum == 0) {
            return 0.0;
        }
        return (wSum / sum) * 5.0;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Rating getRating() {
        return rating;
    }

    public int getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(int activeOrders) {
        this.activeOrders = activeOrders;
    }
}
