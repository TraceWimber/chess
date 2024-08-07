package model;

import java.util.ArrayList;

//This class is merely a wrapper so that gson can properly parse an array into JSON
public class Games {
    private ArrayList<GameData> games;

    public Games(ArrayList<GameData> games) {
        this.games = games;
    }

    public ArrayList<GameData> getGames() {
        return games;
    }
}
