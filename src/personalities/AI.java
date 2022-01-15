/**
 * @file: AI personality, uses game tree and AB pruning
 * 
 * @author Kay Qiang
 * @author qiangkj@cmu.edu
 */
package src.personalities;
import src.GameLogic.Pair;
import src.GameLogic.Status;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import src.GameLogic;
import src.Personality;

public class AI extends Personality{

    public static enum Level{
        Easy,
        Median,
        Hard
    }
    private Status[][] chart = new Status[8][8];
    private int[][] weight = new int[][]{
        {10,-3,1,1,1,1,-3,10},
        {-3,-3,1,1,1,1,-3,-3},
        {1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1},
        {-3,-3,1,1,1,1,-3,-3},
        {10,-3,1,1,1,1,-3,10}
    };

    // Search related variables
    private int max_depth = 8; // max search depth

    public AI(PersonalityClass personality, Level level) {
        super(personality);
        switch (level) {
            case Easy:
            this.max_depth = 4;
            break;
            case Median:
            this.max_depth = 6;
            break;
            case Hard:
            this.max_depth = 8;
            break;
            default:
            break;
        }
    }

    private void arrCopy(Status[][] dest, Status[][] src) {
        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                dest[i][j] = src[i][j];
            }
        }
    }

    private int max(int a, int b){
        if(a >= b){
            return a;
        } else {
            return b;
        }
    }

    private int min(int a, int b){
        if(a <= b){
            return a;
        } else {
            return b;
        }
    }

    @Override
    public Operation handleTurn(Status[][] chart) {
        this.arrCopy(this.chart, chart);
        return Operation.COOD;
    }

    @Override
    public int[] OperationCood() {
        int[] res = new int[2];
        ArrayList<Pair<Integer, Integer>> avail = this.getAvailable(this.chart, Status.White);
        int best = -1;
        int depth_backup = this.max_depth;
        for(int i = 0; i < avail.size(); i++) {
            int val;
            Status[][] newChart = new Status[8][8];
            this.arrCopy(newChart, this.chart);
            newChart[avail.get(i).key()][avail.get(i).value()] = Status.White;
            Instant start = Instant.now();
            val = search(newChart, Status.Black, -2, 2, 0);
            Instant finish = Instant.now();
            long time = Duration.between(start, finish).toSeconds();
            if(time > 2) {
                this.max_depth--;
            }
            if(val > best) {
                best = val;
                res[0] = avail.get(i).key();
                res[1] = avail.get(i).value();
            }
        }
        this.max_depth = depth_backup;
        return res;
    }

    private int search(Status[][] chart, Status color, int alpha, int beta, int depth) {
        ArrayList<Pair<Integer, Integer>> avail = this.getAvailable(chart, color);
        int res;
        if(avail.size() == 0) {
            return evaluation(chart, color);
        }

        if(depth > this.max_depth) {
            return evaluation(chart, color);
        }

        if(color == Status.White) {
            res = -1000;
            for(int i=0; i<avail.size(); i++) {
                Status[][] newChart = new Status[8][8];
                this.arrCopy(newChart, chart);
                this.setPiece(newChart, color, avail.get(i).key(), avail.get(i).value());
                res = this.max(res, search(newChart, this.reverseColor(color), alpha, beta, depth + 1));
                if(res >= beta) break;
                alpha = this.max(alpha, res);
            }
            return res;
        } else {
            res = 1000;
            for(int i=0; i<avail.size(); i++) {
                Status[][] newChart = new Status[8][8];
                this.arrCopy(newChart, chart);
                this.setPiece(newChart, color, avail.get(i).key(), avail.get(i).value());
                res = this.min(res, search(newChart, this.reverseColor(color), alpha, beta, depth + 1));
                if(res <= alpha) break;
                beta = this.min(beta, res);
            }
            return res;
        }
    }

    /**
     * Get reverse color of given color
     * @param color
     * @return
     */
    private Status reverseColor(Status color) {
        if(color == Status.White){
            return Status.Black;
        } else {
            return Status.White;
        }
    }

    /**
     * Get status of one block at given position and chart
     * @param chart
     * @param x
     * @param y
     * @return
     */
    private Status getPiece(Status[][] chart, int x, int y) {
        if(x > 7 || x < 0 || y > 7 || y < 0){
            return Status.Invalid;
        }
        return chart[x][y];
    }

    private boolean flip(Status[][] chart, int x, int y, int offsetX, int offsetY,
    ArrayList<GameLogic.Pair<Integer, Integer>> list, Status selfColor, boolean execute) {
        Status oppositeColor;
        Status currentStatus = this.getPiece(chart, x, y);
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
            list.add(new Pair<Integer,Integer>(x,y));
            return this.flip(chart, x+offsetX, y+offsetY, offsetX, offsetY, list, selfColor, execute);
        }

        if(currentStatus == selfColor && execute) {
            for(int i = 0; i < list.size(); i++) {
                chart[list.get(i).key()][list.get(i).value()] = selfColor;
            }
        }

        if(list.size() > 0) {
            return true;
        }

        return false;
    }

    private boolean isAvailable(Status[][] chart, int x, int y, Status selfColor) {
        boolean mark = false;
        Status colorNeeded = this.reverseColor(selfColor);

        // 1. The block must be blank
        if(chart[x][y] != Status.Blank) return false;

        // 2. The block must be adjcent to a block with reverse piece
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                if(i == 0 && j == 0) continue;
                if(this.getPiece(chart, x + i, y + j) == colorNeeded) {
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
                if(this.flip(chart, x+i, y+j, i, j, new ArrayList<Pair<Integer, Integer>>(), selfColor, false)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Get all available position to given color
     * @param tempChart
     * @param color
     * @return
     */
    private ArrayList<GameLogic.Pair<Integer, Integer>> getAvailable(Status[][] tempChart, Status color) {
        ArrayList<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(this.isAvailable(tempChart, x, y, color)) {
                    res.add(new Pair<Integer, Integer>(x, y));
                }
            }
        }
        return res;
    }

    private void setPiece(Status[][] chart, Status color, int x, int y) {
        chart[x][y] = color;
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                if(i == 0 && j == 0) {
                    continue;
                }
                this.flip(chart, x+i, y+j, i, j, new ArrayList<Pair<Integer, Integer>>(), color, true);
            }
        }
    }

    /**
     * Naive evaluation function, simply counts the number 
     * of friendly pieces.
     * @param Status[][] temporay chart
     * @param color self color
     * @return
     */
    private int evaluation(Status[][] chart, Status color) {
        int count = 0;
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                if(chart[x][y] == color) {
                    count += this.weight[y][x];
                }
            }
        }
        return count;
    }
    
}
