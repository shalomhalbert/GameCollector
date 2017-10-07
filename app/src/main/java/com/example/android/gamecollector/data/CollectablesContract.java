package com.example.android.gamecollector.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by shalom on 2017-10-07.
 * Contract for storing collectables data
 */

public final class CollectablesContract {

    /*Should be empty*/
    private CollectablesContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.gamecollector";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_VIDEO_GAMES = "videoGames";

    /*Defines table contents for the video_games table*/
    public static class VideoGamesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VIDEO_GAMES);

        /*Table name*/
        public static final String TABLE_NAME = "videoGames";

        /* Storage class: INTEGER
         * Table constraints: PRIMARY KEY, AUTOINCREMENT */
        public static final String COLUMN_ID = BaseColumns._ID;
        /* Storage class: TEXT
         * Table constraints: NOT NULL */
        public static final String COLUMN_CONSOLE = "console";
        /* Storage class: TEXT
         * Table constraints: NOT NULL */
        public static final String COLUMN_TITLE = "title";
        /* Storage class: TEXT
         * Table constraints: DEFAULT unknown */
        public static final String COLUMN_LICENSEE = "licensee";
        /* Storage class: TEXT
         * Table constraints: DEFAULT unknown */
        public static final String COLUMN_RELEASED = "released";

        //Add MIME type Strings
    }
}
