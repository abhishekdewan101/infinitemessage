package com.infinitemessage.adewan.infinitemessage.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitemessage.adewan.infinitemessage.R;

/**
 * Custom ViewHolder class to define the view elements
 * for the items within the recyclerview
 * This class can be used later to make more manupilations to the itemview elements.
 * Created by a.dewan on 5/11/17.
 */

class MessageListViewHolder extends RecyclerView.ViewHolder {

    TextView mContactName;
    ImageView mAvatarImage;
    TextView mTimeStamp;
    TextView mMessageContent;

    public MessageListViewHolder(View itemView) {
        super(itemView);
        mContactName = (TextView) itemView.findViewById(R.id.contact_name);
        mAvatarImage = (ImageView) itemView.findViewById(R.id.avatar_image);
        mTimeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
        mMessageContent = (TextView) itemView.findViewById(R.id.message_content);
    }
}
