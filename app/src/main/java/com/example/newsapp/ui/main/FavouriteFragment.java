package com.example.newsapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.adapter.FavouriteNewsAdapter;
import com.example.newsapp.adapter.OnItemListener;
import com.example.newsapp.databinding.FragmentFavouriteBinding;
import com.example.newsapp.model.News;
import com.example.newsapp.repositories.Resource;
import com.example.newsapp.viewmodel.FireStoreViewModel;
import com.example.newsapp.viewmodel.HomeViewModel;
import com.example.newsapp.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class FavouriteFragment extends Fragment {
    int i = 0;
    HomeViewModel homeViewModel;
    FragmentManager fragmentManager;
    List<News> movieModels = new ArrayList<>();
    UserViewModel userViewModel;
    RecyclerView rcFavourite;
    FavouriteNewsAdapter favouriteNewsAdapter;
    FragmentFavouriteBinding binding;
    FireStoreViewModel fireStoreViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        fireStoreViewModel = new ViewModelProvider(this).get(FireStoreViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        binding.setLifecycleOwner(this);
        binding.setFireStoreViewModel(fireStoreViewModel);
        initUI();
        observeNewsIdChange();
        ConfigureRecycleView();
        return binding.getRoot();
    }

    private void initUI() {
        rcFavourite = binding.rcFavourite;
        fragmentManager = requireActivity().getSupportFragmentManager();
    }

    private void ConfigureRecycleView() {
        favouriteNewsAdapter = new FavouriteNewsAdapter(new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                News movieModel = favouriteNewsAdapter.getSelectedNews(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("movie", movieModel);

                NewsDetailFragment movieDetailsFragment = new NewsDetailFragment();
                movieDetailsFragment.setArguments(bundle);

                userViewModel.navigateTo(fragmentManager, movieDetailsFragment);
            }
        });

        rcFavourite.setAdapter(favouriteNewsAdapter);
        rcFavourite.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
    }

    private void observeNewsIdChange() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fireStoreViewModel.getFavouriteNews(user.getUid());
        fireStoreViewModel.getFavoriteNewsLiveData().observe(getViewLifecycleOwner(), new Observer<Resource<List<Integer>>>() {
            @Override
            public void onChanged(Resource<List<Integer>> resource) {
                if (resource.getStatus() == Resource.Status.LOADING) {
                    // Hiển thị loading state
                } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                    List<Integer> movieIDs = resource.getData();

                    // Gọi hàm observeNewsByIdChange với danh sách movie IDs
                    observeNewsByIdChange(movieIDs);

//                    Toast.makeText(requireContext(), "Added to favorite movies", Toast.LENGTH_SHORT).show();
                } else if (resource.getStatus() == Resource.Status.ERROR) {
                    // Hiển thị lỗi
                    Toast.makeText(requireContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void observeNewsByIdChange(List<Integer> movieIDs) {
        HashSet<Integer> observedNewsIDs = new HashSet<>();


        for (Integer movieID : movieIDs) {
            homeViewModel.searchNewsByID(movieID);
            homeViewModel.getSearchNewsByID().observe(getViewLifecycleOwner(), new Observer<Resource<News>>() {
                @Override
                public void onChanged(Resource<News> resource) {
                    if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                        News movie = resource.getData();

                        if (!observedNewsIDs.contains(movie.getId())) {
                            movieModels.add(movie);
                            observedNewsIDs.add(movie.getId());
                            favouriteNewsAdapter.setData(movieModels);
                        }
                    } else if (resource != null && resource.getStatus() == Resource.Status.ERROR) {
                        Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}