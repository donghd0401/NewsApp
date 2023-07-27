package com.example.newsapp.utils;

import com.example.newsapp.model.News;
import com.example.newsapp.response.NewsSearchResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApi {
    // Search for news
    @GET("1/news")
    Single<Response<NewsSearchResponse>> searchNews(
            @Query("q") String query,
            @Query("apikey") String key
    );

    // Get Popular
    @GET("1/news?category=top")
    Single<Response<NewsSearchResponse>> getPopularNews(
            @Query("apikey") String key
    );

    // Get Trending
    @GET("1/news?category=top")
    Single<Response<NewsSearchResponse>> getTrendingNews(
            @Query("apikey") String key
    );

    // Search for news
    @GET("3/news/{news_id}?")
    Single<Response<News>> searchSingleNews(
            @Path("news_id") int news_id,
            @Query("apikey") String key
    );
}
