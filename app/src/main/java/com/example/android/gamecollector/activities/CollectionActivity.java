package com.example.android.gamecollector.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.gamecollector.ItemDialogFragment;
import com.example.android.gamecollector.R;
import com.example.android.gamecollector.adapters.CollectedArrayAdapter;
import com.example.android.gamecollector.data.propertyBags.VideoGame;
import com.example.android.gamecollector.data.sqlite.CollectableContract.VideoGamesEntry;
import com.example.android.gamecollector.utils.MenuUtils;
import com.example.android.gamecollector.utils.VideoGameUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by shalom on 2017-10-05.
 * Displays user's personal collection
 * Also, it's the main activity/
 */

//    TODO(3) Match list item divider width to Sketch wireframe (after switching to RecyclerView)
//    TODO(3) When user signs in, Firebase should update SQLite copies owned after checking if table exists. Build it if it doesn't

public class CollectionActivity extends AppCompatActivity {
    public static final String LOG_TAG = CollectionActivity.class.getSimpleName();
    /*Contains a list of objects with video game data that will be adapted to a ListView*/
    private ArrayList<VideoGame> videoGames = new ArrayList<>();
    /*Handles videoGames list*/
    private CollectedArrayAdapter adapter;
    /*Works with adapter to display data from videoGames*/
    private ListView listView;
    /*Instantiation of Realtime Database objects*/
    private DatabaseReference collectionRef;
    /*Manages dialog fragment*/
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        /*Set toolbar as activity's ActionBar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*Set toolbar's title*/
        toolbar.setTitle(R.string.activity_collection_toolbar_title);

        createDatabaseListeners();

