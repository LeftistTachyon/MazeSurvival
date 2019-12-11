package com.github.leftisttachyon.mazesurvival.game;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * A class that contains all of the dots in this game
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
    
    static {
        AIs = Arrays.asList(Red.SINGLETON);
        USER = new Dot(Color.BLACK);
    }
    
    /**
     * The red dot AI
     */
    private static class Red extends AIDot {
        /**
         * The only Red to be created
         */
        private static final Red SINGLETON = new Red();
        
        /**
         * Creates a new Red instance.
         */
        private Red() {
            super(Color.RED);
        }

        @Override
        public void move() {
        }
    }
}
