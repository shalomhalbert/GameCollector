package com.example.android.gamecollector.data.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.gamecollector.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shalom on 2017-10-12.
 * ListView adapter that uses collectable data given as a Cursor as its resource.
 * Helps create list items for each row of data.
 */

public class CollectablesCursorAdaptor extends CursorAdapter {
    /*Used for tracking Log statments*/
    public static final String LOG_TAG = CursorAdapter.class.getSimpleName();

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
        return LayoutInflater.from(context).inflate(R.layout.activity_add_collectable_list_item, parent, false);
    }

    /*Binds modifications to a list item*/
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /*Initailly this will display a video game's console's logo*/
        ImageView displayImage = (ImageView) view.findViewById(R.id.activity_add_collectable_image_console_logo);
        TextView titleTextView = (TextView) view.findViewById(R.id.activity_add_collectable_text_title);
        TextView copiesTextView = (TextView) view.findViewById(R.id.activity_add_collectable_text_copies_owned);

        /*Extract properties from cursor*/
        String console = cursor.getString(cursor.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_CONSOLE));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_TITLE));
        String copiesOwned = cursor.getString(cursor.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_COPIES_OWNED));

        /*Set appropriate image to display*/
        switch(console) {
            case "GB":
                displayImage.setImageResource(R.drawable.gameboy_logo);
                break;
            case "GBC":
                displayImage.setImageResource(R.drawable.game_boy_color_logo);
                break;
            case "N64":
                displayImage.setImageResource(R.drawable.nintendo_64_logo);
                break;
            case "NES":
                displayImage.setImageResource(R.drawable.nintendo_entertainment_system_logo);
                break;
            case "SNES":
                displayImage.setImageResource(R.drawable.super_nintendo_entertainment_system_logo);
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
                String rowID = cursor.getString(cursor.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_ROW_ID));

//                Intent intent = new Intent(context.getApplicationContext(), CollectableDialogFragment.class);
//                intent.putExtra(CollectablesSQLContract.VideoGamesEntry.COLUMN_ROW_ID, rowID);
//                context.startActivity(intent);
            }
        });
    }
}
