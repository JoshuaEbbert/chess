package chess;

import java.util.HashSet;

public class MovesCalculator {

    public static HashSet<ChessMove> calculate(ChessBoard board, ChessPiece.PieceType type, ChessGame.TeamColor teamColor, ChessPosition startPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        switch (type) {
            case QUEEN:

            case BISHOP:
                for (int i = 1; row + i < 9 && col + i < 9; i++) { // moving to the top right
                    if (board.getPiece(new ChessPosition(row + i, col + i)) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row + i, col + i),
                                null));
                    } else if (board.getPiece(new ChessPosition(row + i, col + i)).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row + i, col + i),
                                null));
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = 1; row - i > 0 && col + i < 9; i++) { // moving to the bottom right
                    if (board.getPiece(new ChessPosition(row - i, col + i)) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row - i, col + i),
                                null));
                    } else if (board.getPiece(new ChessPosition(row - i, col + i)).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row - i, col + i),
                                null));
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = 1; row - i > 0 && col - i > 0; i++) { // moving to the bottom left
                    if (board.getPiece(new ChessPosition(row - i, col - i)) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row - i, col - i),
                                null));
                    } else if (board.getPiece(new ChessPosition(row - i, col - i)).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row - i, col - i),
                                null));
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = 1; row + i < 9 && col - i > 0; i++) { // moving to the top left
                    if (board.getPiece(new ChessPosition(row + i, col - i)) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row + i, col - i),
                                null));
                    } else if (board.getPiece(new ChessPosition(row + i, col - i)).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row + i, col - i),
                                null));
                        break;
                    } else {
                        break;
                    }
                }
                if (type != ChessPiece.PieceType.QUEEN) { break; }

            case ROOK:
                for (int i = 1; row + i < 9; i++) { // moving to the top
                    if (board.getPiece(new ChessPosition(row + i, col)) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row + i, col),
                                null));
                    } else if (board.getPiece(new ChessPosition(row + i, col)).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row + i, col),
                                null));
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = 1; row - i > 0; i++) { // moving to the bottom
                    if (board.getPiece(new ChessPosition(row - i, col)) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row - i, col),
                                null));
                    } else if (board.getPiece(new ChessPosition(row - i, col)).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row - i, col),
                                null));
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = 1; col - i > 0; i++) { // moving to the left
                    if (board.getPiece(new ChessPosition(row, col - i)) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row, col - i),
                                null));
                    } else if (board.getPiece(new ChessPosition(row, col - i)).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row, col - i),
                                null));
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = 1; col + i < 9; i++) { // moving to the right
                    if (board.getPiece(new ChessPosition(row, col + i)) == null) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row, col + i),
                                null));
                    } else if (board.getPiece(new ChessPosition(row, col + i)).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(new ChessPosition(row, col),
                                new ChessPosition(row, col + i),
                                null));
                        break;
                    } else {
                        break;
                    }
                }

                break;

            case PAWN:
                if (teamColor == ChessGame.TeamColor.WHITE) {
                    // check if front space is open
                    if (row < 8 &&
                            board.getPiece(new ChessPosition(row + 1, col)) == null) {
                        // add move
                        if (row + 1 == 8) {
                            promotionMove(moves, startPosition, row + 1, col);
                        } else {
                            moves.add(new ChessMove(new ChessPosition(row, col),
                                    new ChessPosition(row + 1, col),
                                    null));
                        }

                        // check if eligible for initial double move
                        if (notYetMoved(startPosition, teamColor) && board.getPiece(new ChessPosition(row + 2, col)) == null) {
                            moves.add(new ChessMove(new ChessPosition(row, col),
                                    new ChessPosition(row + 2, col),
                                    null));
                        }
                    }
                    // take front left
                    if (row < 8 &&
                            col > 1 &&
                            board.getPiece(new ChessPosition(row + 1, col - 1)) != null &&
                            board.getPiece(new ChessPosition(row + 1, col - 1)).getTeamColor() != teamColor) {
                        // add move
                        if (row + 1 == 8) {
                            promotionMove(moves, startPosition, row + 1, col - 1);
                        } else {
                            moves.add(new ChessMove(new ChessPosition(row, col),
                                    new ChessPosition(row + 1, col - 1),
                                    null));
                        }
                    }

                    // take front right
                    if (row < 8 &&
                            col < 8 &&
                            board.getPiece(new ChessPosition(row + 1, col + 1)) != null &&
                            board.getPiece(new ChessPosition(row + 1, col + 1)).getTeamColor() != teamColor) {
                        // add move
                        if (row + 1 == 8) {
                            promotionMove(moves, startPosition, row + 1, col + 1);
                        } else {
                            moves.add(new ChessMove(new ChessPosition(row, col),
                                    new ChessPosition(row + 1, col + 1),
                                    null));
                        }
                    }
                } else { // teamColor is BLACK
                    // check if front space is open
                    if (row > 1 &&
                            board.getPiece(new ChessPosition(row - 1, col)) == null) {
                        // add move
                        if (row - 1 == 1) {
                            promotionMove(moves, startPosition, row - 1, col);
                        } else {
                            moves.add(new ChessMove(new ChessPosition(row, col),
                                    new ChessPosition(row - 1, col),
                                    null));
                        }

                        // check if eligible for initial double move
                        if (notYetMoved(startPosition, teamColor) && board.getPiece(new ChessPosition(row - 2, col)) == null) {
                            moves.add(new ChessMove(new ChessPosition(row, col),
                                    new ChessPosition(row - 2, col),
                                    null));
                        }
                    }
                    // take front left
                    if (row > 1 &&
                            col > 1 &&
                            board.getPiece(new ChessPosition(row - 1, col - 1)) != null &&
                            board.getPiece(new ChessPosition(row - 1, col - 1)).getTeamColor() != teamColor) {
                        // add move
                        if (row - 1 == 1) {
                            promotionMove(moves, startPosition, row - 1, col - 1);
                        } else {
                            moves.add(new ChessMove(new ChessPosition(row, col),
                                    new ChessPosition(row - 1, col - 1),
                                    null));
                        }
                    }

                    // take front right
                    if (row > 1 &&
                            col < 8 &&
                            board.getPiece(new ChessPosition(row - 1, col + 1)) != null &&
                            board.getPiece(new ChessPosition(row - 1, col + 1)).getTeamColor() != teamColor) {
                        // add move
                        if (row - 1 == 1) {
                            promotionMove(moves, startPosition, row - 1, col + 1);
                        } else {
                            moves.add(new ChessMove(new ChessPosition(row, col),
                                    new ChessPosition(row - 1, col + 1),
                                    null));
                        }
                    }
                }
                break;

            case KNIGHT:
                if (tryMove(board, startPosition, teamColor,row + 2, col - 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row + 2, col - 1));
                }
                if (tryMove(board, startPosition, teamColor,row + 2, col + 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row + 2, col + 1));
                }
                if (tryMove(board, startPosition, teamColor,row + 1, col + 2) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row + 1, col + 2));
                }
                if (tryMove(board, startPosition, teamColor,row - 1, col + 2) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row - 1, col + 2));
                }
                if (tryMove(board, startPosition, teamColor,row - 2, col + 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row - 2, col + 1));
                }
                if (tryMove(board, startPosition, teamColor,row - 2, col - 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row - 2, col - 1));
                }
                if (tryMove(board, startPosition, teamColor,row - 1, col - 2) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row - 1, col - 2));
                }
                if (tryMove(board, startPosition, teamColor,row + 1, col - 2) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row + 1, col - 2));
                }
                break;

            case KING:
                if (tryMove(board, startPosition, teamColor,row + 1, col - 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row + 1, col - 1));
                }
                if (tryMove(board, startPosition, teamColor,row + 1, col) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row + 1, col));
                }
                if (tryMove(board, startPosition, teamColor,row + 1, col + 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row + 1, col + 1));
                }
                if (tryMove(board, startPosition, teamColor, row, col + 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row, col + 1));
                }
                if (tryMove(board, startPosition, teamColor,row - 1, col + 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row - 1, col + 1));
                }
                if (tryMove(board, startPosition, teamColor,row - 1, col) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row - 1, col));
                }
                if (tryMove(board, startPosition, teamColor,row - 1, col - 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor,row - 1, col - 1));
                }
                if (tryMove(board, startPosition, teamColor, row, col - 1) != null) {
                    moves.add(tryMove(board, startPosition, teamColor, row, col - 1));
                }
                break;
        }

        return moves;
    }

    private static boolean notYetMoved(ChessPosition start, ChessGame.TeamColor teamColor) {
        return (teamColor == ChessGame.TeamColor.WHITE && start.getRow() == 2) || (teamColor == ChessGame.TeamColor.BLACK && start.getRow() == 7);
    }
    private static void promotionMove(HashSet<ChessMove> moves, ChessPosition start, int row, int col) {
        moves.add(new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                new ChessPosition(row, col),
                ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                new ChessPosition(row, col),
                ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                new ChessPosition(row, col),
                ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                new ChessPosition(row, col),
                ChessPiece.PieceType.QUEEN));
    }

    private static ChessMove tryMove(ChessBoard board, ChessPosition start, ChessGame.TeamColor teamColor, int row, int col) {
        ChessPosition end = new ChessPosition(row, col);

        if (row > 8 || row < 1 || col > 8 || col < 1) {
            return null;
        } else if (board.getPiece(end) == null || board.getPiece(end).getTeamColor() != teamColor) {
            return new ChessMove(new ChessPosition(start.getRow(), start.getColumn()),
                    new ChessPosition(row, col),
                    null);
        }
        return null;
    }
}