        floatingActionButtonListner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_collection, menu);
        if (menu.findItem(R.id.action_save) != null){
            /*Makes the SAVE action button invisible*/
            menu.findItem(R.id.action_save).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        switch (itemID) {
            case R.id.action_contact_us:
                MenuUtils.ImplicitEmailIntent(getApplicationContext());
                break;
            case R.id.action_sign_out:
                MenuUtils.LogoutUser(getApplicationContext());
        }
        return super.onOptionsItemSelected(item);
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
        collectionRef = VideoGameUtils.GetDatabaseReference();

        /*Used to receive events about changes in the child locations of a given DatabaseReference ref.
        Attach the listener to a location using addChildEventListener(ChildEventListener) and the
        appropriate method will be triggered when changes occur.*/
        collectionRef.addChildEventListener(new ChildEventListener() {
            /*Creates VideoGame objects and adds them to the videoGames ArrayList.
            * Runs after onCreate finishes.*/
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /*Data points from each video_games node*/
                String uniqueId = dataSnapshot.child(VideoGamesEntry.COLUMN_UNIQUE_ID).getValue(String.class);
                String console = dataSnapshot.child(VideoGamesEntry.COLUMN_CONSOLE).getValue(String.class);
                String title = dataSnapshot.child(VideoGamesEntry.COLUMN_TITLE).getValue(String.class);
                String licensee = dataSnapshot.child(VideoGamesEntry.COLUMN_LICENSEE).getValue(String.class);
                String released = dataSnapshot.child(VideoGamesEntry.COLUMN_RELEASED).getValue(String.class);

                long dateAdded = 0;
                if (dataSnapshot.child(VideoGame.KEY_DATE_ADDED_UNIX).getValue(Long.class) != null) {
                    dateAdded = dataSnapshot.child(VideoGame.KEY_DATE_ADDED_UNIX).getValue(Long.class);
                }

                String regionLock = dataSnapshot.child(VideoGame.KEY_REGION_LOCK).getValue(String.class);
                HashMap<String, Boolean> componentsOwned = buildComponentsMap(dataSnapshot);
                String note = dataSnapshot.child(VideoGame.KEY_NOTE).getValue(String.class);
                String uniqueNodeId = dataSnapshot.child(VideoGame.KEY_UNIQUE_NODE_ID).getValue(String.class);

                videoGames.add(new VideoGame(uniqueId, console, title, licensee, released, dateAdded,
                        regionLock, componentsOwned, note, uniqueNodeId));
            }

            /*Currently only handles Edit dialog results*/
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /*Tracks element's index number*/
                int counter = 0;
                /*Checks whether the changed node's date matches a videogame in the ArrayList
                 *If dates match, they are the same node.*/
                for (VideoGame game : videoGames) {
                    if (game.getValueDateAdded() == 0) {
                        /*Arbitrary number used for informing next if statement that no change occurred*/
                        counter = 100;
                        break;
                    } else if (dataSnapshot.child(VideoGame.KEY_DATE_ADDED_UNIX).getValue(Long.class) != null
                            && game.getValueDateAdded() == dataSnapshot.child(VideoGame.KEY_DATE_ADDED_UNIX).getValue(Long.class).longValue()) {
                        break;
                    }
                    counter++;
                }

                /*If counter is larger than the VideoGame ArrayList's size, there was no change*/
                if (counter >= videoGames.size()) {
                    return;
                } else {
                    /*Update the changed element*/
                    videoGames.get(counter).setValueRegionLock(dataSnapshot.child(VideoGame.KEY_REGION_LOCK).getValue(String.class));
                    videoGames.get(counter).setValuesComponentsOwned(buildComponentsMap(dataSnapshot));
                    videoGames.get(counter).setValueNote(dataSnapshot.child(VideoGame.KEY_NOTE).getValue(String.class));
                    /*Informs ArrayAdapter that its ArraList was updated*/
                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
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
                sortTitles();
                listView = (ListView) findViewById(R.id.activity_collection_listview);
                adapter = new CollectedArrayAdapter(getApplicationContext(), videoGames);
                Log.i(LOG_TAG, "Number of VideoGame elements given to adapter: " + videoGames.size());
                listView.setAdapter(adapter);
                /*Set and handle when a list item is clicked*/
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*Initialize bundle that'll be passed to dialog fragment*/
                        Bundle bundle = new Bundle();
                        /*Extract VideoGame object from clicked list item*/
                        VideoGame videoGame = (VideoGame) parent.getItemAtPosition(position);
                        bundle.putString(VideoGame.KEY_UNIQUE_NODE_ID, videoGame.getValueUniqueNodeId());
                        bundle.putString(VideoGame.KEY_CONSOLE, videoGame.getValueConsole());
                        bundle.putString(VideoGamesEntry.COLUMN_TITLE, videoGame.getValueTitle());
                        bundle.putString(VideoGame.KEY_REGION_LOCK, videoGame.getValueRegionLock());
                        bundle.putSerializable(VideoGame.KEY_COMPONENTS_OWNED, videoGame.getValuesComponentsOwned());
                        bundle.putString(VideoGame.KEY_NOTE, videoGame.getValueNote());

                        showDialog(bundle);

                    }
                });

                /*Allows multiple list items to be clicked*/
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                /*Set and handle multiple items being selected with clicks*/
                listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                    /**
                     * Called when an item is checked or unchecked during selection mode.
                     *
                     * @param mode The current ActionMode
                     * @param position Adapter position of the item that was checked or unchecked
                     * @param id Adapter ID of the item that was checked or unchecked
                     * @param checked true if the item is now checked, false if the item is now unchecked.
                     */
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                        /*Hide the action bar while selecting items*/
                        getSupportActionBar().hide();

                        /*Get total number of checked items*/
                        final int checkedCount = listView.getCheckedItemCount();
                        /*Sets title*/
                        mode.setTitle(checkedCount + " Selected");
