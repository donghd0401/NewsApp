package com.example.newsapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.adapter.NotificationAdapter;
import com.example.newsapp.databinding.FragmentNotificationBinding;
import com.example.newsapp.viewmodel.HomeViewModel;
import com.example.newsapp.viewmodel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    HomeViewModel homeViewModel;
    private RecyclerView rcNoti;
    private NotificationAdapter notificationAdapter;
    private FragmentNotificationBinding mFragmentNotificationBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentNotificationBinding = FragmentNotificationBinding.inflate(inflater, container, false);

        NotificationViewModel notificationViewModel = new NotificationViewModel("Button clicked!");
        mFragmentNotificationBinding.setFragmentNotificationViewModel(notificationViewModel);

        rcNoti = mFragmentNotificationBinding.rcNotification;
        rcNoti.setLayoutManager(new LinearLayoutManager(this.getContext()));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
        rcNoti.addItemDecoration(itemDecoration);
        notificationAdapter = new NotificationAdapter();

        rcNoti.setAdapter(notificationAdapter);
        return mFragmentNotificationBinding.getRoot();
    }

    private List<NotificationViewModel> getNotifications() {
        List<NotificationViewModel> lst = new ArrayList<>();
        lst.add(new NotificationViewModel("dong 1"));
        lst.add(new NotificationViewModel("dong 2"));
        lst.add(new NotificationViewModel("dong 3"));
        lst.add(new NotificationViewModel("dong 4"));
        lst.add(new NotificationViewModel("dong 5"));
        lst.add(new NotificationViewModel("dong 6"));
        lst.add(new NotificationViewModel("dong 7"));
        lst.add(new NotificationViewModel("dong 8"));
        lst.add(new NotificationViewModel("dong 9"));
        lst.add(new NotificationViewModel("dong 10"));
        lst.add(new NotificationViewModel("dong 11"));
        lst.add(new NotificationViewModel("dong 12"));
        lst.add(new NotificationViewModel("dong 13"));
        lst.add(new NotificationViewModel("dong 14"));
        lst.add(new NotificationViewModel("dong 15"));
        lst.add(new NotificationViewModel("dong 16"));
        lst.add(new NotificationViewModel("dong 17"));
        lst.add(new NotificationViewModel("dong 18"));
        lst.add(new NotificationViewModel("dong 19"));
        lst.add(new NotificationViewModel("dong 20"));
        return lst;
    }

    @Override
    public void onStart() {
        super.onStart();
        notificationAdapter.setData(getNotifications());
    }


}