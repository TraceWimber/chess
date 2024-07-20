package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    /**
    * Sets the black team player name to given username
    *
    * @param newUsername new player name
    * @return GameData with updated black team player name
    */
    GameData addBlack(String newUsername) {
        return new GameData(gameID, whiteUsername, newUsername, gameName, game);
    }

    /**
     * Sets the white team player name to given username
     *
     * @param newUsername new player name
     * @return GameData with updated white team player name
     */
    GameData addWhite(String newUsername) {
        return new GameData(gameID, newUsername, blackUsername, gameName, game);
    }
}
