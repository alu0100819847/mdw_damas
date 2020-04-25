package es.urjccode.mastercloudapps.adcs.draughts.models;

import java.util.ArrayList;
import java.util.List;

public class Draught extends Piece {

    Draught(Color color) {
    super(color);
    }

    @Override
    Error isCorrectDiagonalMovement(int amountBetweenDiagonalPieces, int pair, Coordinate... coordinates) {
    if (amountBetweenDiagonalPieces > 1)
      return Error.TOO_MUCH_EATINGS;
    return null;
    }

    @Override
    public String getCode(){
        return super.getCode().toUpperCase();
    }

    @Override
    public List<Coordinate> possibleMovements(Coordinate coordinate) {
        ArrayList<Coordinate> piecePossibleMovements = new ArrayList<>();
        piecePossibleMovements.addAll(possibleBottomMovements(coordinate));
        piecePossibleMovements.addAll(possibleTopMovements(coordinate));
        return piecePossibleMovements;
    }
}
