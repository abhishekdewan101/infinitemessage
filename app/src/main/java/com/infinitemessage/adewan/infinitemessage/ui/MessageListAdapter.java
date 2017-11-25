package com.infinitemessage.adewan.infinitemessage.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.infinitemessage.adewan.infinitemessage.R;
import com.infinitemessage.adewan.infinitemessage.model.Message;
import com.infinitemessage.adewan.infinitemessage.model.MessageData;
import com.infinitemessage.adewan.infinitemessage.network.DataManager;
import com.infinitemessage.adewan.infinitemessage.util.Utils;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Class is the custom implementation of recyclerview.adapter class and is
 * written to provide the basic functionality of adding and removing data from
 * the recyclerview.
 *
 * Class also attaches a onScrollListner to the recyclerview to implement the
 * auto data loading feature that makes the recyclerview endless.
 *
 * Class uses the Glide Library in order to insert the pictures and also perform
 * crop circle changes to the images.
 * Created by a.dewan on 5/11/17.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder> {

    public static final String BASE_URL = "http://message-list.appspot.com/";

    /**
     * The following values are used by the onScrollListner to
     * implement the infinite scrolling of the recycler view.
     *
     * totalItemsInRecyclerView - total number of items that the recyclerview has in it's group
     * totalItemCount - total number of items that are present in the layout manager of the recyclerview
     * firstVisibleItem - adapter index of the first visible item
     * MIN_CARDS_LEFT_THRESHOLD_VALUE - parameter that defines how many cards should be left to be seen in the recyclerview
     * previousTotal - previous total number of items were present in the layout manager of the recyclerview.
     * newDataLoaded - boolean value to denotes that some new data was added to the adapter
     *
     */
    private int totalItemsInRecyclerView;
    private static int totalItemCount;
    private int firstVisibleItem;
    static int previousTotal = 0;
    private int MIN_CARDS_LEFT_THRESHOLD_VALUE = 15;
    static boolean newDataLoaded = true;

    /**
     * Boolean value to prevent showing of multiple
     * snackbar as the user is scrolling
     */
    private Boolean isNetworkSnackbarShow = false;

    /**
     * ArrayList of message items that contain the adapter's data.
     */
    static ArrayList<Message> adapterData;

    static RecyclerView mainView;
    static Context context;

    MessageListSwipeCallback messageListSwipeCallback;
    ItemTouchHelper itemTouchHelper;
    LinearLayoutManager layoutManager;

    public MessageListAdapter(ArrayList<Message> objects, final RecyclerView mainView, final Context context) {
        adapterData = objects;
        this.mainView = mainView;
        this.context = context;

        layoutManager = (LinearLayoutManager) mainView.getLayoutManager();
        attachScrollListener(mainView);
        attachItemTouchListener();
    }

    /**
     * Attaches the item touch listener defined in the MessageListSwipeCallback class
     * to the recyclerview and enables the swipe of cards in both right and left directions
     */
    private void attachItemTouchListener() {
        messageListSwipeCallback = new MessageListSwipeCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        itemTouchHelper = new ItemTouchHelper(messageListSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mainView);
    }

    /**
     * Attaches a onScrollListener to the recyclerview.
     * Overrides the onScrolled method to implement the functionality
     * of auto loading data when a certain threshold is met.
     *
     * The basic idea is that if the number of items left to
     * be seen in the recyclerview is less than equal to a certain
     * threshold value then we need to load more data into the recycler
     * otherwise the user will the see the bottom of the list
     */
    private void attachScrollListener(final RecyclerView mainView) {

        mainView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemsInRecyclerView = recyclerView.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                /**
                 * If new data has been loaded and the new totalItemCount is going to be
                 * greater than the previousTotal so we know that new data has finished loading
                 * and we need to be a keep a reference to the new previous total
                 */
                if(newDataLoaded && (totalItemCount > previousTotal)){
                        newDataLoaded = false;
                        previousTotal = totalItemCount;
                }

                /**
                 * If new data has finished loading and if the total number of items left to be seen
                 * is less than equal to the threshold than load more data.
                 *
                 * firstVisibleItem is used to keep the reference of the threshold relevant to
                 * the total number of items that are present in the recyclerview.
                 *
                 * E.g if totalItemCount = 300 and thresholdValue is 15 then the firstVisible item
                 * is added to the threshold value to keep the frame of reference comparable to the
                 * totalItemCount
                 */
                if(!newDataLoaded && (totalItemCount - totalItemsInRecyclerView)<=(firstVisibleItem + MIN_CARDS_LEFT_THRESHOLD_VALUE)){
                    if(Utils.isNetworkAvailable(context)){

                        DataManager.getMessageDataWithToken()
                                   .subscribeOn(Schedulers.newThread())
                                   .retry()
                                   .observeOn(AndroidSchedulers.mainThread())
                                   .subscribe(new Action1<MessageData>() {
                                      @Override
                                      public void call(MessageData messageData) {
                                        DataManager.nextPageToken = messageData.getPageToken();
                                        MessageListAdapter.addToAdapter(messageData.getMessages());
                                      }
                                   });

                        newDataLoaded = true;
                        isNetworkSnackbarShow = false;

                    }else{
                        if(!isNetworkSnackbarShow){
                            Utils.showWifiSnackbar(context,mainView);
                            isNetworkSnackbarShow = true;
                        }
                    }
                }
            }
        });

    }


    @Override
    public MessageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card_layout,parent,false);
        return new MessageListViewHolder(cardItemView);
    }

    /**
     * Method ensures that the correct data is added to the ViewHolder
     * so that it may be displayed to the user.
     *
     * Method uses Glide to handle the loading of images to the imageview.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MessageListViewHolder holder, int position) {
        holder.mMessageContent.setText(adapterData.get(position).getContent());
//        holder.mContactName.setText(adapterData.get(position).getAuthor().getName() + " - "+adapterData.get(position).getId());
        holder.mContactName.setText(adapterData.get(position).getAuthor().getName());
        Glide.with(context).load(BASE_URL + adapterData.get(position).getAuthor().getPhotoUrl()).bitmapTransform(new CropCircleTransformation(context)).into(holder.mAvatarImage);
    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }


    /**
     * Static method that appends more message data to the end of the current adapter data
     * and ensures that the data is saved back to the local cache
     * @param messagesToAdd new messages that need to be added to
     *                      the recyclerview
     */
    public static void addToAdapter(ArrayList<Message> messagesToAdd){
        int lastPosition = adapterData.size();
        adapterData.addAll(lastPosition,messagesToAdd);
        mainView.getAdapter().notifyItemInserted(lastPosition);
        DataManager.saveAdapterData(adapterData,context);
    }

    /**
     * Static method that removes the item specified by i and then makes sure the new adapter
     * data is saved to the local cache and also that the previous total is
     * updated to show that an item was removed.
     * @param i index of the items to be removed.
     */
    public static void removeDataAtIndex(int i){
        adapterData.remove(i);
        mainView.getAdapter().notifyItemRemoved(i);
        DataManager.saveAdapterData(adapterData,context);
        previousTotal--;
    }

    /**
     * Static Method returns the size of the recyclerview adapter
     * @return the size of the arralist that holds the adpater data
     */
    public static int getAdapterSize(){
        return adapterData.size();
    }


    /**
     * Method is responsible for subscribing to the observable that gets
     * more message data from the API and then adds it to the adapter.
     * @param isNetworkCall if true means that the method was called to add more data
     *                      as the network became available so show the newMessageSnackbar
     */
    public static void addMoreDataToAdapter(final boolean isNetworkCall){
        Log.d("[DATA]","Position 3");
        newDataLoaded = true;
        DataManager.getMessageDataWithToken()
                   .subscribeOn(Schedulers.newThread())
                   .observeOn(AndroidSchedulers.mainThread())
                   .retry()
                   .subscribe(new Action1<MessageData>() {
                     @Override
                     public void call(MessageData messageData) {
                      DataManager.nextPageToken = messageData.getPageToken();
                      MessageListAdapter.addToAdapter(messageData.getMessages());
                      if(isNetworkCall){
                          Utils.showNewMessageSnackbar(context,mainView);
                      }
                     }
                   });
    }
}
