package com.github.leftisttachyon.mazesurvival.maze;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.leftisttachyon.mazesurvival.maze.Cell.*;

/**
 * A class that represents a maze
 *
 * @author Jed Wang
 */
public final class Maze extends Component {

    /**
     * The matrix of cells that compose the maze
     */
    private Cell[][] maze;

    /**
     * The height of this maze
     */
    private final int height;

    /**
     * The width of this maze
     */
    private final int width;

    /**
     * Creates a new Maze and initializes it.
     *
     * @param height the height of the maze
     * @param width the width of the maze
     */
    public Maze(int height, int width) {
        this.height = height;
        this.width = width;

        setPreferredSize(new Dimension(width * Cell.WIDTH + 30, height * Cell.WIDTH + 40));

        generate();
    }

    /**
     * Finds and returns a list of all of the unvisited neighbors of the given
     * cell, represented by a number
     *
     * @param y the y-coordinate of the cell
     * @param x the x-coordinate of the cell
     * @return the list of all of the unvisited neighbors of the given cell,
     * represented by a number
     */
    public List<Integer> getUnvisitedNeighbors(int y, int x) {
        if (y < 0 || x < 0 || y >= height || x >= width) {
            throw new IllegalArgumentException("indexes out of bounds: " + y
                    + ", " + x);
        }
        ArrayList<Integer> output = new ArrayList<>();
        if (y + 1 < height && !maze[y + 1][x].isVisited()) {
            output.add(SOUTH);
        }
        if (y - 1 >= 0 && !maze[y - 1][x].isVisited()) {
            output.add(NORTH);
        }
        if (x + 1 < width && !maze[y][x + 1].isVisited()) {
            output.add(EAST);
        }
        if (x - 1 >= 0 && !maze[y][x - 1].isVisited()) {
            output.add(WEST);
        }
        return output;
    }

    /**
     * Generates a new maze inside this maze instance. Implements the Growing
     * Tree algorithm, which is described
     * <a href="http://weblog.jamisbuck.org/2011/1/27/maze-generation-growing-tree-algorithm">here</a>.
     */
    public void generate() {
        maze = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = new Cell(j, i);

                maze[i][j].setWall(NORTH, true);
                maze[i][j].setWall(EAST, true);
                maze[i][j].setWall(SOUTH, true);
                maze[i][j].setWall(WEST, true);
            }
        }

        Random r = new Random();
        ArrayList<Cell> cList = new ArrayList<>();
        Cell first = maze[r.nextInt(height)][r.nextInt(width)];
        first.setVisited(true);
        cList.add(first);
        while (!cList.isEmpty()) {
            // System.out.println("cList size: " + cList.size());

            int idx;
            //<editor-fold defaultstate="collapsed" desc="set idx">
            double rand = r.nextDouble();
            if (rand > 0.25) {
                idx = cList.size() - 1;
            } else if (rand > 0.05) {
                idx = cList.size() / 2;
            } else {
                idx = r.nextInt(cList.size());
            }
            //</editor-fold>
            Cell c = cList.get(idx);

            List<Integer> unvisited = getUnvisitedNeighbors(c.y, c.x);
            if (unvisited.isEmpty()) {
                cList.remove(idx);
                continue;
            }

            int neighborDir = unvisited.get(r.nextInt(unvisited.size()));
            Cell neighbor;
            switch (neighborDir) {
                case WEST:
                    neighbor = maze[c.y][c.x - 1];
                    break;
                case EAST:
                    neighbor = maze[c.y][c.x + 1];
                    break;
                case SOUTH:
                    neighbor = maze[c.y + 1][c.x];
                    break;
                case NORTH:
                    neighbor = maze[c.y - 1][c.x];
                    break;
                default:
                    throw new IllegalStateException("Unknown direction: " + neighborDir);
            }

            neighbor.setVisited(true);

            c.setWall(neighborDir, false);
            neighbor.setWall(Cell.opposite(neighborDir), false);

            cList.add(neighbor);
        } // done with initial maze generation

        final double braidingFactor = 0.6;
        for (Cell[] cells : maze) {
            for (Cell c : cells) {
                if (c.isDeadEnd() && r.nextDouble() < braidingFactor) {
                    List<Integer> canRemove = new ArrayList<>();
                    if (c.getWall(NORTH) && c.y != 0) {
                        canRemove.add(NORTH);
                    }
                    if (c.getWall(EAST) && c.x != width - 1) {
                        canRemove.add(EAST);
                    }
                    if (c.getWall(SOUTH) && c.y != height - 1) {
                        canRemove.add(SOUTH);
                    }
                    if (c.getWall(WEST) && c.x != 0) {
                        canRemove.add(WEST);
                    }
                    int toRemove = canRemove.get(r.nextInt(canRemove.size()));
                    c.setWall(toRemove, false);
                    switch (toRemove) {
                        case NORTH:
                            maze[c.y - 1][c.x].setWall(SOUTH, false);
                            break;
                        case EAST:
                            maze[c.y][c.x + 1].setWall(WEST, false);
                            break;
                        case SOUTH:
                            maze[c.y + 1][c.x].setWall(NORTH, false);
                            break;
                        case WEST:
                            maze[c.y][c.x - 1].setWall(EAST, false);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setStroke(new BasicStroke(3, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER));

        g2D.setColor(Color.BLACK);
        for (int i = 0, y = 0; i < height; i++, y += Cell.WIDTH) {
            for (int j = 0, x = 0; j < width; j++, x += Cell.WIDTH) {
                Cell c = maze[i][j];
                if (c.getWall(NORTH)) {
                    g2D.drawLine(x, y, x + Cell.WIDTH, y);
                }
                if (c.getWall(EAST)) {
                    g2D.drawLine(x + Cell.WIDTH, y, x + Cell.WIDTH,
                            y + Cell.WIDTH);
                }
                if (c.getWall(SOUTH)) {
                    g2D.drawLine(x, y + Cell.WIDTH, x + Cell.WIDTH,
                            y + Cell.WIDTH);
                }
                if (c.getWall(WEST)) {
                    g2D.drawLine(x, y, x, y + Cell.WIDTH);
                }
            }
        }
    }

    /**
     * Returns the cell at the given row and column
     *
     * @param r the row of the cell to get
     * @param c the column of the cell to get
     * @return the cell at the given row and column
     */
    public Cell getCell(int r, int c) {
        return maze[r][c];
    }

    /**
     * Returns the dimensions of this maze
     *
     * @return the dimensions of this maze
     */
    public Dimension getDimensions() {
        return new Dimension(width, height);
    }
}
