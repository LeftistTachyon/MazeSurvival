package com.github.leftisttachyon.mazesurvival.gui;

import com.github.leftisttachyon.mazesurvival.game.Dots;
import com.github.leftisttachyon.mazesurvival.maze.Maze;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;

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
     * Creates a new MazePanel.
     */
    public MazePanel() {
        maze = new Maze(15, 15);
        add(maze);
        
        revalidate();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, getWidth(), getHeight());
        
        g2D.transform(AffineTransform.getTranslateInstance(10, 10));
        
        maze.paint(g2D);
    }

    @Override
    public void run() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::repaint, 0, 16, TimeUnit.MILLISECONDS);
        
        Dots.setMaze(maze);
        System.out.println(Dots.bfs(0, 0, 14, 14));
    }

}
