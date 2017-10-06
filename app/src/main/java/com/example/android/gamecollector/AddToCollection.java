package com.example.android.gamecollector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddToCollection extends AppCompatActivity {

    public static final String LOG_TAG = AddToCollection.class.getSimpleName();

    FirebaseDatabase database;
    DatabaseReference consoleReference;
    DatabaseReference titleReference;
    DatabaseReference licenseeReference;
    DatabaseReference releasedReference;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_collection);

        ParseCSV parseCSV = new ParseCSV(getApplicationContext());

        Log.e(LOG_TAG, "consoleReferenceParseCSV: " + parseCSV.parseFiles().toString());

        addToDatabase(parseCSV.parseFiles());
    }

    private void addToDatabase(ArrayList<List<String>> collectables) {
        database = FirebaseDatabase.getInstance();
        int gameID = 0;

        for(List<String> game : collectables) {
            //ID titling each game's node
            String id = "" + gameID++;

            //Extract data for each game as a String
            String console = game.get(0);
            String title = game.get(1);
            String licensee = game.get(2);
            String released = game.get(3);

            //Setup database reference for each node in which game data should be placed
            consoleReference = database.getReference().child("all_collectables").child(console).child("games").child(id);
            titleReference = consoleReference.child("title");
            licenseeReference = consoleReference.child("licensee");
            releasedReference = consoleReference.child("released");

            //Write values in database
            titleReference.setValue(title);
            licenseeReference.setValue(licensee);
            releasedReference.setValue(released);
        }
    }
}
