package es.urjccode.mastercloudapps.adcs.draughts.models;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    private static char[] CHARACTERS = {'b', 'n'};
    private static final int MAX_DISTANCE = 2;

    Pawn(Color color) {
        super(color);
    }

    @Override
    Error isCorrectDiagonalMovement(int amountBetweenDiagonalPieces, int pair, Coordinate... coordinates) {
		if (!this.isAdvanced(coordinates[pair], coordinates[pair+1]))
			return Error.NOT_ADVANCED;
		int distance = coordinates[pair].getDiagonalDistance(coordinates[pair+1]);
		if (distance > Pawn.MAX_DISTANCE)
			return Error.TOO_MUCH_ADVANCED;
		if (distance == Pawn.MAX_DISTANCE && amountBetweenDiagonalPieces != 1)
			return Error.WITHOUT_EATING;
		return null;
    }

    protected char[] getCodes() {
		return Pawn.CHARACTERS;
	}

    @Override
    public List<Coordinate> possibleMovements(Coordinate coordinate){
        List<Coordinate> piecePossibleMovements = new ArrayList<>();
        if(this.color == Color.WHITE){
            piecePossibleMovements = possibleTopMovements(coordinate);
        }
        if(this.color == Color.BLACK){
            piecePossibleMovements = possibleBottomMovements(coordinate);
        }
        return piecePossibleMovements;
    }
}
