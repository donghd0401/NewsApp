package com.example.newsapp.ui.main;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.model.News;
import com.example.newsapp.repositories.FireStoreRepository;
import com.example.newsapp.repositories.FireStoreRepositoryImpl;
import com.example.newsapp.repositories.Resource;
import com.example.newsapp.utils.Credentials;
import com.example.newsapp.viewmodel.FireStoreViewModel;
import com.example.newsapp.viewmodel.FireStoreViewModelFactory;
import com.example.newsapp.viewmodel.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsDetailFragment extends Fragment {
    HomeViewModel homeViewModel;
    private static NewsDetailFragment instance;

    public static NewsDetailFragment getInstance() {
        if (instance == null) {
            instance = new NewsDetailFragment();
        }
        return instance;
    }

    private TextView tvTitle, tvOverView, tvReleaseDate, tvAddToFavourite;
    private ImageView ivPoster;

    int newsID;
    private RatingBar ratingBar;
    View view;

    private FireStoreViewModel fireStoreViewModel;

    public static NewsDetailFragment newInstance(Bundle bundle) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        FireStoreRepository fireStoreRepository = FireStoreRepositoryImpl.getInstance();
        FireStoreViewModelFactory viewModelFactory = new FireStoreViewModelFactory(fireStoreRepository);
        fireStoreViewModel = new ViewModelProvider(this, viewModelFactory).get(FireStoreViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_news_detail, container, false);
        initUI();
        GetDataFromHome();
        initListener();

        // Inflate the layout for this fragment
        return view;
    }

    private void initListener() {
        tvAddToFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.e("Debug", "id: " + newsID);

                // Thực hiện thêm phim vào danh sách yêu thích
                fireStoreViewModel.addFavoriteNews(user.getUid(), newsID);

                // Quan sát trạng thái của LiveData khi thêm phim
                fireStoreViewModel.getFavoriteNewsLiveData().observe(getViewLifecycleOwner(), new Observer<Resource<List<Integer>>>() {
                    @Override
                    public void onChanged(Resource<List<Integer>> resource) {
                        if (resource.getStatus() == Resource.Status.LOADING) {
                            // Hiển thị loading state
                        } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                            // Hiển thị thành công
                            Toast.makeText(requireContext(), "Added to favorite news", Toast.LENGTH_SHORT).show();
                        } else if (resource.getStatus() == Resource.Status.ERROR) {
                            // Hiển thị lỗi
                            Toast.makeText(requireContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.containerFragment, HomeFragment.getInstance())
                        .commit();
            }
        });
    }

    private void initUI() {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNav);
        bottomNavigationView.setVisibility(View.GONE);
        tvReleaseDate = view.findViewById(R.id.tvReleaseDate);
        ratingBar = view.findViewById(R.id.ratingBar);
        tvTitle = view.findViewById(R.id.tvTitle_NewsDetails);
        tvOverView = view.findViewById(R.id.tvOverView_NewsDetails);
        ivPoster = view.findViewById(R.id.ivPoster_NewsDetails);
        tvAddToFavourite = view.findViewById(R.id.tvAddToFavourite);
    }

    private void GetDataFromHome() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("news")) {
            News newsModel = bundle.getParcelable("news");
            tvTitle.setText(newsModel.getTitle());
            newsID = newsModel.getId();
            Log.e("Debug", "id in getData" + newsModel.getId());
            tvOverView.setText(newsModel.getDescription());
            Glide.with(getContext())
                    .load(newsModel.getImage_url())
                    .into(ivPoster);
            tvReleaseDate.setText(newsModel.getPubDate());

        }
    }
}