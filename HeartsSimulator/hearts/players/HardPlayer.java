package hearts.players;

import java.util.ArrayList;

import hearts.game.Card;
import hearts.game.Direction;
import hearts.game.Game;
import hearts.game.GameStateViewer;
import hearts.game.Suit;

/**
 * 
 * @author 3460 HN
 * @version 1.0
 */
public class HardPlayer implements HeartsPlayer
{

    /**
     * Plays a certain Card by returning the index in the hand.
     * @param playerId 
     *         the index of the current HeartsPlayer
     * @param hand 
     *         ArrayList of the hand of the HeartsPlayer
     * @param currTrick 
     *         ArrayList of all of the Cards in the Trick
     * @param leadSuit 
     *         the lead Suit
     * @param game 
     *         a GameStateViewer which allows the HeartsPlayer to view info
     * @return the index of the played Card
     */
    @Override
    public int play(int playerId, ArrayList<Card> hand, 
            ArrayList<Card> currTrick, Suit leadSuit, 
            GameStateViewer game) 
    {
        int index = 0;
        /*
         * If I can play any card, play the one with the most points.
         */
        if (canPlayAnything(hand, leadSuit, game.getCurrentTrickNumber())) 
        {
            for (int i = 1; i < hand.size(); i++) 
            {
                if (hand.get(i).getPoints() > hand.get(index).getPoints())
                {
                    index = i;
                }
            }
            return index;
        }

        /*
         * play my biggest legal card.
         * This happens when it is either the first trick, or 
         * I am the last person to play and there are no
         * points on the trick. 
         */
        boolean clean = true;
        for (int i = 0; i < currTrick.size(); i++) 
        {
            if (currTrick.get(i).getPoints() != 0)
            {
                clean = false;
            }
        }

        if (game.getCurrentTrickNumber() == 1 
                || (currTrick.size() == 3 && clean)) 
        {
            while (!Game.isLegalPlay(index, hand, currTrick, leadSuit, 
                    game.getCurrentTrickNumber()))
            {
                index++;
            }
            for (int i = index + 1; i < hand.size(); i++) 
            {
                if (((hand.get(index).getRank().compareTo(hand.get(i)
                        .getRank()) < 0) || ((hand.get(index).getRank()
                        .compareTo(hand.get(i).getRank()) == 0) && (hand
                        .get(index).getSuit().compareTo(hand.get(i).getSuit()) 
                        < 0))) && Game.isLegalPlay(i, hand, currTrick, 
                        leadSuit, game.getCurrentTrickNumber()))
                {
                    index = i;
                }
            }
            return index;
        }

        /*
         * By default, play my lowest rank legal play 
         */
        while (!Game.isLegalPlay(index, hand, currTrick, 
                leadSuit, game.getCurrentTrickNumber()))
        {
            index++;
        }
        for (int i = index + 1; i < hand.size(); i++)  
        {
            if (((hand.get(index).getRank().compareTo(hand.get(i)
                    .getRank()) > 0) || ((hand.get(index).getRank()
                    .compareTo(hand.get(i).getRank()) == 0) && (hand
                    .get(index).getSuit().compareTo(hand.get(i).getSuit()) 
                     > 0))) && Game.isLegalPlay(i, hand, currTrick, 
                     leadSuit, game.getCurrentTrickNumber()))
            {
                index = i;
            }
        }
        return index;

    }

    /**
     * Leads with a certain Card by returning the index in the hand.
     * @param playerId 
     *         the index of the current HeartsPlayer
     * @param hand 
     *         ArrayList of the hand of the HeartsPlayer
     * @param game 
     *         a GameStateViewer which allows the HeartsPlayer to view info.
     * @return the index of the played Card
     */
    @Override
    public int lead(int playerId, ArrayList<Card> hand, GameStateViewer game) 
    {
        int index = 0;
        while (!Game.isLegalLead(index, hand, game.heartsBroken()))
        {
            index++;
        }
        for (int i = index + 1; i < hand.size(); i++)
        {
            if (((hand.get(index).getRank().compareTo(hand.get(i)
            	    .getRank()) > 0) || ((hand.get(index).getRank()
            	    .compareTo(hand.get(i).getRank()) == 0) && (hand
            	    .get(index).getSuit().compareTo(hand.get(i).getSuit()) 
            	    > 0))) && Game.isLegalLead(i, hand, game.heartsBroken()))
            {
                index = i;
            }
        }
        return index;
    }

