package com.example.newsapp.repositories;

import com.example.newsapp.model.News;
import com.example.newsapp.viewmodel.SingleLiveEvent;

import java.util.List;

public interface NewsRepository {
    SingleLiveEvent<Resource<List<News>>> getSearchNews();

    SingleLiveEvent<Resource<List<News>>> getPopularNews();

    SingleLiveEvent<Resource<List<News>>> getTrendingNews();

    SingleLiveEvent<Resource<News>> getNewsByID();

    void getNewsByID(int newsID);

    void loadNewsApi();

    void searchNews(String query);
}
