package com.example.android.gamecollector;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shalom on 2017-10-05.
 */

public class MainActivity extends AppCompatActivity{
    // Write a message to the Realtime Database
    FirebaseDatabase database;
    DatabaseReference personalCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Use for writing to the database
        database = FirebaseDatabase.getInstance();
        personalCollection = database.getReference().child("personal_collection");

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

        return true;
    }
}
