package com.example.android.gamecollector;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import android.widget.TextView;

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
    /*Constants for setting Bundle keys that will be passed to this fragment*/
    public static final String SQLITE_DATA = "sqliteData";
    public static final String FIREBASE_DATA = VideoGame.KEY_UNIQUE_NODE_ID;
    private Context context;
    /*Map containing video game data of the clicked list item*/
    private HashMap<String, String> contentProviderBundle;
    /*Object containing video game's data*/
    private VideoGame videoGame;
    /*Instantiated views*/
    private ImageView usaFlag;
    private ImageView japanFlag;
    private ImageView euFlag;
    private ImageView game;
    private ImageView manual;
    private ImageView box;
    private EditText noteEditText;

    /*Initialize contentProviderBundle and componentsOwned, and handle clicking*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(SQLITE_DATA)) {
                /*Extract Hashmap containing video game's data*/
                contentProviderBundle = (HashMap<String, String>) getArguments().getSerializable(CollectableProvider.VIDEO_GAME_DATA);

                /*Set VideoGame's values*/
                videoGame = new VideoGame(contentProviderBundle.get(VideoGame.KEY_UNIQUE_ID),
                        contentProviderBundle.get(VideoGame.KEY_CONSOLE),
                        contentProviderBundle.get(VideoGame.KEY_TITLE),
                        contentProviderBundle.get(VideoGame.KEY_LICENSEE),
                        contentProviderBundle.get(VideoGame.KEY_RELEASED),
                        Integer.valueOf(contentProviderBundle.get(VideoGame.KEY_COPIES_OWNED)));
                /*Express user has not indicated ownership of any components*/
                videoGame.setValueGame(false);
                videoGame.setValueManual(false);
                videoGame.setValueBox(false);
            } else if (getArguments().containsKey(FIREBASE_DATA)) {
                videoGame = new VideoGame();

                for (String key : getArguments().keySet()) {
                    if (key == VideoGame.KEY_COMPONENTS_OWNED) {
                        /*Extract serialized HashMap*/
                        videoGame.setValuesComponentsOwned((HashMap<String, Boolean>) getArguments().getSerializable(VideoGame.KEY_COMPONENTS_OWNED));
                    }
                    switch (key) {
                        case VideoGame.KEY_UNIQUE_NODE_ID:
                            videoGame.setValueUniqueNodeId(getArguments().getString(VideoGame.KEY_UNIQUE_NODE_ID));
                            break;
                        case VideoGame.KEY_TITLE:
                            videoGame.setValueTitle(getArguments().getString(VideoGame.KEY_TITLE));
                            break;
                        case VideoGame.KEY_REGION_LOCK:
                            videoGame.setValueRegionLock(getArguments().getString(VideoGame.KEY_REGION_LOCK));
                            break;
                        case VideoGame.KEY_NOTE:
                            videoGame.setValueNote(getArguments().getString(VideoGame.KEY_NOTE));
                            break;
                        default:
                            Log.i(LOG_TAG, "Case 'default' is running");
                            break;
                    }
                }
            }
        }


    }

    /*Inflate the layout to use as dialog or embedded fragment*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_collectable_dialog, container, false);

        /*Set string for Toolbar's title*/
        String toolbarTitle = setToolbarTitle();
        /*Find Toolbar*/
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
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

        /*Initialize views*/
        noteEditText = (EditText) view.findViewById(R.id.activity_collectable_edittext_notes);
        handleButtons(container, view);

        /*Populate views if possible*/
        if (videoGame.getValueRegionLock() != null) {
            clickRegionLock(videoGame.getValueRegionLock());
        }
        if (videoGame.getValueNote() != null && videoGame.getValueNote() != "") {
            populateNote(videoGame.getValueNote());
        }
        int componentsClicked = clickComponentsOwned();
        Log.i(LOG_TAG, componentsClicked + " componenets were clicked");

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
                if (videoGame.getValueUniqueNodeId() == null) {
                    setDate();
                    setNote();
                    videoGame.createNode();
                /*Updated number of copes owned*/
                    int updatedCopies = videoGame.getValueCopiesOwned() + 1;
                /*Key value pair used for updating database*/
                    ContentValues sqliteUpdate = new ContentValues();
                    sqliteUpdate.put(VideoGamesEntry.COLUMN_COPIES_OWNED, String.valueOf(updatedCopies));
                /*Update SQLite database*/
                    int rowsUpdate = getContext().getContentResolver().update(VideoGamesEntry.CONTENT_URI, sqliteUpdate,
                            VideoGamesEntry.COLUMN_UNIQUE_ID + "=" + videoGame.getValueUniqueID(),
                            null);

                    Log.i(LOG_TAG, "Rows updated: " + rowsUpdate);
                } else {
                    setNote();
                    videoGame.updateNode();
                    dismiss();
                    break;
                }

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
        usaFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_usa);
        japanFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_japan);
        euFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_european_union);
        game = (ImageView) view.findViewById(R.id.activity_collectable_image_game);
        manual = (ImageView) view.findViewById(R.id.activity_collectable_image_manual);
        box = (ImageView) view.findViewById(R.id.activity_collectable_image_box);

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
                if (videoGame.getValueRegionLock() != VideoGame.USA) {
                    setSingleIconAsActive(usaFlag, japanFlag, euFlag);
                    videoGame.setValueRegionLock(VideoGame.USA);
                } else if (videoGame.getValueRegionLock() == VideoGame.USA) {
                    setIconAsInactive(usaFlag);
                    videoGame.setValueRegionLock(null);
                }
            }
        });

        japanFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoGame.getValueRegionLock() != VideoGame.JAPAN) {
                    setSingleIconAsActive(japanFlag, usaFlag, euFlag);
                    videoGame.setValueRegionLock(VideoGame.JAPAN);
                } else if (videoGame.getValueRegionLock() == VideoGame.JAPAN) {
                    setIconAsInactive(japanFlag);
                    videoGame.setValueRegionLock(null);
                }
            }
        });

        euFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoGame.getValueRegionLock() != VideoGame.EUROPEAN_UNION) {
                    setSingleIconAsActive(euFlag, usaFlag, japanFlag);
                    videoGame.setValueRegionLock(VideoGame.EUROPEAN_UNION);
                } else if (videoGame.getValueRegionLock() == VideoGame.EUROPEAN_UNION) {
                    setIconAsInactive(euFlag);
                    videoGame.setValueRegionLock(null);
                }
            }
        });
    }

    /*Initializes and handles onClickListeners for responding to when components are tapped*/
    private void setComponentsOwned(final ImageView game, final ImageView manual, final ImageView box) {
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoGame.getValueGame() == false) {
                    setIconAsActive(game);
                    videoGame.setValueGame(true);
                } else if (videoGame.getValueGame() == true) {
                    setIconAsInactive(game);
                    videoGame.setValueGame(false);
                }
            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoGame.getValueManual() == false) {
                    setIconAsActive(manual);
                    videoGame.setValueManual(true);
                } else if (videoGame.getValueManual() == true) {
                    setIconAsInactive(manual);
                    videoGame.setValueManual(false);
                }
            }
        });

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoGame.getValueBox() == false) {
                    setIconAsActive(box);
                    videoGame.setValueBox(true);
                } else if (videoGame.getValueBox() == true) {
                    setIconAsInactive(box);
                    videoGame.setValueBox(false);
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

    /*Populates unset VideoGame values*/
    private void setDate() {
        /*Get current date and time*/
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        videoGame.setValueDateAdded(currentDateTimeString);
    }

    private void setNote() {
        if (noteEditText.getText().toString() != null || noteEditText.getText().toString() != "") {
            videoGame.setValueNote(noteEditText.getText().toString());
        } else {
            videoGame.setValueNote(VideoGame.UNDEFINED_TRAIT);
        }
    }

    /**
     * Simulates clicking a region lock flag's ImageView
     *
     * @return boolean that relays if click was successful. True means it was.
     */
    public boolean clickRegionLock(String regionLock) {
        boolean wasClicked = false;

        switch (regionLock) {
            case VideoGame.USA:
                wasClicked = usaFlag.performClick();
                break;
            case VideoGame.JAPAN:
                wasClicked = japanFlag.performClick();
                break;
            case VideoGame.EUROPEAN_UNION:
                wasClicked = euFlag.performClick();
                break;
            case VideoGame.UNDEFINED_TRAIT:
                Log.i(LOG_TAG, "Trait was undefined");
                break;
            default:
                Log.e(LOG_TAG, "Trouble clicking region lock");
                break;
        }
        return wasClicked;
    }

    /**
     * Simulates clicking a componenet owned ImageView
     *
     * @return boolean that relays if click was successful. True means it was.
     */
    public int clickComponentsOwned() {
        /*Counts how many clicks occur*/
        int clicks = 0;

        if (videoGame.getValueGame() == true) {
            game.performClick();
            clicks++;
        }
        if (videoGame.getValueManual() == true) {
            manual.performClick();
            clicks++;
        }
        if (videoGame.getValueBox() == true) {
            box.performClick();
            clicks++;
        }
        return clicks;
    }

    /**
     * Populates the note EditText with the argument
     *
     * @param note A string which will be displayed
     */
    public void populateNote(String note) {
        if (note == null || note == "") {
            return;
        }
        noteEditText.setText(note, TextView.BufferType.EDITABLE);
    }

    public String setToolbarTitle () {
        if (videoGame.getValueUniqueNodeId() == null) {
           return "Add " + videoGame.getValueTitle();
        } else {
            return "Edit " + videoGame.getValueTitle();
        }
    }
}
