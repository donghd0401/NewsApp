package com.example.newsapp.response;

import com.example.newsapp.model.News;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//This clas is for getting multiple news
public class NewsSearchResponse {
    @SerializedName("totalResults")
    @Expose()
    private int totalResults;
    @SerializedName("results")
    @Expose()
    private List<News> newsList;

    public int getTotalResults(){
        return totalResults;
    }

    public List<News> getNews(){
        return newsList;
    }

    @Override
    public String toString() {
        return "NewsSearchResponse{" +
                "total_count=" + totalResults +
                ", newsList=" + newsList +
                '}';
    }
}
