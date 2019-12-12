package com.github.leftisttachyon.mazesurvival.game;

import com.github.leftisttachyon.mazesurvival.maze.Cell;
import com.github.leftisttachyon.mazesurvival.maze.Maze;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

import static com.github.leftisttachyon.mazesurvival.maze.Cell.*;

/**
 * A class that contains all of the dots in this game.<br>
 * The AI of the dots are loosely based off of Pac-Man ghosts, of which the AIs
 * are explained
 * <a href="https://gameinternals.com/understanding-pac-man-ghost-behavior">here</a>.
 *
 * @author Jed Wang
 */
public class Dots {

    /**
     * All static methods
     */
    private Dots() {
    }

    /**
     * The list of AIs in this game
     */
    private static final List<AIDot> AIs;

    /**
     * The dot controlled by the user
     */
    private static final Dot USER;

    /**
     * The maze object to use for navigation.
     */
    private static Maze maze;

    static {
        AIs = Arrays.asList(
                Red.RED,
                Blue.BLUE,
                Pink.PINK,
                Orange.ORANGE);
        USER = new Dot(Color.BLACK);
    }

    /**
     * The red dot AI
     */
    private static class Red extends AIDot {

        /**
         * The only Red to be created
         */
        private static final Red RED = new Red();

        /**
         * Creates a new Red instance.
         */
        private Red() {
            super(Color.RED);
        }

        @Override
        public void move() {
            int direction = getNextMove(y, x, USER.y, USER.x);
            moveOne(direction);
        }
    }

    /**
     * The blue dot AI
     */
    private static class Blue extends AIDot {

        /**
         * The only Blue to be created
         */
        private static final Blue BLUE = new Blue();

        /**
         * Creates a new Blue instance.
         */
        private Blue() {
            super(Color.BLUE);
        }

        @Override
        public void move() {
        }
    }

    /**
     * The pink dot AI
     */
    private static class Pink extends AIDot {

        /**
         * The only Pink to be created
         */
        private static final Pink PINK = new Pink();

        /**
         * Creates a new Pink instance.
         */
        public Pink() {
            super(Color.PINK);
        }

        @Override
        public void move() {
        }
    }

    /**
     * The orange dot AI
     */
    private static class Orange extends AIDot {

        /**
         * The only Orange to be created
         */
        private static final Orange ORANGE = new Orange();

        /**
         * Creates a new Orange instance.
         */
        public Orange() {
            super(Color.ORANGE);
        }

        @Override
        public void move() {
            int direction, dist = Math.abs(x - USER.x) + Math.abs(y - USER.y);
            if(dist > 8) {
                direction = getNextMove(y, x, USER.y, USER.x);
            } else {
                direction = getNextMove(y, x, maze.getDimensions().height - 1, 0);
            }

            moveOne(direction);
        }
    }

    /**
     * Paints all the dots with the given Graphics2D object
     *
     * @param g2D the Graphics2D object to use
     */
    public static void paint(Graphics2D g2D) {
        for (AIDot dot : AIs) {
            dot.paint(g2D);
        }

        USER.paint(g2D);
    }

    /**
     * Moves the AI dots.
     */
    public static void moveAIs() {
        if (maze == null) {
            throw new IllegalStateException("maze is null!");
        }

        for (AIDot dot : AIs) {
            dot.move();
        }
    }

    /**
     * Returns the dot that is controlled by the user
     *
     * @return the dot that is controlled by the user
     */
    public static Dot getUserDot() {
        return USER;
    }

    /**
     * Sets the maze to use for navigation
     *
     * @param maze the maze to use for navigation
     */
    public static void setMaze(Maze maze) {
        Dots.maze = maze;
    }

