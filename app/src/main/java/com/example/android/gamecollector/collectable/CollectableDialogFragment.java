package com.example.android.gamecollector.collectable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.android.gamecollector.R;
import com.example.android.gamecollector.data.sqlite.CollectablesSQLContract;
import com.example.android.gamecollector.collected.videoGames.PersonalCollectionActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by shalom on 2017-11-13.
 * A full-screen dialog that allows user to input data about a collectable they are adding
 * to their collection.
 */

public class CollectableDialogFragment extends DialogFragment {
    Context context;

    /*Inflate the layout to use as dialog or embedded fragment*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_collectable_dialog, container, false);



        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    /*Inflates activity_collectable_dialog_menu as the actionbar*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_collectable_dialog_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        setup switch-case for each action
        return super.onOptionsItemSelected(item);
    }

    /*Head method for handling the dialog's buttons*/
    private void handleButtons(ViewGroup container, View view) {
        /*Initialization of every ImageView on activty_add_collectable_dialog.xml for programmatic use*/
        ImageView usaFlag = (ImageView) view.findViewById(R.id.activity_add_collectable_image_usa);
        ImageView japanFlag = (ImageView) view.findViewById(R.id.activity_add_collectable_image_japan);
        ImageView euFlag = (ImageView) view.findViewById(R.id.activity_add_collectable_image_european_union);
        ImageView game = (ImageView) view.findViewById(R.id.activity_add_collectable_image_game);
        ImageView manual = (ImageView) view.findViewById(R.id.activity_add_collectable_image_manual);
        ImageView box = (ImageView) view.findViewById(R.id.activity_add_collectable_image_box);

        /*ArrayList of all icons*/
        ArrayList<ImageView> imageViews = new ArrayList<>();
        imageViews.add(usaFlag);
        imageViews.add(japanFlag);
        imageViews.add(euFlag);
        imageViews.add(game);
        imageViews.add(manual);
        imageViews.add(box);

        setButtonTintInactive(imageViews);

        setRegionLock(usaFlag, japanFlag, euFlag);

        setComponentsOwned(game, manual, box);

    }

