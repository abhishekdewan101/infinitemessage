package com.infinitemessage.adewan.infinitemessage.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class that uses the retrofit API to create
 * a API client capable of performing the API calls
 * defined in the ApiInterface class.
 * Created by a.dewan on 5/11/17.
 */

public class ApiManger {

    public static final String BASE_URL = "http://message-list.appspot.com/";
    private static Retrofit retrofit = null;

    /**
     * Static method to keep a single retrofit client active
     * and provide a instance whenever needed.
     *
     * @returns a instance of the retrofit client that was created
     */
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
