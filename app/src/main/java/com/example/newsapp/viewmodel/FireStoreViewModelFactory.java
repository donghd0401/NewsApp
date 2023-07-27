package com.example.newsapp.viewmodel;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.newsapp.repositories.FireStoreRepository;

public class FireStoreViewModelFactory implements ViewModelProvider.Factory {
    private FireStoreRepository fireStoreRepository;

    public FireStoreViewModelFactory(FireStoreRepository fireStoreRepository) {
        this.fireStoreRepository = fireStoreRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FireStoreViewModel.class)) {
            return (T) new FireStoreViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
