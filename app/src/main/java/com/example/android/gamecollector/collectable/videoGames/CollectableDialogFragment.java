package com.example.android.gamecollector.collectable.videoGames;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.android.gamecollector.R;
import com.example.android.gamecollector.data.sqlite.CollectableContract.VideoGamesEntry;
import com.example.android.gamecollector.data.sqlite.CollectableProvider;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by shalom on 2017-11-13.
 * A full-screen dialog that allows user to input data about a collectable they are adding
 * to their collection.
 */

public class CollectableDialogFragment extends DialogFragment {
    public static final String LOG_TAG = CollectableDialogFragment.class.getSimpleName();
    private Context context;
    /*Map containing video game data of the clicked list item*/
    private HashMap<String, String> videoGameData;
    /*Object containing video game's data*/
    private VideoGame videoGame;
    /*String naming which region video game is locked to*/
    private String regionLock = null;
    /*Map of boolean values with keys for the possible components and values that realy whether the user has the component
     *True means the component is owned*/
    private HashMap<String, Boolean> componentsOwned = new HashMap<>();
    /**/
    private EditText note;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            /*Extract Hashmap containing video game's data*/
            videoGameData = (HashMap<String, String>) getArguments().getSerializable(CollectableProvider.VIDEO_GAME_DATA);
        }

        componentsOwned.put(VideoGame.GAME, false);
        componentsOwned.put(VideoGame.MANUAL, false);
        componentsOwned.put(VideoGame.BOX, false);
    }

    /*Inflate the layout to use as dialog or embedded fragment*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_collectable_dialog, container, false);

        /*Set string for Toolbar's title*/
        String toolbarTitle = R.string.fragment_collectable_title + " " + videoGameData.get(VideoGamesEntry.COLUMN_TITLE);
        /*Find Toolbar*/
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.activity_collectable_dialog_toolbar);
        /*Sets toolbar as ActionBar*/
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        /*Set toolbar's title*/
        toolbar.setTitle(toolbarTitle);
        /*Enable home button and supply a custom icon*/
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            /*Show custom drawable for up icon*/
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        note = (EditText) view.findViewById(R.id.activity_collectable_edittext_notes);

        /*Report that this fragment would like to participate in populating the options menu by
        receiving a call to onCreateOptionsMenu(Menu, MenuInflater) and related methods.*/
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*Causes dialog to cover whole screen*/
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /*Inflates activity_collectable_dialog_menu as the actionbar*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_collectable_dialog_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_collectable_dialog_action_save:
//                Update SQLite database Copies_Owned
                videoGame = populateVideoGame();
                videoGame.updateFirebase();

                /*Updated number of copes owned*/
                int updatedCopies = Integer.valueOf(videoGameData.get(VideoGamesEntry.COLUMN_COPIES_OWNED)) + 1;

                /*Key value pair used for updating database*/
                ContentValues sqliteUpdate = new ContentValues();
                sqliteUpdate.put(VideoGamesEntry.COLUMN_COPIES_OWNED, String.valueOf(updatedCopies));

                /*Update call*/
                int rowsUpdate = getContext().getContentResolver().update(VideoGamesEntry.CONTENT_URI, sqliteUpdate,
                        VideoGamesEntry.COLUMN_UNIQUE_ID + "=" + videoGame.getUniqueID(),
                        null);

                Log.i(LOG_TAG, "Rows updated: " + rowsUpdate);

                dismiss();

                break;
            case android.R.id.home:
//                Make sure to confirm discard of data if data was input
                dismiss();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Head method for handling the dialog's buttons*/
    private void handleButtons(ViewGroup container, View view) {
        /*Initialization of every ImageView on activty_add_collectable_dialog.xml for programmatic use*/
        ImageView usaFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_usa);
        ImageView japanFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_japan);
        ImageView euFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_european_union);
        ImageView game = (ImageView) view.findViewById(R.id.activity_collectable_image_game);
        ImageView manual = (ImageView) view.findViewById(R.id.activity_collectable_image_manual);
        ImageView box = (ImageView) view.findViewById(R.id.activity_collectable_image_box);

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
            @Override
            public void onClick(View v) {
                if (regionLock != VideoGame.USA) {
                    setSingleIconAsActive(usaFlag, japanFlag, euFlag);
                    regionLock = VideoGame.USA;
                } else if (regionLock == VideoGame.USA) {
                    setIconAsInactive(usaFlag);
                    regionLock = null;
                }
            }
        });

        japanFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regionLock != VideoGame.JAPAN) {
                    setSingleIconAsActive(japanFlag, usaFlag, euFlag);
                    regionLock = VideoGame.JAPAN;
                } else if (regionLock == VideoGame.JAPAN) {
                    setIconAsInactive(japanFlag);
                    regionLock = null;
                }
            }
        });

        euFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regionLock != VideoGame.EUROPEAN_UNION) {
                    setSingleIconAsActive(euFlag, usaFlag, japanFlag);
                    regionLock = VideoGame.EUROPEAN_UNION;
                } else if (regionLock == VideoGame.EUROPEAN_UNION) {
                    setIconAsInactive(euFlag);
                    regionLock = null;
                }
            }
        });
    }

    /*Initializes and handles onClickListeners for responding to when components are tapped*/
    private void setComponentsOwned(final ImageView game, final ImageView manual, final ImageView box) {
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (componentsOwned.get(VideoGame.GAME) == false) {
                    setIconAsActive(game);
                    componentsOwned.put(VideoGame.GAME, true);
                } else if (componentsOwned.get(VideoGame.GAME) == true) {
                    setIconAsInactive(game);
                    componentsOwned.put(VideoGame.GAME, false);
                }
            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (componentsOwned.get(VideoGame.MANUAL) == false) {
                    setIconAsActive(manual);
                    componentsOwned.put(VideoGame.GAME, true);
                } else if (componentsOwned.get(VideoGame.MANUAL) == true) {
                    setIconAsInactive(manual);
                    componentsOwned.put(VideoGame.GAME, false);
                }
            }
        });

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (componentsOwned.get(VideoGame.BOX) == false) {
                    setIconAsActive(box);
                    componentsOwned.put(VideoGame.GAME, true);
                } else if (componentsOwned.get(VideoGame.MANUAL) == true) {
                    setIconAsInactive(box);
                    componentsOwned.put(VideoGame.GAME, false);
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

    /*Populates VideoGame object*/
    private VideoGame populateVideoGame() {
        /*Get values from videoGameData*/
        String uniqueId = videoGameData.get(VideoGamesEntry.COLUMN_UNIQUE_ID);
        String console = videoGameData.get(VideoGamesEntry.COLUMN_CONSOLE);
        String title = videoGameData.get(VideoGamesEntry.COLUMN_TITLE);
        String licensee = videoGameData.get(VideoGamesEntry.COLUMN_LICENSEE);
        String released = videoGameData.get(VideoGamesEntry.COLUMN_RELEASED);

        VideoGame videoGame1 = new VideoGame(uniqueId, console, title, licensee, released);

                /*Get current date and time*/
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        videoGame1.setDateAdded(currentDateTimeString);
        videoGame1.setRegionLock(regionLock);
        videoGame1.setComponentsOwned(componentsOwned);
        videoGame1.setNote(note.getText().toString());

        return videoGame1;
    }
}
