package com.highfive.highfive.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 18.04.17.
 */

public class Order implements Parcelable {

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("deadline")
    @Expose
    private String deadline;
    @SerializedName("offer")
    @Expose
    private int offer;
    @SerializedName("lastStatusChange")
    @Expose
    private String lastStatusChange;
    @SerializedName("status")
    @Expose
    private String status;
    //        @SerializedName("options")
//        @Expose
//        private List<Object> options = null;
    @SerializedName("files")
    @Expose
    private List<String> files = null;
    @SerializedName("bids")
    @Expose
    private List<String> bidsIds = null;

    @SerializedName("selectedBid")
    @Expose
    private String selectedBid;
    @SerializedName("chatToken")
    @Expose
    private String chatToken;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("theme")
    @Expose
    private String theme;

    private ArrayList<Bid> bidlist = new ArrayList<>();


    public final static Parcelable.Creator<Order> CREATOR = new Creator<Order>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Order createFromParcel(Parcel in) {
            Order instance = new Order();
            instance.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.subject = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.creator = ((String) in.readValue((String.class.getClassLoader())));
            instance.deadline = ((String) in.readValue((String.class.getClassLoader())));
            instance.offer = ((int) in.readValue((int.class.getClassLoader())));
            instance.lastStatusChange = ((String) in.readValue((String.class.getClassLoader())));
            instance.status = ((String) in.readValue((String.class.getClassLoader())));
//                in.readList(instance.options, (java.lang.Object.class.getClassLoader()));
            in.readList(instance.files, (java.lang.String.class.getClassLoader()));
            in.readList(instance.bidsIds, (java.lang.String.class.getClassLoader()));
            instance.selectedBid = ((String) in.readValue((String.class.getClassLoader())));
            instance.chatToken = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.bidlist = in.readArrayList(Bid.class.getClassLoader());
            return instance;
        }

        public Order[] newArray(int size) {
            return (new Order[size]);
        }

    };

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getOffer() {
        return offer;
    }

    public void setOffer(int offer) {
        this.offer = offer;
    }

    public String getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(String lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }

    public String getStatus() {
        switch (status) {
            case "active":
                return "В аукционе";
            case "in work":
                return "В работе";
            case "waiting for author":
                return "Ждет подтверждения";
            case "cancelled":
                return "Отменен";
            case "on guarantee":
                return "На гарантии";
            case "in rework":
                return "На доработке";
            case "closed":
                return "Завершен";
            default:
                return null;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

//        public List<Object> getOptions() {
//            return options;
//        }
//
//        public void setOptions(List<Object> options) {
//            this.options = options;
//        }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<String> getBids() {
        return bidsIds;
    }

    public void setBids(List<String> bidsIds) {
        this.bidsIds = bidsIds;
    }

    public String getSelectedBid() {
        return selectedBid;
    }

    public void setSelectedBid(String selectedBid) {
        this.selectedBid = selectedBid;
    }

    public String getChatToken() {
        return chatToken;
    }

    public void setChatToken(String chatToken) {
        this.chatToken = chatToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(updatedAt);
        dest.writeValue(createdAt);
        dest.writeValue(title);
        dest.writeValue(description);
        dest.writeValue(subject);
        dest.writeValue(type);
        dest.writeValue(creator);
        dest.writeValue(deadline);
        dest.writeValue(offer);
        dest.writeValue(lastStatusChange);
        dest.writeValue(status);
//            dest.writeList(options);
        dest.writeList(files);
        dest.writeList(bidsIds);
        dest.writeValue(selectedBid);
        dest.writeValue(chatToken);
        dest.writeValue(id);
        dest.writeList(bidlist);
    }

    public int describeContents() {
        return 0;
    }

    public String getdeadLine() {
        String time = deadline.substring(0, 4) + "." + deadline.substring(5, 7) + "." +
                deadline.substring(8, 10) + " " + deadline.substring(11, 16);
        return time;
    }
    public int getNumOfBids() {
        return bidsIds.size();
        //return bidArraySize;
    }

    public void addBid(String id, int price) {
        Bid bid = new Bid(price, id);
        bidlist.add(bid);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
