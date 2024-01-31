package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard chessBoard;
    private TeamColor teamTurn;
    public ChessGame() {
        chessBoard = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = chessBoard.getPiece(startPosition);
        HashSet<ChessMove> validMoves = new HashSet<>();
        if (piece == null) { return null; }

        for (ChessMove move: MovesCalculator.calculate(chessBoard, piece.getPieceType(), piece.getTeamColor(), startPosition)) {
            chessBoard.addPiece(startPosition, null);
            ChessPiece tmp = null;
            if (chessBoard.getPiece(move.getEndPosition()) != null) {
                tmp = chessBoard.getPiece(move.getEndPosition());
            }
            chessBoard.addPiece(move.getEndPosition(), piece);

            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

            chessBoard.addPiece(startPosition, piece);
            chessBoard.addPiece(move.getEndPosition(), tmp);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = chessBoard.getKingPosition(teamColor);
//        System.out.println("King position: " + kingPosition);
        for (ChessPiece.PieceType type: ChessPiece.PieceType.values()) {
//            System.out.println("About to check for " + type);
            if (type == ChessPiece.PieceType.PAWN) {
                return checkPawns(kingPosition, teamColor);
            } else {
                for (ChessMove possibleMove : MovesCalculator.calculate(getBoard(), type, teamColor, kingPosition)) {
//                    System.out.println("About to test possibleMove: " + possibleMove.toString());
                    if (chessBoard.getPiece(possibleMove.getEndPosition()) != null &&
                            chessBoard.getPiece(possibleMove.getEndPosition()).getPieceType() == type) {
                        return true;
                    }
                }
            }
//            System.out.println("Finished checking for " + type + "!");
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }

    private boolean checkPawns(ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        int kingRow = kingPosition.getRow();
        int kingCol = kingPosition.getColumn();

        if (teamColor == TeamColor.WHITE) {
            if (kingRow < 7) { // possible pawn attack
                if (kingCol > 1) { // possible front left attack
                    if (chessBoard.getPiece(new ChessPosition(kingRow + 1, kingCol - 1)) != null &&
                            chessBoard.getPiece(new ChessPosition(kingRow + 1, kingCol - 1)).getPieceType() == ChessPiece.PieceType.PAWN) {
                        return true;
                    }
                }
                if (kingCol < 8) { // possible front right attack
                    if (chessBoard.getPiece(new ChessPosition(kingRow + 1, kingCol + 1)) != null &&
                            chessBoard.getPiece(new ChessPosition(kingRow + 1, kingCol + 1)).getPieceType() == ChessPiece.PieceType.PAWN) {
                        return true;
                    }
                }
            }
        } else { // BLACK
            if (kingRow > 2) { // possible pawn attack
                if (kingCol > 1) { // possible front left attack
                    if (chessBoard.getPiece(new ChessPosition(kingRow - 1, kingCol - 1)) != null &&
                            chessBoard.getPiece(new ChessPosition(kingRow - 1, kingCol - 1)).getPieceType() == ChessPiece.PieceType.PAWN) {
                        return true;
                    }
                }
                if (kingCol < 8) { // possible front right attack
                    if (chessBoard.getPiece(new ChessPosition(kingRow - 1, kingCol + 1)) != null &&
                            chessBoard.getPiece(new ChessPosition(kingRow - 1, kingCol + 1)).getPieceType() == ChessPiece.PieceType.PAWN) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