    /**
     * Passes three Cards chosen by the HeartsPlayer.
     * @param playerId 
     *         the index of the current HeartsPlayer
     * @param hand 
     *         ArrayList of the hand of the HeartsPlayer
     * @param passDirection 
     *         the Direction which the Cards will be passed
     * @param game 
     *         a GameStateViewer which allows the HeartsPlayer to view info.
     * @return the three Cards to pass store in an array
     */
    @Override
    public int[] pass(int playerId, ArrayList<Card> hand, 
            Direction passDirection, GameStateViewer game) 
    {
        Suit fewest = findFewestSuit(hand);
        int numFound = 0;
        int[] passCards = new int[3];

        /*
         * Try to fill the array with 3 cards from my fewest suit
         */
        for (int i = 0; i < hand.size(); i++) 
        {
            if (hand.get(i).getSuit() == fewest) 
            {
                passCards[numFound] = i;
                numFound++;
            }
            if (numFound > 2)
            {
                break;
            }
        }

        /*
         * For any additional needed cards to pass, fill with my
         * biggest cards
         */
        while (numFound < 3) 
        {
            int index = 0;
            while (inArray(passCards, index))
            {
                index++;
            }
            for (int i = index + 1; i < hand.size(); i++) 
            {
                if (inArray(passCards, i)) 
                {
                    continue;
                }
                if (((hand.get(index).getRank().compareTo(hand.get(i)
                    .getRank()) < 0) || ((hand.get(index).getRank()
                    .compareTo(hand.get(i).getRank()) == 0) && (hand
                    .get(index).getSuit().compareTo(hand.get(i).getSuit()) 
                    < 0))))
                {
                    index = i;
                }
            }
            passCards[numFound] = index;
            numFound++;
        }
        return passCards;
    }

    /**
     * Checks array a to see if element b is within.
     * @param a
     *         The array through which to check
     * @param b
     *         The element being checked for
     * @return true if the element is in the array
     */
    private boolean inArray(int[] a, int b) 
    {
        for (int i = 0; i < a.length; i++)
        {
            if (a[i] == b)
            {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks to see if any play on the current
     * trick from the player's current hand is legal.
     * This happens when the player is void the lead
     * suit and it is not the first trick
     * @param hand
     *         The player's hand
     * @param leadSuit
     *         The suit lead on the current trick
     * @param trickNo
     *         The current trick number
     * @return true if any play is legal
     */
    private boolean canPlayAnything(ArrayList<Card> hand, 
            Suit leadSuit, int trickNo) 
    {
        if (trickNo == 1)
        {
            return false;
        }
        for (int i = 0; i < hand.size(); i++)
        {
            if (hand.get(i).getSuit() == leadSuit)
            {
                return false;
            }
        }
        return true;

    }

    /**
     * Finds the smallest suit from a player's hand.
     * @param hand
     *         The player's current hand.
     * @return The smallest suit in the hand
     */
    private Suit findFewestSuit(ArrayList<Card> hand) 
    {
        Suit[] suits = Suit.values();
        int[] numOf = new int[4];
        for (int i = 0; i < hand.size(); i++) 
        {
            numOf[hand.get(i).getSuit().ordinal()]++;
        }
        int lowest = 0;
        while (true)
        {
            if (numOf[lowest] == 0)
            {
             	lowest++;
            }
            else
            {
            	break;
            }
        }
        for (int i = lowest + 1; i < 4; i++) 
        {
            if (numOf[lowest] > numOf[i] && numOf[i] != 0)
            {
                lowest = i;
            }
        }
        return suits[lowest];
    }
}