//                      /**/
                        adapter.toggleSelection(position);
                    }

                    /**
                     * Called when action mode is first created. The menu supplied will be
                     * used to generate action buttons for the action mode.
                     *
                     * @param mode ActionMode being created
                     * @param menu Menu used to populate action buttons
                     * @return true if the action mode should be created, false if entering
                     * this mode should be aborted.
                     */
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        getMenuInflater().inflate(R.menu.multi_select_collection_menu, menu);
                        return true;
                    }

                    /**
                     * Called to refresh an action mode's action menu whenever it is invalidated.
                     *
                     * @param mode ActionMode being prepared
                     * @param menu Menu used to populate action buttons
                     * @return true if the menu or action mode was updated, false otherwise.
                     */
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        //TODO auto-generated method stub
                        return false;
                    }

                    /**
                     * Called to report a user click on an action button.
                     *
                     * @param mode The current ActionMode
                     * @param item The item that was clicked
                     * @return true if this callback handled the event, false if the
                     * standard MenuItem invocation should continue.
                     */
                    @Override
                    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                                /*ID of clicked action button*/
                        int id = item.getItemId();

                        if (id == R.id.delete) {
                                    /*Dialog for delete confirmation*/
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionActivity.this);

                            alertDialogBuilder.setMessage(R.string.multi_delete_collection_alertdialog_question);

                            /*Handle positive response*/
                            alertDialogBuilder.setPositiveButton(R.string.multi_delete_collection_alertdialog_answer_yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            /*Get ids of selected items*/
                                            SparseBooleanArray selectedIds = adapter.getSelectedIds();
                                            /*Iterate through all selected list items*/
                                            for (int i = (selectedIds.size() - 1); i >= 0; i--) {

                                                if (selectedIds.valueAt(i)) {
                                                    VideoGame selectedVideoGame = adapter.getItem(selectedIds.keyAt(i));
                                                            /*Removes node from Firebase database*/
                                                    VideoGameUtils.DeleteNode(selectedVideoGame);
                                                            /*Remove selected item from adapter ArrayList<VideoGame>*/
                                                    adapter.remove(selectedVideoGame);
                                                }
                                            }

                                            mode.finish();
                                            selectedIds.clear();
                                        }
                                    });

                            alertDialogBuilder.setNegativeButton(R.string.multi_delete_collection_alertdialog_answer_no,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO  Auto-generated method stub
                                        }
                                    });
                            AlertDialog alert =  alertDialogBuilder.create();
//                            alert.setIcon(R.drawable.questionicon);// dialog  Icon
                            alert.setTitle("Confirmation"); // dialog  Title
                            alert.show();
                            return true;
                        }
                        return false;
                    }

                    /**
                     * Called when an action mode is about to be exited and destroyed.
                     *
                     * @param mode The current ActionMode being destroyed
                     */
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        /*Show ActionBar again*/
                        getSupportActionBar().show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "DatabaseError: " + databaseError.getDetails());
            }
        });
    }

    /**
     * Builds HashMap of componenets owned from Firebase DataSnapshot
     *
     * @param dataSnapshot DataSnapshot object provided by onChildAdded()
     * @return HashMap populated with values representing whether component is owned
     */
    private HashMap<String, Boolean> buildComponentsMap(DataSnapshot dataSnapshot) {
        /*Placeholder HashMap*/
        HashMap<String, Boolean> componentsOwned = new HashMap<>();
        /*Extract values from dataSnapshot*/
        Boolean game = dataSnapshot.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.GAME).getValue(Boolean.class);
        Boolean manual = dataSnapshot.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.MANUAL).getValue(Boolean.class);
        Boolean box = dataSnapshot.child(VideoGame.KEY_COMPONENTS_OWNED).child(VideoGame.BOX).getValue(Boolean.class);

        /*Add values to HashMap*/
        componentsOwned.put(VideoGame.GAME, game);
        componentsOwned.put(VideoGame.MANUAL, manual);
        componentsOwned.put(VideoGame.BOX, box);

        return componentsOwned;
    }

    /*Sort ArrayList according to title, alphabetically (1-999, then a/z)*/
    private void sortTitles() {
        Collections.sort(videoGames, new Comparator<VideoGame>() {
            @Override
            public int compare(VideoGame game1, VideoGame game2) {

                if (game1.getValueTitle() == null || game2.getValueTitle() == null) {
                    return 0;
                }
                return game1.getValueTitle().compareToIgnoreCase(game2.getValueTitle());
            }
        });
    }

    /*Opens populated DialogFragment*/
    private void showDialog(Bundle bundle) {
        /*Required for fragmentTransaction.add()*/
        int containerViewId = android.R.id.content;

        /*Initialize ItemDialogFragment*/
        ItemDialogFragment dialogFragment = new ItemDialogFragment();
        /*Supplies arguments to dialogFragment*/
        dialogFragment.setArguments(bundle);
        /*FragmentManager is taken in constructor and FragmentTransaction makes transactions*/
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        /*Sets transition effect for when dialog opens*/
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.add(containerViewId, dialogFragment).addToBackStack(null).commit();
    }
}
