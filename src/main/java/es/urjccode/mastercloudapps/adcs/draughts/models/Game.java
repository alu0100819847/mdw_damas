package es.urjccode.mastercloudapps.adcs.draughts.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private Board board;
    private Turn turn;

    Game(Board board) {
        this.turn = new Turn();
        this.board = board;
    }

    public Game() {
        this(new Board());
        this.reset();
    }

    public void reset() {
        for (int i = 0; i < Coordinate.getDimension(); i++)
            for (int j = 0; j < Coordinate.getDimension(); j++) {
                Coordinate coordinate = new Coordinate(i, j);
                Color color = Color.getInitialColor(coordinate);
                Piece piece = null;
                if (color != null)
                    piece = new Pawn(color);
                this.board.put(coordinate, piece);
            }
        if (this.turn.getColor() != Color.WHITE)
            this.turn.change();
    }

    public Error move(Coordinate... coordinates) {
        Error error = null;
        List<Coordinate> removedCoordinates = new ArrayList<Coordinate>();
        int pair = 0;
        do {
            error = this.isCorrectPairMove(pair, coordinates);
            if (error == null) {
                List<Coordinate> canJumpPieces = this.getCanJumpPieces();
                int numberOfPiecesWithOppositeColor = this.getNumberOfPiecesWithOppositeColor();
                this.isCanJumpPiecesMoving(canJumpPieces, pair, coordinates);
                this.pairMove(removedCoordinates, pair, coordinates);
                this.removePieceIfNotJump(numberOfPiecesWithOppositeColor, canJumpPieces);
                pair++;
            }
        } while (pair < coordinates.length - 1 && error == null);
        error = this.isCorrectGlobalMove(error, removedCoordinates, coordinates);
        if (error == null)
            this.turn.change();
        else
            this.unMovesUntilPair(removedCoordinates, pair, coordinates);
        return error;
    }

    private Error isCorrectPairMove(int pair, Coordinate... coordinates) {
        assert coordinates[pair] != null;
        assert coordinates[pair + 1] != null;
        if (board.isEmpty(coordinates[pair]))
            return Error.EMPTY_ORIGIN;
        if (this.turn.getOppositeColor() == this.board.getColor(coordinates[pair]))
            return Error.OPPOSITE_PIECE;
        if (!this.board.isEmpty(coordinates[pair + 1]))
            return Error.NOT_EMPTY_TARGET;
        List<Piece> betweenDiagonalPieces =
            this.board.getBetweenDiagonalPieces(coordinates[pair], coordinates[pair + 1]);
        return this.board.getPiece(coordinates[pair]).isCorrectMovement(betweenDiagonalPieces, pair, coordinates);
    }

    private void pairMove(List<Coordinate> removedCoordinates, int pair, Coordinate... coordinates) {
        Coordinate forRemoving = this.getBetweenDiagonalPiece(pair, coordinates);
        if (forRemoving != null) {
            removedCoordinates.add(0, forRemoving);
            this.board.remove(forRemoving);
        }
        this.board.move(coordinates[pair], coordinates[pair + 1]);
        if (this.board.getPiece(coordinates[pair + 1]).isLimit(coordinates[pair + 1])) {
            Color color = this.board.getColor(coordinates[pair + 1]);
            this.board.remove(coordinates[pair + 1]);
            this.board.put(coordinates[pair + 1], new Draught(color));
        }
    }

    private Coordinate getBetweenDiagonalPiece(int pair, Coordinate... coordinates) {
        assert coordinates[pair].isOnDiagonal(coordinates[pair + 1]);
        List<Coordinate> betweenCoordinates = coordinates[pair].getBetweenDiagonalCoordinates(coordinates[pair + 1]);
        if (betweenCoordinates.isEmpty())
            return null;
        for (Coordinate coordinate : betweenCoordinates) {
            if (this.getPiece(coordinate) != null)
                return coordinate;
        }
        return null;
    }

    private Error isCorrectGlobalMove(Error error, List<Coordinate> removedCoordinates, Coordinate... coordinates) {
        if (error != null)
            return error;
        if (coordinates.length > 2 && coordinates.length > removedCoordinates.size() + 1)
            return Error.TOO_MUCH_JUMPS;
        return null;
    }

    private void unMovesUntilPair(List<Coordinate> removedCoordinates, int pair, Coordinate... coordinates) {
        for (int j = pair; j > 0; j--)
            this.board.move(coordinates[j], coordinates[j - 1]);
        for (Coordinate removedPiece : removedCoordinates)
            this.board.put(removedPiece, new Pawn(this.getOppositeTurnColor()));
    }

    public boolean isBlocked() {
        for (Coordinate coordinate : this.getCoordinatesWithActualColor())
            if (!this.isBlocked(coordinate))
                return false;
        return true;
    }

    private List<Coordinate> getCoordinatesWithActualColor() {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        for (int i = 0; i < this.getDimension(); i++) {
            for (int j = 0; j < this.getDimension(); j++) {
                Coordinate coordinate = new Coordinate(i, j);
                Piece piece = this.getPiece(coordinate);
                if (piece != null && piece.getColor() == this.getTurnColor())
                    coordinates.add(coordinate);
            }
        }
        return coordinates;
    }

    private int getNumberOfPiecesWithOppositeColor() {
        int numberOfPieces = 0;
        for (int i = 0; i < this.getDimension(); i++) {
            for (int j = 0; j < this.getDimension(); j++) {
                Coordinate coordinate = new Coordinate(i, j);
                Piece piece = this.getPiece(coordinate);
                if (piece != null && piece.getColor() == this.getOppositeTurnColor())
                    numberOfPieces++;
            }
        }
        return numberOfPieces;
    }

    private boolean isBlocked(Coordinate coordinate) {
        for (int i = 1; i <= 2; i++)
            for (Coordinate target : coordinate.getDiagonalCoordinates(i))
                if (this.isCorrectPairMove(0, coordinate, target) == null)
                    return false;
        return true;
    }

    public void cancel() {
        for (Coordinate coordinate : this.getCoordinatesWithActualColor())
            this.board.remove(coordinate);
        this.turn.change();
    }

    public Color getColor(Coordinate coordinate) {
        assert coordinate != null;
        return this.board.getColor(coordinate);
    }

    public Color getTurnColor() {
        return this.turn.getColor();
    }

    private Color getOppositeTurnColor() {
        return this.turn.getOppositeColor();
    }

    public Piece getPiece(Coordinate coordinate) {
        assert coordinate != null;
        return this.board.getPiece(coordinate);
    }

    public int getDimension() {
        return Coordinate.getDimension();
    }

    @Override
    public String toString() {
        return this.board + "\n" + this.turn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((turn == null) ? 0 : turn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Game other = (Game) obj;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        if (turn == null) {
            if (other.turn != null)
                return false;
        } else if (!turn.equals(other.turn))
            return false;
        return true;
    }

    public List<Coordinate> getCanJumpPieces() {
        List<Coordinate> piecesCoordinates = this.getCoordinatesWithActualColor();
        List<Coordinate> numberOfPieces = new ArrayList<Coordinate>();
        for (Coordinate coordinate : piecesCoordinates) {
            if (this.getTurnColor() == Color.WHITE || (getPiece(coordinate).color == this.getTurnColor() && getPiece(coordinate) instanceof Draught)) {
                boolean move1CanEat = false;
                boolean move2CanEat = false;
                if (coordinate.getRow() > 1) {
                    if (coordinate.getColumn() < 6) {
                        Coordinate[] coordinates1 = new Coordinate[]{coordinate, new Coordinate(coordinate.getRow() - 2, coordinate.getColumn() + 2)};
                        Error error = this.isCorrectPairMove(0, coordinates1);
                        if (error == null) {
                            move1CanEat = getBetweenDiagonalPiece(0, coordinates1) != null;
                        }
                    }
                    if (coordinate.getColumn() > 1) {
                        Coordinate[] coordinates2 = new Coordinate[]{coordinate, new Coordinate(coordinate.getRow() - 2, coordinate.getColumn() - 2)};
                        Error error = this.isCorrectPairMove(0, coordinates2);
                        if (error == null) {
                            move2CanEat = getBetweenDiagonalPiece(0, coordinates2) != null;
                        }
                    }
                    if (move1CanEat || move2CanEat) {
                        numberOfPieces.add(coordinate);
                    }
                }
            }
            if (this.getTurnColor() == Color.BLACK || (getPiece(coordinate).color == this.getTurnColor() && getPiece(coordinate) instanceof Draught)) {
                boolean move1CanEat = false;
                boolean move2CanEat = false;
                if (coordinate.getRow() < 6) {
                    if (coordinate.getColumn() < 6) {
                        Coordinate[] coordinates1 = new Coordinate[]{coordinate, new Coordinate(coordinate.getRow() + 2, coordinate.getColumn() + 2)};
                        Error error = this.isCorrectPairMove(0, coordinates1);
                        if (error == null) {
                            move1CanEat = getBetweenDiagonalPiece(0, coordinates1) != null;
                        }
                    }
                    if (coordinate.getColumn() > 1) {
                        Coordinate[] coordinates2 = new Coordinate[]{coordinate, new Coordinate(coordinate.getRow() + 2, coordinate.getColumn() - 2)};
                        Error error = this.isCorrectPairMove(0, coordinates2);
                        if (error == null) {
                            move2CanEat = getBetweenDiagonalPiece(0, coordinates2) != null;
                        }
                    }
                    if (move1CanEat || move2CanEat) {
                        numberOfPieces.add(coordinate);
                    }
                }
            }
        }
        return numberOfPieces;
    }

    private void isCanJumpPiecesMoving(List<Coordinate> canJumpPieces, int pair, Coordinate...coordinates){
        if(canJumpPieces.contains(coordinates[pair])){
            canJumpPieces.set(canJumpPieces.indexOf(coordinates[pair]), coordinates[pair +1]);
        }
    }

    private void removePieceIfNotJump(int numberOfPiecesWithOppositeColor, List<Coordinate> canJumpPieces){
        if(numberOfPiecesWithOppositeColor == this.getNumberOfPiecesWithOppositeColor() && !canJumpPieces.isEmpty()){
            this.board.remove(canJumpPieces.get(new Random(System.currentTimeMillis()).nextInt(canJumpPieces.size())));
        }
    }

}


