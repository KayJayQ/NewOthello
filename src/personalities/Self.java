/**
 * @file: Player personality
 * 
 * @author Kay Qiang
 * @author qiangkj@cmu.edu
 */
package src.personalities;

import java.util.ArrayList;

import src.GameLogic.Pair;
import src.GameLogic.Status;
import src.Personality;

public class Self extends Personality {

    public Self(PersonalityClass personality) {
        super(personality);
    }

    @Override
    public Operation handleTurn(Status[][] chart) {
        return Operation.USER;
    }

    @Override
    public int[] OperationCood() {
        int array[] = new int[2];
        array[0] = 0;
        array[1] = 0;
        return array;
    }

    
    
}
