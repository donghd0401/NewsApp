package com.example.newsapp.repositories;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface FireStoreRepository {
    void getDocumentDataFromFirestore(String userID);
    LiveData<Resource<List<Integer>>> getFavoriteNewsLiveData();
}
