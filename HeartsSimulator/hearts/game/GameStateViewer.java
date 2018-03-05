package hearts.game;

import java.util.ArrayList;

/**
 * GameStateViewer object which retrieves
 * information from the GameState.
 * @author Willow Sapphire
 * @version 1.0
 */
public class GameStateViewer
{
    /**
     * The GameState to access.
     */
    private GameState game;
    
    /**
     * Constructor which sets the game field to a clone of
     * the passed GameState.
     * @param game the GameState to view
     */
    public GameStateViewer(GameState game)
    {
        this.game = game.clone();
    }
    
    /**
     * Accessor which retrieves the current Round.
     * @return the current Round
     */
    public Round getCurrentRound()
    {
        return game.getCurrentRound();
    }
    
    /**
     * Accessor which retrieves all of the Rounds played.
     * @return an ArrayList containing all of the Rounds played in order
     */
    public ArrayList<Round> getRoundsSoFar()
    {
        return game.getRounds();
    }
    
    /**
     * Accessor which retrieves whether hearts has been broken.
     * @return whether hearts has been broken
     */
    public boolean heartsBroken()
    {
        return game.heartsBroken();
    }
    
    /**
     * Accessor which retrieves the Direction which Cards have been passed.
     * @return the Direction which Cards have been passed
     */
    public int getPassDirection()
    {
        return game.getPassDirection();
    }
    
    /**
     * Accessor which retrieves all of the scores.
     * @return an array containing all of the scores
     */
    public int[] getScores()
    {
        return game.getScores();
    }
    
    /**
     * Accessor which retrieves the current Trick number.
     * @return the current Trick number
     */
    public int getCurrentTrickNumber()
    {
        return game.getCurrentTrickNumber();
    }
}
