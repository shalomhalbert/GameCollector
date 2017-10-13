package com.example.android.gamecollector.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.gamecollector.R;
import com.example.android.gamecollector.data.CollectablesContract.*;

/**
 * Created by shalom on 2017-10-12.
 * ListView adapter that uses collectable data given as a Cursor as its resource.
 * Helps create list items for each row of data.
 */

public class VideoGameCursorAdaptor extends CursorAdapter {

    /**
     * @param context   The context
     * @param c         Cursor from which data is extracted
     */
    public VideoGameCursorAdaptor(Context context, Cursor c) {
        /*Set flags to 0*/
        super(context, c, 0);
    }

    /**
     * Inflates ViewGroup used by Adapter
     * @param context   App context
     * @param cursor    The cursor from which to get the data. The cursor is already moved to the correct position.
     * @param parent    The parent to which the new view is attached to
     * @return          The newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        /*Layout that will be inflated*/
        return LayoutInflater.from(context).inflate(R.layout.item_description, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /*TextViews that'll be inflated*/
        TextView titleTextView = (TextView) view.findViewById(R.id.collectable_title);
        TextView brandTextView = (TextView) view.findViewById(R.id.collectable_brand);

        /*Extract properties from cursor*/
        String title = cursor.getString(cursor.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_TITLE));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_CONSOLE));

        /*Set Cursor properties to views*/
        titleTextView.setText(title);
        brandTextView.setText(brand);
    }
}
