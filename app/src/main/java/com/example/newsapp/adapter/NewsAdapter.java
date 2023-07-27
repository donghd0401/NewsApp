package com.example.newsapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.model.News;
import com.example.newsapp.utils.Credentials;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<News> news;
    private OnItemListener onItemListener;

    public NewsAdapter(OnItemListener onItemListener){
        this.onItemListener = onItemListener;
    }

    public void setData(List<News> list){
        this.news = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news,parent,false);
        return new NewsViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News n = news.get(position);
        if(n == null){
            return;
        }
        holder.tvTitle.setText(n.getTitle());
        Glide.with(holder.itemView.getContext()).load(n.getImage_url())
                .error(R.drawable.img_not_found)
                .into(holder.ivPoter);
    }

    @Override
    public int getItemCount() {
        if(news !=null){
            return news.size();
        }
        return 0;
    }

    public News getSelectedNews(int position) {
        if(news != null){
            if(news.size() >0){
                return news.get(position);
            }
        }
        return null;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle;
        private ImageView ivPoter;
        OnItemListener onItemListener;

        public NewsViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPoter = itemView.findViewById(R.id.ivNews);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }
}
