package com.shalbert.hobby.gamecollector;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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

import com.shalbert.hobby.gamecollector.data.propertyBags.VideoGame;
import com.shalbert.hobby.gamecollector.utils.VideoGameUtils;

import java.util.HashMap;

/**
 * Created by shalom on 2017-11-13.
 * A full-screen dialog that allows user to input data about a collectable they are adding
 * to their collection.
 */

public class ItemDialogFragment extends DialogFragment {
    public static final String LOG_TAG = ItemDialogFragment.class.getSimpleName();
    /*Constants for setting Bundle keys that will be passed to this fragment*/
    public static final String SQLITE_DATA = "sqliteData";
    public static final String FIREBASE_DATA = VideoGame.KEY_UNIQUE_NODE_ID;
    private Context context;
    /*Object containing video game's data*/
    private VideoGame videoGame;
    /*Instantiated views*/
    private View view;
    private TextView regionLockTextView;
    private TextView componentsOwnedTextView;
    private TextView noteTextView;
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

        Log.i(LOG_TAG, "Entered DialogFragment!");
        if (getArguments() != null) {
            /*Handles adding new item to collection*/
            if (getArguments().containsKey(SQLITE_DATA)) {
                /*Extract Hashmap containing video game's data*/
                HashMap<String, String> contentProviderBundle = (HashMap<String, String>) getArguments().getSerializable(SQLITE_DATA);
                /*Set VideoGame's values*/
                videoGame = new VideoGame(contentProviderBundle.get(VideoGame.KEY_UNIQUE_ID),
                        contentProviderBundle.get(VideoGame.KEY_CONSOLE),
                        contentProviderBundle.get(VideoGame.KEY_TITLE),
                        contentProviderBundle.get(VideoGame.KEY_LICENSEE),
                        contentProviderBundle.get(VideoGame.KEY_RELEASED),
                        Integer.valueOf(contentProviderBundle.get(VideoGame.KEY_COPIES_OWNED))
                );
                /*Express user has not indicated ownership of any components*/
                videoGame.setValueGame(false);
                videoGame.setValueManual(false);
                videoGame.setValueBox(false);
                /*Preset Region Lock*/
                videoGame.setValueRegionLock(VideoGame.UNDEFINED_TRAIT);
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
                    /*Set VideoGame's values*/
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
        view = inflater.inflate(R.layout.dialogfragment_item, container, false);

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
            actionBar.setDisplayHomeAsUpEnabled(true);
            /*Show custom drawable for up icon*/
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white);
        }

        /*Initialize TextViews and EditText*/
        regionLockTextView = (TextView) view.findViewById(R.id.region_lock_textview);
        componentsOwnedTextView = (TextView) view.findViewById(R.id.components_owned_textview);
        noteTextView = (TextView) view.findViewById(R.id.notes_textview);
        noteEditText = (EditText) view.findViewById(R.id.notes_edittext);

        /*Set typeface for TextViews and EditText*/
        Typeface robotoBoldTypeface = Typeface.createFromAsset(getActivity().getAssets(), "roboto_bold.ttf");
        regionLockTextView.setTypeface(robotoBoldTypeface);
        componentsOwnedTextView.setTypeface(robotoBoldTypeface);
        noteTextView.setTypeface(robotoBoldTypeface);

        Typeface robotoRegularTypeface = Typeface.createFromAsset(getActivity().getAssets(), "roboto_regular.ttf");
        noteEditText.setTypeface(robotoRegularTypeface);

        handleButtons(container);

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

