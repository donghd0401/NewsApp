package com.example.newsapp.ui.main;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.MainActivity;
import com.example.newsapp.R;
import com.example.newsapp.adapter.CategoryAdapter;
import com.example.newsapp.adapter.NewsAdapter;
import com.example.newsapp.adapter.OnItemListener;
import com.example.newsapp.databinding.FragmentHomeBinding;
import com.example.newsapp.model.Category;
import com.example.newsapp.model.News;
import com.example.newsapp.repositories.Resource;
import com.example.newsapp.utils.Constants;
import com.example.newsapp.viewmodel.HomeViewModel;
import com.example.newsapp.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static HomeFragment instance;
    public static HomeFragment getInstance() {
        if (instance == null) {
            instance = new HomeFragment();
        }
        return instance;
    }
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    ProgressDialog progressDialog;
    private FragmentManager fragmentManager;
    private RecyclerView rcCategories;
    private RecyclerView rcPopular;
    private RecyclerView rcUpcoming;
    private RecyclerView rcSearch;
    private CategoryAdapter categoryAdapter;
    private FragmentHomeBinding binding;
    private NewsAdapter popularAdapter;
    private NewsAdapter upcomingAdapter;
    private NewsAdapter searchAdapter;
    private SearchView searchView;
    private HomeViewModel homeViewModel;
    private UserViewModel userViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Debug", "CREATE");
