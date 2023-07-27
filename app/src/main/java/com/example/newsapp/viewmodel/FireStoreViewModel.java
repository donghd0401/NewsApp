package com.example.newsapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.newsapp.repositories.FireStoreRepositoryImpl;
import com.example.newsapp.repositories.NewsRepositoryImpl;
import com.example.newsapp.repositories.Resource;

import java.util.List;

public class FireStoreViewModel extends ViewModel {
    private FireStoreRepositoryImpl fireStoreRepositoryImpl;

    private NewsRepositoryImpl newsRepository;
    private LiveData<Resource<List<Integer>>> favoriteNewsLiveData;

    public FireStoreViewModel() {
        fireStoreRepositoryImpl = FireStoreRepositoryImpl.getInstance();
        favoriteNewsLiveData = fireStoreRepositoryImpl.getFavoriteNewsLiveData();
    }

    public LiveData<Resource<List<Integer>>> getFavoriteNewsLiveData() {
        return favoriteNewsLiveData;
    }

    public void getFavouriteNews(String userID){
        fireStoreRepositoryImpl.getDocumentDataFromFirestore(userID);
    }

    public void addFavoriteNews(String userID, int newsID) {
        fireStoreRepositoryImpl.addFavoriteNewsToFireStore(userID, newsID);
    }
}
