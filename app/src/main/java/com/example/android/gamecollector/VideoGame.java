package com.example.android.gamecollector;

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
    public static final String LOG_TAG = VideoGame.class.getSimpleName();
    /*Constants which can be valueRegionLock's value*/
    public static final String USA = "USA";
    public static final String JAPAN = "Japan";
    public static final String EUROPEAN_UNION = "EU";
    /*Constants for valuesComponentsOwned's keys*/
    public static final String GAME = "Game";
    public static final String MANUAL = "Manual";
    public static final String BOX = "Box";
    /*Constants for other Firebase's key values that are not one of table VideoGames's column names*/
    public static final String KEY_UNIQUE_ID = CollectableContract.VideoGamesEntry.COLUMN_UNIQUE_ID;
    public static final String KEY_CONSOLE = CollectableContract.VideoGamesEntry.COLUMN_CONSOLE;
    public static final String KEY_LICENSEE = CollectableContract.VideoGamesEntry.COLUMN_LICENSEE;
    public static final String KEY_RELEASED = CollectableContract.VideoGamesEntry.COLUMN_RELEASED;
    public static final String KEY_TITLE = CollectableContract.VideoGamesEntry.COLUMN_TITLE;
    public static final String KEY_COPIES_OWNED = CollectableContract.VideoGamesEntry.COLUMN_COPIES_OWNED;
    public static final String KEY_DATE_ADDED = "Date_Added";
    public static final String KEY_REGION_LOCK = "Region_Lock";
    public static final String KEY_COMPONENTS_OWNED = "Components_Owned";
    public static final String KEY_NOTE = "Note";
    public static final String KEY_UNIQUE_NODE_ID = "Unique_Node_Id";
    /*Used when region lock wasn't defined by user*/
    public static final String UNDEFINED_TRAIT = "undefined";
    /*Console names*/
    public static final String NINTENDO_ENTERTAINMENT_SYSTEM = "NES";
    public static final String SUPER_NINTENDO_ENTERTAINMENT_SYSTEM = "SNES";
    public static final String NINTENDO_64 = "N64";
    public static final String NINTENDO_GAMEBOY = "GB";
    public static final String NINTENDO_GAMEBOY_COLOR = "GBC";
    /*Data that will be inputted into Firebase Realtime Database*/
    private String valueUniqueID = null; //Create by concatenating valueTitle and valueConsole name
    private String valueConsole = null;
    private String valueTitle = null;
    private String valueLicensee = null;
    private String valueReleased = null;
    private String valueDateAdded = null;
    private String valueRegionLock = null;
    private HashMap<String, Boolean> valuesComponentsOwned = new HashMap<>();
    private String valueNote = null;
    private String valueUniqueNodeId = null; //Created with a random character generator
    private int valueCopiesOwned = 0;
    /*Firebase Realtime Database initializations*/
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    /*Use when wanting to set values individually*/
    public VideoGame() { }


    public VideoGame(String valueUniqueID, String valueConsole, String valueTitle, String valueLicensee, String valueReleased, int valueCopiesOwned) {
        this.valueUniqueID = valueUniqueID;
        this.valueConsole = valueConsole;
        this.valueTitle = valueTitle;
        this.valueLicensee = valueLicensee;
        this.valueReleased = valueReleased;
        this.valueCopiesOwned = valueCopiesOwned;
    }

    public VideoGame(String valueUniqueID, String valueConsole, String valueTitle, String valueLicensee, String valueReleased,
                     String valueDateAdded, String valueRegionLock, HashMap<String,Boolean> valuesComponentsOwned,
                     String valueNote) {
        this.valueUniqueID = valueUniqueID;
        this.valueConsole = valueConsole;
        this.valueTitle = valueTitle;
        this.valueLicensee = valueLicensee;
        this.valueReleased = valueReleased;
        this.valueDateAdded = valueDateAdded;
        this.valueRegionLock = valueRegionLock;
        this.valuesComponentsOwned = valuesComponentsOwned;
        this.valueNote = valueNote;
    }

    public void createNode() {
        /*Create a unique ID that names a node for an individual video game when it's added*/
        valueUniqueNodeId = UUID.randomUUID().toString();

        if (valueRegionLock == null) {
            valueRegionLock = UNDEFINED_TRAIT;
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference()
                .child("collectables_owned")
                .child("video_games")
                .child(valueUniqueNodeId);

        databaseReference.child(KEY_UNIQUE_ID).setValue(valueUniqueID);
        databaseReference.child(KEY_CONSOLE).setValue(valueConsole);
        databaseReference.child(KEY_TITLE).setValue(valueTitle);
        databaseReference.child(KEY_LICENSEE).setValue(valueLicensee);
        databaseReference.child(KEY_RELEASED ).setValue(valueReleased);
        databaseReference.child(KEY_DATE_ADDED).setValue(valueDateAdded);
        databaseReference.child(KEY_REGION_LOCK).setValue(valueRegionLock);
        databaseReference.child(KEY_COMPONENTS_OWNED).child(GAME).setValue(valuesComponentsOwned.get(GAME).toString());
        databaseReference.child(KEY_COMPONENTS_OWNED).child(MANUAL).setValue(valuesComponentsOwned.get(MANUAL).toString());
        databaseReference.child(KEY_COMPONENTS_OWNED).child(BOX).setValue(valuesComponentsOwned.get(BOX).toString());
        databaseReference.child(KEY_NOTE).setValue(valueNote);
        databaseReference.child(KEY_UNIQUE_NODE_ID).setValue(valueUniqueNodeId);
    }

    public void updateNode() {
        if (valueRegionLock == null) {
            valueRegionLock = UNDEFINED_TRAIT;
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference()
                .child("collectables_owned")
                .child("video_games")
                .child(valueUniqueNodeId);
        /*Update region lock*/
        databaseReference.child(KEY_REGION_LOCK).child(valueRegionLock);
        /*Update components owned*/
        databaseReference.child(KEY_COMPONENTS_OWNED).child(GAME).setValue(getValueGame());
        databaseReference.child(KEY_COMPONENTS_OWNED).child(MANUAL).setValue(getValueManual());
        databaseReference.child(KEY_COMPONENTS_OWNED).child(BOX).setValue(getValueBox());
        /*Update note*/
        databaseReference.child(KEY_NOTE).setValue(valueNote);

    }

    public String getValueUniqueID() {
        return valueUniqueID;
    }

    public void setValueUniqueID(String valueUniqueID) {
        this.valueUniqueID = valueUniqueID;
    }

    public String getValueConsole() {
        return valueConsole;
    }

    public void setValueConsole(String valueConsole) {
        this.valueConsole = valueConsole;
    }

    public String getValueTitle() {
        return valueTitle;
    }

    public void setValueTitle(String valueTitle) {
        this.valueTitle = valueTitle;
    }

    public String getValueLicensee() {
        return valueLicensee;
    }

    public void setValueLicensee(String valueLicensee) {
        this.valueLicensee = valueLicensee;
    }

    public String getValueReleased() {
        return valueReleased;
    }

    public void setValueReleased(String valueReleased) {
        this.valueReleased = valueReleased;
    }

    public String getValueDateAdded() {
        return valueDateAdded;
    }

    public void setValueDateAdded(String valueDateAdded) {
        this.valueDateAdded = valueDateAdded;
    }

    public String getValueRegionLock() {
        return valueRegionLock;
    }

    public void setValueRegionLock(String valueRegionLock) {
        this.valueRegionLock = valueRegionLock;
    }

    public HashMap<String, Boolean> getValuesComponentsOwned() {
        return valuesComponentsOwned;
    }

    /*Set using HashMap as argument*/
    public void setValuesComponentsOwned(HashMap<String, Boolean> valuesComponentsOwned) {
        this.valuesComponentsOwned = valuesComponentsOwned;
    }

    /*Get boolean value of game in valuesComponentsOwned*/
    public boolean getValueGame() { return valuesComponentsOwned.get(GAME);}

    /*Set boolean value of game in valuesComponentsOwned*/
    public void setValueGame(boolean ownership) { valuesComponentsOwned.put(GAME, ownership); }

    /*Get boolean value of manual in valuesComponentsOwned*/
    public boolean getValueManual() { return valuesComponentsOwned.get(MANUAL);}

    /*Set boolean value of manual in valuesComponentsOwned*/
    public void setValueManual(boolean ownership) { valuesComponentsOwned.put(MANUAL, ownership); }

    /*Get boolean value of box in valuesComponentsOwned*/
    public boolean getValueBox() { return valuesComponentsOwned.get(BOX);}

    /*Set boolean value of box in valuesComponentsOwned*/
    public void setValueBox(boolean ownership) { valuesComponentsOwned.put(BOX, ownership); }

    public String getValueNote() {
        return valueNote;
    }

    public void setValueNote(String valueNote) {
        this.valueNote = valueNote;
    }

    public String getValueUniqueNodeId() {
        return valueUniqueNodeId;
    }

    public void setValueUniqueNodeId(String valueUniqueNodeId) { this.valueUniqueNodeId = valueUniqueNodeId; }

    public int getValueCopiesOwned() { return valueCopiesOwned; }

    public void setValueCopiesOwned (int valueCopiesOwned) { this.valueCopiesOwned = valueCopiesOwned; }
}