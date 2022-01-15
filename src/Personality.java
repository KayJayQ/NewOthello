/**
 * @file: Abstract class of personality. Represents the entity
 * who play agianst the player.
 * A personality can be the player itself, an AI, and another
 * human from internet connection.
 * 
 * @author Kay Qiang
 * @author qiangkj@cmu.edu
 */
package src;

import java.util.ArrayList;

import src.GameLogic.Status;

public abstract class Personality {

    public static enum PersonalityClass{
        Self,
        AI,
        Internet
    }

    public static enum Operation{
        USER,
        COOD
    }
    
    protected PersonalityClass personality;

    public Personality(PersonalityClass personality){
        this.personality = personality;
    }

    abstract public Operation handleTurn(Status[][] chart);
    abstract public int[] OperationCood();

}
