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

public class FavouriteNewsAdapter extends RecyclerView.Adapter<FavouriteNewsAdapter.FavouriteNewsViewHolder> {
    private List<News> newsList;
    private OnItemListener onItemListener;

    public FavouriteNewsAdapter(OnItemListener onItemListener){
        this.onItemListener = onItemListener;
    }

    public void setData(List<News> list){
        this.newsList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavouriteNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_news,parent,false);
        return new FavouriteNewsViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteNewsViewHolder holder, int position) {
        News news = newsList.get(position);
        if(news == null){
            return;
        }
        holder.tvTitle.setText(news.getTitle());
        holder.tvOverView.setText(news.getDescription());
        Glide.with(holder.itemView.getContext()).load(news.getImage_url())
                .error(R.drawable.img_not_found)
                .into(holder.ivPoter);
    }

    @Override
    public int getItemCount() {
        if(newsList !=null){
            return newsList.size();
        }
        return 0;
    }

    public News getSelectedNews(int position) {
        if(newsList != null){
            if(newsList.size() >0){
                return newsList.get(position);
            }
        }
        return null;
    }

    public class FavouriteNewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle, tvOverView;
        private ImageView ivPoter;
        OnItemListener onItemListener;

        public FavouriteNewsViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title_favourite_news);
            ivPoter = itemView.findViewById(R.id.ivPoster_favourite_news);
            tvOverView = itemView.findViewById(R.id.tv_over_view_favourite_news);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

}

