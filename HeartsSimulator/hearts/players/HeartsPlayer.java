package hearts.players;

import java.util.ArrayList;

import hearts.game.Card;
import hearts.game.Direction;
import hearts.game.GameStateViewer;
import hearts.game.Suit;

/**
 * Interface which specifies what methods a HeartsPlayer must contain.
 * @author 3460 HN
 * @version 1.0
 */
public interface HeartsPlayer
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
    public int play(int playerId, ArrayList<Card> hand, 
    		  ArrayList<Card> currTrick, Suit leadSuit, GameStateViewer game);
	
	/**
	 * Leads with a certain Card by returning the index in the hand.
     * @param playerId the index of the current HeartsPlayer
	 * @param hand ArrayList of the hand of the HeartsPlayer
     * @param game a GameStateViewer which allows the HeartsPlayer to view info.
	 * @return the index of the played Card
	 */
    public int lead(int playerId, ArrayList<Card> hand, GameStateViewer game);
	
	/**
	 * Passes three Cards chosen by the HeartsPlayer.
     * @param playerId the index of the current HeartsPlayer
     * @param hand ArrayList of the hand of the HeartsPlayer
     * @param passDirection the Direction which the Cards will be passed
     * @param game a GameStateViewer which allows the HeartsPlayer to view info.
	 * @return the three Cards to pass store in an array
	 */
    public int[] pass(int playerId, ArrayList<Card> hand, 
    		  Direction passDirection, GameStateViewer game);
}
