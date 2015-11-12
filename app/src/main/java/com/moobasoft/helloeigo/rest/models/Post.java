package com.moobasoft.helloeigo.rest.models;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Post implements Parcelable {

    private int id;
    private String title;
    private String body;
    private List<Comment> comments;
    private List<String> tags;
    private Date createdAt;
    private String imageUrl;
    private String thumbnailUrl;
    private int commentsCount;
    private boolean bookmarked;

    public Post() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body != null ? body : "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Comment> getComments() {
        return comments != null ? comments : Collections.emptyList();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags != null ? tags : Collections.emptyList();
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    @Override
    public String toString() {
        return String.format("%s%n%s", title, body);
    }

    /**
     *  Code to implement Parcelable
     */

    public Post(android.os.Parcel source) {
        id    = source.readInt();
        title = source.readString();
        body  = source.readString();

        comments = new ArrayList<>();
        source.readTypedList(comments, Comment.CREATOR);
        tags = new ArrayList<>();
        source.readStringList(tags);

        createdAt     = new Date(source.readLong());
        imageUrl      = source.readString();
        thumbnailUrl  = source.readString();
        commentsCount = source.readInt();
        bookmarked    = (source.readByte() == 1);
    }

    @Override
    public int describeContents() { return id; }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeTypedList(comments);
        dest.writeStringList(tags);
        dest.writeLong(createdAt.getTime());
        dest.writeString(imageUrl);
        dest.writeString(thumbnailUrl);
        dest.writeInt(commentsCount);
        dest.writeByte((byte) (bookmarked ? 1 : 0));
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {

        public Post createFromParcel(android.os.Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}