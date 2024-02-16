package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData setGame(ChessGame updatedGame) {
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, this.gameName, updatedGame);
    }
}
