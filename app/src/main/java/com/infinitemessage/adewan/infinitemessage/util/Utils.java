package com.infinitemessage.adewan.infinitemessage.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class that provides the app the ability to check if the network
 * is connected and we can start to download messages.
 *
 * Also the class allows for creating and showing snackbars that
 * help the user to be able to get feedback on network operations
 * and also take action if needed.
 * Created by a.dewan on 5/13/17.
 */

public class Utils {

    /**
     * Returns if the network is connected as a boolean.
     * @param context the context of the activity calling it.
     * @return if network is available for use.
     */
    public static boolean isNetworkAvailable(Context context) {
        if(context != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }else{
            return false;
        }
    }

    /**
     * Returns if the WIFI is turned on as a boolean.
     * @param context the context of the activity calling it.
     * @return if network is available for use.
     */
    public static boolean isWifiAvailable(Context context) {
        if(context != null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wifiManager.isWifiEnabled();
        }else{
            return false;
        }
    }

    /**
     * Shows the wifi snackbar that informs the user that the network is unavailable
     * and that he/she can use the action button to turn on the wifi.
     * @param context the context of the activity calling it.
     * @param mainMessageList the view of the snackbar that is trying to the snackbar
     */
    public static void showWifiSnackbar(final Context context, RecyclerView mainMessageList){
        Snackbar.make(mainMessageList,"Network unavailable! Please turn on the network",Snackbar.LENGTH_INDEFINITE)
                .setAction("Turn on", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                    }
                }).show();
    }

    /**
     * Shows the new message downloaded snackbar that informs the user that the network is available
     * and that he/she can scroll to see new messages.
     * @param context the context of the activity calling it.
     * @param mainMessageList the view of the snackbar that is trying to the snackbar
     */
    public static void showNewMessageSnackbar(Context context, RecyclerView mainMessageList){
        Snackbar.make(mainMessageList,"Network become available. Please scroll to see new messages.",Snackbar.LENGTH_LONG).show();
    }
}
