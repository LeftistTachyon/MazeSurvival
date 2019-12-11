package com.github.leftisttachyon.mazesurvival.game;

import java.awt.Color;

/**
 * A dot that is controlled by an AI
 *
 * @author Jed Wang
 */
public abstract class AIDot extends Dot {

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
     * @param x the x-coordinate to use
     * @param y the y-coordinate to use
     * @param color the color of the AI dot
     */
    public AIDot(int x, int y, Color color) {
        super(x, y, color);
    }

    /**
     * Moves this dot.
     */
    public abstract void move();
}
