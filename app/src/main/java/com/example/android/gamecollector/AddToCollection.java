package com.example.android.gamecollector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddToCollection extends AppCompatActivity {

    public static final String LOG_TAG = AddToCollection.class.getSimpleName();

    FirebaseDatabase database;
    DatabaseReference allGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_collection);

        database = FirebaseDatabase.getInstance();
        allGames = database.getReference().child("all_collectables").child("console");

        ParseCSV parseCSV = new ParseCSV(getApplicationContext());
        ArrayList<ArrayList<List<String>>> allGamesParsedCSV = new ArrayList<>();
        allGamesParsedCSV.add(parseCSV.parseFiles());

        addToDatabase(allGamesParsedCSV);
    }

    private void addToDatabase(ArrayList<ArrayList<List<String>>> collectables) {
        
    }
}
