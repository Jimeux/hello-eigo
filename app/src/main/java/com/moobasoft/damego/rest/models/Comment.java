package com.moobasoft.damego.rest.models;

import android.os.Parcelable;

import java.util.Date;

public class Comment implements Parcelable {

    private int id;
    private User user;
    private String body;
    private int upVotes;
    private Date createdAt;

    public Comment() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *  Code to implement Parcelable
     */

    public Comment(android.os.Parcel source) {
        id        = source.readInt();
        user      = source.readParcelable(User.class.getClassLoader());
        body      = source.readString();
        upVotes   = source.readInt();
        createdAt = new Date(source.readLong());
    }

    @Override
    public int describeContents() { return id; }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(user, flags);
        dest.writeString(body);
        dest.writeInt(upVotes);
        dest.writeLong(createdAt.getTime());
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {

        public Comment createFromParcel(android.os.Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