//        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel = ((MainActivity) requireActivity()).homeViewModel;
        userViewModel = ((MainActivity) requireActivity()).userViewModel;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Debug", "CREATE VIEW");

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.setLifecycleOwner(this);
        binding.setHomeViewModel(homeViewModel);
        binding.setUserViewModel(userViewModel);

        initUI();

        // Categories
        ConfigureCategoryRecyclerView();

        // Popular:
        ConfigurePopularRecyclerView();

        // Trending
        ConfigureTrendingRecyclerView();

        //Search News
        ConfigureSearchRecyclerView();

        ObservePopularChange();
        ObserveTrendingChange();
        ObserverSearchChange();

        // Set up Search view
        SetupSearchView();

        initListener();

        return view;
    }

    private void ConfigureCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        rcCategories.setLayoutManager(linearLayoutManager);
        rcCategories.setNestedScrollingEnabled(false);
        categoryAdapter.setData(getListCategory());
        rcCategories.setAdapter(categoryAdapter);
    }

    private void initListener() {
        binding.tvWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchNews("play");
            }
        });
        // Click Small Image in Home to navigate to Profile Fragment
        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userViewModel.navigateTo(fragmentManager, new ProfileFragment());
            }
        });

        //Press twice to exit
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            private boolean doubleBackToExitPressedOnce = false;

            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    requireActivity().finish();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(getContext(), "Press back to close", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 3000);
                }
            }
        });
    }

    private void initUI() {
        fragmentManager = requireActivity().getSupportFragmentManager();
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNav);
        bottomNavigationView.setVisibility(View.VISIBLE);
        searchView = binding.svHome;
        rcCategories = binding.rcCategories;
        rcPopular = binding.rcPopular;
        rcSearch = binding.rcSearch;
        progressDialog = new ProgressDialog(getContext());
        rcUpcoming = binding.rcUpcoming;

        //Glide
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userViewModel.setUser(user);
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                Uri photoUrl = firebaseUser.getPhotoUrl();
                Glide.with(getContext()).load(photoUrl).error(R.drawable.img_not_found).into(binding.ivProfile);
                if (TextUtils.isEmpty(firebaseUser.getDisplayName())) {
                    binding.tvWelcome.setText(Constants.WELCOME);
                }
                binding.tvWelcome.setText(Constants.WELCOME + firebaseUser.getDisplayName());
            }
        });
    }

    private void SetupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("Debug", query);
                searchNews(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHandler.removeCallbacks(searchRunnable); // Hủy bỏ việc chạy trước đó (nếu có)

                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // Xử lý khi người dùng thay đổi nội dung tìm kiếm
                        if (newText.isEmpty()) {
                            // Ẩn ViewGroup nếu không có kết quả tìm kiếm
                            rcSearch.setVisibility(View.GONE);
                        } else {
                            // Hiển thị ViewGroup nếu có kết quả tìm kiếm
                            rcSearch.setVisibility(View.VISIBLE);
                            searchNews(newText);

                            // Hiển thị kết quả tìm kiếm trong ViewGroup
                            // Có thể sử dụng ListView, RecyclerView hoặc các thành phần khác để hiển thị kết quả
                        }
                    }
                };

                // Đặt delay 2 giây trước khi thực hiện tìm kiếm
                searchHandler.postDelayed(searchRunnable, 1000);
                return true;
            }
        });
    }

    private void ObserverSearchChange() {
        homeViewModel.getSearchNews().observe(getViewLifecycleOwner(), new Observer<Resource<List<News>>>() {
            @Override
            public void onChanged(Resource<List<News>> resource) {
                if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                    List<News> newsModels = resource.getData();
                    if (newsModels != null) {
                        Toast.makeText(getContext(), "Loading Success", Toast.LENGTH_SHORT).show();
                        searchAdapter.setData(newsModels);
                    }
                    progressDialog.dismiss();
                } else if (resource != null && resource.getStatus() == Resource.Status.ERROR) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ObservePopularChange() {
        homeViewModel.getPopularNews().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                List<News> newsModels = resource.getData();
                if (newsModels != null) {
                    Toast.makeText(getContext(), "Loading Pop Success", Toast.LENGTH_SHORT).show();
                    popularAdapter.setData(newsModels);
//                    Toast.makeText(getContext(), "Loading Pop Success", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            } else if (resource != null && resource.getStatus() == Resource.Status.ERROR) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ObserveTrendingChange() {
        homeViewModel.getTrendingNews().observe(getViewLifecycleOwner(),
                resource -> {
                    if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                        List<News> newsModels = resource.getData();
                        if (newsModels != null) {
                            Toast.makeText(getContext(), "Loading Trending Success", Toast.LENGTH_SHORT).show();
                            upcomingAdapter.setData(newsModels);
                        }
                        progressDialog.dismiss();
                    } else if (resource != null && resource.getStatus() == Resource.Status.ERROR) {
                        progressDialog.dismiss();
//                        Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchNews(String query) {
        progressDialog.show();
        homeViewModel.searchNews(query);
    }

    private void ConfigureSearchRecyclerView() {
        searchAdapter = new NewsAdapter(position -> {
            News newsModel = searchAdapter.getSelectedNews(position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("news", newsModel);

            NewsDetailFragment newsDetailsFragment = new NewsDetailFragment();
            newsDetailsFragment.setArguments(bundle);

            userViewModel.navigateTo(fragmentManager, newsDetailsFragment);
        });

        rcSearch.setAdapter(searchAdapter);
        rcSearch.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        rcSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void ConfigurePopularRecyclerView() {
        popularAdapter = new NewsAdapter(new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                News newsModel = popularAdapter.getSelectedNews(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("news", newsModel);

                NewsDetailFragment newsDetailsFragment = new NewsDetailFragment();
                newsDetailsFragment.setArguments(bundle);

                userViewModel.navigateToAndAdd(fragmentManager, newsDetailsFragment);
            }
        });

        rcPopular.setAdapter(popularAdapter);
        rcPopular.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        rcPopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void ConfigureTrendingRecyclerView() {
        upcomingAdapter = new NewsAdapter(new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                News newsModel = upcomingAdapter.getSelectedNews(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("news", newsModel);

                NewsDetailFragment newsDetailsFragment = new NewsDetailFragment();
                newsDetailsFragment.setArguments(bundle);

                userViewModel.navigateTo(fragmentManager, newsDetailsFragment);
            }
        });

        rcUpcoming.setAdapter(upcomingAdapter);
        rcUpcoming.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        rcUpcoming.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private List<Category> getListCategory() {
        List<Category> lst = new ArrayList<>();
        lst.add(new Category("top"));
        lst.add(new Category("sports"));
        lst.add(new Category("technology"));
        lst.add(new Category("business"));
        lst.add(new Category("science"));
        lst.add(new Category("entertainment"));
        lst.add(new Category("health"));
        lst.add(new Category("world"));
        lst.add(new Category("politics"));
        lst.add(new Category("environment"));
        lst.add(new Category("food"));
        return lst;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("Debug", "PAUSE");
        // Stop observing LiveData when navigating to other fragments
        homeViewModel.getTrendingNews().removeObservers(getViewLifecycleOwner());
        homeViewModel.getPopularNews().removeObservers(getViewLifecycleOwner());
    }
}
