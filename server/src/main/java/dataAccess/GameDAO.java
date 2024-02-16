package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException; // returns gameID
    GameData getGame(int gameID) throws DataAccessException;
    HashSet<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException;
    void clear() throws DataAccessException;
}
