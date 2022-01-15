/**
 * @file: Menu class, includes menu page and menu bar
 * 
 * @author: Kay Qiang
 * @author: qiangkj@cmu.edu
 */
package src;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import src.Personality.PersonalityClass;
import src.personalities.AI.Level;

import java.awt.*;
import java.awt.event.*;

public class Menu {

    // GUI components, frame from main class
    private JFrame frame;
    private JPanel panel;
    private JMenuBar bar;
    private Menu THIS;
    // Title icon resource
    static ImageIcon titleIMG = new ImageIcon("src/image/title.png");
    // Game launch configs
    private boolean AI = false;
    public Level level = Level.Hard;

    public Menu(JFrame frame) {
        this.frame = frame;
        this.THIS = this;
        this.createUI();
        this.refresh();
    }

    private void createUI() {
        new Text();
        this.panel = new JPanel(null);

        this.bar = new JMenuBar();
        JMenu config = new JMenu(Text.textLib.get(2));
        JMenu view = new JMenu(Text.textLib.get(3));
        JMenu about = new JMenu(Text.textLib.get(4));
        JMenu lang = new JMenu(Text.textLib.get(5));
        this.bar.add(config);
        this.bar.add(view);
        this.bar.add(about);
        this.bar.add(lang);
        
        JMenuItem cn = new JMenuItem("中文");
        JMenuItem en = new JMenuItem("English");
        lang.add(cn);
        lang.add(en);

        cn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                new Text().loadText(0);
                refresh();
            }
        });

        en.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                new Text().loadText(1);
                refresh();
            }
        });

        JMenuItem aboutWindow = new JMenuItem(Text.textLib.get(4));
        about.add(aboutWindow);

        aboutWindow.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog log = new JDialog(frame, Text.textLib.get(4));
                log.setModal(true);
                log.setSize(400,200);
                JTextArea text = new JTextArea(Text.textLib.get(10));
                text.setSize(350,200);
                text.setLineWrap(true);
                text.setEditable(false);
                log.setLayout(new FlowLayout());
                log.add(text);
                log.setVisible(true);
            }
        });

        JMenuItem enableAI = new JCheckBoxMenuItem(Text.textLib.get(11));
        config.add(enableAI);

        enableAI.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                AI = enableAI.isSelected();
            }
        });

        JMenuItem AIEasy = new JMenuItem(Text.textLib.get(21));
        JMenuItem AIMedian = new JMenuItem(Text.textLib.get(22));
        JMenuItem AIHard = new JMenuItem(Text.textLib.get(23));
        config.add(AIEasy);
        config.add(AIMedian);
        config.add(AIHard);

        AIEasy.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                level = Level.Easy;
            }
        });

        AIMedian.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                level = Level.Median;
            }
        });

        AIHard.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                level = Level.Hard;
            }
        });
        
        JLabel label1 = new JLabel(Text.textLib.get(1));
        label1.setIcon(Menu.titleIMG);
        label1.setBounds(300,25,200,300);
        label1.setFont(new Font("Dialog", 1, 25));
        label1.setVerticalTextPosition(JLabel.BOTTOM);
        label1.setHorizontalTextPosition(JLabel.CENTER);
        this.panel.add(label1);

        JButton singlePlayer = new JButton(Text.textLib.get(6));
        singlePlayer.setBounds(350, 350, 100, 50);
        this.panel.add(singlePlayer);

        singlePlayer.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.validate();
                if(AI)
                    new Game(frame, THIS, PersonalityClass.Self, PersonalityClass.AI);
                else
                    new Game(frame, THIS, PersonalityClass.Self, PersonalityClass.Self);
            }
        });

        JButton multiplayer = new JButton(Text.textLib.get(7));
        multiplayer.setBounds(350, 410, 100, 50);
        this.panel.add(multiplayer);

        JButton exit = new JButton(Text.textLib.get(8));
        exit.setBounds(350, 470, 100, 50);
        this.panel.add(exit);

        exit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public void refresh() {
        if(this.panel != null){
            this.frame.remove(this.panel);
            this.createUI();
        }
        this.frame.setContentPane(this.panel);
        this.frame.setJMenuBar(this.bar);
        this.frame.validate();
    }
}
