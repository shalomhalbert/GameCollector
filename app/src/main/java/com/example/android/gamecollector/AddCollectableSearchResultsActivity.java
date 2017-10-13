package com.example.android.gamecollector;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.example.android.gamecollector.data.CollectablesContract.FtsVideoGamesEntry;
import com.example.android.gamecollector.data.CollectablesContract.VideoGamesEntry;
import com.example.android.gamecollector.data.CollectablesDbHelper;
import com.example.android.gamecollector.data.VideoGameCursorAdaptor;

/**
 * Created by shalom on 2017-10-11.
 */

public class AddCollectableSearchResultsActivity extends ListActivity {
    CollectablesDbHelper collectablesDbHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cursor queryResults = handleIntent(getIntent());

        ListView listView = (ListView) findViewById(R.id.display_collectables_listview);
        VideoGameCursorAdaptor videoGameCursorAdaptor = new VideoGameCursorAdaptor(this, queryResults);
        listView.setAdapter(videoGameCursorAdaptor);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private Cursor handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            sqLiteDatabase = collectablesDbHelper.getReadableDatabase();

            /*Get String from search widget via ACTION_SEARCH Intent*/
            String[] selectionArgs = {intent.getStringExtra(SearchManager.QUERY)};

            /*SQLite statement for searching the video_games table by matching its 'title' column with
            * the resulting row's 'docid' that is in the indexed fts_video_games table*/
            String sqlQuery = "SELECT * FROM " + VideoGamesEntry.TABLE_NAME + " WHERE "
                    + VideoGamesEntry.COLUMN_ID + " IN (SELECT " + FtsVideoGamesEntry.COLUMN_DOC_ID
                    + " FROM " + FtsVideoGamesEntry.TABLE_NAME + " WHERE " + FtsVideoGamesEntry.TABLE_NAME
                    + " MATCH ? );";

            Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);
            return cursor;
        }
        return null;
    }
}
