package com.infinitemessage.adewan.infinitemessage.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import android.net.wifi.WifiManager;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.infinitemessage.adewan.infinitemessage.R;
import com.infinitemessage.adewan.infinitemessage.model.Message;
import com.infinitemessage.adewan.infinitemessage.model.MessageData;
import com.infinitemessage.adewan.infinitemessage.network.DataManager;
import com.infinitemessage.adewan.infinitemessage.util.Utils;;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    RecyclerView mMainMessageList;
    DataManager mDataManager;
    Context mContext;
    Toolbar mMainToolbar;
    TextView mNoNetworkText;
    WifiBroadcastReceiver mReceiver;

    public boolean isThisReceieverEcho = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataManager = new DataManager();
        mContext = this;

        mReceiver = new WifiBroadcastReceiver();
        mMainMessageList = (RecyclerView) findViewById(R.id.main_message_list);
        mMainMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMainMessageList.setLayoutManager(linearLayoutManager);



        /**
         * mainToolBar has a on touch listener that helps to scroll to
         * the top of the feed if needed.
         */
        mMainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mMainToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mMainMessageList.smoothScrollToPosition(0);
                return false;
            }
        });
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /**
         * noNetworkText is a textview shown to the user when the user does
         * not have a local cache of messages and his internet is down.
         */
        mNoNetworkText = (TextView) findViewById(R.id.no_network);


        /**
         * If the network is available, download content from the API and show to the user.
         *
         * If network is not available, then search the sharedpreferences and see if local
         * cache exists and if it does show that to the user.
         *
         * If network is not available and no local cache exists provide user with feedback
         * and snackbar that allows him to turn on his WIFI
         */
        if(Utils.isNetworkAvailable(mContext)){
            DataManager.getMessageData()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .retry()
                    .subscribe(new Action1<MessageData>() {
                        @Override
                        public void call(MessageData messageData) {
                            Log.d("[DATA]","Position 1");
                            DataManager.nextPageToken = messageData.getPageToken();
                            DataManager.saveAdapterData(messageData.getMessages(), mContext);
                            mMainMessageList.setAdapter(new MessageListAdapter(messageData.getMessages(), mMainMessageList, mContext));

                        }
                    });
        }else{
            ArrayList<Message> tempMessage = DataManager.getLastSavedData(mContext);
            if(tempMessage != null){
                mMainMessageList.setAdapter(new MessageListAdapter(tempMessage, mMainMessageList, mContext));
            }else{
                mMainMessageList.setVisibility(View.GONE);
                mNoNetworkText.setVisibility(View.VISIBLE);
                Utils.showWifiSnackbar(mContext, mMainMessageList);
            }
            Log.d("[MESSAGE]","Internet Down");
        }
    }

    /**
     * Registers the broadcast receiver and also sets the intent filters.
     */
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver,intentFilter);
    }

    /**
     * unregisters the broadcast receiver
     */
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    /**
     * Override to provide a way of scrolling to the bottom of the the recyclerview
     * feed by holding down the volume down
     * button and not having to swipe to scroll.
     *
     * Please note this is a hidden easter egg that aims to make it easier to scroll
     * through the list.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            mMainMessageList.smoothScrollToPosition(mMainMessageList.getAdapter().getItemCount() -1);
        }
        return true;
    }


    /**
     * Broadcast reciever that allows to listen in on network changes
     * and load more data is the network becomes available to do so.
     */
    class WifiBroadcastReceiver extends BroadcastReceiver {

        /**
         * Boolean value that helps to mitigate the first broadcast received
         * as it is done when registering the broadcast.
         */
        public boolean secondTime = false;

        /**
         * isThisReceiverEcho - boolean value that helps to filter out multiple broadcasts that are being
         * received in older versions of android ( android lollipop below ) and also on different manufacturer
         * phones. (Know issue please refer to : http://stackoverflow.com/questions/8412714/broadcastreceiver-receives-multiple-identical-messages-for-one-event)
         *
         * Once the broadcast is determined to be a network changes a new delayed runnable is posted
         * that checks if the internet is available and then depending on whether some information is being
         * show to the user or not calls different methods to add more data to the user recyclerview adapater
         */
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (secondTime && !isThisReceieverEcho) {
                if (intent != null) {
                    String action = intent.getAction();
                    if (action.equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
                        isThisReceieverEcho = true;
                        if(Utils.isWifiAvailable(context)){
                            mNoNetworkText.setText("Network available.\n Please wait while new messages are downloaded.");
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("[WIFI]", Utils.isNetworkAvailable(context) + "");
                                isThisReceieverEcho = false;
                                if(Utils.isNetworkAvailable(context)){
                                    if(mMainMessageList.getAdapter() != null){
                                        MessageListAdapter.addMoreDataToAdapter(true);
                                    }else {
                                        mMainMessageList.setVisibility(View.VISIBLE);
                                        mNoNetworkText.setVisibility(View.GONE);
                                        Log.d("[DATA]","Position 2");
                                        DataManager.getMessageData()
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .retry()
                                                .subscribe(new Action1<MessageData>() {
                                                    @Override
                                                    public void call(MessageData messageData) {
                                                        DataManager.nextPageToken = messageData.getPageToken();
                                                        mMainMessageList.setAdapter(new MessageListAdapter(messageData.getMessages(), mMainMessageList,context));
                                                    }
                                                });
                                    }
                                }
                            }
                        }, 3000);
                    }
                }
            }else {
                Log.d("[WIFI]","First Time Broacast.");
                secondTime = true;
            }
        }
    }
}
