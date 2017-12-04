package com.example.android.gamecollector;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.gamecollector.customviews.CustomEditText;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by shalom on 2017-11-13.
 * A full-screen dialog that allows user to input data about a collectable they are adding
 * to their collection.
 */

//    TODO(1) USA flag is displaying improperly
//    TODO(1) Replace cartridge icons
//    TODO(1) Change CustomEditText to appropraite color
//    TODO(1) Set home action button to send user to origination activity and save to Personal Collection
//    TODO(1) Add: Remove search icon from Toolbar
//    TODO(1) Edit: Undefined note shouldn't display "undefined"
//    TODO(1) Edit: Buttons are not highlighting
//    TODO(1) Edit: If update encompasses removing all values, it currently doesn't display no icons

public class CollectableDialogFragment extends DialogFragment {
    public static final String LOG_TAG = CollectableDialogFragment.class.getSimpleName();
    /*Constants for setting Bundle keys that will be passed to this fragment*/
    public static final String SQLITE_DATA = "sqliteData";
    public static final String FIREBASE_DATA = VideoGame.KEY_UNIQUE_NODE_ID;
    private Context context;
    /*Object containing video game's data*/
    private VideoGame videoGame;
    /*Instantiated views*/
    private View view;
    private ImageView usaFlag;
    private ImageView japanFlag;
    private ImageView euFlag;
    private ImageView game;
    private ImageView manual;
    private ImageView box;
    private CustomEditText noteEditText;

    /*Initialize contentProviderBundle and componentsOwned, and handle clicking*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "Entered DialogFragment!");
        if (getArguments() != null) {
            /*Handles adding new item to collection*/
            if (getArguments().containsKey(SQLITE_DATA)) {
                /*Extract Hashmap containing video game's data*/
                HashMap<String, String> contentProviderBundle = (HashMap<String, String>) getArguments().getSerializable(SQLITE_DATA);
                /*Set VideoGame's values*/
                videoGame = new VideoGame(getContext(), contentProviderBundle.get(VideoGame.KEY_UNIQUE_ID),
                        contentProviderBundle.get(VideoGame.KEY_CONSOLE),
                        contentProviderBundle.get(VideoGame.KEY_TITLE),
                        contentProviderBundle.get(VideoGame.KEY_LICENSEE),
                        contentProviderBundle.get(VideoGame.KEY_RELEASED),
                        Integer.valueOf(contentProviderBundle.get(VideoGame.KEY_COPIES_OWNED)));
                /*Express user has not indicated ownership of any components*/
                videoGame.setValueGame(false);
                videoGame.setValueManual(false);
                videoGame.setValueBox(false);
                /*Pass rowID*/
                videoGame.setValueRowID(contentProviderBundle.get(VideoGame.KEY_ROW_ID));
            } else if (getArguments().containsKey(FIREBASE_DATA)) {
                /*Handles editing a collected item*/
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
                        case VideoGame.KEY_CONSOLE:
                            videoGame.setValueConsole(getArguments().getString(VideoGame.KEY_CONSOLE));
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
        view = inflater.inflate(R.layout.activity_collectable_dialog, container, false);

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
        noteEditText = (CustomEditText) view.findViewById(R.id.activity_collectable_customedittext_notes);
        handleButtons(container);

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
                    /*Handles Add item*/
                    setDate();
                    setNote();
                    videoGame.createNode();
                    dismiss();
                    break;
                } else {
                    /*Handles Edit item*/
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
    private void handleButtons(ViewGroup container) {
        /*Initialization of every ImageView on activty_add_collectable_dialog.xml for programmatic use*/
        usaFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_usa);
        japanFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_japan);
        euFlag = (ImageView) view.findViewById(R.id.activity_collectable_image_european_union);
        game = (ImageView) view.findViewById(R.id.activity_collectable_image_game);
        manual = (ImageView) view.findViewById(R.id.activity_collectable_image_manual);
        box = (ImageView) view.findViewById(R.id.activity_collectable_image_box);

        setImageResources();

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

        setCartridgeIcon();

