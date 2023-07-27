package com.example.newsapp.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface FavouriteNewsDAO {

    @Insert
    Completable insert(FavoriteNews favoriteNews);

    @Update
    Completable update(FavoriteNews favoriteNews);

    @Delete
    Completable delete(FavoriteNews favoriteNews);

    @Query("SELECT * FROM favourite_news WHERE user_id ==:userId")
    Flowable<List<FavoriteNews>> getFavouriteNews(int userId);
}
