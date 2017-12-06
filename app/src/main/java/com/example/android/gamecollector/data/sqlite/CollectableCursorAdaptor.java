package com.example.android.gamecollector.data.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.android.gamecollector.CollectableDialogFragment;
import com.example.android.gamecollector.R;
import com.example.android.gamecollector.customviews.CustomTextView;
import com.example.android.gamecollector.data.sqlite.CollectableContract.VideoGamesEntry;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shalom on 2017-10-12.
 * ListView adapter that uses collectable data given as a Cursor as its resource.
 * Helps create list items for each row of data.
 */

//    TODO(1) Fix text fonts

public class CollectableCursorAdaptor extends CursorAdapter {
    /*Used for tracking Log statments*/
    public static final String LOG_TAG = CursorAdapter.class.getSimpleName();

    /*Instantiations for Firebase Realtime Database*/
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FragmentManager fragmentManager;

    /**
     * @param context The context
     * @param c       Cursor from which data is extracted
     */
    public CollectableCursorAdaptor(Context context, Cursor c, FragmentManager fragmentManager) {
        /*Set flags to 0*/
        super(context, c, 0);
        this.fragmentManager = fragmentManager;
    }

    /**
     * Inflates ViewGroup used by Adapter
     * @param context App context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return The newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        /*Initialize ListView to setup onItemClickListener*/
        ListView listView = (ListView) parent.findViewById(R.id.activity_collectable_listview);
        listItemListener(listView, context, cursor);
        /*Layout that will be inflated*/
        return LayoutInflater.from(context).inflate(R.layout.activity_collectable_list_item, parent, false);
    }

    /*Binds modifications to a list item*/
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /*Initailly this will display a video game's console's logo*/
        ImageView displayImage = (ImageView) view.findViewById(R.id.image_console_logo);
        CustomTextView titleTextView = (CustomTextView) view.findViewById(R.id.customtext_title);
        CustomTextView copiesTextView = (CustomTextView) view.findViewById(R.id.customtext_copies_owned);

        /*Extract properties from cursor*/
        String console = cursor.getString(cursor.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_CONSOLE));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_TITLE));
        String copiesOwned = cursor.getString(cursor.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_COPIES_OWNED));

        /*Set appropriate image to display*/
        switch(console) {
            case "GB":
                displayImage.setImageResource(R.drawable.ic_gameboy_logo);
                break;
            case "GBC":
                displayImage.setImageResource(R.drawable.ic_gameboy_color_logo);
                break;
            case "N64":
                displayImage.setImageResource(R.drawable.ic_n64_logo);
                break;
            case "NES":
                displayImage.setImageResource(R.drawable.ic_nes_logo);
                break;
            case "SNES":
                displayImage.setImageResource(R.drawable.ic_snes_logo);
                break;
            default:
                Log.e(LOG_TAG, "Unable to locate console named: " + console);
                break;
        }

        /*Display appropriate title*/
        titleTextView.setText(title);

        /*Show nothing if zero copies are owned*/
        if(Integer.valueOf(copiesOwned) == 0) {
            copiesTextView.setVisibility(View.INVISIBLE);
        } else {
            copiesTextView.setText(copiesOwned + " Owned");
        }
    }

    /*Initializes OnItemClickListener()
     *When clicked it passes rowID through explicit intent*/
    private void listItemListener(final ListView listView, final Context context, final Cursor cursor) {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*Bundle that'll be passed onto the dialog fragment*/
                Bundle dialogBundle = new Bundle();
                dialogBundle.putString(VideoGamesEntry.COLUMN_ROW_ID, String.valueOf(id));
                

                /*Get cursor's data as a Bundle which holds a HashMap*/
                Bundle cursorDataBundle = context.getContentResolver()
                        .call(VideoGamesEntry.CONTENT_URI, "getItemData", 
                                null, dialogBundle);


                showDialog(cursorDataBundle);
            }
        });
    }

    /*Opens dialog*/
    private void showDialog(Bundle bundle) {
        /*Required for fragmentTransaction.add()*/
        int containerViewId = android.R.id.content;

                /*Initialize CollectableDialogFragment*/
        CollectableDialogFragment dialogFragment = new CollectableDialogFragment();
                /*Supplies arguments to dialogFragment*/
        dialogFragment.setArguments(bundle);
                /*fragmentManager is taken in constructor and FragmentTransaction makes transactions*/
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                /*Sets transition effect for when dialog opens*/
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.add(containerViewId, dialogFragment).addToBackStack(null).commit();
    }
}
