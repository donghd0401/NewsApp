package com.example.newsapp.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_news" , foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id", childColumns = "user_id", onDelete = CASCADE))
public class FavoriteNews extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "favourite_news_id")
    private int newsId;
    @ColumnInfo(name = "favourite_news_title")
    private String title;
    @ColumnInfo(name = "favourite_news_image_url")
    private byte[] image_url;
    @ColumnInfo(name = "favourite_news_description")
    private String description;
    @ColumnInfo(name = "favourite_news_pubDate")
    private String pubDate;
    @ColumnInfo(name = "user_id")
    private int userId;

    public FavoriteNews(int newsId, String title, byte[] image_url, String description, String pubDate, int userId) {
        this.newsId = newsId;
        this.title = title;
        this.image_url = image_url;
        this.description = description;
        this.pubDate = pubDate;
        this.userId = userId;
    }

    @Ignore
    public FavoriteNews() {
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getImage_url() {
        return image_url;
    }

    public void setImage_url(byte[] image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
