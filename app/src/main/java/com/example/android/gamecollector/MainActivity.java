package com.example.android.gamecollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

        personalCollection.setValue("Hello collection");
    }

    /*
     * Directs onClick attribute defined for the FloatingActionButton in activity_main.xml
     */
    public void collectionAddition(View target) {
        Intent intent = new Intent(MainActivity.this, AddToCollection.class);
        startActivity(intent);
    }
}
