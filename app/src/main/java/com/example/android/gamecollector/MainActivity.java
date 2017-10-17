package com.example.android.gamecollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

        floatingActionButtonListner();

        //Use for writing to the database
//        database = FirebaseDatabase.getInstance();
//        personalCollection = database.getReference().child("personal_collection");

    }

    /*Handles tapping the FloatingActionButton by starting AddCollectableSearchResultsActivity*/
    private void floatingActionButtonListner() {
        FloatingActionButton floatingActionButton = (FloatingActionButton)
                findViewById(R.id.add_collectable_floating_action_button);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startAddCollectableSearchResultsActivity = new Intent(getApplicationContext(),
                        AddCollectableSearchResultsActivity.class);
                startActivity(startAddCollectableSearchResultsActivity);
            }
        });
    }
}
