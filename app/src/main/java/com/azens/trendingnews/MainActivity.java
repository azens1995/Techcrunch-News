package com.azens.trendingnews;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.azens.trendingnews.adapter.NewsAdapter;
import com.azens.trendingnews.api.NewsClient;
import com.azens.trendingnews.api.ServiceGenerator;
import com.azens.trendingnews.model.News;
import com.azens.trendingnews.model.NewsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshData;
    private ProgressDialog dialog;
    private Snackbar snackbar;
    private static final String API_KEY = BuildConfig.API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View parentView = findViewById(R.id.constraintLayout);
        refreshData = (SwipeRefreshLayout) findViewById(R.id.refresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        loadJSON();
        refreshData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkConnected()){
                    loadJSON();
                    Toast.makeText(MainActivity.this, "Latest data is loaded...", Toast.LENGTH_SHORT).show();
                }else{
                    refreshData.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });


        if (isNetworkConnected()){
            //Progress Dialog for User Interaction
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Trending News");
            dialog.setMessage("Please wait...");
            dialog.show();

            loadJSON();
        }else {
            snackbar.make(parentView, "No internet connection...", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isNetworkConnected()){
                                loadJSON();
                            }
                        }
                    }).show();
        }

    }

    private void loadJSON() {
        NewsClient client = ServiceGenerator.createService(NewsClient.class);

        Call<NewsResponse> call = client.getNews("techcrunch", API_KEY);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                List<News> news =response.body().getNews();
                refreshData.setRefreshing(false);
                recyclerView.setAdapter(new NewsAdapter(MainActivity.this, news));
                dialog.hide();
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkConnected () {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
