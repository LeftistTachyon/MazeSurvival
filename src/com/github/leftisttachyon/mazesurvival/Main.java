package com.github.leftisttachyon.mazesurvival;

import com.github.leftisttachyon.mazesurvival.gui.MazePanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * The main class
 *
 * @author Jed Wang
 */
public class Main {

    /**
     * The main method
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Maze Survival");
            MazePanel panel = new MazePanel();
            frame.add(panel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            frame.setVisible(true);
            new Thread(panel).start();
        });
    }
}
