package com.github.leftisttachyon.mazesurvival.gui;

import com.github.leftisttachyon.mazesurvival.game.AIDot;
import com.github.leftisttachyon.mazesurvival.game.Dot;
import com.github.leftisttachyon.mazesurvival.game.Dots;
import com.github.leftisttachyon.mazesurvival.maze.Cell;
import com.github.leftisttachyon.mazesurvival.maze.Maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.awt.event.KeyEvent.*;

/**
 * The class that controls the graphics of this application
 *
 * @author Jed Wang
 */
public final class MazePanel extends JPanel implements Runnable {

    /**
     * The internal Maze object
     */
    private Maze maze;

    /**
     * Stores whether the arrow keys are pressed
     */
    private boolean[] pressed;

    /**
     * Stores the time survived
     */
    private int[] times;

    /**
     * Stores whether the game is over or not
     */
    private boolean gameOver = false;

    /**
     * A frame counter
     */
    private int frameCnt = -1;

    /**
     * The internal ScheduledExecutorService that controls timing
     */
    private ScheduledExecutorService service;

    /**
     * The delay for the AIs to move
     */
    private int moveDelay = 500;

    /**
     * The delay for change for AIs
     */
    private int moveDelayDelay = 0;

    /**
     * Creates a new MazePanel.
     */
    public MazePanel() {
        maze = new Maze(15, 15);
        add(maze);

        Dots.setMaze(maze);
        setDotPositions();

        // 0=UP, 1=RIGHT, 2=DOWN, 3=LEFT
        pressed = new boolean[4];

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // System.out.println("Pressed " + e.getKeyCode());
                if (gameOver) {
                    if (frameCnt >= 400) {
                        restart();
                    }
                    return;
                }

                Dot user = Dots.getUserDot();
                Cell c = maze.getCell(user.getY(), user.getX());

                switch (e.getKeyCode()) {
                    case VK_UP:
                        if (!pressed[0] && !c.getWall(Cell.NORTH)) {
                            Dots.moveUserDot(Cell.NORTH);
                            pressed[0] = true;
                        } else {
                            return;
                        }
                        break;
                    case VK_RIGHT:
                        if (!pressed[1] && !c.getWall(Cell.EAST)) {
                            Dots.moveUserDot(Cell.EAST);
                            pressed[1] = true;
                        } else {
                            return;
                        }
                        break;
                    case VK_DOWN:
                        if (!pressed[2] && !c.getWall(Cell.SOUTH)) {
                            Dots.moveUserDot(Cell.SOUTH);
                            pressed[2] = true;
                        } else {
                            return;
                        }
                        break;
                    case VK_LEFT:
                        if (!pressed[3] && !c.getWall(Cell.WEST)) {
                            Dots.moveUserDot(Cell.WEST);
                            pressed[3] = true;
                        } else {
                            return;
                        }
                        break;
                }

                if (Dots.isOverlapping()) {
                    gameOver = true;
                    service.shutdownNow();

                    service = Executors.newSingleThreadScheduledExecutor();
                    service.scheduleAtFixedRate(() -> {
                        try {
                            repaint();
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    }, 0, 16, TimeUnit.MILLISECONDS);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("Released " + e.getKeyCode());

                if (gameOver) {
                    return;
                }

                switch (e.getKeyCode()) {
                    case VK_UP:
                        pressed[0] = false;
                        break;
                    case VK_RIGHT:
                        pressed[1] = false;
                        break;
                    case VK_DOWN:
                        pressed[2] = false;
                        break;
                    case VK_LEFT:
                        pressed[3] = false;
                        break;
                }
            }
        });

        times = new int[4];

