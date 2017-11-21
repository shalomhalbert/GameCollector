package com.example.android.gamecollector.data.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.gamecollector.R;
import com.example.android.gamecollector.collectable.videoGames.VideoGame;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shalom on 2017-10-18.
 * ArrayAdapter for CollectableActivity that binds values to list items
 */

public class CollectedArrayAdapter extends ArrayAdapter<VideoGame> {
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
        }

        /*Initialize views*/
        ImageView consoleLogoView = (ImageView) convertView.findViewById(R.id.activity_collection_image_console_logo);
        TextView titleView = (TextView) convertView.findViewById(R.id.activity_collection_text_title);
        /*Initialize informational icons which are located beneath the title TextView
         *Numbers range from 1 (leftmost) to 5 (rightmost)*/
        ImageView icon0 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_1);
        ImageView icon1 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_2);
        ImageView icon2 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_3);
        ImageView icon3 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_4);
        ImageView icon4 = (ImageView) convertView.findViewById(R.id.activity_collection_list_item_image_5);

        /*List which holds ImageViews for icon# (1-5)*/
        ArrayList<ImageView> iconsList = new ArrayList<>();
        iconsList.add(icon0);
        iconsList.add(icon1);
        iconsList.add(icon2);
        iconsList.add(icon3);
        iconsList.add(icon4);
        /*Instantiated ImageView that will only be initialized to equal an ImageView from iconsList*/
        ImageView icon;
        /*Used for tracking which icon ImageView should be used*/
        int iconNumber = 0;

        /*Map of components owned where True means the component is owned*/
        HashMap<String, Boolean> componenetsOwned = videoGame.getComponentsOwned();
        /*Iterate through every key*/
        for (String key : componenetsOwned.keySet()) {
            /*Handle cases where componenet is owned*/
            if (componenetsOwned.get(key) == true) {
                switch (key) {
                    case VideoGame.GAME:
                        icon = iconsList.get(iconNumber);
                        icon.setImageResource(setGameImageSrc(videoGame.getConsole()));
                        iconNumber++;
                        break;
                    case VideoGame.MANUAL:
                        icon = iconsList.get(iconNumber);
                        icon.setImageResource(R.drawable.video_game_manual_icon);
                        iconNumber++;
                        break;
                    case VideoGame.BOX:
                        icon = iconsList.get(iconNumber);
                        icon.setImageResource(R.drawable.box_icon);
                        iconNumber++;
                        break;
                }
            }
        }

        /*Handles displaying a drawable for regionLock*/
        if (videoGame.getRegionLock() != null) {
            switch (videoGame.getRegionLock()) {
                case VideoGame.USA:
                    icon = iconsList.get(iconNumber);
                    icon.setImageResource(R.drawable.flag_usa);
                    iconNumber++;
                    break;
                case VideoGame.JAPAN:
                    icon = iconsList.get(iconNumber);
                    icon.setImageResource(R.drawable.flag_japan);
                    iconNumber++;
                    break;
                case VideoGame.EUROPEAN_UNION:
                    icon = iconsList.get(iconNumber);
                    icon.setImageResource(R.drawable.flag_european_union);
                    iconNumber++;
                    break;
            }
        }

        if (videoGame.getNote() != VideoGame.UNDEFINED_TRAIT) {
            icon = iconsList.get(iconNumber);
            icon.setImageResource(R.drawable.ic_note_black_24dp);
            iconNumber++;
        }

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
        }
    }
}
