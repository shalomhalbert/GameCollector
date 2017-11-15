package com.example.android.gamecollector.data.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.android.gamecollector.R;

import java.util.ArrayList;

/**
 * Created by shalom on 2017-10-18.
 * ArrayAdapter for CollectablesActivity that binds values to list items
 */

public class CollectedArrayAdapter extends ArrayAdapter<CollectedVideoGame>{

    /**
     * @param context Activity's context
     * @param videoGames Arraylist populated with video game data
     */
    public CollectedArrayAdapter(Context context, ArrayList<CollectedVideoGame> videoGames) {
        super(context, 0, videoGames);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*Gets the data item associated with the specified position in the data set*/
        final CollectedVideoGame videoGame = getItem(position);

        /*Handles a null convertView*/
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_list_item, parent, false);
        }

//       Display information according to Sketch wireframe

//        /*Initialize views*/
//        ImageView gameImage = (ImageView) convertView.findViewById(R.id.collectable_image);
//        TextView gameConsole = (TextView) convertView.findViewById(R.id.collectable_brand);
//        TextView gameTitle = (TextView) convertView.findViewById(R.id.collectable_title);
//        TextView gameValue = (TextView) convertView.findViewById(R.id.collectable_value);
//        /*Set text in views for which there is data*/
//        gameConsole.setText(videoGame.getConsole());
//        gameTitle.setText(videoGame.getTitle());
//
////        Make invisible or gone (depending on UI decision) until relevant data is available
//        gameImage.setVisibility(View.INVISIBLE);
//        gameValue.setVisibility(View.INVISIBLE);

        return convertView;
    }
}
