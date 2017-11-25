package com.infinitemessage.adewan.infinitemessage.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.infinitemessage.adewan.infinitemessage.model.Author;
import com.infinitemessage.adewan.infinitemessage.model.Message;
import com.infinitemessage.adewan.infinitemessage.model.MessageData;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Class is responsible for managing any data related activities
 * for the entire application.
 *
 * Class handles providing network calls observables, writing to the
 * local cache and retrieving data from the local cache.
 * Created by a.dewan on 5/11/17.
 */

public class DataManager {

    private static ApiInterface apiInterface;

    /**
     * Varaiable that holds a static reference to the nextPageToken so that
     * it can be changed whenever data is received from the server to ensure
     * that the next call is properly redirected.
     */
    public static String nextPageToken;

    /**
     * DataManager constructor initializes the apiInterface to the static instance of the
     * retrofit client created in the ApiManager class.
     */
    public DataManager(){
        apiInterface = ApiManger.getClient().create(ApiInterface.class);
    }


    /**
     * Methods provides an Observable that can be subscribed to
     * later in order to make an api call and then get some data.
     * This method deals with the API calls that do not require a
     * page token such as the first call and the 3000,6000,9000 and
     * so on calls.
     *
     * @return observable of messageData class.
     * retrofit client created in the ApiManager class.
     */
    public static Observable<MessageData> getMessageData(){
        Observable<MessageData> firstBatchOfMessages = apiInterface.getMessageData();
        return firstBatchOfMessages;
    }

    /**
     * Methods provides an Observable that can be subscribed to
     * later in order to make an api call and then get some data.
     * This method deals with the API calls that require a page token
     * to be accessed.
     *
     * @return observable of messageData class.
     * retrofit client created in the ApiManager class.
     */
    public static Observable<MessageData> getMessageDataWithToken(){
        /**
         * Some pages of the API calls will not return a pageToken such as
         * 3000,6000,9000 and so on calls. This check ensure that a valid
         * page token is present other it calls the getMessageData() method
         * to handle that call.
         *
         * This method works for now since the page for the first call and
         * 3000 call is the same data, however in the case the data was different
         * this would need to be modified.
         */
        if(nextPageToken != null){
            Log.d("[DATA]",nextPageToken);
            Observable<MessageData> moreMessages = apiInterface.getMessageDataWithToken(nextPageToken);
            return moreMessages;
        }else{
            return getMessageData();
        }
    }


    /**
     * Method is responsible for retrieving the last saved shared preference data
     * The data is read a JSON string and then passed through a formatter method
     * formatData() to get original data.
     *
     * This method uses the GSON library to get the json string from the shared
     * preferences
     *
     * Please note that even though the image urls are stored in the local json
     * cache, caching of the actual pics is done by Glide.
     * @param context
     * @return arraylist of Message items that hold the content of each message.
     */
    public static ArrayList<Message> getLastSavedData(Context context) {
        ArrayList<Message> messageList = null;
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String localJSON = appPreferences.getString("appData","");
        nextPageToken = appPreferences.getString("pageToken","");
        messageList = formatData(gson.fromJson(localJSON,ArrayList.class));
        return messageList;
    }


    /**
     * Method is responsible for saving the recyclerview adpater data to shared preference
     * as a JSON string, to enable the local cache mecahnism for the app.
     *
     * This method uses GSON library to convert the adapterData to a json string.
     *
     * The methods rewrites the entire adapter data when its called at this moment,
     * however in the future we could employ a stratergy to just save the changes.
     *
     * Please note that this methods for this particular implementation as the majority of the
     * data is text based. However, in the future if the data needs change such as images, more text
     * then a different stratergy should be used such as SQLite Database.
     * @param adapterData
     * @param context
     */
    public static void saveAdapterData(final ArrayList<Message> adapterData, final Context context){
        /**
         * Here an observable is used to save the data to the shared preference
         * as we don't want the saving process to be a blocking process.
         *
         * Using a observable also helps improve the performance when many messages are loaded
         * and the data needs to be updated to the local sharedpreference cache.
         */
        Observable.just(adapterData)
                  .subscribeOn(Schedulers.newThread())
                  .retry()
                  .subscribe(new Action1<ArrayList<Message>>() {
                      @Override
                      public void call(ArrayList<Message> adapterData) {
                          SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                          SharedPreferences.Editor prefsEditor = appPreferences.edit();
                          Gson gson = new Gson();
                          String messageJSON = gson.toJson(adapterData);
                          prefsEditor.putString("appData",messageJSON);
                          prefsEditor.putString("pageToken",nextPageToken);
                          Log.d("[SAVE DATA]",prefsEditor.commit()+"");
                      }
                  });
    }

    /**
     * Method is used by the getLastSavedData() method to format the retrieved JSON string
     * and convert it back to a arraylist of message items that contain the message data.
     *
     * Please note that this implementation would need to be changed if the data needs are changed
     * @param arrayList
     * @return arraylist of message items that contain the message data.
     */
    private static ArrayList<Message> formatData(ArrayList arrayList) {
        ArrayList<Message> tempMessageData = new ArrayList<Message>();
        if(arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                LinkedTreeMap tempData = (LinkedTreeMap) arrayList.get(i);
                LinkedTreeMap tempAuthor = (LinkedTreeMap) tempData.get("author");
                Double id = new Double((Double) tempData.get("id"));
                Author author = new Author((String) tempAuthor.get("name"), (String) tempAuthor.get("photoUrl"));
                Message message = new Message((String) tempData.get("content"), (String) tempData.get("updated"), id.intValue(), author);
                tempMessageData.add(message);
            }
        }else{
            tempMessageData = null;
        }
        return tempMessageData;
    }
}
