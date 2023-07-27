package com.example.newsapp.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.newsapp.model.FavoriteNews;
import com.example.newsapp.model.FavouriteNewsDAO;
import com.example.newsapp.model.FavouriteNewsDB;
import com.example.newsapp.model.User;
import com.example.newsapp.model.UserDAO;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavouriteNewsRepositoryImpl implements FavouriteNewsRepository {
    private FavouriteNewsDAO favouriteNewsDAO;
    private UserDAO userDAO;

    private final CompositeDisposable compositeDisposable;
    private MutableLiveData<Resource<List<FavoriteNews>>> favouriteNews;
    private MutableLiveData<Resource<User>> user;

    private LiveData<Resource<List<FavoriteNews>>> getFavouriteNews(){
        return  favouriteNews;
    };
    private LiveData<Resource<User>> getUser(){
        return  user;
    };

    public FavouriteNewsRepositoryImpl(Application application) {
        compositeDisposable = new CompositeDisposable();
        favouriteNews = new MutableLiveData<>();
        user = new MutableLiveData<>();
        FavouriteNewsDB favouriteNewsDB = FavouriteNewsDB.getInstance(application);
        favouriteNewsDAO = favouriteNewsDB.favouriteNewsDAO();
        userDAO = favouriteNewsDB.userDAO();
    }

    @Override
    public void GetUserByMail(String mail) {
        compositeDisposable.add(
                userDAO.getUserByMail(mail)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> user.postValue(Resource.loading()))
                        .subscribe(
                                response -> {
                                    user.postValue(Resource.success(response));
                                },
                                throwable -> {
                                    user.postValue(Resource.error("Failed to get user"));
                                }
                        )
        );
    }

    // Implement other methods if needed
}
