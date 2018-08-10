package com.azens.trendingnews.api;

import com.azens.trendingnews.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Azens Eklak on 8/7/18.
 */
public interface NewsClient {
    @GET("everything")
    Call<NewsResponse> getNews(@Query("sources") String source,
                               @Query("apiKey") String APIKey);
}
