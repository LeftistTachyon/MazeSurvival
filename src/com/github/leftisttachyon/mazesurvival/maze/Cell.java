package com.github.leftisttachyon.mazesurvival.maze;

import java.util.Arrays;

/**
 * A class that represents a cell in the maze
 *
 * @author Jed Wang
 */
public final class Cell {

    /**
     * An integer that represents north
     */
    public static final int NORTH = 0;

    /**
     * An integer that represents east
     */
    public static final int EAST = 1;

    /**
     * An integer that represents south
     */
    public static final int SOUTH = 2;

    /**
     * An integer that represents west
     */
    public static final int WEST = 3;

    /**
     * The width of a cell when drawn graphically
     */
    public static final int WIDTH = 20;

    /**
     * An array of booleans that represent the walls
     */
    private final boolean[] walls;

    /**
     * A boolean that stores whether this cell has been visited or not
     */
    private boolean visited;

    /**
     * The coordinates of this cell.
     */
    public final int x, y;

    /**
     * Creates a new Cell
     *
     * @param x the x-coordinate of this cell
     * @param y the y-coordinate of this cell
     */
    public Cell(int x, int y) {
        this.walls = new boolean[4];
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the given wall to the given solidity
     *
     * @param wall the wall to set
     * @param solid whether the wall should be solid
     */
    public void setWall(int wall, boolean solid) {
        walls[wall] = solid;
    }

    /**
     * Returns the solidity of the given wall
     *
     * @param wall the wall to get
     * @return the solidity of the given wall
     */
    public boolean getWall(int wall) {
        return walls[wall];
    }

    /**
     * Sets the visited state of this cell
     *
     * @param visited the state to set the visited state of this cell to
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Returns whether this cell has been visited or not
     *
     * @return whether this cell has been visited or not
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Returns the opposite direction of the given one
     *
     * @param direction the direction to flip
     * @return the opposite direction of the given one
     */
    public static int opposite(int direction) {
        return (direction + 2) % 4;
    }

    /**
     * Determines whether this Cell is a dead end, which means that this Cell
     * has three or more solid walls.
     *
     * @return whether this Cell is a dead end
     */
    public boolean isDeadEnd() {
        int solids = 0;
        for (boolean solid : walls) {
            if (solid) {
                solids++;
            }
        }
        return solids >= 3;
    }

    @Override
    public String toString() {
        return "[Cell x=" + x + " y=" + y + " walls="
                + Arrays.toString(walls) + "]";
    }
}
