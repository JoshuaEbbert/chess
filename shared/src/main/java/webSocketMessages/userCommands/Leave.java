package webSocketMessages.userCommands;

import chess.ChessGame;

public class Leave extends UserGameCommand {
    private final int gameID;
    private final ChessGame.TeamColor color;

    public Leave(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
        this.color = color;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
