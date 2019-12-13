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
     * The last move of the user
     */
    private static int lastMove = 0;

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
            TreeMap<Integer, List<Integer>> bestMoves = getBestMoves(y, x, USER.y, USER.x);
            moveOne(bestMoves);
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
            Point plus2 = transform(new Point(USER.x, USER.y), lastMove, 2);
            int dy = plus2.y - y, dx = plus2.x - x;
            int _y = dy + plus2.y, _x = dx + plus2.x;
            if (_y < 0) {
                _y = 0;
            }
            if (_x < 0) {
                _x = 0;
            }

            Dimension dim = maze.getDimensions();

            // moveOne(getNextMove(y, x, _y % dim.height, _x % dim.width));
            TreeMap<Integer, List<Integer>> bestMoves = getBestMoves(y, x, _y % dim.height, _x % dim.width);
            moveOne(bestMoves);
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
            Point to = transformAndTrim(new Point(USER.x, USER.y), lastMove, 4);
            // moveOne(getNextMove(y, x, to.y, to.x));

            TreeMap<Integer, List<Integer>> bestMoves = getBestMoves(y, x, to.y, to.x);
            moveOne(bestMoves);
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
            int dist = Math.abs(x - USER.x) + Math.abs(y - USER.y);
            TreeMap<Integer, List<Integer>> bestMoves;
            if (dist > 8) {
                // direction = getNextMove(y, x, USER.y, USER.x);
                bestMoves = getBestMoves(y, x, USER.y, USER.x);
            } else {
                // direction = getNextMove(y, x, maze.getDimensions().height - 1, 0);
                bestMoves = getBestMoves(y, x, maze.getDimensions().height - 1, 0);
            }

            // moveOne(direction);
            moveOne(bestMoves);
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

        // System.out.println("Moving!");

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
     * Returns optimal moves for moving from the first given square to the
     * second one. This algorithm relies on breadth first search.
     *
     * @param fromR the starting row
     * @param fromC the starting column
     * @param toR   the ending row
     * @param toC   the ending column
     * @return a TreeMap of possible moves, in (weight, direction(s)) pairs
     */
    public static TreeMap<Integer, List<Integer>> getBestMoves(int fromR, int fromC, int toR, int toC) {
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
        TreeMap<Integer, List<Integer>> output = new TreeMap<>();
        if (fromR - 1 >= 0 && !c.getWall(NORTH)) {
            int temp = bfs(fromR - 1, fromC, toR, toC);
            ArrayList<Integer> list = new ArrayList<>();
            list.add(NORTH);
            output.put(temp, list);
        }
        if (fromR + 1 < dim.height && !c.getWall(SOUTH)) {
            int temp = bfs(fromR + 1, fromC, toR, toC);
            if (output.containsKey(temp)) {
                output.get(temp).add(SOUTH);
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(SOUTH);
                output.put(temp, list);
            }
        }
        if (fromC - 1 >= 0 && !c.getWall(WEST)) {
            int temp = bfs(fromR, fromC - 1, toR, toC);
            if (output.containsKey(temp)) {
                output.get(temp).add(WEST);
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(WEST);
                output.put(temp, list);
            }
        }
        if (fromC + 1 < dim.width && !c.getWall(EAST)) {
            int temp = bfs(fromR, fromC + 1, toR, toC);
            if (output.containsKey(temp)) {
                output.get(temp).add(EAST);
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(EAST);
                output.put(temp, list);
            }
        }

        return output;
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

    /**
     * Moves the user dot in the given direction one unit.
     *
     * @param direction the direction to move the user dot in
     */
    public static void moveUserDot(int direction) {
        USER.moveOne(direction);
        lastMove = direction;
    }

    /**
     * Transforms the given Point object the given amount of units in the given direction
     *
     * @param start     the starting Point
     * @param direction the direction to transform the Point
     * @param units     the amount to transform the Point
     * @return the newly transformed Point
     */
    public static Point transform(Point start, int direction, int units) {
        switch (direction) {
            case NORTH:
                return new Point(start.x, start.y - units);
            case EAST:
                return new Point(start.x + units, start.y);
            case SOUTH:
                return new Point(start.x, start.y + units);
            case WEST:
                return new Point(start.x - units, start.y);
            default:
                throw new IllegalArgumentException("Invalid parameter: direction=" + direction);
        }
    }

    /**
     * Transforms the given Point object the given amount of units in the given direction
     * and ensures that this point is within the bounds of the internally stored maze
     *
     * @param start     the starting Point
     * @param direction the direction to transform the Point
     * @param units     the amount to transform the Point
     * @return the newly transformed Point, guaranteed to be within the bounds of the
     * internally stored maze
     */
    public static Point transformAndTrim(Point start, int direction, int units) {
        if (maze == null) {
            throw new IllegalStateException("maze is null!");
        }

        int x, y;
        switch (direction) {
            case NORTH:
                x = start.x;
                y = start.y - units;
                break;
            case EAST:
                x = start.x + units;
                y = start.y;
                break;
            case SOUTH:
                x = start.x;
                y = start.y + units;
                break;
            case WEST:
                x = start.x - units;
                y = start.y;
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter: direction=" + direction);
        }

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }

        Dimension dim = maze.getDimensions();

        return new Point(x % dim.width, y % dim.height);
    }

    /**
     * Determines whether the given square on the maze is occupied by an AIDot.
     *
     * @param r the row of the square
     * @param c the column of the square
     * @return whether the given square is occupied by an AIDot
     */
    public static boolean isOccupied(int r, int c) {
        for (AIDot dot : AIs) {
            if (dot.x == c && dot.y == r) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the list of AIs
     *
     * @return the list of AIs
     */
    public static List<AIDot> getAIs() {
        return AIs;
    }
}
