package com.example.android.gamecollector.collectable.videoGames;

import com.example.android.gamecollector.data.sqlite.CollectableContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by shalom on 2017-11-17.
 * An object whose sole purpose is temproary storage of a single video game's data
 */

public class VideoGame {
    /*Constants which can be regionLock's value*/
    public static final String USA = "USA";
    public static final String JAPAN = "Japan";
    public static final String EUROPEAN_UNION = "EU";
    /*Constants for componentsOwned's keys*/
    public static final String GAME = "Game";
    public static final String MANUAL = "Manual";
    public static final String BOX = "Box";
    /*Constants for other Firebase's key values that are not one of table VideoGames's column names*/
    public static final String DATE_ADDED = "Date_Added";
    public static final String REGION_LOCK = "Region_Lock";
    public static final String COMPONENTS_OWNED = "Components_Owned";
    public static final String NOTE = "Note";
    public static final String UNDEFINED_TRAIT = "undefined";
    /*Console names*/
    public static final String NINTENDO_ENTERTAINMENT_SYSTEM = "NES";
    public static final String SUPER_NINTENDO_ENTERTAINMENT_SYSTEM = "SNES";
    public static final String NINTENDO_64 = "N64";
    public static final String NINTENDO_GAMEBOY = "GB";
    public static final String NINTENDO_GAMEBOY_COLOR = "GBC";
    /*Data that will be inputted into Firebase Realtime Database*/
    private String uniqueID;
    private String console;
    private String title;
    private String licensee;
    private String released;
    private String dateAdded;
    private String regionLock;
    private HashMap<String, Boolean> componentsOwned = new HashMap<>();
    private String note;
    /*Firbase Realtime Database initializations*/
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    public VideoGame(String uniqueID, String console, String title, String licensee, String released) {
        this.uniqueID = uniqueID;
        this.console = console;
        this.title = title;
        this.licensee = licensee;
        this.released = released;
    }

    public VideoGame(String uniqueID, String console, String title, String licensee, String released,
                     String dateAdded, String regionLock, HashMap<String,Boolean> componentsOwned,
                     String note) {
        this.uniqueID = uniqueID;
        this.console = console;
        this.title = title;
        this.licensee = licensee;
        this.released = released;
        this.dateAdded = dateAdded;
        this.regionLock = regionLock;
        this.componentsOwned = componentsOwned;
        this.note = note;
    }

    public void updateFirebase() {
        /*Create a unique ID that names a node for an individual video game when it's added*/
        String uniqueNodeId = UUID.randomUUID().toString();

        if (regionLock == null) {
            regionLock = UNDEFINED_TRAIT;
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference()
                .child("collectables_owned")
                .child("video_games")
                .child(uniqueNodeId);

        databaseReference.child(CollectableContract.VideoGamesEntry.COLUMN_UNIQUE_ID).setValue(uniqueID);
        databaseReference.child(CollectableContract.VideoGamesEntry.COLUMN_CONSOLE).setValue(console);
        databaseReference.child(CollectableContract.VideoGamesEntry.COLUMN_TITLE).setValue(title);
        databaseReference.child(CollectableContract.VideoGamesEntry.COLUMN_LICENSEE).setValue(licensee);
        databaseReference.child(CollectableContract.VideoGamesEntry.COLUMN_RELEASED).setValue(released);
        databaseReference.child(DATE_ADDED).setValue(dateAdded);
        databaseReference.child(REGION_LOCK).setValue(regionLock);
        databaseReference.child(COMPONENTS_OWNED).child(GAME).setValue(componentsOwned.get(GAME).toString());
        databaseReference.child(COMPONENTS_OWNED).child(MANUAL).setValue(componentsOwned.get(MANUAL).toString());
        databaseReference.child(COMPONENTS_OWNED).child(BOX).setValue(componentsOwned.get(BOX).toString());
        databaseReference.child(NOTE).setValue(note);
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getConsole() {
        return console;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLicensee() {
        return licensee;
    }

    public void setLicensee(String licensee) {
        this.licensee = licensee;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getRegionLock() {
        return regionLock;
    }

    public void setRegionLock(String regionLock) {
        this.regionLock = regionLock;
    }

    public HashMap<String, Boolean> getComponentsOwned() {
        return componentsOwned;
    }

    public void setComponentsOwned(HashMap<String, Boolean> componentsOwned) {
        this.componentsOwned = componentsOwned;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}