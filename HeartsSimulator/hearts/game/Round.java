package hearts.game;

import java.util.ArrayList;

/**
 * Round object which stores all of the Tricks in a Round.
 * @author Willow Sapphire
 * @version 1.0
 */
public class Round implements Cloneable
{
    /**
     * The number of Tricks in a Round.
     */
    public static final int NUM_TRICKS_IN_ROUND = 13;
    /**
     * The number of HeartsPlayers.
     */
    public static final int NUM_PLAYERS = Game.NUM_PLAYERS;
    
    /**
     * An ArrayList containing all of the Tricks in this Round.
     */
    private ArrayList<Trick> tricks;
    /**
     * An array which contains the pointSpread in the Round.
     */
    private int[] pointSpread;
    /**
     * An array which contains the scores after the Round of each
     * HeartsPlayer.
     */
    private int[] scoresAfter;
    
    /**
     * No-Arg constructor which initializes all fields to empty.
     */
    public Round()
    {
        tricks = new ArrayList<Trick>(NUM_TRICKS_IN_ROUND);
        pointSpread = new int[NUM_PLAYERS];
        scoresAfter = new int[NUM_PLAYERS];
    }
    
    /**
     * Adds a Trick to the Round.
     * @param t the trick to add.
     */
    void addTrick(Trick t)
    {
        tricks.add(t);
    }
    
    /**
     * Accessor which retrieves the Tricks in the Round.
     * @return an ArrayList containing the Tricks
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Trick> getTricks()
    {
        return (ArrayList<Trick>) tricks.clone();
    }
    
    /**
     * Accessor which retrieves the number of Tricks in the Round.
     * @return the number of Tricks
     */
    public int getNumTricks()
    {
        return tricks.size();
    }
    
    /**
     * Accessor which retrieves a specific Trick.
     * @param index the index of the desired Trick
     * @return the Trick at that index
     */
    public Trick getTrick(int index)
    {
        return tricks.get(index);
    }
    
    /**
     * Accessor which retrieves a copy of the scores after the Round.
     * @return an array containing the scores
     */
    public int[] getScoresAfter()
    {
        int[] s = new int[this.scoresAfter.length];
        for (int i = 0; i < this.scoresAfter.length; i++)
        {
            s[i] = this.pointSpread[i];
        }
        return s;
    }
    
    /**
     * Accessor which retrieves the score of a specific HeartsPlayer.
     * @param player the index of the HeartsPlayer
     * @return the score
     */
    public int getScoreAfterForPlayer(int player)
    {
        return scoresAfter[player];
    }
    
    /**
     * Accessor which retrieves a copy of the point spread of the Round.
     * @return an array containing the point spread
     */
    public int[] getPointSpread()
    {
        int[] s = new int[this.pointSpread.length];
        for (int i = 0; i < this.pointSpread.length; i++)
        {
            s[i] = this.pointSpread[i];
        }
        return s;
    }
    
    /**
     * Accessor which retrieves the points for a specific HeartsPlayer.
     * @param player the index of the HeartsPlayer
     * @return the points
     */
    public int getPointsForPlayer(int player)
    {
        return pointSpread[player];
    }
    
    /**
     * Goes through the tricks Arraylist and calculates the points
     * each player received. This currently erase pointSpread and
     * replaces it.
     */
    void calcPointSpread()
    {
        for (int i = 0; i < pointSpread.length; i++)
        {
            pointSpread[i] = 0;
        }
        
        for (int i = 0; i < tricks.size(); i++)
        {
            Card[] cards = tricks.get(i).getCards();
            int points = 0;
            
            for (int j = 0; j < NUM_PLAYERS; j++)
            {
                points += cards[j].getPoints();
            }
            pointSpread[tricks.get(i).getPlayer()] += points;
        }
    }
    
    /**
     * Clone method which creates a deep copy of Round
     * including fields.
     * @return the cloned Round
     */
    public Round clone()
    {
        Round r = new Round();
        //Clone the tricks field
        r.tricks = new ArrayList<Trick>();
        for (Trick t : this.tricks)
        {
            r.tricks.add(t.clone());
        }
        //Clone the pointSpread field
        r.pointSpread = new int[this.pointSpread.length];
        for (int i = 0; i < this.pointSpread.length; i++)
        {
            r.pointSpread[i] = this.pointSpread[i];
        }
        //Clone the scoresAfter field
        r.scoresAfter = new int[this.scoresAfter.length];
        for (int i = 0; i < this.scoresAfter.length; i++)
        {
            r.scoresAfter[i] = this.scoresAfter[i];
        }
        
        return r;
    }
}
