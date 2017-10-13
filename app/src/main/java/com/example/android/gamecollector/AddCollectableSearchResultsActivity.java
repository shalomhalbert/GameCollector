package com.example.android.gamecollector;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.android.gamecollector.data.CollectablesContract.FtsVideoGamesEntry;
import com.example.android.gamecollector.data.CollectablesContract.VideoGamesEntry;

/**
 * Created by shalom on 2017-10-11.
 */

public class AddCollectableSearchResultsActivity extends AppCompatActivity {
    /*Use in Log statements*/
    public static final String LOG_TAG = AddCollectableSearchResultsActivity.class.getSimpleName();
    /*Constant for intent.putExtra() in SearchView.OnQueryTextListener*/
    private static final String SEARCH_QUERY= "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collectable_search_results);

        Log.e(LOG_TAG, "getIntent().getAction(): " + getIntent().getAction());

        handleIntent(getIntent());

//        ListView listView = (ListView) findViewById(R.id.add_collectable_search_list_view);
//        VideoGameCursorAdaptor videoGameCursorAdaptor = new VideoGameCursorAdaptor(this, queryResults);
//        listView.setAdapter(videoGameCursorAdaptor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*Inflates res/menu/options_menu.xml which adds the search widget to the action bar*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        /*Associate 'searchable configuration' with the SearchView*/
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_add_collectable).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), AddCollectableSearchResultsActivity.class);
                intent.putExtra(SEARCH_QUERY, query);
                intent.setAction(Intent.ACTION_SEARCH);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            /*Get String from search widget via ACTION_SEARCH Intent*/
        String[] selectionArgs = {intent.getStringExtra(SEARCH_QUERY)};

            /*SQLite statement for searching the video_games table by matching its 'title' column with
            * the resulting row's 'docid' that is in the indexed fts_video_games table*/
        String sqlQuery = "SELECT * FROM " + VideoGamesEntry.TABLE_NAME + " WHERE "
                + VideoGamesEntry.COLUMN_ID + " IN (SELECT " + FtsVideoGamesEntry.COLUMN_DOC_ID
                + " FROM " + FtsVideoGamesEntry.TABLE_NAME + " WHERE " + FtsVideoGamesEntry.TABLE_NAME
                + " MATCH ? );";

//        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);
        }
    }
}