package es.urjccode.mastercloudapps.adcs.draughts.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveRandomPieceAfterNotEatTest {

    @Test
    public void testCountCanJumpPieces(){
        Game game = new GameBuilder().rows(
            "        ",
            "        ",
            "   n    ",
            "  b b   ",
            "     b  ",
            "b       ",
            "        ",
            "        ").build();
        assertEquals(game.getCanJumpPieces().size(), 2);
    }

    @Test
    public void testRemoveAfterNotEat(){
        Game game = new GameBuilder().rows(
            "        ",
            "        ",
            "   n   n",
            "  b b n ",
            "     b  ",
            "b       ",
            "        ",
            "        ").build();

        game.move(new Coordinate(4,2), new Coordinate(3,1));
        assertEquals(game.getCanJumpPieces().size(), 1);
    }

}
