package com.example.android.gamecollector;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import com.example.android.gamecollector.data.sqlite.CollectablesCursorAdaptor;
import com.example.android.gamecollector.data.sqlite.CollectablesSQLContract.FtsVideoGamesEntry;
import com.example.android.gamecollector.data.sqlite.CollectablesSQLContract.VideoGamesEntry;

/**
 * Created by shalom on 2017-10-11.
 * Handles searching for and adding new collectibles to user's personal collection.
 * Uses search widget.
 */

public class AddCollectableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    /*Use in Log statements*/
    public static final String LOG_TAG = AddCollectableActivity.class.getSimpleName();
    /*Constant for intent.putExtra() in SearchView.OnQueryTextListener*/
    private static final String SEARCH_QUERY= "query";

    /*Instaniated to help manage CollectablesCursorAdaptor*/
    CollectablesCursorAdaptor collectablesCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collectable);

        /*Ensures only necessary columns are given to Cursor Adaptor*/
        String[] projection = {VideoGamesEntry.COLUMN_ID, VideoGamesEntry.COLUMN_CONSOLE, VideoGamesEntry.COLUMN_TITLE};
        /*Requests all data in the video_games table and is displayed in an identical order to their row number's*/
        Cursor getTable = getContentResolver().query(VideoGamesEntry.CONTENT_URI, projection, null, null, null, null);

        /*Sets up ListView and attaches Cursor Adaptor to it, and tells Adaptor which Cursor it'll interpret*/
        ListView listView = (ListView) findViewById(R.id.collectables_list_view);
        collectablesCursorAdaptor = new CollectablesCursorAdaptor(this, getTable);
        listView.setAdapter(collectablesCursorAdaptor);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    /*Sets up Options Menu and extracts query from search widget*/
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
                Intent intent = new Intent(getApplicationContext(), AddCollectableActivity.class);
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
        /*Should update listView instantiated in onCreate()*/
        Cursor queryResults = handleIntent(intent);
        collectablesCursorAdaptor.changeCursor(queryResults);
    }

    /*Handles query*/
    private Cursor handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            /*Get String from search widget via ACTION_SEARCH Intent*/
            String[] selectionArgs = {intent.getStringExtra(SEARCH_QUERY)};

            /*Every argument except uri and selectionArgs are provided in the contentProvider*/
            Cursor queryResponseCursor = getContentResolver()
                    .query(FtsVideoGamesEntry.CONTENT_URI, null, null, selectionArgs, null);
            return queryResponseCursor;
        } else {
            return null;
        }
    }

    /*Unimplemented loader methods*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, VideoGamesEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        collectablesCursorAdaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        collectablesCursorAdaptor.swapCursor(null);
    }
}