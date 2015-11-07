package com.moobasoft.helloeigo.rest.models;

import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable {

    private int id;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private Date createdAt;
    private String avatarUrl;

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     *  Code to implement Parcelable
     */

    public User(android.os.Parcel source) {
        id        = source.readInt();
        username  = source.readString();
        email     = source.readString();
        password  = source.readString();
        avatar    = source.readString();
//        createdAt = new Date(source.readLong());
        avatarUrl = source.readString();
    }

    @Override
    public int describeContents() { return id; }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(avatar);
//        dest.writeLong(createdAt.getTime());
        dest.writeString(avatarUrl);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        public User createFromParcel(android.os.Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}