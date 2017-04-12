package com.highfive.highfive.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dan on 12.04.17.
 */

public class BidComment implements Parcelable {
    private String updatedAt;
    private String createdAt;
    private String text;
    private String owner;
    private String _id;

    public BidComment() {}

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        String time = createdAt.substring(11, 16) + " " + createdAt.substring(8, 10) + "." +
                createdAt.substring(5, 7) + "." + createdAt.substring(0, 4);
        return time;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(updatedAt);
        dest.writeString(createdAt);
        dest.writeString(text);
        dest.writeString(owner);
        dest.writeString(_id);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public BidComment createFromParcel(Parcel in) {
            return new BidComment(in);
        }

        public BidComment[] newArray(int size) {
            return new BidComment[size];
        }
    };

    // "De-parcel object
    public BidComment(Parcel in) {
        updatedAt = in.readString();
        createdAt = in.readString();
        text = in.readString();
        owner = in.readString();
        _id = in.readString();
    }
}
