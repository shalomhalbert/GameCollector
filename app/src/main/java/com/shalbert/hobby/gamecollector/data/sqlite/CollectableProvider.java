package com.shalbert.hobby.gamecollector.data.sqlite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.shalbert.hobby.gamecollector.ItemDialogFragment;
import com.shalbert.hobby.gamecollector.data.sqlite.CollectableContract.VideoGamesEntry;

import java.util.HashMap;

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
            + VideoGamesEntry.TABLE_NAME + " WHERE "
            + VideoGamesEntry.COLUMN_ROW_ID
            + " IN (SELECT docid FROM " + CollectableContract.FtsVideoGamesEntry.TABLE_NAME
            + " WHERE " + CollectableContract.FtsVideoGamesEntry.TABLE_NAME
            + " MATCH ?)";

    /*Bundle in call()'s serializable's key*/
    public static final String TABLE_EXISTS_BOOLEAN = "TableExists";


    static {
        URI_MATCHER.addURI(CollectableContract.CONTENT_AUTHORITY,
                CollectableContract.PATH_VIDEO_GAMES, VIDEO_GAMES);
        URI_MATCHER.addURI(CollectableContract.CONTENT_AUTHORITY,
                CollectableContract.PATH_VIDEO_GAMES + "/#", VIDEO_GAME_ID);
        URI_MATCHER.addURI(CollectableContract.CONTENT_AUTHORITY,
                CollectableContract.PATH_FTS_VIDEO_GAMES, FTS_VIDEO_GAMES);
        URI_MATCHER.addURI(CollectableContract.CONTENT_AUTHORITY,
                CollectableContract.PATH_FTS_VIDEO_GAMES + "/#", FTS_VIDEO_GAMES_ID);
    }

    @Override
    public boolean onCreate() {
        /*Initialize CollectableDbHelper*/
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
                cursor = sqLiteDatabase.query(VideoGamesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VIDEO_GAME_ID:
                /*SQL statement for extracting a single row from the video_games table using row ID*/
                String sqlString = "SELECT * FROM "
                        + VideoGamesEntry.TABLE_NAME
                        + " WHERE " + VideoGamesEntry.COLUMN_ROW_ID + "=?";
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
                return VideoGamesEntry.CONTENT_TYPE;
            case VIDEO_GAME_ID:
                return VideoGamesEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    /*Users will not insert data into this database*/
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    /*Runs update and returns the number of rows affected*/
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;

        switch (URI_MATCHER.match(uri)) {
            case VIDEO_GAMES:
                Log.e(LOG_TAG, "Incorrect Uri received");
                break;
            case VIDEO_GAME_ID:
                /*Updates a row based on a given Unique_ID*/
                rowsUpdated = sqLiteDatabase.update(VideoGamesEntry.TABLE_NAME,
                        values,
                        selection,
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

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Bundle bundle = new Bundle();
        if (method == "getItemData") {
            bundle.putSerializable(ItemDialogFragment.SQLITE_DATA, getItemData(extras.getString(VideoGamesEntry.COLUMN_ROW_ID)));
        } else if(method == "tableExists") {
            bundle.putBoolean(TABLE_EXISTS_BOOLEAN, tableExists());
        } else {
            Log.e(LOG_TAG, "Trouble calling getItemData() via call()");
            return null;
        }
        return bundle;
    }

    /**
     * Only accessible via call()
     * @param rowId Row ID for a specific video game
     * @return A HashMap filled with the video game's data
     */
    private HashMap<String, String> getItemData(String rowId) {
        /*Map will contain item data*/
        HashMap<String, String> map = new HashMap<>();

        /*Convert rowId to long for appended Uri*/
        long rowIdLong = Long.valueOf(rowId);
        /*Create Uri for the tapped collectable item*/
        Uri individualItemUri = ContentUris.withAppendedId(VideoGamesEntry.CONTENT_URI, rowIdLong);

        String[] selectionArgs = {rowId};
        Log.i(LOG_TAG, "Running getItemData() for rowId: " + rowId);
        /*Get cursor with data belonging to the tapped collectable item*/
        Cursor newCollectable = query(individualItemUri, null,
                null, selectionArgs, null);

        if (newCollectable != null && newCollectable.moveToFirst()) {
            /*Get every value from cursor*/
            //Reformat code to centralize this action
            String collectableRowId = newCollectable.getString(newCollectable.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_ROW_ID));
            String collectableConsole = newCollectable.getString(newCollectable.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_CONSOLE));
            String collectableTitle = newCollectable.getString(newCollectable.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_TITLE));
            String collectableLicensee = newCollectable.getString(newCollectable.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_LICENSEE));
            String collectableReleased = newCollectable.getString(newCollectable.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_RELEASED));
            String collectableUniqueId = newCollectable.getString(newCollectable.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_UNIQUE_ID));
            String collectableCopies = newCollectable.getString(newCollectable.getColumnIndexOrThrow(VideoGamesEntry.COLUMN_COPIES_OWNED));

            /*Add values to Map*/
            map.put(VideoGamesEntry.COLUMN_ROW_ID, collectableRowId);
            map.put(VideoGamesEntry.COLUMN_CONSOLE, collectableConsole);
            map.put(VideoGamesEntry.COLUMN_TITLE, collectableTitle);
            map.put(VideoGamesEntry.COLUMN_LICENSEE, collectableLicensee);
            map.put(VideoGamesEntry.COLUMN_RELEASED, collectableReleased);
            map.put(VideoGamesEntry.COLUMN_UNIQUE_ID, collectableUniqueId);
            map.put(VideoGamesEntry.COLUMN_COPIES_OWNED, collectableCopies);
        } else {
            Log.e(LOG_TAG, "Problem getting cursor values");
            return null;
        }

        return map;
    }

    /*Checks if table video_games exists
    * True means table exists*/
    boolean tableExists() {
        if (VideoGamesEntry.TABLE_NAME == null || sqLiteDatabase == null || !sqLiteDatabase.isOpen())
        {
            return false;
        }
        String sqlStatement = "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?;";
        Cursor cursor = sqLiteDatabase.rawQuery(sqlStatement,
                new String[] {"table", VideoGamesEntry.TABLE_NAME});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
