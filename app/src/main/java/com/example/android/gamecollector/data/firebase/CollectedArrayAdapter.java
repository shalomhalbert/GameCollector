package com.example.android.gamecollector.data.firebase;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.gamecollector.R;
import com.example.android.gamecollector.VideoGame;
import com.example.android.gamecollector.customviews.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shalom on 2017-10-18.
 * ArrayAdapter for CollectableActivity that binds values to list items
 */

//    TODO(1) Fix text fonts
//    TODO(1) Replace cartridge icons
//    TODO(1) Show console logo instead of cartridge
//    TODO(1) Note icon displaying when there is no note. When tapped, most editText express no note (i.e. shows hint text), and one shows "undefined"

public class CollectedArrayAdapter extends ArrayAdapter<VideoGame> {
    public static final String LOG_TAG = CollectedArrayAdapter.class.getSimpleName();
    /*Instantiation of every view*/
    private ImageView consoleLogoView;
    private CustomTextView titleView;
    private ImageView icon0;
    private ImageView icon1;
    private ImageView icon2;
    private ImageView icon3;
    private ImageView icon4;
    /*Used for tracking which icon ImageView should be used*/
    private int iconNumber;
    /*Utilized for setting image resources for icon# ImageViews*/
    private ArrayList<ImageView> iconsList;


    /**
     * @param context    Activity's context
     * @param videoGames Arraylist populated with video game data
     */
    public CollectedArrayAdapter(Context context, ArrayList<VideoGame> videoGames) {
        super(context, 0, videoGames);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*Gets the data item associated with the specified position in the data set*/
        final VideoGame videoGame = getItem(position);

        iconNumber = 0;

        /*Handles a null convertView*/
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_collection_list_item, parent, false);
        } else {
            return convertView;
        }

        /*Initialize views*/
        consoleLogoView = (ImageView) convertView.findViewById(R.id.activity_collection_image_console_logo);
        titleView = (CustomTextView) convertView.findViewById(R.id.activity_collection_customtext_title);
        /*Initialize informational icons which are located beneath the title TextView
         *Numbers range from 1 (leftmost) to 5 (rightmost)*/
        icon0 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_1);
        icon1 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_2);
        icon2 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_3);
        icon3 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_4);
        icon4 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_5);

        /*List which holds ImageViews for icon# (1-5)*/
        iconsList = new ArrayList<>();
        /*Add ImageViews to ArrayList*/
        iconsList.add(icon0);
        iconsList.add(icon1);
        iconsList.add(icon2);
        iconsList.add(icon3);
        iconsList.add(icon4);

        /*Set logo and title*/
        consoleLogoView.setImageResource(setGameImageSrc(videoGame.getValueConsole()));
        titleView.setText(videoGame.getValueTitle());

        setComponentIcons(videoGame);
        setRegionIcon(videoGame);
        setNoteIcon(videoGame);

        return convertView;
    }

    /**
     * Handles selecting which drawable file an icon should display if a game is owned
     *
     * @param console Provided with videoGame.getconsole()
     * @return Relevant cartridge's drawable resource ID
     */
    private int setGameImageSrc(String console) {
        switch (console) {
            case VideoGame.NINTENDO_ENTERTAINMENT_SYSTEM:
                return R.drawable.ic_nes_cartridge;
            case VideoGame.SUPER_NINTENDO_ENTERTAINMENT_SYSTEM:
                return R.drawable.ic_snes_cartridge;
            case VideoGame.NINTENDO_64:
                return R.drawable.ic_n64_cartridge;
            case VideoGame.NINTENDO_GAMEBOY:
                return R.drawable.ic_gameboy_cartridge;
            case VideoGame.NINTENDO_GAMEBOY_COLOR:
                return R.drawable.ic_gameboy_cartridge;
            default:
                Log.e(LOG_TAG, "Error setting cartridge icon");
                return R.drawable.ic_n64_cartridge;
        }
    }

    private void setComponentIcons(VideoGame videoGame) {
        ImageView icon;
        /*Map of components owned where True means the component is owned*/
        HashMap<String, Boolean> componentsOwned = videoGame.getValuesComponentsOwned();
        /*Check is component is owned and handle it*/
        if (componentsOwned.get(VideoGame.GAME)) {
            icon = iconsList.get(iconNumber);
            icon.setImageResource(setGameImageSrc(videoGame.getValueConsole()));
            iconNumber++;
        } else if (!componentsOwned.get(VideoGame.GAME)) {
            Log.e(LOG_TAG, "Game is unowned");
        } else if (componentsOwned.get(VideoGame.GAME) == null) {
            Log.e(LOG_TAG, "Key VideoGame.GAME has null value");
        }

        if (componentsOwned.get(VideoGame.MANUAL)) {
            icon = iconsList.get(iconNumber);
            icon.setImageResource(R.drawable.ic_manual);
            iconNumber++;
        } else if (!componentsOwned.get(VideoGame.MANUAL)) {
            Log.e(LOG_TAG, "Manual is unowned");
        } else if (componentsOwned.get(VideoGame.GAME) == null) {
            Log.e(LOG_TAG, "Key VideoGame.MANUAL has null value");
        }

        if (componentsOwned.get(VideoGame.BOX)) {
            icon = iconsList.get(iconNumber);
            icon.setImageResource(R.drawable.ic_box);
            iconNumber++;
        } else if (!componentsOwned.get(VideoGame.BOX)) {
            Log.e(LOG_TAG, "Box is unowned");
        } else if (componentsOwned.get(VideoGame.GAME) == null) {
            Log.e(LOG_TAG, "Key VideoGame.BOX has null value");
        }
    }

    private void setRegionIcon(VideoGame videoGame) {
        ImageView icon;
        /*Check for regionLock and handle it*/
        switch (videoGame.getValueRegionLock()) {
            case VideoGame.USA:
                icon = iconsList.get(iconNumber);
                icon.setImageResource(R.drawable.ic_flag_usa);
                iconNumber++;
                Log.e(LOG_TAG, "Case VideoGame.USA running for videoGame.getValueRegionLock(): " + videoGame.getValueRegionLock());
                break;
            case VideoGame.JAPAN:
                icon = iconsList.get(iconNumber);
                icon.setImageResource(R.drawable.ic_flag_japan);
                iconNumber++;
                Log.e(LOG_TAG, "Case VideoGame.JAPAN running for videoGame.getValueRegionLock(): " + videoGame.getValueRegionLock());
                break;
            case VideoGame.EUROPEAN_UNION:
                icon = iconsList.get(iconNumber);
                icon.setImageResource(R.drawable.ic_flag_european_union);
                iconNumber++;
                Log.e(LOG_TAG, "Case VideoGame.EUROPEAN_UNION running for videoGame.getValueRegionLock(): " + videoGame.getValueRegionLock());
                break;
            case VideoGame.UNDEFINED_TRAIT:
                Log.e(LOG_TAG, "Region lock not defined");
                break;
            default:
                Log.e(LOG_TAG, "Problem setting region lock");
        }
    }

    private void setNoteIcon(VideoGame videoGame) {
        ImageView icon;
        if (videoGame.getValueNote().trim() == VideoGame.UNDEFINED_TRAIT) {
            icon = iconsList.get(iconNumber);
            icon.setImageResource(R.drawable.ic_note_black_24dp);
            iconNumber++;
            Log.e(LOG_TAG, "videoGame.getValueNote().trim(): " + videoGame.getValueNote().trim());
        } else {
            Log.i(LOG_TAG, "videoGame.getValueNote(): " + videoGame.getValueNote());
        }
    }

}
