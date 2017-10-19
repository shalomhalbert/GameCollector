package com.example.android.gamecollector.data.firebase;

/**
 * Created by shalom on 2017-10-18.
 * Used for creating CollectedVideoGame objects containing video game data extracted from Firebase Realtime
 * Database.
 */

public class CollectedVideoGame {
    private int id;
    private String console;
    private String title;
    private String licensee;
    private String released;

    /**
     * Takes video game data as arguments
     * @param id Game's ID in the SQLite database (may depreciate after database upgrading)
     * @param console Console the game is made for
     * @param title Game's title
     * @param licensee Company who licensed the game
     * @param released Game's release date
     */
    public CollectedVideoGame(int id, String console, String title, String licensee, String released) {
        this.id = id;
        this.console = console;
        this.title = title;
        this.licensee = licensee;
        this.released = released;
    }

    public int getId() {
        return id;
    }
    public String getConsole() {
        return console;
    }
    public String getTitle() {
        return title;
    }
    public String getLicensee() {
        return licensee;
    }
    public String getReleased() {
        return released;
    }
}
