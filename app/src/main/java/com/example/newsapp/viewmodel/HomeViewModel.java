package com.example.newsapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.newsapp.model.News;
import com.example.newsapp.repositories.NewsRepository;
import com.example.newsapp.repositories.NewsRepositoryImpl;
import com.example.newsapp.repositories.Resource;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private final NewsRepository newsRepository;
    private final SingleLiveEvent<Resource<List<News>>> mSearchNews;
    private final SingleLiveEvent<Resource<List<News>>> mTrendingNews;
    private final SingleLiveEvent<Resource<List<News>>> mPopularNews;
    private final SingleLiveEvent<Resource<News>> mSearchNewsByID;

    public int s = 6;

    public HomeViewModel() {
        Log.d("TAG", "HomeViewModel: ");
        newsRepository = NewsRepositoryImpl.getInstance();
        mSearchNews = newsRepository.getSearchNews();
        mTrendingNews = newsRepository.getTrendingNews();
        mPopularNews = newsRepository.getPopularNews();
        mSearchNewsByID = newsRepository.getNewsByID();
        loadNews();
    }

    public SingleLiveEvent<Resource<List<News>>> getSearchNews() {
        return mSearchNews;
    }

    public SingleLiveEvent<Resource<List<News>>> getTrendingNews() {
        return mTrendingNews;
    }

    public SingleLiveEvent<Resource<List<News>>> getPopularNews() {
        return mPopularNews;
    }

    public LiveData<Resource<News>> getSearchNewsByID() {
        return mSearchNewsByID;
    }

    public void searchNewsByID(int newsID) {
        newsRepository.getNewsByID(newsID);
    }

    public void loadNews() {
        newsRepository.loadNewsApi();
    }

    public void searchNews(String query) {
        newsRepository.searchNews(query);
    }
}