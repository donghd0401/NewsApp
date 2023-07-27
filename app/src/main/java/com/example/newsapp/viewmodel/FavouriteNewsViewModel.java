package com.example.newsapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.newsapp.model.FavoriteNews;
import com.example.newsapp.model.User;
import com.example.newsapp.repositories.FavouriteNewsRepository;
import com.example.newsapp.repositories.FavouriteNewsRepositoryImpl;
import com.example.newsapp.repositories.Resource;

import java.util.List;

public class FavouriteNewsViewModel extends ViewModel {
    private FavouriteNewsRepository repository;

    private LiveData<List<FavoriteNews>> allFavouriteNews;
    private LiveData<Resource<User>> user;

    private LiveData<Resource<User>> getUser(){
      return user;
    }

    public FavouriteNewsViewModel(Application application) {
        repository = new FavouriteNewsRepositoryImpl(application);
    }

    public void getUserByMail(String mail){
        repository.GetUserByMail(mail);
    }
}
