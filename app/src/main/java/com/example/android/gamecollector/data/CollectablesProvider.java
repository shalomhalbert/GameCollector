package com.example.android.gamecollector.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.gamecollector.data.CollectablesContract.FtsVideoGamesEntry;
import com.example.android.gamecollector.data.CollectablesContract.VideoGamesEntry;

/**
 * Created by shalom on 2017-10-11.
 * ContentProvider for giving users access to a databse containing all collectable items.
 * Relating to CRUD: Users can query, but not insert, delete, or update the database.
 */

public class CollectablesProvider extends ContentProvider {
    public static final String LOG_TAG = CollectablesProvider.class.getSimpleName();
    /*URI integer codes that will be matched to Uri's by UriMatcher*/
    private static final int VIDEO_GAMES = 100;
    private static final int VIDEO_GAME_ID = 101;
    private static final int FTS_VIDEO_GAMES = 200;
    private static final int FTS_VIDEO_GAMES_ID = 201;
    /*Initialize UriMatcher*/
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    /*Instantiate CollectablesDbHelper (SQLiteOpenHelper)*/
    CollectablesDbHelper collectablesDbHelper;
    /*Exposes methods for managing database*/
    SQLiteDatabase sqLiteDatabase;

    /*SQLite statement for searching the video_games table by matching its 'title' column with
     * the resulting row's 'docid' that is in the indexed fts_video_games table*/
    private static final String SQL_QUERY_ADD_COLLECTABLE_SEARCH_RESULTS_ACTIVITY = "SELECT * FROM "
            + VideoGamesEntry.TABLE_NAME + " WHERE "
            + VideoGamesEntry.COLUMN_ID + " IN (SELECT docid FROM " + FtsVideoGamesEntry.TABLE_NAME + " WHERE " + FtsVideoGamesEntry.TABLE_NAME
            + " MATCH ?)";

    static {
        URI_MATCHER.addURI(CollectablesContract.CONTENT_AUTHORITY, CollectablesContract.PATH_VIDEO_GAMES, VIDEO_GAMES);
        URI_MATCHER.addURI(CollectablesContract.CONTENT_AUTHORITY, CollectablesContract.PATH_VIDEO_GAMES + "/#", VIDEO_GAME_ID);
        URI_MATCHER.addURI(CollectablesContract.CONTENT_AUTHORITY, CollectablesContract.PATH_FTS_VIDEO_GAMES, FTS_VIDEO_GAMES);
        URI_MATCHER.addURI(CollectablesContract.CONTENT_AUTHORITY, CollectablesContract.PATH_FTS_VIDEO_GAMES + "/#", FTS_VIDEO_GAMES_ID);
    }

    @Override
    public boolean onCreate() {
        collectablesDbHelper = new CollectablesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        sqLiteDatabase = collectablesDbHelper.getReadableDatabase();
        Cursor cursor;

        Log.e(LOG_TAG, "URI_MATCHER.match(uri): " + URI_MATCHER.match(uri));

        switch (URI_MATCHER.match(uri)) {
            case VIDEO_GAMES:
                cursor = sqLiteDatabase.query(CollectablesContract.VideoGamesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VIDEO_GAME_ID:
                /*Add actions once this case is a possibility*/
                cursor = null;
                break;
            case FTS_VIDEO_GAMES:
                /*Enables selectionArgs to filter row selection*/
                selection = CollectablesContract.VideoGamesEntry.COLUMN_ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.rawQuery(SQL_QUERY_ADD_COLLECTABLE_SEARCH_RESULTS_ACTIVITY, selectionArgs);
                break;
            case FTS_VIDEO_GAMES_ID:
                /*Add actions once this case is a possiblity*/
                cursor = null;
                break;
            default:
                throw new IllegalStateException("Cannot query unkown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    /*Returns MIME type of the given Uri argument*/
    @Override
    public String getType(Uri uri) {
        switch(URI_MATCHER.match(uri)) {
            case VIDEO_GAMES:
                return CollectablesContract.VideoGamesEntry.CONTENT_TYPE;
            case VIDEO_GAME_ID:
                return CollectablesContract.VideoGamesEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    /*Users will not insert data into this database*/
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    /*Users will not delete data into this database*/
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /*Users will not update data into this database*/
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
