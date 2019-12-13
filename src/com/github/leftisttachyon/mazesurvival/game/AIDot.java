package com.github.leftisttachyon.mazesurvival.game;

import com.github.leftisttachyon.mazesurvival.maze.Cell;

import java.awt.*;
import java.util.List;
import java.util.TreeMap;

/**
 * A dot that is controlled by an AI
 *
 * @author Jed Wang
 */
public abstract class AIDot extends Dot {

    /**
     * Stores the previous move of this AIDot
     */
    protected int avoidMove = -1;

    /**
     * Creates a new AI dot.
     *
     * @param color the color of the AI dot
     */
    public AIDot(Color color) {
        super(color);
    }

    /**
     * Creates a new AI dot and places it at the given coordinates.
     *
     * @param x     the x-coordinate to use
     * @param y     the y-coordinate to use
     * @param color the color of the AI dot
     */
    public AIDot(int x, int y, Color color) {
        super(x, y, color);
    }

    @Override
    public void moveOne(int direction) {
        super.moveOne(direction);
        avoidMove = Cell.opposite(direction);
        // System.out.println(getClass().getSimpleName() + " should avoid " + direction);
    }

    /**
     * Moves this AIDot in the best manner given the possible move candidates.
     *
     * @param candidates the candidates for moving this AIDot
     */
    public void moveOne(TreeMap<Integer, List<Integer>> candidates) {
        int firstNotOccupied = -1;
        for(int key : candidates.keySet()) {
            for(int dir : candidates.get(key)) {
                Point p = Dots.transform(new Point(x, y), dir, 1);
                if(!Dots.isOccupied(p.y, p.x)) {
                    firstNotOccupied = dir;
                    if(dir != avoidMove) {
                        moveOne(dir);
                        return;
                    }
                }
            }
        }

        if (firstNotOccupied != -1) {
            moveOne(firstNotOccupied);
            return;
        }
    }

    /**
     * Moves this dot one unit.
     */
    public abstract void move();
}
