package com.example.android.gamecollector.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.gamecollector.ParseCSV;

import java.util.List;

/**
 * Created by shalom on 2017-10-07.
 * Uses CollectablesSQLContract class's constants to create SQLite database.
 * Also, enables database management.
 */

public class CollectablesDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = CollectablesDbHelper.class.getSimpleName();
    /*Database will contain all constant data about collectable items*/
    public static final String DATABASE_NAME = "collector_opportunities.db";
    /*Database version as of 2017-10-07*/
    public static final int DATABASE_VERION = 1;

    /*Used for creating table with all data*/
    private static final String SQL_CREATE_VIDEO_GAMES_TABLE = "CREATE TABLE "
            + CollectablesSQLContract.VideoGamesEntry.TABLE_NAME + " ("
            + CollectablesSQLContract.VideoGamesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CollectablesSQLContract.VideoGamesEntry.COLUMN_CONSOLE + " TEXT NOT NULL, "
            + CollectablesSQLContract.VideoGamesEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + CollectablesSQLContract.VideoGamesEntry.COLUMN_LICENSEE + " TEXT DEFAULT unknown, "
            + CollectablesSQLContract.VideoGamesEntry.COLUMN_RELEASED + " TEXT DEFAULT unknown);";
    /*Deletes video_games table*/
    private static final String SQL_DELETE_VIDEO_GAMES_TABLE = "DROP TABLE IF EXISTS " + CollectablesSQLContract.VideoGamesEntry.TABLE_NAME;

    /*Used for creating virtual table that uses fts4*/
    private static final String SQL_CREATE_FTS_VIDEO_GAMES_TABLE = "CREATE VIRTUAL TABLE "
            + CollectablesSQLContract.FtsVideoGamesEntry.TABLE_NAME + " USING " + CollectablesSQLContract.FtsVideoGamesEntry.FTS_VERSION
            + " (" + "content='" + CollectablesSQLContract.VideoGamesEntry.TABLE_NAME + "'" + ", "
            + CollectablesSQLContract.FtsVideoGamesEntry.COLUMN_TITLE + ");";
    /*Used for populating fts_video_games table with data from video_games table*/
    private static final String SQL_POPULATE_FTS_VIDEO_GAMES_TABLE = "INSERT INTO "
            + CollectablesSQLContract.FtsVideoGamesEntry.TABLE_NAME + " (" + CollectablesSQLContract.FtsVideoGamesEntry.COLUMN_DOC_ID + ", "
            + CollectablesSQLContract.FtsVideoGamesEntry.COLUMN_TITLE + ") SELECT " + CollectablesSQLContract.VideoGamesEntry.COLUMN_ID + ", "
            + CollectablesSQLContract.VideoGamesEntry.COLUMN_TITLE + " FROM " + CollectablesSQLContract.VideoGamesEntry.TABLE_NAME + ";";
    /*Rebuilds fts_video_games table everytime the video_games table is updated*/
    private static final String SQL_REBUILD_FTS_VIDEO_GAMES_TABLE = "INSERT INTO "
            + CollectablesSQLContract.FtsVideoGamesEntry.TABLE_NAME + "(" + CollectablesSQLContract.FtsVideoGamesEntry.TABLE_NAME
            + ") VALUES ('rebuild')";

    private ParseCSV parseCSV;
    private Context context;

    public CollectablesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERION);
        this.context = context;
    }

    /*Only runs when there is no database. Once database is setup this won't be called anymore*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*Create and populate video_games table*/
        db.execSQL(SQL_CREATE_VIDEO_GAMES_TABLE);
        populateVideoGames(db);

        /*Create and populate fts_video_games table*/
        db.execSQL(SQL_CREATE_FTS_VIDEO_GAMES_TABLE);
        db.execSQL(SQL_POPULATE_FTS_VIDEO_GAMES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_VIDEO_GAMES_TABLE);
        db.execSQL(SQL_REBUILD_FTS_VIDEO_GAMES_TABLE);
    }

    /*Parse .csv files and add each line to the database*/
    private void populateVideoGames(SQLiteDatabase db) {
        parseCSV = new ParseCSV(context.getApplicationContext());

        for(List<String> game : parseCSV.parseFiles()) {
            //Extract data for each game as a String
            String console = game.get(0);
            String title = game.get(1);
            String licensee = game.get(2);
            String released = game.get(3);

            /*Handles titles that contain an apostrophe, which must be escaped before use in a SQL statement*/
            if(title.contains("'")) {
                title = title.replace("'", "''");
            }

            /*SQL script used for inserting each video game into the VideoGames table*/
            String SQL_INSERT_VIDEO_GAMES = "INSERT INTO " + CollectablesSQLContract.VideoGamesEntry.TABLE_NAME
                    + " (" + CollectablesSQLContract.VideoGamesEntry.COLUMN_CONSOLE + ", " + CollectablesSQLContract.VideoGamesEntry.COLUMN_TITLE
                    + ", " + CollectablesSQLContract.VideoGamesEntry.COLUMN_LICENSEE + ", " + CollectablesSQLContract.VideoGamesEntry.COLUMN_RELEASED
                    + ") VALUES ('" + console + "', '" + title + "', '" + licensee + "', '" + released + "');";

            db.execSQL(SQL_INSERT_VIDEO_GAMES);
        }
    }
}