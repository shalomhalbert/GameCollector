package com.example.android.gamecollector.data.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by shalom on 2017-10-11.
 * ContentProvider for giving users access to a databse containing all collectable items.
 * Relating to CRUD: Users can query, but not insert, delete, or update the database.
 */

public class CollectableProvider extends ContentProvider {
    public static final String LOG_TAG = CollectableProvider.class.getSimpleName();
    /*URI integer codes that will be matched to Uri's by UriMatcher*/
    private static final int VIDEO_GAMES = 100;
    private static final int VIDEO_GAME_ID = 101;
    private static final int FTS_VIDEO_GAMES = 200;
    private static final int FTS_VIDEO_GAMES_ID = 201;
    /*Initialize UriMatcher*/
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    /*Instantiate CollectableDbHelper (SQLiteOpenHelper)*/
    CollectableDbHelper collectableDbHelper;
    /*Exposes methods for managing database*/
    SQLiteDatabase sqLiteDatabase;

    /*SQLite statement for searching the video_games table by matching its 'title' column with
     * the resulting row's 'docid' that is in the indexed fts_video_games table*/
    private static final String SQL_QUERY_ADD_COLLECTABLE_SEARCH_RESULTS_ACTIVITY = "SELECT * FROM "
            + CollectableSQLContract.VideoGamesEntry.TABLE_NAME + " WHERE "
            + CollectableSQLContract.VideoGamesEntry.COLUMN_ROW_ID
            + " IN (SELECT docid FROM " + CollectableSQLContract.FtsVideoGamesEntry.TABLE_NAME
            + " WHERE " + CollectableSQLContract.FtsVideoGamesEntry.TABLE_NAME
            + " MATCH ?)";

    static {
        URI_MATCHER.addURI(CollectableSQLContract.CONTENT_AUTHORITY,
                CollectableSQLContract.PATH_VIDEO_GAMES, VIDEO_GAMES);
        URI_MATCHER.addURI(CollectableSQLContract.CONTENT_AUTHORITY,
                CollectableSQLContract.PATH_VIDEO_GAMES + "/#", VIDEO_GAME_ID);
        URI_MATCHER.addURI(CollectableSQLContract.CONTENT_AUTHORITY,
                CollectableSQLContract.PATH_FTS_VIDEO_GAMES, FTS_VIDEO_GAMES);
        URI_MATCHER.addURI(CollectableSQLContract.CONTENT_AUTHORITY,
                CollectableSQLContract.PATH_FTS_VIDEO_GAMES + "/#", FTS_VIDEO_GAMES_ID);
    }

    @Override
    public boolean onCreate() {
        collectableDbHelper = new CollectableDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        sqLiteDatabase = collectableDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (URI_MATCHER.match(uri)) {
            case VIDEO_GAMES:
                cursor = sqLiteDatabase.query(CollectableSQLContract.VideoGamesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VIDEO_GAME_ID:
                /*SQL statement for extracting a single row from the video_games table using row ID*/
                String sqlString = "SELECT * FROM "
                        + CollectableSQLContract.VideoGamesEntry.TABLE_NAME
                        + " WHERE " + CollectableSQLContract.VideoGamesEntry.COLUMN_ROW_ID + "=?";
                /*Cursor containing all data for the selected row*/
                cursor = sqLiteDatabase.rawQuery(sqlString, selectionArgs);
                break;
            case FTS_VIDEO_GAMES:
                /*Enables selectionArgs to filter row selection*/
                cursor = sqLiteDatabase.rawQuery(SQL_QUERY_ADD_COLLECTABLE_SEARCH_RESULTS_ACTIVITY,
                        selectionArgs);
                break;
            case FTS_VIDEO_GAMES_ID:
                /*Add actions once this case is a possiblity*/
                cursor = null;
                break;
            default:
                throw new IllegalStateException("Cannot query unkown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /*Returns MIME type of the given Uri argument*/
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case VIDEO_GAMES:
                return CollectableSQLContract.VideoGamesEntry.CONTENT_TYPE;
            case VIDEO_GAME_ID:
                return CollectableSQLContract.VideoGamesEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    /*Users will not insert data into this database*/
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    /*Runs update and returns the number of rows affected*/
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;

        switch (URI_MATCHER.match(uri)) {
            case VIDEO_GAMES:
                break;
            case VIDEO_GAME_ID:
                /*Updates a row based on a given Unique_ID*/
                rowsUpdated = sqLiteDatabase.update(CollectableSQLContract.VideoGamesEntry.TABLE_NAME,
                        values,
                        CollectableSQLContract.VideoGamesEntry.COLUMN_UNIQUE_ID + "=?",
                        selectionArgs);
                break;
            default:
                throw new IllegalStateException("Cannot update unkown URI " + uri);
        }

        return rowsUpdated;
    }

    /*Users will not delete data into this database*/
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
