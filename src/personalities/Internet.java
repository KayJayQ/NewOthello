/**
 * @file: Internet personality
 * 
 * @author Kay Qiang
 * @author qiangkj@cmu.edu
 */
package src.personalities;

import src.GameLogic.Status;
import src.Connection;
import src.Personality;

public class Internet extends Personality {


    public Internet(PersonalityClass personality) {
        super(personality);
        this.personality = PersonalityClass.Internet;
    }

    @Override
    public Operation handleTurn(Status[][] chart) {
        try{
            Thread.sleep(200);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return Operation.USER;
    }

    @Override
    public int[] OperationCood() {
        String buffer = Connection.getMessage(3);
        int[] res = new int[2];
        res[0] = Integer.valueOf(buffer.split(" ")[1]);
        res[1] = Integer.valueOf(buffer.split(" ")[2]);
        try{
            Thread.sleep(200);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }
    
}