    /*Sets every icon's tint to colorInactiveIcon*/
    private void setButtonTintInactive(ArrayList<ImageView> imageViews) {
        for (ImageView icon : imageViews) {
            icon.setColorFilter(ContextCompat.getColor(getView().getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
        }
    }

    /*Initializes and handles onClickListeners for responding to when flags are tapped*/
    private void setRegionLock(final ImageView usaFlag, final ImageView japanFlag, final ImageView euFlag) {
        usaFlag.setOnClickListener(new View.OnClickListener() {
            boolean activated = false;

            @Override
            public void onClick(View v) {
                if (activated == false) {
                    setSingleIconAsActive(usaFlag, japanFlag, euFlag);
                    activated = true;
                } else if (activated == true) {
                    setIconAsInactive(usaFlag);
                    activated = false;
                }
            }
        });

        japanFlag.setOnClickListener(new View.OnClickListener() {
            boolean activated = false;

            @Override
            public void onClick(View v) {
                if (activated == false) {
                    setSingleIconAsActive(japanFlag, usaFlag, euFlag);
                    activated = true;
                } else if (activated == true) {
                    setIconAsInactive(japanFlag);
                    activated = false;
                }
            }
        });

        euFlag.setOnClickListener(new View.OnClickListener() {
            boolean activated = false;

            @Override
            public void onClick(View v) {
                if (activated == false) {
                    setSingleIconAsActive(euFlag, usaFlag, japanFlag);
                    activated = true;
                } else if (activated == true) {
                    setIconAsInactive(euFlag);
                    activated = false;
                }
            }
        });
    }

    /*Initializes and handles onClickListeners for responding to when components are tapped*/
    private void setComponentsOwned(final ImageView game, final ImageView manual, final ImageView box) {
        game.setOnClickListener(new View.OnClickListener() {
            boolean activated = false;

            @Override
            public void onClick(View v) {
                if (activated == false) {
                    setIconAsActive(game);
                    activated = true;
                } else if (activated == true) {
                    setIconAsInactive(game);
                    activated = false;
                }
            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            boolean activated = false;

            @Override
            public void onClick(View v) {
                if (activated == false) {
                    setIconAsActive(manual);
                    activated = true;
                } else if (activated == true) {
                    setIconAsInactive(manual);
                    activated = false;
                }
            }
        });

        box.setOnClickListener(new View.OnClickListener() {
            boolean activated = false;

            @Override
            public void onClick(View v) {
                if (activated == false) {
                    setIconAsActive(box);
                    activated = true;
                } else if (activated == true) {
                    setIconAsInactive(box);
                    activated = false;
                }
            }
        });
    }

    /*Sets tints to display only the tapped icon as active*/
    private void setSingleIconAsActive(ImageView active, ImageView inactive1, ImageView inactive2) {
        /*Changes icon's tint to inactive color*/
        active.setColorFilter(ContextCompat.getColor(getView().getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
        /*Changes icon's tint to inactive color*/
        inactive1.setColorFilter(ContextCompat.getColor(getView().getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
        inactive2.setColorFilter(ContextCompat.getColor(getView().getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
    }

    /*Changes icon's tint to active color*/
    private void setIconAsActive(ImageView imageView) {
        imageView.setColorFilter(ContextCompat.getColor(getView().getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
    }

    /*Changes icon's tint to inactive color*/
    private void setIconAsInactive(ImageView imageView) {
        imageView.setColorFilter(ContextCompat.getColor(getView().getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
    }


    /*Runs an explicit intent that opens PersonalCollectionActivity*/
    private void returnToMainActivity(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), PersonalCollectionActivity.class);
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
                Map<String, String> cursorData = getItemData(context, cursor);

                /*Initialize individual variables for each datapoint in cursor*/
                String collectableUniqueId = cursorData.get(CollectablesSQLContract.VideoGamesEntry.COLUMN_UNIQUE_ID);
                String collectableConsole = cursorData.get(CollectablesSQLContract.VideoGamesEntry.COLUMN_CONSOLE);
                String collectableTitle = cursorData.get(CollectablesSQLContract.VideoGamesEntry.COLUMN_TITLE);
                String collectableLicensee = cursorData.get(CollectablesSQLContract.VideoGamesEntry.COLUMN_LICENSEE);
                String collectableReleased = cursorData.get(CollectablesSQLContract.VideoGamesEntry.COLUMN_RELEASED);

                /*Create a unique ID that names a node for an individual video game when it's added*/
                String uniqueNodeId = UUID.randomUUID().toString();

                String logInfo = "uniqueNodeId: " + collectableUniqueId
                        + ", Console: " + collectableConsole
                        + ", Title: " + collectableTitle;
                Log.i(LOG_TAG, logInfo);

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference()
                        .child("collectables_owned")
                        .child("video_games")
                        .child(uniqueNodeId);

                databaseReference.child(CollectablesSQLContract.VideoGamesEntry.COLUMN_UNIQUE_ID).setValue(collectableUniqueId);
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
        long rowId = cursor.getInt(cursor.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_ROW_ID));
        String[] selectionArgs = {String.valueOf(rowId)};
        /*Create Uri for the tapped collectable item*/
        Uri individualItemUri = ContentUris.withAppendedId(CollectablesSQLContract.VideoGamesEntry.CONTENT_URI, rowId);

        /*Get cursor with data belonging to the tapped collectable item*/
        Cursor newCollectable = context.getContentResolver().query(individualItemUri, null, null, selectionArgs, null);

        if (newCollectable != null && newCollectable.moveToFirst()) {
            /*Get every value from cursor*/
            //Reformat code to centralize this action
            String collectableId = newCollectable.getString(newCollectable.getColumnIndexOrThrow(CollectablesSQLContract.VideoGamesEntry.COLUMN_ROW_ID));
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
