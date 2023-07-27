package com.example.newsapp.request;

import com.example.newsapp.utils.Credentials;
import com.example.newsapp.utils.NewsApi;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static OkHttpClient.Builder okBuilder;
    private static Retrofit.Builder retrofitBuilder;
    private static Retrofit retrofit;
    private static NewsApi newsApi;

    public static NewsApi getNewsApi() {
        if (newsApi == null) {
            initNewsApi();
        }
        return newsApi;
    }

    private static void initNewsApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        okBuilder = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    // Thêm API key vào header của yêu cầu
                    Request originalRequest = chain.request();
                    Request newRequest = originalRequest.newBuilder()
                            .header("apikey", Credentials.API_KEY)
                            .build();
                    return chain.proceed(newRequest);
                });

        retrofitBuilder = new Retrofit.Builder()
                .baseUrl(Credentials.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okBuilder.build());

        retrofit = retrofitBuilder.build();
        newsApi = retrofit.create(NewsApi.class);
    }
}
