package com.github.leftisttachyon.mazesurvival.game;

import com.github.leftisttachyon.mazesurvival.maze.Cell;

import java.awt.*;

import static com.github.leftisttachyon.mazesurvival.maze.Cell.*;

/**
 * A dot that may be controlled by AIs or by the player.
 *
 * @author Jed Wang
 */
public class Dot {

    /**
     * The width of this Dot
     */
    public static final int WIDTH = 8;

    /**
     * The coordinates of this dot
     */
    protected int x, y;

    /**
     * The color of this dot
     */
    protected Color color;

    /**
     * Creates a new dot and places it at the default location of (0, 0).
     *
     * @param color the color of this dot
     */
    public Dot(Color color) {
        this(0, 0, color);
    }

    /**
     * Creates a new dot and places it at the given coordinates
     *
     * @param x     the x-coordinate to place this dot at
     * @param y     the y-coordinate to place this dot at
     * @param color the color of this dot
     */
    public Dot(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Paints this dot.
     *
     * @param g2D the Graphics2D object to use to paint the dot
     */
    public void paint(Graphics2D g2D) {
        g2D.setColor(color);
        int cX = x * Cell.WIDTH + Cell.WIDTH / 2,
                cY = y * Cell.WIDTH + Cell.WIDTH / 2;
        g2D.fillOval(cX - WIDTH / 2, cY - WIDTH / 2, WIDTH, WIDTH);
    }

    /**
     * Moves this dot by the given amounts in the x and y directions.
     *
     * @param dx the amount to move this dot in the x direction
     * @param dy the amount to move this dot in the y direction
     */
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Sets the x-coordinate of this dot
     *
     * @param x the x-coordinate to use
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of this dot
     *
     * @param y the y-coordinate to use
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the position of this dot
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the position of this dot
     *
     * @param p a Point object that contains the position to use for this dot
     */
    public void setPosition(Point p) {
        x = p.x;
        y = p.y;
    }

    /**
     * Returns the x-coordinate of this dot
     *
     * @return the x-coordinate of this dot
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this dot
     *
     * @return the y-coordinate of this dot
     */
    public int getY() {
        return y;
    }

    /**
     * Moves this dot one unit in the given direction
     *
     * @param direction the direction to move this dot
     */
    public void moveOne(int direction) {
        switch (direction) {
            case NORTH:
                y--;
                break;
            case EAST:
                x++;
                break;
            case SOUTH:
                y++;
                break;
            case WEST:
                x--;
                break;
        }
    }
}
