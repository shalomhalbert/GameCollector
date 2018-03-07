package com.shalbert.hobby.gamecollector.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.shalbert.hobby.gamecollector.data.propertyBags.VideoGame;
import com.shalbert.hobby.gamecollector.data.sqlite.CollectableContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

/**
 * Created by shalom on 2018-01-12.
 */

public final class VideoGameUtils {
    private static final String LOG_TAG = VideoGameUtils.class.getSimpleName();

    /*Constant names of database nodes*/
    public static final String NODE_COLLECTABLES_OWNED = "collectables_owned";
    public static final String NODE_USERS = "users";
    public static final String NODE_VIDEO_GAMES = "video_games";

    public static void CreateNode(VideoGame videoGame) {
        /*Create a unique ID that names a node for an individual video game when it's added*/
        videoGame.setValueUniqueNodeId(UUID.randomUUID().toString());

        CheckRegionLock(videoGame);


        DatabaseReference dbRef = GetDatabaseReference().child(videoGame.getValueUniqueNodeId());

        dbRef.child(VideoGame.KEY_UNIQUE_ID).setValue(videoGame.getValueUniqueID());
        dbRef.child(VideoGame.KEY_CONSOLE).setValue(videoGame.getValueConsole());
        dbRef.child(VideoGame.KEY_TITLE).setValue(videoGame.getValueTitle());
        dbRef.child(VideoGame.KEY_LICENSEE).setValue(videoGame.getValueLicensee());
        dbRef.child(VideoGame.KEY_RELEASED).setValue(videoGame.getValueReleased());
        dbRef.child(VideoGame.KEY_DATE_ADDED_UNIX).setValue(videoGame.getValueDateAdded());
        dbRef.child(VideoGame.KEY_REGION_LOCK).setValue(videoGame.getValueRegionLock());
        dbRef.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.GAME).setValue(videoGame.getValuesComponentsOwned().get(VideoGame.GAME));
        dbRef.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.MANUAL).setValue(videoGame.getValuesComponentsOwned().get(VideoGame.MANUAL));
        dbRef.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.BOX).setValue(videoGame.getValuesComponentsOwned().get(VideoGame.BOX));
        dbRef.child(VideoGame.KEY_NOTE).setValue(videoGame.getValueNote());
        dbRef.child(VideoGame.KEY_UNIQUE_NODE_ID).setValue(videoGame.getValueUniqueNodeId());

//        int rowsUpdated = updateCopiesOwned();
//        Log.i(LOG_TAG, "Rows updated: " + rowsUpdated);
    }

    public static void UpdateNode(VideoGame videoGame) {

        Log.i(LOG_TAG, "regionLock: " + videoGame.getValueRegionLock()
                + ", game: " + videoGame.getValueGame()
                + ", manual: " + videoGame.getValueManual()
                + ", box: " + videoGame.getValueBox());

        CheckRegionLock(videoGame);

        DatabaseReference dbRef = GetDatabaseReference().child(videoGame.getValueUniqueNodeId());
        /*Update region lock*/
        dbRef.child(VideoGame.KEY_REGION_LOCK).setValue(videoGame.getValueRegionLock());
        /*Update components owned*/
        dbRef.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.GAME).setValue(videoGame.getValueGame());
        dbRef.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.MANUAL).setValue(videoGame.getValueManual());
        dbRef.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.BOX).setValue(videoGame.getValueBox());
        /*Update note*/
        dbRef.child(VideoGame.KEY_NOTE).setValue(videoGame.getValueNote());

    }

    /**
     * Will be used later for informing SQLite database that video game was added or deleted from
     * user's collection
     * @param context
     * @param videoGame
     * @return How many rows were updates. Should only be one (test for this)
     */
    private static int UpdateCopiesOwned(Context context, VideoGame videoGame) {

        /*Updated number of copes owned*/
        int updatedCopies = videoGame.getValueCopiesOwned() + 1;
        /*Uri with appropriate rowId appended*/
        Uri uri = Uri.withAppendedPath(CollectableContract.VideoGamesEntry.CONTENT_URI, videoGame.getValueRowID());
        /*Key value pair used for updating database*/
        ContentValues sqliteUpdate = new ContentValues();
        sqliteUpdate.put(CollectableContract.VideoGamesEntry.COLUMN_COPIES_OWNED, String.valueOf(updatedCopies));

        String selection = VideoGame.KEY_UNIQUE_ID + "=?";
        String[] selectionArgs = {videoGame.getValueUniqueID()};


        /*Update SQLite database*/
        int rowsUpdated = context.getContentResolver().update(uri, sqliteUpdate, selection, selectionArgs);

//        if (rowsUpdated != 1) thrown new Exception;

        return rowsUpdated;
    }

    /*Removes the video game's node from Firebase*/
    public static void DeleteNode(VideoGame videoGame) {
        GetDatabaseReference().child(videoGame.getValueUniqueNodeId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }

    /**
     * Handles a null valueRegionLock by setting it's value to VideoGame.UNDEFINED_TRAIT
     * @param videoGame Video object
     */
    private static void CheckRegionLock(VideoGame videoGame) {
        if (videoGame.getValueRegionLock() == null) {
            videoGame.setValueRegionLock(VideoGame.UNDEFINED_TRAIT);
        }
    }
    /**
     * @return reference to a node containing all video games with uniqueNodeIDs as their key's
     */
    public static DatabaseReference GetDatabaseReference() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference()
                .child(NODE_COLLECTABLES_OWNED)
                .child(NODE_USERS)
                .child(GetUserID())
                .child(NODE_VIDEO_GAMES);

        return dbRef;
    }

    /**
     * Extracts and tests for existence of current user
     * @return UID from FirebaseAuth object
     */
    private static String GetUserID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        /*Holds user's ID*/
        String uid = null;
        /*Handles there being no user*/
        if (user != null)  {
            uid = user.getUid();
            return uid;
        } else {
            throw new UnsupportedOperationException("There is no current user");
        }
    }

    /**
     * @return Current Unix time
     */
    public static long GetUnixTime() {
        return System.currentTimeMillis();
    }
}
