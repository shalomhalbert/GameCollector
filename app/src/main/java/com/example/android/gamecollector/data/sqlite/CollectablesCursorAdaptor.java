package com.example.android.gamecollector.data.sqlite;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.gamecollector.MainActivity;
import com.example.android.gamecollector.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by shalom on 2017-10-12.
 * ListView adapter that uses collectable data given as a Cursor as its resource.
 * Helps create list items for each row of data.
 */

public class CollectablesCursorAdaptor extends CursorAdapter {
    /*Used for tracking Log statments*/
    public static final String LOG_TAG = CursorAdapter.class.getSimpleName();

    /*Column names in SQLite database for video games collectables*/
    public static final String _ID = "_id";
    public static final String CONSOLE = "console";
    public static final String TITLE = "title";
    public static final String LICENSEE = "licensee";
    public static final String RELEASED = "released";

    /*Instantiations for Firebase Realtime Database*/
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    /**
     * @param context The context
     * @param c       Cursor from which data is extracted
     */
    public CollectablesCursorAdaptor(Context context, Cursor c) {
        /*Set flags to 0*/
        super(context, c, 0);
    }

    /**
     * Inflates ViewGroup used by Adapter
     *
     * @param context App context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return The newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        /*Initialize ListView to setup onItemClickListener*/
        ListView listView = (ListView) parent.findViewById(R.id.collectables_list_view);
        listItemListener(listView, context, cursor);
        /*Layout that will be inflated*/
        return LayoutInflater.from(context).inflate(R.layout.collectable_list_item, parent, false);
    }

    /*Binds modifications to a list item*/
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /*TextViews that'll be inflated*/
        TextView titleTextView = (TextView) view.findViewById(R.id.collectable_title);
        TextView brandTextView = (TextView) view.findViewById(R.id.collectable_brand);

        /*Extract properties from cursor*/
        String title = cursor.getString(cursor.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_TITLE));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_CONSOLE));

        /*Set Cursor properties to views*/
        titleTextView.setText(title);
        brandTextView.setText(brand);
    }

    /*Initializes OnItemClickListener()
    * Gets the item's data, tests if it's a duplicate, if not the data is put through to the popup window*/
    private void listItemListener(final ListView listView, final Context context, final Cursor cursor) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupWindow(context, cursor);
            }
        });
    }

    /*After list item is tapped, a popup window appears*/
    public void showPopupWindow(final Context context, final Cursor cursor) {
        /*Inflate the layout of the popup window*/
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_collectable_popup, null);

        /*Get a reference to the already inflated layout*/
        RelativeLayout popup_window_relative_layout = (RelativeLayout) popupView.findViewById(R.id.relative_layout_add_collectable_popup);

        /*Create the popup window*/
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; /*Lets taps outside the popup also dismiss it*/
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        /*Show the popup window*/
        popupWindow.showAtLocation(popup_window_relative_layout, Gravity.CENTER, 0, 0);

        handleButtons(context, cursor, popupView, popupWindow);

    }

    /*Runs an explicit intent that opens the MainActivity*/
    private void returnToMainActivity(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        context.startActivity(intent);
    }

    /*Handles onClickListeners for buttons in the popup window*/
    private void handleButtons(final Context context, final Cursor cursor, View popupView, final PopupWindow popupWindow) {
         /*Initialize response buttons*/
        Button confirmButton = (Button) popupView.findViewById(R.id.confirm_addition);
        Button declineButton = (Button) popupView.findViewById(R.id.decline_addition);

        /*If user confirms addition of a collectable item to their collection, it's data will be
        * add to the Firebase Realtime Database*/
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Initialize a Map containing all the values of the cursor*/
                Map<String,String> cursorData = getItemData(context, cursor);

                /*Initialize individual variables for each datapoint in cursor*/
                String collectableId = cursorData.get(_ID);
                String collectableConsole = cursorData.get(CONSOLE);
                String collectableTitle = cursorData.get(TITLE);
                String collectableLicensee = cursorData.get(LICENSEE);
                String collectableReleased = cursorData.get(RELEASED);

                /*Create a unique ID that names a node for an individual video game when it's added*/
                String uniqueNodeId = UUID.randomUUID().toString();

                String logInfo = "uniqueNodeId: " + uniqueNodeId + ", ID: " + collectableId
                        + ", Console: " + collectableConsole + ", Title: " + collectableTitle;
                Log.i(LOG_TAG, logInfo);

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference()
                        .child("collectables_owned")
                        .child("video_games")
                        .child(uniqueNodeId);

                databaseReference.child(CollectablesSQLContract.VideoGamesEntry.COLUMN_ID).setValue(collectableId);
                databaseReference.child(CollectablesSQLContract.VideoGamesEntry.COLUMN_CONSOLE).setValue(collectableConsole);
                databaseReference.child(CollectablesSQLContract.VideoGamesEntry.COLUMN_TITLE).setValue(collectableTitle);
                databaseReference.child(CollectablesSQLContract.VideoGamesEntry.COLUMN_LICENSEE).setValue(collectableLicensee);
                databaseReference.child(CollectablesSQLContract.VideoGamesEntry.COLUMN_RELEASED).setValue(collectableReleased);

                returnToMainActivity(context);
            }
        });

        /*If user doesn't want the item added to their collection, the popup window disappears*/
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /*Returns the selected collectable's data as a Map */
    private Map<String, String> getItemData(final Context context, final Cursor cursor) {
        /*Map will contain item data*/
        Map<String, String> map = new HashMap<>();

        /*Get row ID for tapped collectable item*/
        long rowId = cursor.getInt(cursor.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_ID));
        String[] selectionArgs = {String.valueOf(rowId)};
        /*Create Uri for the tapped collectable item*/
        Uri individualItemUri = ContentUris.withAppendedId(CollectablesSQLContract.VideoGamesEntry.CONTENT_URI, rowId);

        /*Get cursor with data belonging to the tapped collectable item*/
        Cursor newCollectable = context.getContentResolver().query(individualItemUri, null, null, selectionArgs, null);

        if (newCollectable != null && newCollectable.moveToFirst()) {
            /*Get every value from cursor*/
            //Reformat code to centralize this action
            String collectableId = newCollectable.getString(newCollectable.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_ID));
            String collectableConsole = newCollectable.getString(newCollectable.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_CONSOLE));
            String collectableTitle = newCollectable.getString(newCollectable.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_TITLE));
            String collectableLicensee = newCollectable.getString(newCollectable.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_LICENSEE));
            String collectableReleased = newCollectable.getString(newCollectable.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_RELEASED));

            /*Add values to Map*/
            map.put(_ID, collectableId);
            map.put(CONSOLE, collectableConsole);
            map.put(TITLE, collectableTitle);
            map.put(LICENSEE, collectableLicensee);
            map.put(RELEASED, collectableReleased);
        } else {
            Log.e(LOG_TAG, "Problem getting cursor values");
            return null;
        }

        return map;
    }
}