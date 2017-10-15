package com.example.android.gamecollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shalom on 2017-10-05.
 */

public class MainActivity extends AppCompatActivity{
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Write a message to the Realtime Database
//    FirebaseDatabase database;
//    DatabaseReference personalCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplicationContext(),AddCollectableSearchResultsActivity.class);
        startActivity(intent);

        //Use for writing to the database
//        database = FirebaseDatabase.getInstance();
//        personalCollection = database.getReference().child("personal_collection");

    }

//    Add floatingactionbutton onClick method

}
