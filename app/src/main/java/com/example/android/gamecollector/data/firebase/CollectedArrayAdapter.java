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
//    TODO(1) Make sure icons display properly

public class CollectedArrayAdapter extends ArrayAdapter<VideoGame> {
    public static final String LOG_TAG = CollectedArrayAdapter.class.getSimpleName();

    /*List which holds ImageViews for icon# (1-5)*/
    private ArrayList<ImageView> iconsList = new ArrayList<>();
    /*Used for tracking which icon ImageView should be used*/
    private int iconNumber = 0;


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


        /*Handles a null convertView*/
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_collection_list_item, parent, false);
        } else {
            return convertView;
        }

        Log.e(LOG_TAG, "position: " + position);

        /*Initialize views*/
        ImageView consoleLogoView = (ImageView) convertView.findViewById(R.id.activity_collection_image_console_logo);
        CustomTextView titleView = (CustomTextView) convertView.findViewById(R.id.activity_collection_customtext_title);

        /*Initialize informational icons which are located beneath the title TextView
         *Numbers range from 1 (leftmost) to 5 (rightmost)*/
        ImageView icon0 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_1);
        ImageView icon1 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_2);
        ImageView icon2 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_3);
        ImageView icon3 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_4);
        ImageView icon4 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_5);

        /*Set logo and title*/
        consoleLogoView.setImageResource(setGameImageSrc(videoGame.getValueConsole()));
        titleView.setText(videoGame.getValueTitle());

        Log.e(LOG_TAG, "videoGame.getValueTitle(): " + videoGame.getValueTitle());

        iconsList.add(icon0);
        iconsList.add(icon1);
        iconsList.add(icon2);
        iconsList.add(icon3);
        iconsList.add(icon4);

        Log.e(LOG_TAG, "Before iconNumber: " + iconNumber);

        setComponentIcons(videoGame);
        setRegionIcon(videoGame);
        setNoteIcon(videoGame);

        Log.e(LOG_TAG, "After iconNumber: " + iconNumber);


        /*Resets */
        iconNumber = 0;

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
                return R.drawable.nes_cartridge_icon;
            case VideoGame.SUPER_NINTENDO_ENTERTAINMENT_SYSTEM:
                return R.drawable.snes_cartridge;
            case VideoGame.NINTENDO_64:
                return R.drawable.n64_cartridge_icon;
            case VideoGame.NINTENDO_GAMEBOY:
                return R.drawable.gameboy_cartridge_icon;
            case VideoGame.NINTENDO_GAMEBOY_COLOR:
                return R.drawable.gameboy_cartridge_icon;
            default:
                Log.e(LOG_TAG, "Error setting cartridge icon");
                return R.drawable.n64_cartridge_icon;
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
            icon.setImageResource(R.drawable.video_game_manual_icon);
            iconNumber++;
        } else if (!componentsOwned.get(VideoGame.MANUAL)) {
            Log.e(LOG_TAG, "Manual is unowned");
        } else if (componentsOwned.get(VideoGame.GAME) == null) {
            Log.e(LOG_TAG, "Key VideoGame.MANUAL has null value");
        }

        if (componentsOwned.get(VideoGame.BOX)) {
            icon = iconsList.get(iconNumber);
            icon.setImageResource(R.drawable.box_icon);
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
                icon.setImageResource(R.drawable.flag_usa);
                iconNumber++;
                Log.e(LOG_TAG, "Case VideoGame.USA running for videoGame.getValueRegionLock(): " + videoGame.getValueRegionLock());
                break;
            case VideoGame.JAPAN:
                icon = iconsList.get(iconNumber);
                icon.setImageResource(R.drawable.flag_japan);
                iconNumber++;
                Log.e(LOG_TAG, "Case VideoGame.JAPAN running for videoGame.getValueRegionLock(): " + videoGame.getValueRegionLock());
                break;
            case VideoGame.EUROPEAN_UNION:
                icon = iconsList.get(iconNumber);
                icon.setImageResource(R.drawable.flag_european_union);
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
