package com.infinitemessage.adewan.infinitemessage.ui;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Custom implementation for the ItemTouchHelper.SimpleCallback class
 * to handle the swipe off of elements within the recycler view and
 * to perform opertations that allow for animating the alpha while swiping
 * cards away in the recycler view.
 * Created by a.dewan on 5/13/17.
 */

public class MessageListSwipeCallback extends ItemTouchHelper.SimpleCallback {

    /**
     * While swiping the cards away the threshold value ensure that more
     * cards are added to the list if the value of cards in the recyclerview
     * dips below this threshold value.
     */
    int MIN_CARDS_IN_LIST_THRESHOLD_VALUE = 5;

    public MessageListSwipeCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * We are only interested in this method as when the item is swiped away, this method is called
     * and we can use the viewholder to tell the adapter to remove the required item and
     * do the required changes to its layout
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        /**
         * Swiped items position's adapater position is used passed to the removeDataAtIndex()
         * method in order to remove that particular data point from the adapter and notify
         * the recyclerview about any changes made.
         *
         * Also checks to see if the current recycler view adapater size is less than threshold
         * then it calls the addMoreDataToAdapter() method to get more messages and notify the
         * recyclerview.
         */
        MessageListAdapter.removeDataAtIndex(viewHolder.getAdapterPosition());
        if(MessageListAdapter.getAdapterSize() < MIN_CARDS_IN_LIST_THRESHOLD_VALUE){
            MessageListAdapter.addMoreDataToAdapter(false);
        }
    }

    /**
     * Called by the ItemTouchHelper class to draw the view when the user interacts with the items
     * Customizing this to respond the swipe direction and change the alpha value accordingly
     * provides us with the animation of the element fading while being swiped.
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        /**
         * The basic idea is that since this methods provides us with the details about how
         * much the card in the feed has moved in the x-direction, we can use that value as
         * a ratio with the width of the parent to help change the alpha value of the card
         * to give it the animation effect of fading away as it's being swiped.
         *
         * e.g alphaChange = maxAlpha - (change in x-direction / view height) // the more the movement in
         * x direction the less the alpha value is and the maxAlpha is at the state where the card is
         * in it's original position.
         *
         * Also since we are overriding this method, we need to also take care of the actual translation
         * of the card in the x direction.
         */
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            float newAlphaValue =  1.0f - Math.abs(dX)/(float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(newAlphaValue);
            viewHolder.itemView.setTranslationX(dX);
        }else{
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
