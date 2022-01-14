/**
 * @file: Game page of othello, includes a chart and scoreboard
 * 
 * Personality:
 *  Self
 *  AI
 *  Internet
 * 
 * @author Kay Qiang
 * @author qiangkj@cmu.edu
 */
package src;

import java.awt.*;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;

import src.GameLogic.Color;
import src.GameLogic.Turn;
import src.Personality.PersonalityClass;
import src.personalities.Self;

public class Game {
    // GUI related
    JFrame frame;
    private JPanel panel;
    Menu menu;
    // Image resources
    ImageIcon chartIMG = new ImageIcon("src/image/chart.png");
    ImageIcon blankIMG = new ImageIcon("src/image/blank.png");
    ImageIcon whiteIMG = new ImageIcon("src/image/white.png");
    ImageIcon blackIMG = new ImageIcon("src/image/black.png");
    ImageIcon availIMG = new ImageIcon("src/image/available.png");
    // Logic Related
    GameLogic logic;
    boolean repaint = false;

    public Game(JFrame frame, Menu menu, PersonalityClass player, PersonalityClass match) {
        this.frame = frame;
        this.menu = menu;
        this.frame.setJMenuBar(null);

        switch(match){
            case Self:
            this.logic = new GameLogic(this, Color.Black, new Self(PersonalityClass.Self), new Self(PersonalityClass.Self));
            case AI:
            this.logic = new GameLogic(this, Color.Black, new Self(PersonalityClass.Self), new Self(PersonalityClass.AI));
            case Internet:
            this.logic = new GameLogic(this, Color.Black, new Self(PersonalityClass.Self), new Self(PersonalityClass.Internet));
        }

        this.createUI();
        this.refresh();
    }

    private void createUI() {
        this.panel = new JPanel(null);
        JButton quit = new JButton(Text.textLib.get(9));
        quit.setBounds(10, 10, 100, 50);
        this.panel.add(quit);

        quit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                backToMenu();
            }
        });

        this.drawChart();

        JLabel chartLabel = new JLabel();
        chartLabel.setIcon(chartIMG);
        chartLabel.setBounds(200, 100, 400, 400);
        this.panel.add(chartLabel);

        JLabel playerTurn = new JLabel((logic.getColor() == Color.Black) ? Text.textLib.get(17) : Text.textLib.get(18));
        playerTurn.setBounds(10,100,200,50);
        playerTurn.setFont(new Font("Dialog",1,25));
        this.panel.add(playerTurn);

        JLabel currentTurn = new JLabel((logic.getTurn() == Turn.Player) ? Text.textLib.get(19) : Text.textLib.get(20));
        currentTurn.setBounds(10,150,200,50);
        currentTurn.setFont(new Font("Dialog",1,15));
        this.panel.add(currentTurn);

        JLabel blackCount = new JLabel(Text.textLib.get(12) + ":" + String.valueOf(logic.getBlack()));
        blackCount.setBounds(10,200,200,50);
        blackCount.setFont(new Font("Dialog",1,15));
        this.panel.add(blackCount);

        JLabel whiteCount = new JLabel(Text.textLib.get(13) + ":" + String.valueOf(logic.getWhite()));
        whiteCount.setBounds(10,250,200,50);
        whiteCount.setFont(new Font("Dialog",1,15));
        this.panel.add(whiteCount);
    }

    private void drawChart() {
        GameLogic.Status[][] chart = logic.getChart();
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                final int cood_x = x;
                final int cood_y = y;
                switch(chart[x][y]){
                    case Black:
                        JLabel blk = new JLabel();
                        blk.setIcon(this.blackIMG);
                        blk.setBounds(200 + x*50, 100 + y*50, 50, 50);
                        this.panel.add(blk);
                        break;
                    case White:
                        JLabel wht = new JLabel();
                        wht.setIcon(this.whiteIMG);
                        wht.setBounds(200 + x*50, 100 + y*50, 50, 50);
                        this.panel.add(wht);
                        break;
                    case Avail:
                        repaint = false;
                        JLabel avl = new JLabel();
                        avl.setIcon(this.availIMG);
                        avl.setBounds(200 + x*50, 100 + y*50, 50, 50);
                        this.panel.add(avl);
                        avl.addMouseListener(new MouseAdapter(){
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                logic.clickHandler(cood_x, cood_y);
                            }
                        });
                        break;
                    case Blank:
                        // NOOP
                        break;
                }
            }
        }
        if(this.repaint) {
            this.refresh();
            this.repaint = false;
        }
    }

    public void gameEndPage() {
        JDialog log = new JDialog(frame, Text.textLib.get(14));
        log.setSize(200,150);
        log.setLayout(new FlowLayout());
        JLabel label1 = new JLabel(Text.textLib.get(12) + ":" + logic.getBlack());
        log.add(label1);
        JLabel label2 = new JLabel(Text.textLib.get(13) + ":" + logic.getWhite());
        log.add(label2);
        JLabel label3 = new JLabel((logic.getWhite() >= logic.getBlack()) ? 
            Text.textLib.get(15) : Text.textLib.get(16));
        log.add(label3);
        log.setVisible(true);
    }

    public void refresh(){
        if(this.panel != null) {
            this.frame.remove(this.panel);
            this.createUI();
        }
        this.frame.setContentPane(this.panel);
        this.frame.validate();
    }

    private void backToMenu(){
        this.menu.refresh();
    }

}