        setComponentsOwned(game, manual, box);

    }

    /*Set image resource for all ImageViews except R.id.activity_collectable_image_game*/
    private void setImageResources() {
        usaFlag.setImageResource(R.drawable.flag_usa);
        japanFlag.setImageResource(R.drawable.flag_japan);
        euFlag.setImageResource(R.drawable.flag_european_union);
        manual.setImageResource(R.drawable.video_game_manual_icon);
        box.setImageResource(R.drawable.box_icon);
    }

    /*Sets every icon's tint to colorInactiveIcon*/
    private void setButtonTintInactive(ArrayList<ImageView> imageViews) {
        for (ImageView icon : imageViews) {
            icon.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
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
                if (!videoGame.getValueGame()) {
                    setIconAsActive(game);
                    videoGame.setValueGame(true);
                } else if (videoGame.getValueGame()) {
                    setIconAsInactive(game);
                    videoGame.setValueGame(false);
                }
            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoGame.getValueManual()) {
                    setIconAsActive(manual);
                    videoGame.setValueManual(true);
                } else if (videoGame.getValueManual()) {
                    setIconAsInactive(manual);
                    videoGame.setValueManual(false);
                }
            }
        });

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoGame.getValueBox()) {
                    setIconAsActive(box);
                    videoGame.setValueBox(true);
                } else if (videoGame.getValueBox()) {
                    setIconAsInactive(box);
                    videoGame.setValueBox(false);
                }
            }
        });
    }

    /*Sets tints to display only the tapped icon as active*/
    private void setSingleIconAsActive(ImageView active, ImageView inactive1, ImageView inactive2) {
        /*Changes icon's tint to inactive color*/
        active.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
        /*Changes icon's tint to inactive color*/
        inactive1.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
        inactive2.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
    }

    /*Changes icon's tint to active color*/
    private void setIconAsActive(ImageView imageView) {
        imageView.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
    }

    /*Changes icon's tint to inactive color*/
    private void setIconAsInactive(ImageView imageView) {
        imageView.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
    }

    /*Populates unset VideoGame values*/
    private void setDate() {
        /*Get current date and time*/
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        videoGame.setValueDateAdded(currentDateTimeString);
    }

    private void setNote() {
        String text = noteEditText.getText().toString().trim();

        if (text.isEmpty() || text.length() == 0 || text.equals("") || text == null) {
            videoGame.setValueNote(VideoGame.UNDEFINED_TRAIT);
        } else {
            videoGame.setValueNote(noteEditText.getText().toString());

        }
    }

    /*handle setting the cartridge icon under componentsOwned*/
    private void setCartridgeIcon() {

        /*Handles null videoGame.getValueConsole()*/
        if (videoGame.getValueConsole() == null) {
            Log.e(LOG_TAG, "videoGame.getValueConsole() is null");
            return;
        }
        int resID = 0;
        switch (videoGame.getValueConsole().trim()) {
            case VideoGame.NINTENDO_ENTERTAINMENT_SYSTEM:
                resID = R.drawable.nes_cartridge_icon;
                break;
            case VideoGame.SUPER_NINTENDO_ENTERTAINMENT_SYSTEM:
                resID = R.drawable.snes_cartridge;
                break;
            case VideoGame.NINTENDO_64:
                resID = R.drawable.n64_cartridge_icon;
                break;
            case VideoGame.NINTENDO_GAMEBOY:
                resID = R.drawable.gameboy_cartridge_icon;
                break;
            case VideoGame.NINTENDO_GAMEBOY_COLOR:
                resID = R.drawable.gameboy_cartridge_icon;
                break;
            default:
                Log.e(LOG_TAG, "Error setting cartridge icon");
                resID = R.drawable.n64_cartridge_icon;
                break;
        }
        game.setImageResource(resID);
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

        if (videoGame.getValueGame()) {
            game.performClick();
            clicks++;
        }
        if (videoGame.getValueManual()) {
            manual.performClick();
            clicks++;
        }
        if (videoGame.getValueBox()) {
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
        if (note == VideoGame.UNDEFINED_TRAIT) {
            return;
        }
        noteEditText.setText(note, TextView.BufferType.EDITABLE);
    }

    public String setToolbarTitle() {
        if (videoGame.getValueUniqueNodeId() == null) {
            return "Add " + videoGame.getValueTitle();
        } else {
            return "Edit " + videoGame.getValueTitle();
        }
    }
}
