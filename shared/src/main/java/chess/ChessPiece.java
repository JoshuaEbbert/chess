package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Creating collection to add valid moves to
        HashSet<ChessMove> moves = new HashSet<>();
        PieceType promotionPiece = null;
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        // Valid moves vary by piece
        if (type == PieceType.BISHOP) {
            for (int i = 1; row + i < 9 && column + i < 9; i++) { // moving to top right
                ChessPiece piece = board.getPiece(new ChessPosition(row + i, column + i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column + i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column + i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; row - i > 0 && column + i < 9; i++) { // moving to bottom right
                ChessPiece piece = board.getPiece(new ChessPosition(row - i, column + i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column + i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column + i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; row - i > 0 && column - i > 0; i++) { // moving to bottom left
                ChessPiece piece = board.getPiece(new ChessPosition(row - i, column - i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column - i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column - i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; row + i < 9 && column - i > 0; i++) { // moving to top left
                ChessPiece piece = board.getPiece(new ChessPosition(row + i, column - i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column - i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column - i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

        } else if (type == PieceType.KING) {
            if (tryMove(board, myPosition, row - 1, column - 1) != null) {
                moves.add(tryMove(board, myPosition, row - 1, column - 1));
            }
            if (tryMove(board, myPosition, row - 1, column) != null) {
                moves.add(tryMove(board, myPosition, row - 1, column));
            }
            if (tryMove(board, myPosition, row - 1, column + 1) != null) {
                moves.add(tryMove(board, myPosition, row - 1, column + 1));
            }
            if (tryMove(board, myPosition, row, column + 1) != null) {
                moves.add(tryMove(board, myPosition, row, column + 1));
            }
            if (tryMove(board, myPosition, row, column - 1) != null) {
                moves.add(tryMove(board, myPosition, row, column - 1));
            }
            if (tryMove(board, myPosition, row + 1, column + 1) != null) {
                moves.add(tryMove(board, myPosition, row + 1, column + 1));
            }
            if (tryMove(board, myPosition, row + 1, column - 1) != null) {
                moves.add(tryMove(board, myPosition, row + 1, column - 1));
            }
            if (tryMove(board, myPosition, row + 1, column) != null) {
                moves.add(tryMove(board, myPosition, row + 1, column));
            }

        } else if (type == PieceType.KNIGHT) {
            if (tryMove(board, myPosition, row - 2, column - 1) != null) {
                moves.add(tryMove(board, myPosition, row - 2, column - 1));
            }
            if (tryMove(board, myPosition, row - 2, column + 1) != null) {
                moves.add(tryMove(board, myPosition, row - 2, column + 1));
            }
            if (tryMove(board, myPosition, row - 1, column - 2) != null) {
                moves.add(tryMove(board, myPosition, row - 1, column - 2));
            }
            if (tryMove(board, myPosition, row - 1, column + 2) != null) {
                moves.add(tryMove(board, myPosition, row - 1, column + 2));
            }
            if (tryMove(board, myPosition, row + 1, column - 2) != null) {
                moves.add(tryMove(board, myPosition, row + 1, column - 2));
            }
            if (tryMove(board, myPosition, row + 1, column + 2) != null) {
                moves.add(tryMove(board, myPosition, row + 1, column + 2));
            }
            if (tryMove(board, myPosition, row + 2, column - 1) != null) {
                moves.add(tryMove(board, myPosition, row + 2, column - 1));
            }
            if (tryMove(board, myPosition, row + 2, column + 1) != null) {
                moves.add(tryMove(board, myPosition, row + 2, column + 1));
            }
        } else if (type == PieceType.PAWN) {
            if (teamColor == ChessGame.TeamColor.WHITE) {
                if (row + 1 < 9 && board.getPiece(new ChessPosition(row + 1, column)) == null) {
                    if (row + 1 == 8) {
                        promotionMove(myPosition, row + 1, column, moves);
                    } else {
                        moves.add(new ChessMove(
                                new ChessPosition(row, column),
                                new ChessPosition(row + 1, column),
                                promotionPiece));
                    }
                    if (notYetMoved(myPosition) && board.getPiece(new ChessPosition(row + 2, column)) == null) {
                        moves.add(tryMove(board, myPosition, row + 2, column));
                    }
                }

                // taking pieces diagonal left
                if (row + 1 < 9 && column - 1 > 0 && board.getPiece(new ChessPosition(row + 1, column - 1)) != null) {
                    if (board.getPiece(new ChessPosition(row + 1, column - 1)).teamColor != teamColor) {
                        if (row + 1 == 8) {
                            promotionMove(myPosition, row + 1, column - 1, moves);
                        } else {
                            moves.add(new ChessMove(
                                    new ChessPosition(row, column),
                                    new ChessPosition(row + 1, column - 1),
                                    promotionPiece));
                        }
                    }
                }

                // taking pieces diagonal right
                if (row + 1 < 9 && column + 1 < 9 && board.getPiece(new ChessPosition(row + 1, column + 1)) != null) {
                    if (board.getPiece(new ChessPosition(row + 1, column + 1)).teamColor != teamColor) {
                        if (row + 1 == 8) {
                            promotionMove(myPosition, row + 1, column + 1, moves);
                        } else {
                            moves.add(new ChessMove(
                                    new ChessPosition(row, column),
                                    new ChessPosition(row + 1, column + 1),
                                    promotionPiece));
                        }
                    }
                }
            } else { // teamColor is black
                // moving downward
                if (row - 1 > 0 && board.getPiece(new ChessPosition(row - 1, column)) == null) {
                    if (row - 1 == 1) {
                        promotionMove(myPosition, row - 1, column, moves);
                    } else {
                        moves.add(new ChessMove(
                                new ChessPosition(row, column),
                                new ChessPosition(row - 1, column),
                                promotionPiece));
                    }
                    if (notYetMoved(myPosition) && board.getPiece(new ChessPosition(row - 2, column)) == null) {
                        moves.add(tryMove(board, myPosition, row - 2, column));
                    }
                }

                // taking pieces diagonal left
                if (row - 1 > 0 && column - 1 > 0 && board.getPiece(new ChessPosition(row - 1, column - 1)) != null) {
                    if (board.getPiece(new ChessPosition(row - 1, column - 1)).teamColor != teamColor) {
                        if (row - 1 == 1) {
                            promotionMove(myPosition, row - 1, column - 1, moves);
                        } else {
                            moves.add(new ChessMove(
                                    new ChessPosition(row, column),
                                    new ChessPosition(row - 1, column - 1),
                                    promotionPiece));
                        }
                    }
                }

                // taking pieces diagonal right
                if (row - 1 > 0 && column + 1 < 9 && board.getPiece(new ChessPosition(row - 1, column + 1)) != null) {
                    if (board.getPiece(new ChessPosition(row - 1, column + 1)).teamColor != teamColor) {
                        if (row - 1 == 1) {
                            promotionMove(myPosition, row - 1, column + 1, moves);
                        } else {
                            moves.add(new ChessMove(
                                    new ChessPosition(row, column),
                                    new ChessPosition(row - 1, column + 1),
                                    promotionPiece));
                        }
                    }
                }
            }
        } else if (type == PieceType.ROOK) {
            for (int i = 1; row + i < 9; i++) { // moving upward
                ChessPiece piece = board.getPiece(new ChessPosition(row + i, column));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; row - i > 0; i++) { // moving downward
                ChessPiece piece = board.getPiece(new ChessPosition(row - i, column));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; column - i > 0; i++) { // moving to the left
                ChessPiece piece = board.getPiece(new ChessPosition(row, column - i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row, column - i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row, column - i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; column + i < 9; i++) { // moving to the right
                ChessPiece piece = board.getPiece(new ChessPosition(row, column + i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row, column + i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row, column + i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }
        } else { // QUEEN
            // Rook functionality
            for (int i = 1; row + i < 9; i++) { // moving upward
                ChessPiece piece = board.getPiece(new ChessPosition(row + i, column));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; row - i > 0; i++) { // moving downward
                ChessPiece piece = board.getPiece(new ChessPosition(row - i, column));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; column - i > 0; i++) { // moving to the left
                ChessPiece piece = board.getPiece(new ChessPosition(row, column - i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row, column - i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row, column - i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; column + i < 9; i++) { // moving to the right
                ChessPiece piece = board.getPiece(new ChessPosition(row, column + i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row, column + i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row, column + i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            // Bishop functionality
            for (int i = 1; row + i < 9 && column + i < 9; i++) { // moving to top right
                ChessPiece piece = board.getPiece(new ChessPosition(row + i, column + i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column + i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column + i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; row - i > 0 && column + i < 9; i++) { // moving to bottom right
                ChessPiece piece = board.getPiece(new ChessPosition(row - i, column + i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column + i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column + i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; row - i > 0 && column - i > 0; i++) { // moving to bottom left
                ChessPiece piece = board.getPiece(new ChessPosition(row - i, column - i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column - i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row - i, column - i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }

            for (int i = 1; row + i < 9 && column - i > 0; i++) { // moving to top left
                ChessPiece piece = board.getPiece(new ChessPosition(row + i, column - i));
                if (piece == null) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column - i),
                            promotionPiece));
                } else if (piece.teamColor != this.teamColor) {
                    moves.add(new ChessMove(
                            new ChessPosition(row, column),
                            new ChessPosition(row + i, column - i),
                            promotionPiece));
                    break;
                } else {
                    break;
                }
            }
        }

        return moves;
    }

    private ChessMove tryMove(ChessBoard board, ChessPosition myPosition, int newRow, int newCol) {
        PieceType promotionPiece = null;

        if (newCol > 0 && newCol < 9 && newRow > 0 && newRow < 9) {
            ChessPiece piece = board.getPiece(new ChessPosition(newRow, newCol));
            if (piece == null || piece.teamColor != this.teamColor) {
                return new ChessMove(
                        new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                        new ChessPosition(newRow, newCol),
                        promotionPiece);
            }
        }

        return null;
    }

    private boolean notYetMoved(ChessPosition myPosition) {
        return (teamColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) || (teamColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7);
    }

    private void promotionMove(ChessPosition myPosition, int newRow, int newColumn, HashSet<ChessMove> moves) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        moves.add(new ChessMove(
                new ChessPosition(row, column),
                new ChessPosition(newRow, newColumn),
                PieceType.QUEEN));
        moves.add(new ChessMove(
                new ChessPosition(row, column),
                new ChessPosition(newRow, newColumn),
                PieceType.KNIGHT));
        moves.add(new ChessMove(
                new ChessPosition(row, column),
                new ChessPosition(newRow, newColumn),
                PieceType.ROOK));
        moves.add(new ChessMove(
                new ChessPosition(row, column),
                new ChessPosition(newRow, newColumn),
                PieceType.BISHOP));
    }
}
