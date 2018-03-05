package hearts.game;

import java.util.ArrayList;

/**
 * GameState object which is constantly updated
 * with the Game's state.
 * @author Willow Sapphire
 * @version 1.0
 */
public class GameState implements Cloneable
{
    /**
     * The number of HeartsPlayers in the Game.
     */
    public static final int NUM_PLAYERS = Game.NUM_PLAYERS;
    
    /**
     * An ArrayList of Rounds, in order, which represents the Game.
     */
    private ArrayList<Round> game;
    /**
     * An array of scores of each HeartsPlayer.
     */
    private int[] scores;
    /**
     * Whether or not hearts has been broken.
     */
    private boolean heartsBroken;
    /**
     * The Direction the Card has been passed.
     */
    private int passDirection;
    
    /**
     * No-arg constructor which initializes all fields to empty and 0.
     */
    public GameState()
    {
        game = new ArrayList<Round>();
        scores = new int[NUM_PLAYERS];
        heartsBroken = false;
        passDirection = 0;
    }
    
    /**
     * Adds a Trick to the Round, at the end of the ArrayList.
     * @param t the Trick to be added
     */
    void addTrickToCurrentRound(Trick t)
    {
        game.get(game.size() - 1).addTrick(t);
    }
    
    /**
     * Accessor which retrieves the current Round which is stored
     * at the ArrayList.size() - 1.
     * @return the current Round
     */
    Round getCurrentRound()
    {
        return game.get(game.size() - 1);
    }
    
    /**
     * Creates a new Round and returns it.
     * @return the new Round
     */
    Round addNewRound()
    {
        game.add(new Round());
        return game.get(game.size() - 1);
    }
    
    /**
     * Accessor which retrieves a copy of each Round which has occurred.
     * @return an ArrayList with each Round in order
     */
    @SuppressWarnings("unchecked")
    ArrayList<Round> getRounds()
    {
        return (ArrayList<Round>) game.clone();
    }
    
    /**
     * Accessor which retrieves whether hearts has been broken or not.
     * @return whether hearts has been broken or not
     */
    boolean heartsBroken()
    {
        return heartsBroken;
    }
    
    /**
     * Accessor which retrieves the pass Direction.
     * @return the pass Direction
     */
    int getPassDirection()
    {
        return passDirection;
    }
    
    /**
     * Updates the pass Direction.
     */
    void updatePassDirection()
    {
        passDirection = (passDirection + 1) % 4;
    }
    
    /**
     * Mutator which sets the whether hearts has
     * been broken or not.
     * @param h whether hearts has been broken
     */
    void setHeartsBroken(boolean h)
    {
        heartsBroken = h;
    }

    /**
     * Accessor which retrieves the number of the current Trick.
     * @return the number of the current Trick
     */
    int getCurrentTrickNumber()
    {
        return game.get(game.size() - 1).getNumTricks() + 1;
    }
    
    /**
     * Calculates the scores in the game thus far.
     */
    public void updateScores()
    {
        Round r = game.get(game.size() - 1);
        r.calcPointSpread();
        
        for (int i = 0; i < NUM_PLAYERS; i++)
        {
            scores[i] += r.getPointsForPlayer(i);
        } 
    }
    
    /**
     * Accessor which retrieves the scores of the HeartsPLayers.
     * @return an array containing all of the scores
     */
    int[] getScores()
    {
        return scores;
    }
    
    /**
     * Determines if a HeartsPlayer has lost the game.
     * @return true if a HeartsPlayer has lost
     */
    boolean playerLost()
    {
        for (int i = 0; i < NUM_PLAYERS; i++) 
        {
            if (scores[i] >= 100)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Iterates through the scores to determine the winning HeartsPlayer.
     * @return the index of the winning HeartsPlayer
     */
    int findWinningPlayer()
    {
        int winningPlayer = 0;
        
        for (int i = 1; i < NUM_PLAYERS; i++)
        {
            if (scores[i] < scores[winningPlayer])
            {
                winningPlayer = i;
            }
        }
        return winningPlayer;
    }
    
    /**
     * Creates an array of for ints, where each int
     * corresponds to a player id. They are ordered such
     * that [0] is the player that came in first, [1] came in
     * second, etc.
     * @return the array of player rankings
     */
    int[] findPlayerRankings() 
    {
        int[] rankings = {-1, -1, -1, -1};
        for (int i = 0; i < NUM_PLAYERS; i++) 
        {
            int place = 0;
            for (int j = 0; j < NUM_PLAYERS; j++)
            {
                if (i == j) 
                {
                    continue;
                }
                if (scores[i] > scores[j]) 
                {
                    place++;
                }
            }
            
            while (rankings[place] != -1) 
            {
                place = (place + 1) % NUM_PLAYERS; 
            }
            
            rankings[place] = i;
        }
        return rankings;
    }
    
    /**
     * Clone method which creates a deep clone of the GameState
     * including fields.
     * @return the cloned GameState
     */
    public GameState clone()
    {
        GameState g = new GameState();
        //Clone the game field
        g.game = new ArrayList<Round>();
        for (Round r : this.game)
        {
            g.game.add(r.clone());
        }
        //Clone the scores field
        g.scores = new int[this.scores.length];
        for (int i = 0; i < this.scores.length; i++)
        {
            g.scores[i] = this.scores[i];
        }
        //Clone the other fields
        g.heartsBroken = this.heartsBroken;
        g.passDirection = this.passDirection;
        
        return g;
    }
}
