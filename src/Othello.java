/**
 * @file: Entry class, initializes window frame
 * Entry class, intializes window frame
 * 
 * @author: Kay Qiang
 * @author: qiangkj@cmu.edu
 */
package src;

import javax.swing.*;
import src.Menu;

public class Othello{
    // version
    public static final String version = "v0.1";

    public static JFrame frame; // Public window frame
    // fixed window size
    static final int width = 800;
    static final int height = 600;

    public Othello() {
        frame = new JFrame("Othello");
        frame.setSize(800,600);
        frame.setResizable(false);
        frame.setTitle("Othello " + Othello.version);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void createMenu(){
        Menu menu = new Menu(Othello.frame);
    }
    public static void main(String[] args) {
        System.out.println("Othello Started");
        // Event handler
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                // load Text at first
                new Text().loadText(0);
                Othello game = new Othello();
                game.createMenu();
            }
        });
    }
}