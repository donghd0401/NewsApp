package com.example.newsapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class News implements Parcelable {
    private int id;
    private String title;
    private String link;
    private String description;
    private String content;
    private String pubDate;
    private String image_url;
    private String source_id;

    public News() {
    }

    public News(int id, String title, String link, String description, String content, String pubDate, String image_url, String source_id) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.content = content;
        this.pubDate = pubDate;
        this.image_url = image_url;
        this.source_id = source_id;
    }

    protected News(Parcel in) {
        id = in.readInt();
        title = in.readString();
        link = in.readString();
        description = in.readString();
        content = in.readString();
        pubDate = in.readString();
        image_url = in.readString();
        source_id = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(description);
        parcel.writeString(content);
        parcel.writeString(pubDate);
        parcel.writeString(image_url);
        parcel.writeString(source_id);
    }
}
