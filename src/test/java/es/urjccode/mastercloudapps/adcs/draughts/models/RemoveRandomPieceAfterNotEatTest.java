package es.urjccode.mastercloudapps.adcs.draughts.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveRandomPieceAfterNotEatTest extends GameTest{

    @Test
    public void testRemoveAfterNotEatWhite(){
        this.setGame(Color.WHITE,
            "        ",
            "        ",
            "   n   n",
            "  b   n ",
            "     b  ",
            "b       ",
            "        ",
            "        ");
        this.setExpectedGame(Color.BLACK,
            "        ",
            "        ",
            "   n   n",
            "      n ",
            "     b  ",
            "b       ",
            "        ",
            "        ");
        this.game.move(new Coordinate(3,2), new Coordinate(2,1));
        assertEquals(this.expectedGame, this.game);
    }

    @Test
    public void testRemoveAfterNotEatBlack(){
        this.setGame(Color.BLACK,
            "        ",
            "        ",
            "   n   n",
            "  b b   ",
            "     b  ",
            "b       ",
            "        ",
            "        ");
        this.setExpectedGame(Color.WHITE,
            "        ",
            "        ",
            "        ",
            "  b b n ",
            "     b  ",
            "b       ",
            "        ",
            "        ");
        this.game.move(new Coordinate(2,7), new Coordinate(3,6));
        assertEquals(this.expectedGame, this.game);
    }

}
