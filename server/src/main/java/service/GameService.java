package service;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public class GameService extends BaseService{
    public HashSet<GameData> listGames(String authToken) {
        throw new UnsupportedOperationException();
    }

    public int createGame(String authToken, String gameName) {
        throw new UnsupportedOperationException();
    }

    public void joinGame(String authToken, ChessGame.TeamColor clientColor, int gameID) {
        throw new UnsupportedOperationException();
    }
}
