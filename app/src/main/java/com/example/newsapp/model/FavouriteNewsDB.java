package com.example.newsapp.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class, FavoriteNews.class}, version = 1)
public abstract class FavouriteNewsDB extends RoomDatabase {
    public abstract FavouriteNewsDAO favouriteNewsDAO();

    public abstract UserDAO userDAO();

    // Singleton Pattern
    private static FavouriteNewsDB instance;

    public static synchronized FavouriteNewsDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            FavouriteNewsDB.class, "favourite_news_DB")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
