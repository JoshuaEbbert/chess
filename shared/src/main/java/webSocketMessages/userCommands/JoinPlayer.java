package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {
    private final int gameID;
    private final ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = color;
        this.commandType = CommandType.JOIN_PLAYER;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
