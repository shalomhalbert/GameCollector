package com.shalbert.hobby.gamecollector.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import com.shalbert.hobby.gamecollector.R;
import com.shalbert.hobby.gamecollector.data.sqlite.CollectableContract.FtsVideoGamesEntry;
import com.shalbert.hobby.gamecollector.data.sqlite.CollectableContract.VideoGamesEntry;
import com.shalbert.hobby.gamecollector.adapters.CollectableCursorAdaptor;

/**
 * Created by shalom on 2017-10-11.
 * Handles searching for and adding new collectibles to user's personal collection.
 * Uses search widget.
 */

//    TODO(3) Enable partial word search to work (e.g. "poke" for "pokemon")-- Entails more advanced query search
//    TODO(3) Re-add Copies Owned, and ensure it's accurate

public class CollectableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /*Use in Log statements*/
    public static final String LOG_TAG = CollectableActivity.class.getSimpleName();
    /*Constant for intent.putExtra() in SearchView.OnQueryTextListener*/
    private static final String SEARCH_QUERY = "query";
    /*Instantiate Toolbar*/
    Toolbar toolbar;
    /*Instaniated to help manage CollectableCursorAdaptor*/
    CollectableCursorAdaptor collectableCursorAdaptor;
    /*Instantiate cursor of entire table*/
    private Cursor getTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectable);

        /*Set toolbar as activity's action bar*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*Get a support ActionBar corresponding to this Toolbar*/
        ActionBar actionBar = getSupportActionBar();
        /*Enable the Up button*/
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*Get a support ActionBar correspinding to this toolbar*/
        ActionBar ab = getSupportActionBar();
        /*Enable up button*/
        ab.setDisplayHomeAsUpEnabled(true);

        /*Ensures only necessary columns are given to Cursor Adaptor*/
        String[] projection = {VideoGamesEntry.COLUMN_ROW_ID, VideoGamesEntry.COLUMN_CONSOLE,
                VideoGamesEntry.COLUMN_TITLE, VideoGamesEntry.COLUMN_COPIES_OWNED};
        /*Requests all data in the video_games table and is displayed in an identical order to their row number's*/
        getTable = getContentResolver().query(VideoGamesEntry.CONTENT_URI, projection, null, null, null, null);

        /*Sets up ListView and attaches Cursor Adaptor to it, and tells Adaptor which Cursor it'll interpret*/
        ListView listView = (ListView) findViewById(R.id.activity_collectable_listview);
        collectableCursorAdaptor = new CollectableCursorAdaptor(this, getTable, getSupportFragmentManager());
        listView.setAdapter(collectableCursorAdaptor);


        getSupportLoaderManager().initLoader(0, null, this);
    }

    /*Sets up Options Menu and extracts query from search widget*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*Inflates res/menu/activity_collectable_menu.xmlnu.xml which adds the search widget to the action bar*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_collectable_menu, menu);

        /*Associate 'searchable configuration' with the SearchView*/
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.activity_collectable_search_menu).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        /*Hint displayed after search icon is clicked*/
        searchView.setQueryHint("Enter a title...");


        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            /*Handles searching when text is submitted*/
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Intent intent = new Intent(getApplicationContext(), CollectableActivity.class);
//                intent.putExtra(SEARCH_QUERY, query);
//                intent.setAction(Intent.ACTION_SEARCH);
//                startActivity(intent);
                return false;
            }

            /*Handles searching as text is typed*/
            @Override
            public boolean onQueryTextChange(String newText) {
                Intent intent = new Intent(getApplicationContext(), CollectableActivity.class);
                intent.putExtra(SEARCH_QUERY, newText);
                intent.setAction(Intent.ACTION_SEARCH);
                startActivity(intent);
                return true;
            }
        };
        /*Connect SeachView and OnQueryTextListener*/
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        /*Should update listView instantiated in onCreate() with Cursor from handleIntent*/
        Cursor queryResults = handleIntent(intent);
        /*Update Adaptor's cursor*/
        collectableCursorAdaptor.changeCursor(queryResults);
    }

    /*Handles query using extras supplied by OnQueryTextListener's Intent*/
    private Cursor handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            /*Get String from search widget via ACTION_SEARCH Intent*/
            String[] selectionArgs = {intent.getStringExtra(SEARCH_QUERY)};

            Log.i(LOG_TAG, "Search Query: +" + selectionArgs[0] + "+");

            /*Handles empty search query*/
            if (selectionArgs[0].equals("")) {
                /*Handles a closed getTableCursor*/
                if (getTable.isClosed()) {
                    /*Ensures only necessary columns are given to Cursor Adaptor*/
                    String[] projection = {VideoGamesEntry.COLUMN_ROW_ID, VideoGamesEntry.COLUMN_CONSOLE,
                            VideoGamesEntry.COLUMN_TITLE, VideoGamesEntry.COLUMN_COPIES_OWNED};
                    /*Requests all data in the video_games table and is displayed in an identical order to their row number's*/
                    getTable = getContentResolver().query(VideoGamesEntry.CONTENT_URI, projection, null, null, null, null);
                }
                /*Returns cursor containing the entire video_games table*/
                return getTable;
            }

            /*Every argument except uri and selectionArgs are provided in the contentProvider*/
            Cursor queryResponseCursor = getContentResolver()
                    .query(FtsVideoGamesEntry.CONTENT_URI, null, null, selectionArgs, null);
            /*Returns a cursor containing data belonging to rows with titles contaning the queried word*/
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
        collectableCursorAdaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        collectableCursorAdaptor.swapCursor(null);
    }
}