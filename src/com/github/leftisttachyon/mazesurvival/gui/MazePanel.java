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
                    default:
                        return;
                }

                Dots.moveAIs();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("Released " + e.getKeyCode());

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

        revalidate();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, getWidth(), getHeight());

        g2D.transform(AffineTransform.getTranslateInstance(15, 15));

        maze.paint(g2D);

        Dots.paint(g2D);
    }

    @Override
    public void run() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::repaint, 0, 16, TimeUnit.MILLISECONDS);
    }

}