        revalidate();
    }

    /**
     * Resets this maze panel to its original state and starts a game anew.
     */
    private void restart() {
        gameOver = false;
        frameCnt = -1;
        moveDelay = 500;
        moveDelayDelay = 0;

        maze = new Maze(15, 15);

        Dots.setMaze(maze);
        setDotPositions();

        times = new int[4];

        revalidate();

        service.shutdown();

        run();
    }

    @Override
    public void paint(Graphics g) {
        // System.out.println("Painting");

        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, getWidth(), getHeight());

        g2D.setFont(new Font("Consolas", Font.PLAIN, 15));
        if (frameCnt < 300) {
            g2D.setColor(Color.BLACK);
            g2D.drawString(String.format("%02d:%02d:%02d.%02d", times[0], times[1], times[2], times[3]), 20, 20);
        }

        AffineTransform transform = AffineTransform.getTranslateInstance(20, 30);
        g2D.transform(transform);

        maze.paint(g2D);

        Dots.paint(g2D);

        if (gameOver) {
            if (frameCnt < 400) {
                frameCnt++;
            }

            try {
                g2D.transform(transform.createInverse());
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
            }

            Dimension dim = maze.getDimensions();
            int totalWidth = getWidth(), totalHeight = getHeight();

            g2D.setColor(new Color(255, 255, 255, frameCnt >= 255 ? 255 : frameCnt));
            g2D.fillRect(-3, -3, totalWidth + 6, totalHeight + 6);

            if (frameCnt >= 300) {
                g2D.setColor(Color.BLACK);

                g2D.setFont(new Font("Consolas", Font.PLAIN, 30));
                FontMetrics metrics = g2D.getFontMetrics();
                String s = String.format("%02d:%02d:%02d.%02d", times[0], times[1], times[2], times[3]);
                int width = metrics.stringWidth(s);
                int bottom = (totalHeight + metrics.getHeight()) / 2, top = bottom - metrics.getHeight();

                g2D.drawString(s, (totalWidth - width) / 2, bottom);

                g2D.setFont(new Font("Consolas", Font.PLAIN, 15));
                metrics = g2D.getFontMetrics();
                s = "Your time:";
                width = metrics.stringWidth(s);

                g2D.drawString(s, (totalWidth - width) / 2, top);

                if (frameCnt >= 400) {
                    g2D.setFont(new Font("Consolas", Font.PLAIN, 14));
                    metrics = g2D.getFontMetrics();
                    s = "Press any key to retry";
                    width = metrics.stringWidth(s);

                    g2D.drawString(s, (totalWidth - width) / 2, bottom + metrics.getHeight() + 15);
                }
            }
        }
    }

    /**
     * Sets the positions of the dots
     */
    private void setDotPositions() {
        Random r = new Random();
        Point aiTopLeft;
        Dimension dim = maze.getDimensions();
        switch (r.nextInt(4)) {
            case 0: // AIs in top left, I'm in bottom right
                Dots.getUserDot().setPosition(dim.width - 1, dim.height - 1);
                aiTopLeft = new Point(0, 0);
                break;
            case 1: // AIs in top right, I'm in bottom left
                Dots.getUserDot().setPosition(0, dim.height - 1);
                aiTopLeft = new Point(dim.width - 2, 0);
                break;
            case 2: // AIs in bottom right, I'm in top left
                Dots.getUserDot().setPosition(0, 0);
                aiTopLeft = new Point(dim.width - 2, dim.height - 2);
                break;
            case 3: // AIs in bottom left, I'm in top right
                Dots.getUserDot().setPosition(dim.width - 1, 0);
                aiTopLeft = new Point(0, dim.height - 2);
                break;
            default:
                throw new IllegalStateException("java.util.Random gave an unexpected value");
        }

        java.util.List<Point> aiPos = Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1));
        Collections.shuffle(aiPos);
        List<AIDot> ais = Dots.getAIs();
        for (int i = 0; i < ais.size(); i++) {
            Point p = aiPos.get(i);
            ais.get(i).setPosition(aiTopLeft.x + p.x, aiTopLeft.y + p.y);
        }
    }

    @Override
    public void run() {
        service = Executors.newScheduledThreadPool(3);
        // repaint
        service.scheduleAtFixedRate(() -> {
            try {
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 16, TimeUnit.MILLISECONDS);

        // move things
        service.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Dots.moveAIs();

                    if (Dots.isOverlapping()) {
                        gameOver = true;
                        service.shutdownNow();

                        service = Executors.newSingleThreadScheduledExecutor();
                        service.scheduleAtFixedRate(() -> {
                            try {
                                repaint();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 0, 16, TimeUnit.MILLISECONDS);
                    }

                    if (!gameOver) service.schedule(this, moveDelay, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, moveDelay, TimeUnit.MILLISECONDS);

        // update
        service.scheduleAtFixedRate(() -> {
            try {
                times[3]++;
                if (times[3] >= 100) {
                    times[3] -= 100;
                    times[2]++;
                }
                if (times[2] >= 60) {
                    times[2] -= 60;
                    times[1]++;
                }
                if (times[1] >= 60) {
                    times[1] -= 60;
                    times[0]++;
                }

                // System.out.println(moveDelay + " " + moveDelayDelay);

                moveDelayDelay++;
                if (moveDelayDelay >= 1000) {
                    moveDelay -= 25;
                    moveDelayDelay = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

}
