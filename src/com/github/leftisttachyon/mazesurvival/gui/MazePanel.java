package com.github.leftisttachyon.mazesurvival.gui;

import com.github.leftisttachyon.mazesurvival.game.Dot;
import com.github.leftisttachyon.mazesurvival.game.Dots;
import com.github.leftisttachyon.mazesurvival.maze.Cell;
import com.github.leftisttachyon.mazesurvival.maze.Maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
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
     * Creates a new MazePanel.
     */
    public MazePanel() {
        maze = new Maze(15, 15);
        add(maze);

        Dots.setMaze(maze);

        Dots.getUserDot().setPosition(14, 14);

        // 0=UP, 1=RIGHT, 2=DOWN, 3=LEFT
        pressed = new boolean[4];

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // System.out.println("Pressed " + e.getKeyCode());
                if (gameOver) {
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
                    service.shutdown();

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

        g2D.transform(AffineTransform.getTranslateInstance(20, 30));

        maze.paint(g2D);

        Dots.paint(g2D);

        if (gameOver) {
            if (frameCnt < 300) {
                frameCnt++;
            }

            Dimension dim = maze.getDimensions();
            int totalWidth = dim.width * Cell.WIDTH, totalHeight = dim.height * Cell.WIDTH;

            g2D.setColor(new Color(255, 255, 255, frameCnt >= 255 ? 255 : frameCnt));
            g2D.fillRect(-3, -3, totalWidth + 6, totalHeight + 6);

            if(frameCnt == 300) {
                g2D.setColor(Color.BLACK);

                FontMetrics metrics = g2D.getFontMetrics();
                String s = "Your time:";
                int width = metrics.stringWidth(s);
                g2D.drawString(s, (totalWidth - width) / 2, totalHeight / 2 - 5);

                g2D.setFont(new Font("Consolas", Font.PLAIN, 30));
                metrics = g2D.getFontMetrics();
                s = String.format("%02d:%02d:%02d.%02d", times[0], times[1], times[2], times[3]);
                width = metrics.stringWidth(s);
                g2D.drawString(s, (totalWidth - width) / 2, totalHeight / 2 + metrics.getHeight());
            }
        }
    }

    @Override
    public void run() {
        service = Executors.newScheduledThreadPool(3);
        service.scheduleAtFixedRate(() -> {
            try {
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 16, TimeUnit.MILLISECONDS);
        service.scheduleAtFixedRate(() -> {
            try {
                Dots.moveAIs();

                if (Dots.isOverlapping()) {
                    gameOver = true;
                    service.shutdown();

                    service = Executors.newSingleThreadScheduledExecutor();
                    service.scheduleAtFixedRate(() -> {
                        try {
                            repaint();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 0, 16, TimeUnit.MILLISECONDS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

}
