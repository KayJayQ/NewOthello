/**
 * @file: Logic class of a game
 * 
 * @author Kay Qiang
 * @author qiangkj@cmu.edu
 */
package src;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import src.Personality.Operation;
import src.Personality.PersonalityClass;

public class GameLogic {

    public static class Pair<K, V> {
        private K k;
        private V v;
    
        public Pair(K k, V v) {
            this.k = k;
            this.v = v;
        }
    
        public K key() {
            return k;
        }
    
        public V value() {
            return v;
        }
    }

    public static enum Status{
        Blank,
        White,
        Black,
        Avail,
        Invalid
    }

    public static enum Turn{
        Player,
        Match
    }

    public static enum Color{
        Black,
        White
    }

    private Status[][] chart = new Status[8][8];
    private Turn turn = Turn.Player;
    private Color color;

    private Game game;

    private Personality playerRole;
    private Personality matchRole;

    boolean canPlayerPlace = true;
    boolean canMatchPlace = true;

    public GameLogic(Game game, Color color, Personality playerRole, Personality matchRole){
        this.color = color;
        this.game = game;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++){
                chart[i][j] = Status.Blank;
            }
        }
        chart[3][3] = Status.White;
        chart[3][4] = Status.Black;
        chart[4][3] = Status.Black;
        chart[4][4] = Status.White;

        this.playerRole = playerRole;
        this.matchRole = matchRole;
        assert this.playerRole.personality == PersonalityClass.Self;
    }

    private Status getPiece(int x, int y) {
        if(x > 7 || x < 0 || y > 7 || y < 0){
            return Status.Invalid;
        }
        return this.chart[x][y];
    }

    public Status[][] getChart() {
        this.clearAvailable();
        if(this.turn == Turn.Player) {
            playerRole.handleTurn(this.chart);
            if(this.markAvailable() == 0) {
                this.canPlayerPlace = false;
                this.skipTurn();
                this.game.repaint = true; 
            } else {
                this.canPlayerPlace = true;
            }
        } else {
            Operation result = matchRole.handleTurn(this.chart);
            if(result == Operation.USER){
                if(this.markAvailable() == 0) {
                    this.canMatchPlace = false;
                    this.skipTurn();
                    this.game.repaint = true;
                } else {
                    this.canMatchPlace = true;
                }
            } else {
                ArrayList<Pair<Integer, Integer>> avail = this.getAvailable();
                if(avail.size() == 0) {
                    this.canMatchPlace = false;
                    this.skipTurn();
                    this.game.repaint = true;
                } else {
                    this.canMatchPlace = true;
                    SwingWorker<Void, Void> matchTurn = 
                    new SwingWorker<Void, Void>() {
                        @Override
                        public Void doInBackground() {
                            int[] res = matchRole.OperationCood();
                            while(game.mutex != 0) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            clickHandler(res[0], res[1]);
                            return null;
                        }
                    };
                    matchTurn.execute();
                }
            }
        }

        if(!this.canPlayerPlace && !this.canMatchPlace) {
            this.gameEnd();
        }
        return this.chart;
    }

    public void gameEnd() {
        this.game.repaint = false;
        this.game.gameEndPage();
    }

    public Turn getTurn() {
        return this.turn;
    }

    public int getBlack() {
        int count = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++){
                if(this.chart[i][j] == Status.Black) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getWhite() {
        int count = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++){
                if(this.chart[i][j] == Status.White) {
                    count++;
                }
            }
        }
        return count;
    }

    public Color getColor() {
        return this.color;
    }

    public void clickHandler(int x, int y) {
        this.setPiece(x, y);
        this.skipTurn();
        game.refresh();
    }

    private void skipTurn() {
        if(this.turn == Turn.Player){
            this.turn = Turn.Match;
        } else {
            this.turn = Turn.Player;
        }
    }

    public int markAvailable() {
        int count = 0;
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(this.isAvailable(x, y)) {
                    count++;
                    this.chart[x][y] = Status.Avail;
                }
            }
        }
        return count;
    }

    private ArrayList<Pair<Integer, Integer>> getAvailable() {
        ArrayList<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(this.isAvailable(x, y)) {
                    res.add(new Pair<Integer, Integer>(x, y));
                }
            }
        }
        return res;
    }

    public void clearAvailable() {
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(this.chart[i][j] == Status.Avail){
                    this.chart[i][j] = Status.Blank;
                }
            }
        }
    }

    private Status currentColor() {
        Status setColor;
        if(this.turn == Turn.Player) {
            if(this.color == Color.Black){
                setColor = Status.Black;
            } else {
                setColor = Status.White;
            }
        } else {
            if(this.color == Color.Black){
                setColor = Status.White;
            } else {
                setColor = Status.Black;
            }
        }
        return setColor;
    }

    public void setPiece(int x, int y) {
        this.chart[x][y] = this.currentColor();
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                if(i == 0 && j == 0) {
                    continue;
                }
                this.flip(x+i, y+j, i, j, new ArrayList<Integer>(), new ArrayList<Integer>(), true);
            }
        }
    }

    private boolean flip(int x, int y, int offsetX, int offsetY,
        ArrayList<Integer> xList, ArrayList<Integer> yList, boolean execute) {
        Status selfColor = this.currentColor();
        Status oppositeColor;
        Status currentStatus = this.getPiece(x, y);
        if(selfColor == Status.Black) {
            oppositeColor = Status.White;
        } else {
            oppositeColor = Status.Black;
        }

        switch(currentStatus) {
            case Blank:
            return false;
            case Invalid:
            return false;
            case Avail:
            return false;
            case White:
            break;
            case Black:
            break;
        }

        if(currentStatus == oppositeColor) {
            xList.add(x);
            yList.add(y);
            return this.flip(x+offsetX, y+offsetY, offsetX, offsetY, xList, yList, execute);
        }

        if(currentStatus == selfColor && execute) {
            for(int i = 0; i < xList.size(); i++) {
                this.chart[xList.get(i)][yList.get(i)] = selfColor;
            }
        }

        if(xList.size() > 0) {
            return true;
        }

        return false;
    }

    private boolean isAvailable(int x, int y) {
        boolean mark = false;
        Status colorNeeded = 
        (this.currentColor() == Status.Black) ? Status.White : Status.Black;

        // 1. The block must be blank
        if(this.chart[x][y] != Status.Blank) return false;

        // 2. The block must be adjcent to a block with reverse piece
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                if(i == 0 && j == 0) continue;
                if(this.getPiece(x + i, y + j) == colorNeeded) {
                    mark = true;
                }
            }
        }
        if(!mark) return false;

        // step 1 and 2 are designed to reduce unnecessary recursion

        // 3. The block must can flip at least one piece after being placed 
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                if(i == 0 && j == 0) {
                    continue;
                }
                if(this.flip(x+i, y+j, i, j, new ArrayList<Integer>(), new ArrayList<Integer>(), false)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
}
