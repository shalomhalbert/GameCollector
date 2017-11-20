package com.example.android.gamecollector.data.sqlite;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by shalom on 2017-10-07.
 * Contract for storing collectables data
 */

public final class CollectableSQLContract {

    /*Should be empty*/
    private CollectableSQLContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.gamecollector";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_VIDEO_GAMES = "video_games";
    public static final String PATH_FTS_VIDEO_GAMES = "fts_video_games";

    /*Defines table contents for the video_games table*/
    public static final class VideoGamesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VIDEO_GAMES);

        public static final String TABLE_NAME = "video_games";

        /* Storage class: INTEGER
         * Table constraints: PRIMARY KEY, AUTOINCREMENT */
        public static final String COLUMN_ROW_ID = BaseColumns._ID;
        /* Storage class: TEXT
         * Table constraints: NOT NULL */
        public static final String COLUMN_CONSOLE = "Console";
        /* Storage class: TEXT
         * Table constraints: NOT NULL */
        public static final String COLUMN_TITLE = "Title";
        /* Storage class: TEXT
         * Table constraints: DEFAULT unknown */
        public static final String COLUMN_LICENSEE = "Licensee";
        /* Storage class: TEXT
         * Table constraints: DEFAULT unknown */
        public static final String COLUMN_RELEASED = "Released";
        /* Storage class: INTEGER
         * Table constraints: DEFAULT 0 */
        public static final String COLUMN_COPIES_OWNED = "Copies";
        /* ID constructed from concatenating console and title text after removing JSON's special
         * characters. Used to match with Firebase Realtime Database nodes.
         * Storage class: TEXT
         * Table constraints: NOT NULL*/
        public static final String COLUMN_UNIQUE_ID = "Unique_ID";

        /*MIME type for a list of video games*/
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_VIDEO_GAMES;

        /*MIME type for an individual video game*/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_VIDEO_GAMES;
    }

    /*Defines table contents for the fts_video_games table which enables full-text search implementation
    * on the video_games table.
    * Indexes the 'title' column from video_games table for faster full-text searching*/
    public static final class FtsVideoGamesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FTS_VIDEO_GAMES);

        public static final String TABLE_NAME = "fts_video_games";

        /*Full-text search version is fts4*/
        public static final String FTS_VERSION = "fts4";

        /*Used in CREATE VIRTUAL TABLE statement to show the first column should equal video_games table's row ids*/
        public static final String COLUMN_DOC_ID = "docid";
        /*This is the column from video_games table that we want to implement full-text search on*/
        public static final String COLUMN_TITLE = VideoGamesEntry.COLUMN_TITLE;

        /*MIME type for a list from fts_video_games*/
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_FTS_VIDEO_GAMES;

        /*MIME type for an single row*/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_FTS_VIDEO_GAMES;
    }
}
