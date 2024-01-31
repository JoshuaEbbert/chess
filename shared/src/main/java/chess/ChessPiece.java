package chess;


import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor teamColor;
    public ChessPiece(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        this.type = type;
        this.teamColor = teamColor;
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
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        return MovesCalculator.calculate(board, type, teamColor, myPosition);
    }

    private boolean notYetMoved(ChessPosition start, ChessGame.TeamColor teamColor) {
        return (teamColor == ChessGame.TeamColor.WHITE && start.getRow() == 2) || (teamColor == ChessGame.TeamColor.BLACK && start.getRow() == 7);
    }

    private void promotionMove(HashSet<ChessMove> moves, ChessPosition start, int row, int col) {
        moves.add(new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                new ChessPosition(row, col),
                PieceType.BISHOP));
        moves.add(new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                new ChessPosition(row, col),
                PieceType.KNIGHT));
        moves.add(new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                new ChessPosition(row, col),
                PieceType.ROOK));
        moves.add(new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                new ChessPosition(row, col),
                PieceType.QUEEN));
    }

    private ChessMove tryMove(ChessBoard board, ChessPosition start, int row, int col) {
        ChessPosition end = new ChessPosition(row, col);

        if (row > 8 || row < 1 || col > 8 || col < 1) {
            return null;
        } else if (board.getPiece(end) == null || board.getPiece(end).teamColor != this.teamColor) {
            return new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                    new ChessPosition(row, col),
                    null);
        }
        return null;
    }

    @Override
    public String toString() {
        return teamColor + " " + type;
    }
}
