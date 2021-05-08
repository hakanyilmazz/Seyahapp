package com.hakanyilmazz.seyahapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hakanyilmazz.seyahapp.R;
import com.hakanyilmazz.seyahapp.adapter.CryptoApiRecyclerAdapter;
import com.hakanyilmazz.seyahapp.model.CryptoModel;
import com.hakanyilmazz.seyahapp.model.NotificationUtil;
import com.hakanyilmazz.seyahapp.model.ResultReceiver;
import com.hakanyilmazz.seyahapp.service.CryptoAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationActivity extends AppCompatActivity {

    private final String BASE_URL = "https://api.nomics.com/v1/";
    private ArrayList<CryptoModel> cryptoModels;
    private Retrofit retrofit;
    private RecyclerView recyclerView;
    private CryptoApiRecyclerAdapter cryptoApiRecyclerAdapter;
    private CompositeDisposable compositeDisposable;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.recyclerView);

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        loadData();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    cryptoApiRecyclerAdapter = new CryptoApiRecyclerAdapter(cryptoModels);
                } else {
                    ArrayList<CryptoModel> cryptoModelTemp = new ArrayList<>();

                    for (CryptoModel cryptoModel : cryptoModels) {
                        if (cryptoModel.currency.startsWith(s.toString().toUpperCase())) {
                            cryptoModelTemp.add(cryptoModel);
                        }
                    }

                    cryptoApiRecyclerAdapter = new CryptoApiRecyclerAdapter(cryptoModelTemp);
                }

                recyclerView.setAdapter(cryptoApiRecyclerAdapter);
                System.gc();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showNotification(String title, String text) {
        Intent intent = new Intent(getApplicationContext(), ResultReceiver.class);
        intent.setAction(ResultReceiver.ACTION_CLICK);
        NotificationUtil.with(getApplicationContext()).showNotification(
                title,
                text,
                R.drawable.seyahapp_icon,
                intent
        );
    }

    private void loadData() {
        CryptoAPI cryptoAPI = retrofit.create(CryptoAPI.class);

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(cryptoAPI.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse));
    }

    private void handleResponse(List<CryptoModel> cryptoModelList) {
        cryptoModels = new ArrayList<>(cryptoModelList);

        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        cryptoApiRecyclerAdapter = new CryptoApiRecyclerAdapter(cryptoModels);
        recyclerView.setAdapter(cryptoApiRecyclerAdapter);

        showNotification("Information", "Crypto currencies informations have been uploaded.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
    }

}