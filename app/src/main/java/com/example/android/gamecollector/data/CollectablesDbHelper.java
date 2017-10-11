package com.example.android.gamecollector.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.gamecollector.ParseCSV;
import com.example.android.gamecollector.data.CollectablesContract.VideoGamesEntry;

import java.util.List;

/**
 * Created by shalom on 2017-10-07.
 * Uses CollectablesContract class's constants to create SQLite database.
 * Also, enables database management.
 */

public class CollectablesDbHelper extends SQLiteOpenHelper {
    /*Database will contain all constant data about collectable items*/
    public static final String DATABASE_NAME = "collector_opportunities.db";
    /*Database version as of 2017-10-07*/
    public static final int DATABASE_VERION = 1;
    /*Used for creating */
    public static final String SQL_CREATE_VIDEO_GAMES_TABLE = "CREATE TABLE "
            + VideoGamesEntry.TABLE_NAME + " ("
            + VideoGamesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VideoGamesEntry.COLUMN_CONSOLE + " TEXT NOT NULL, "
            + VideoGamesEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + VideoGamesEntry.COLUMN_LICENSEE + " TEXT DEFAULT unknown, "
            + VideoGamesEntry.COLUMN_RELEASED + " TEXT DEFAULT unknown);";
    private static final String SQL_DELETE_VIDEO_GAMES_TABLE = "DROP TABLE IF EXISTS "
            + VideoGamesEntry.TABLE_NAME;
    private ParseCSV parseCSV;
    private Context context;

    public CollectablesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_VIDEO_GAMES_TABLE);

        /*Parse .csv files and add each line to the database*/
        parseCSV = new ParseCSV(context.getApplicationContext());

        for(List<String> game : parseCSV.parseFiles()) {
            //Extract data for each game as a String
            String console = game.get(0);
            String title = game.get(1);
            String licensee = game.get(2);
            String released = game.get(3);

            /*SQL script used for inserting each video game into the VideoGames table*/
            String SQL_INSERT_VIDEO_GAMES = "INSERT INTO " + VideoGamesEntry.TABLE_NAME
                    + " (" + VideoGamesEntry.COLUMN_CONSOLE + ", " + VideoGamesEntry.COLUMN_TITLE
                    + ", " + VideoGamesEntry.COLUMN_LICENSEE + ", " + VideoGamesEntry.COLUMN_RELEASED
                    + ") VALUES (" + console + ", " + title + ", " + licensee + ", " + released + ");";

            db.execSQL(SQL_INSERT_VIDEO_GAMES);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_VIDEO_GAMES_TABLE);
    }
}
