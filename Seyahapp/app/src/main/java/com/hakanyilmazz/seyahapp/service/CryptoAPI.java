package com.hakanyilmazz.seyahapp.service;

import com.hakanyilmazz.seyahapp.model.CryptoModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface CryptoAPI {

    @GET("prices?key=5373f6bfaffb7f8180e293237edb3f47")
    Observable<List<CryptoModel>> getData();

}
