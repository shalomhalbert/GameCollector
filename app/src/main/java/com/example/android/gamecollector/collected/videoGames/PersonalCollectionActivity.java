package com.example.android.gamecollector.collected.videoGames;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.android.gamecollector.R;
import com.example.android.gamecollector.collectable.videoGames.CollectableActivity;
import com.example.android.gamecollector.data.firebase.CollectedArrayAdapter;
import com.example.android.gamecollector.data.firebase.CollectedVideoGame;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by shalom on 2017-10-05.
 * Displays user's personal collection
 * Also, it's the main activity/
 */

public class PersonalCollectionActivity extends AppCompatActivity{
    public static final String LOG_TAG = PersonalCollectionActivity.class.getSimpleName();
    /*Contains a list of objects with video game data that will be adapted to a ListView*/
    private ArrayList<CollectedVideoGame> videoGames = new ArrayList<>();
    /*Handles videoGames list*/
    private CollectedArrayAdapter adapter;
    /*Works with adapter to display data from videoGames*/
    private ListView listView;
    /*Instantiation of Realtime Database objects*/
    private FirebaseDatabase database;
    private DatabaseReference collectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Set toolbar as activity's ActionBar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_collection_toolbar);
        setSupportActionBar(toolbar);

        createDatabaseListeners();

        floatingActionButtonListner();
    }

    /*Handles tapping the FloatingActionButton by starting CollectableActivity*/
    private void floatingActionButtonListner() {
        final FloatingActionButton floatingActionButton = (FloatingActionButton)
                findViewById(R.id.activity_collection_floating_action_button);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(getApplicationContext(),
                        CollectableActivity.class);
                startActivity(changeActivity);
            }
        });
    }

    /*Initializes a ChildEventListener and ValueEventListener, and adapts videoGames to adapter,
    * and sets adapter as listView's adapter*/
    private void createDatabaseListeners() {
        database = FirebaseDatabase.getInstance();
        collectionRef = database.getReference().child("collectables_owned").child("video_games");

        /*Used to receive events about changes in the child locations of a given DatabaseReference ref.
        Attach the listener to a location using addChildEventListener(ChildEventListener) and the
        appropriate method will be triggered when changes occur.*/
        collectionRef.addChildEventListener(new ChildEventListener() {
            /*Creates CollectedVideoGame objects and adds them to the videoGames ArrayList.
            * Runs after onCreate finishes.*/
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /*Data points from each video_games node*/
                int id = dataSnapshot.child("_id").getValue(Integer.class);
                String console = dataSnapshot.child("console").getValue(String.class);
                String title = dataSnapshot.child("title").getValue(String.class);
                String licensee = dataSnapshot.child("licensee").getValue(String.class);
                String released = dataSnapshot.child("released").getValue(String.class);

                videoGames.add(new CollectedVideoGame(id, console, title, licensee, released));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*Used to receive events about data changes at a location given a DatabaseReference.*/
        collectionRef.addValueEventListener(new ValueEventListener() {
            /*Gives videoGames as argument to adapter, and sets adapter as listView's adapter.
            * Runs after ChildEventListener finishes.*/
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listView = (ListView) findViewById(R.id.activity_collection_listview);
                adapter = new CollectedArrayAdapter(getApplicationContext(), videoGames);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