    /**
     * Returns the optimal move for moving from the first given square to the
     * second one. This algorithm relies on breadth first search.
     *
     * @param fromR the starting row
     * @param fromC the starting column
     * @param toR   the ending row
     * @param toC   the ending column
     * @return the optimal next move
     */
    public static int getNextMove(int fromR, int fromC, int toR, int toC) {
        if (maze == null) {
            throw new IllegalStateException("maze is null!");
        }

        Dimension dim = maze.getDimensions();
        if (fromR < 0 || fromR >= dim.height) {
            throw new IllegalArgumentException("Invalid starting row: " + fromR);
        }
        if (fromC < 0 || fromC >= dim.width) {
            throw new IllegalArgumentException("Invalid starting column: " + fromC);
        }
        if (toR < 0 || toR >= dim.height) {
            throw new IllegalArgumentException("Invalid ending row: " + toR);
        }
        if (toC < 0 || toC >= dim.width) {
            throw new IllegalArgumentException("Invalid ending column: " + toC);
        }

        Cell c = maze.getCell(fromR, fromC);
        int nextMove = -1, minMoves = Integer.MAX_VALUE;
        if (fromR - 1 >= 0 && !c.getWall(NORTH)) {
            int temp = bfs(fromR - 1, fromC, toR, toC);
            if (temp < minMoves) {
                nextMove = NORTH;
                minMoves = temp;
            }
        }
        if (fromR + 1 < dim.height && !c.getWall(SOUTH)) {
            int temp = bfs(fromR + 1, fromC, toR, toC);
            if (temp < minMoves) {
                nextMove = SOUTH;
                minMoves = temp;
            }
        }
        if (fromC - 1 >= 0 && !c.getWall(WEST)) {
            int temp = bfs(fromR, fromC - 1, toR, toC);
            if (temp < minMoves) {
                nextMove = WEST;
                minMoves = temp;
            }
        }
        if (fromC + 1 < dim.width && !c.getWall(EAST)) {
            int temp = bfs(fromR, fromC + 1, toR, toC);
            if (temp < minMoves) {
                nextMove = EAST;
                // minMoves = temp;
            }
        }

        return nextMove;
    }

    /**
     * Performs a breadth-first search between the first given square to the
     * second one. Determines, then returns, the length of the shortest path
     * between the two squares.
     *
     * @param fromR the starting row
     * @param fromC the starting column
     * @param toR   the ending row
     * @param toC   the ending column
     * @return the length of the shortest path to get from the first given
     * square to the second one.
     */
    public static int bfs(int fromR, int fromC, int toR, int toC) {
        if (maze == null) {
            throw new IllegalStateException("maze is null!");
        }

        Dimension dim = maze.getDimensions();
        if (fromR < 0 || fromR >= dim.height) {
            throw new IllegalArgumentException("Invalid starting row: " + fromR);
        }
        if (fromC < 0 || fromC >= dim.width) {
            throw new IllegalArgumentException("Invalid starting column: " + fromC);
        }
        if (toR < 0 || toR >= dim.height) {
            throw new IllegalArgumentException("Invalid ending row: " + toR);
        }
        if (toC < 0 || toC >= dim.width) {
            throw new IllegalArgumentException("Invalid ending column: " + toC);
        }

        HashSet<Point> visited = new HashSet<>();
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{fromR, fromC, 0});
        while (!q.isEmpty() && (q.element()[0] != toR || q.element()[1] != toC)) {
            int[] cur = q.remove();
            Point point = new Point(cur[0], cur[1]);
            Cell c = maze.getCell(cur[0], cur[1]);
            if (visited.contains(point)) {
                continue;
            }

            if (cur[0] + 1 < dim.height && !c.getWall(SOUTH)) {
                q.add(new int[]{cur[0] + 1, cur[1], cur[2] + 1});
            }
            if (cur[0] - 1 >= 0 && !c.getWall(NORTH)) {
                q.add(new int[]{cur[0] - 1, cur[1], cur[2] + 1});
            }
            if (cur[1] + 1 < dim.width && !c.getWall(EAST)) {
                q.add(new int[]{cur[0], cur[1] + 1, cur[2] + 1});
            }
            if (cur[1] - 1 >= 0 && !c.getWall(WEST)) {
                q.add(new int[]{cur[0], cur[1] - 1, cur[2] + 1});
            }

            visited.add(point);
        }
        return q.element()[2];
    }

    /**
     * Determines whether the user-controlled dot is overlapping with any of the AI-controlled dots
     *
     * @return if the user-controlled dot is overlapping with an AI-controlled dots
     */
    public static boolean isOverlapping() {
        for (AIDot dot : AIs) {
            if (dot.x == USER.x && dot.y == USER.y) {
                return true;
            }
        }

        return false;
    }
}