    /*Inflates activity_collectable_dialog_menu as the actionbar at sets visibility for action buttons*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_collectable_dialog_menu, menu);
        if (menu.findItem(R.id.activity_collectable_search_menu) != null) {
            menu.findItem(R.id.activity_collectable_search_menu).setVisible(false);
        } else if (menu.findItem(R.id.action_contact_us) != null && menu.findItem(R.id.action_sign_out) != null){
            menu.findItem(R.id.action_contact_us).setVisible(false);
            menu.findItem(R.id.action_sign_out).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_collectable_dialog_action_save:
                if (videoGame.getValueUniqueNodeId() == null) {
                    /*Handles saving Add item*/
                    setDate();
                    setNote();
                    VideoGameUtils.CreateNode(videoGame);
                    dismiss();
                    break;
                } else {
                    /*Handles saving edited item*/
                    setNote();
                    VideoGameUtils.UpdateNode(videoGame);
                    dismiss();
                    break;
                }

            case android.R.id.home:
                dismiss();
                break;
            default:
                Log.e(LOG_TAG, "Problem, reached no actionable OptionItem");
        }
        return true;
    }

    /*Head method for handling the dialog's buttons*/
    private void handleButtons(ViewGroup container) {
        /*Initialization of every ImageView on activty_add_collectable_dialog.xml for programmatic use*/
        usaFlag = (ImageView) view.findViewById(R.id.usa_imageview);
        japanFlag = (ImageView) view.findViewById(R.id.japan_imageview);
        euFlag = (ImageView) view.findViewById(R.id.european_union_imageview);
        game = (ImageView) view.findViewById(R.id.game_imageview);
        manual = (ImageView) view.findViewById(R.id.manual_imageview);
        box = (ImageView) view.findViewById(R.id.box_imageview);

        setImageResources();

        /*Set OnClickListeners for every button*/
        setRegionLockOnClickListeners(usaFlag, japanFlag, euFlag);
        setComponentsOwnedOnClickListeners(game, manual, box);

        /*Sets appropriate tints for images*/
        setButtonTint();
    }

    /*Set image resource for all ImageViews except R.id.activity_collectable_image_game*/
    private void setImageResources() {
        usaFlag.setImageResource(R.drawable.ic_flag_usa);
        japanFlag.setImageResource(R.drawable.ic_flag_japan);
        euFlag.setImageResource(R.drawable.ic_flag_european_union);
        manual.setImageResource(R.drawable.ic_manual);
        box.setImageResource(R.drawable.ic_box);
        game.setImageResource(setCartridgeIcon());
    }

    /*Sets every icon's tint to colorInactiveIcon*/
    private void setButtonTint() {
        usaFlag.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
        japanFlag.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
        euFlag.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
        manual.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
        box.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);
        game.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorInactiveIcon), PorterDuff.Mode.SRC_IN);

        Log.d(LOG_TAG, "videoGame.getValueRegionLock(): " + videoGame.getValueRegionLock());
        Log.d(LOG_TAG, "videoGame.getValueNote(): " + videoGame.getValueNote());

        /*If user opens dialog in CollectionActivity, the following translate the item's data to the UI*/
        if (getArguments().containsKey(FIREBASE_DATA)) {
            handleRegionLock();
            handleComponentsOwned();
            populateNote();
        }
    }

    /*Initializes and handles onClickListeners for responding to when flags are tapped*/
    private void setRegionLockOnClickListeners(final ImageView usaFlag, final ImageView japanFlag, final ImageView euFlag) {
        usaFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoGame.getValueRegionLock().equals(VideoGame.USA)) {
                    setSingleIconAsActive(usaFlag, japanFlag, euFlag);
                    videoGame.setValueRegionLock(VideoGame.USA);
                } else if (videoGame.getValueRegionLock().equals(VideoGame.USA)) {
                    setIconAsInactive(usaFlag);
                    videoGame.setValueRegionLock(VideoGame.UNDEFINED_TRAIT);
                }
            }
        });

        japanFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoGame.getValueRegionLock().equals(VideoGame.JAPAN)) {
                    setSingleIconAsActive(japanFlag, usaFlag, euFlag);
                    videoGame.setValueRegionLock(VideoGame.JAPAN);
                } else if (videoGame.getValueRegionLock().equals(VideoGame.JAPAN)) {
                    setIconAsInactive(japanFlag);
                    videoGame.setValueRegionLock(VideoGame.UNDEFINED_TRAIT);
                }
            }
        });

        euFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoGame.getValueRegionLock().equals(VideoGame.EUROPEAN_UNION)) {
                    setSingleIconAsActive(euFlag, usaFlag, japanFlag);
                    videoGame.setValueRegionLock(VideoGame.EUROPEAN_UNION);
                } else if (videoGame.getValueRegionLock().equals(VideoGame.EUROPEAN_UNION)) {
                    setIconAsInactive(euFlag);
                    videoGame.setValueRegionLock(VideoGame.UNDEFINED_TRAIT);
                }
            }
        });
    }

    /*Initializes and handles onClickListeners for responding to when components are tapped*/
    private void setComponentsOwnedOnClickListeners(final ImageView game, final ImageView manual, final ImageView box) {
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
        videoGame.setValueDateAdded(VideoGameUtils.GetUnixTime());
    }

    /*Saves note written in EditText to VideoGame object*/
    private void setNote() {
        String text = noteEditText.getText().toString();

        if (!text.isEmpty() && !text.equals("") && text != null) {
            videoGame.setValueNote(text);
        } else {
            videoGame.setValueNote(VideoGame.UNDEFINED_TRAIT);
        }
    }

    /**
     * Uses videoGame.getValueConsole()'s value to determine the appropriate Res ID
     *
     * @return Res ID of cartridge which should be displayed
     */
    private int setCartridgeIcon() {

        /*Handles null videoGame.getValueConsole()*/
        if (videoGame.getValueConsole() == null) {
            Log.e(LOG_TAG, "videoGame.getValueConsole() is null");
            return 0;
        }

        int resID = 0;
        switch (videoGame.getValueConsole()) {
            case VideoGame.NINTENDO_ENTERTAINMENT_SYSTEM:
                resID = R.drawable.ic_nes_cartridge;
                break;
            case VideoGame.SUPER_NINTENDO_ENTERTAINMENT_SYSTEM:
                resID = R.drawable.ic_snes_cartridge;
                break;
            case VideoGame.NINTENDO_64:
                resID = R.drawable.ic_n64_cartridge;
                break;
            case VideoGame.NINTENDO_GAMEBOY:
                resID = R.drawable.ic_gameboy_cartridge;
                break;
            case VideoGame.NINTENDO_GAMEBOY_COLOR:
                resID = R.drawable.ic_gameboy_cartridge;
                break;
            default:
                Log.e(LOG_TAG, "Error setting cartridge icon");
                resID = R.drawable.ic_n64_cartridge;
                break;
        }
        return resID;
    }

    /*Sets valueRegionLock's related flag to appropriate color*/
    public void handleRegionLock() {
        if (!videoGame.getValueRegionLock().equals(VideoGame.UNDEFINED_TRAIT) && videoGame.getValueRegionLock() != null) {
            switch (videoGame.getValueRegionLock()) {
                case VideoGame.USA:
                    usaFlag.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
                    break;
                case VideoGame.JAPAN:
                    japanFlag.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
                    break;
                case VideoGame.EUROPEAN_UNION:
                    euFlag.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
                    break;
                case VideoGame.UNDEFINED_TRAIT:
                    Log.e(LOG_TAG, "Got to handleRegionLock() with undefined valueRegionLock");
                    break;
                default:
                    Log.e(LOG_TAG, "Trouble clicking region lock");
                    break;
            }
        } else {
            Log.i(LOG_TAG, "No region lock was set");
        }
    }

    /*Sets appropriate colors for componentsOwned*/
    public void handleComponentsOwned() {
        if (videoGame.getValueGame()) {
            game.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
        }
        if (videoGame.getValueManual()) {
            manual.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
        }
        if (videoGame.getValueBox()) {
            box.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorActiveIcon), PorterDuff.Mode.SRC_IN);
        }
    }

    /*Populates the note EditText with saved String */
    public void populateNote() {
        if (!videoGame.getValueNote().equals(VideoGame.UNDEFINED_TRAIT)
                && !videoGame.getValueNote().equals("")
                && videoGame.getValueNote() != null) {
            noteEditText.setText(videoGame.getValueNote(), TextView.BufferType.EDITABLE);
        } else {
            Log.i(LOG_TAG, "EditText wasn't populated with valueNote");
        }
    }

    /**
     * Sets toolbar title based on whether dialog was opened by CollectionActivity or CollectableActivity
     * @return String that the ToolBar title should be set to
     */
    public String setToolbarTitle() {
        if (getArguments().containsKey(FIREBASE_DATA)) {
            /*Opened by CollectableActivity to add a video game*/
            return "Add " + videoGame.getValueTitle();
        } else {
            /*Opened by CollectionActivity to edit a video game*/
            return "Edit " + videoGame.getValueTitle();
        }
    }
}
