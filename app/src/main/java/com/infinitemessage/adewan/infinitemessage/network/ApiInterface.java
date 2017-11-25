package com.infinitemessage.adewan.infinitemessage.network;

import com.infinitemessage.adewan.infinitemessage.model.MessageData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Class that defines the API calls that need
 * to be made in order to download the data.
 *
 * Class also deals with any optional paramters that
 * needs to be passed such as pageToken in order
 * to properly make API calls.
 *
 * Class relies of retrofit library
 * Created by a.dewan on 5/11/17.
 */

public interface ApiInterface {

    @GET("messages?limit=100")
    Observable<MessageData> getMessageData();

    @GET("messages?limit=100")
    Observable<MessageData> getMessageDataWithToken(@Query("pageToken") String pageToken);

}
