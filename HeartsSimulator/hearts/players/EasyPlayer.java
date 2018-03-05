package hearts.players;

import java.util.ArrayList;

import hearts.game.Card;
import hearts.game.Direction;
import hearts.game.Game;
import hearts.game.GameStateViewer;
import hearts.game.Suit;

/**
 * A hearts player that plays randomly.
 * @author 3460 HN
 * @version 1.0
 */
public class EasyPlayer implements HeartsPlayer
{
    /**
     * Plays a certain Card by returning the index in the hand.
     * @param playerId the index of the current HeartsPlayer
     * @param hand ArrayList of the hand of the HeartsPlayer
     * @param currTrick ArrayList of all of the Cards in the Trick
     * @param leadSuit the lead Suit
     * @param game a GameStateViewer which allows the HeartsPlayer to view info.
     * @return the index of the played Card
     */
    @Override
    public int play(int playerId, ArrayList<Card> hand, 
    		  ArrayList<Card> currTrick, 
    		  Suit leadSuit, GameStateViewer game) 
    {
        for (int i = 0; i < hand.size(); i++) 
        {
            if (Game.isLegalPlay(i, hand, currTrick, 
            	    leadSuit, game.getCurrentTrickNumber())) 
            {
                return i;        
            }
        }
        return 0;
    }

    /**
     * Leads with a certain Card by returning the index in the hand.
     * @param playerId the index of the current HeartsPlayer
     * @param hand ArrayList of the hand of the HeartsPlayer
     * @param game a GameStateViewer which allows the HeartsPlayer to view info.
     * @return the index of the played Card
     */
    @Override
    public int lead(int playerId, ArrayList<Card> hand, GameStateViewer game) 
    {
        for (int i = 0; i < hand.size(); i++)
        {
            if (Game.isLegalLead(i, hand, game.heartsBroken()))
            {
             	return i;
            }
        }
        return 0;
    }
    
    /**
     * Passes three Cards chosen by the HeartsPlayer.
     * @param playerId the index of the current HeartsPlayer
     * @param hand ArrayList of the hand of the HeartsPlayer
     * @param passDirection the Direction which the Cards will be passed
     * @param game a GameStateViewer which allows the HeartsPlayer to view info.
     * @return the three Cards to pass store in an array
     */
    @Override
    public int[] pass(int playerId, ArrayList<Card> hand, 
    		  Direction passDirection, GameStateViewer game) 
    {
        int[] array = {0, 1, 2};
        return array;
    }
}
