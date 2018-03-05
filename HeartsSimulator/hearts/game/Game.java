package hearts.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import hearts.players.HeartsPlayer;

/**
 * Game object which manages all aspects of the Hearts Game.
 * Contains a boolean print variable 
 * which allows for printing to the terminal, if desired.
 * @author Willow Sapphire
 * @version 1.0
 */
public class Game 
{
    /**
     * The number of HeartsPlayers in the Game.
     */
    public static final int NUM_PLAYERS = 4;
    
    /**
     * The number of Cards in a full deck.
     */
    public static final int NUM_CARDS_IN_DECK = 52;
    
    /**
     * The number of Cards in a full hand.
     */
    public static final int NUM_CARDS_IN_HANDS = 13;
    
    /**
     * The HeartsPlayers of the Game.
     */
    private ArrayList<HeartsPlayer> players;
    
    /**
     * The Game deck.
     */
    private ArrayList<Card> deck;
    
    /**
     * The Game portrayed as a GameState object.
     */
    private GameState game;
    
    /**
     * The HeartsPlayers' hands.
     */
    private ArrayList<ArrayList<Card>> hands;
    
    /**
     * The different Directions a Card could be passed.
     */
    private Direction[] directions;
    
    /**
     * Whether or not to print.
     */
    private boolean print;
    
    /**
     * Constructor for the Game object
     * which creates an ArrayList of HeartsPlayers and 
     * fills the ArrayList with the HeartPlayers passed into the constructor.
     * Then creates an ArrayList of Cards which represents all of the hands
     * of each HeartsPlayer.
     * Then creates an Array which holds the scores of each HeartsPlayer.
     * Then begins recording the history of the game with an ArrayList of
     * Tricks, every time a Trick is played, it is added to this ArrayList.
     * @param p1 player 1
     * @param p2 player 2
     * @param p3 player 3
     * @param p4 player 4
     * @param p Whether or not to print.
     */
    public Game(HeartsPlayer p1, HeartsPlayer p2, 
    		  HeartsPlayer p3, HeartsPlayer p4, boolean p) {
        players = new ArrayList<HeartsPlayer>(NUM_PLAYERS);
        hands = new ArrayList<ArrayList<Card>>();
        deck = new ArrayList<Card>(NUM_CARDS_IN_DECK);
        game = new GameState();
        
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        
        for (int i = 0; i < NUM_PLAYERS; i++) {
            hands.add(new ArrayList<Card>(NUM_CARDS_IN_HANDS));
        }
        
        print = p;
        directions = Direction.values();
    }
    
    /**
     * Plays a single game of Hearts.
     * If a HeartsPlayer hasn't lost,
     * add a new Trick to the game ArrayList,
     * then shuffle the deck,
     * then deal the Cards,
     * then try to play a round (catching any exceptions which might occur),
     * then update the current direction.
     * If someone loses, 
     * print the scores and return.
     * @return the winning player (index in HeartsPlayers ArrayList)
     */
    public int[] playGame() {
        fillDeck();
        while (!game.playerLost()) {
            game.addNewRound();
            game.setHeartsBroken(false);
            shuffleDeck();
            dealCards();
            try {
                playRound();
            } catch (HeartsException e) {
                e.printStackTrace();
                break;
            }
            game.updatePassDirection();
        }
        printScores();
        return game.findPlayerRankings();
    }
    
    /**
     * Determines if a HeartPlayer's choice is a legal play.
     * @param choice the index in the HeartPlayer's hand which they want to play
     * @param hand the hand of the player
     * @param currTrick what has been played on the trick thus far.
     * @param leadSuit the Suit led on the Trick.
     * @param trickNo the Trick's number in the round
     * @return true if legal play, false otherwise
     */
    public static boolean isLegalPlay(int choice, ArrayList<Card> hand, 
    		  ArrayList<Card> currTrick, Suit leadSuit, int trickNo) {    
        if (trickNo == 1 && (hand.get(choice).getPoints() != 0)) {
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).getPoints() == 0) {
                    return false;
                }
            }
        }
        boolean hasTheSuit = false;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getSuit() == leadSuit) {
                hasTheSuit = true;
                break;
            }
        }
        if ((hand.get(choice).getSuit() != leadSuit) && hasTheSuit) {
            return false;
        }
        return true;
    }
    
    /**
     * Check's if a HeartsPlayer's choice of Card to lead is legal.
     * @param choice the HeartPlayer's choice
     * @param hand the HeartPlayer's hand
     * @param heartsBroken whether or not hearts is broken
     * @return true if legal choice, false otherwise
     */
    public static boolean isLegalLead(int choice, ArrayList<Card> hand, 
    		  boolean heartsBroken)
    {
        boolean hasOtherSuits = false;
        
        for (int i = 0; i < hand.size(); i++)
        {
            if (hand.get(i).getSuit() != Suit.Hearts)
            {
                hasOtherSuits = true;
                break;
            }
        }
        return !(!heartsBroken && (hand.get(choice).getSuit() 
        		  == Suit.Hearts) && hasOtherSuits);
    }
    
    /**
     * Check's if a HeartsPlayer's chosen Cards to pass are legal.
     * @param choices the HeartsPlayer's choices
     * @return true if legal choices, false otherwise
     */
    public static boolean isLegalPass(int[] choices)
    {
        return !(choices.length > 3 || choices[0] > 12 || choices[1] > 12 
        		  || choices[2] > 12 || choices[0] == choices [1] 
        		  || choices[1] == choices[2] || choices[0] == choices[2]);
    }
    
    /**
     * Deals Cards to the HeartPlayers in the HeartPlayers ArrayList
     * from the deck. 
     * 
     * This method assumes the HeartPlayer's hands are empty.
     */
    private void dealCards()
    {
        int player = 0;
        
        for (int i = 0; i < NUM_CARDS_IN_DECK; i++)
        {
            hands.get(player).add(deck.get(i));
            player = advancePlayer(player);
        }
    }
    
    /**
     * Plays a single round of Hearts.
     * @throws TwoOfClubsException 
     * @throws FirstTrickException 
     * @throws IllegalPlayException 
     * @throws IllegalPassException 
     */
    private void playRound() throws FirstTrickException, TwoOfClubsException, 
      IllegalPlayException, IllegalPassException
    {
        passCards(); 
        int currPlayer = playFirstTrick();
        
        for (int i = 0; i < 12; i++)
        {
            currPlayer = playTrick(currPlayer);
        }
        game.updateScores();
    }
    
    /**
     * Passes Cards for a round.
     * @throws IllegalPassException if a player attempts an illegal pass
     */
    private void passCards() throws IllegalPassException
    {
        // It's the hold phase
        if (game.getPassDirection() == 3)
        {
            return;
        }
        
        // Get player's pass choices
        int[][] playersChoices = new int[4][3];
        for (int i = 0; i < NUM_PLAYERS; i++)
        {
          //Clone Hand to pass
            ArrayList<Card> handTemp = hands.get(i);
            ArrayList<Card> handCopy = new ArrayList<Card>();
            for (Card c : handTemp)
            {
                handCopy.add(c.clone());
            }
            playersChoices[i] = players.get(i).pass(i, handCopy, 
            		directions[game.getPassDirection()], 
            		new GameStateViewer(game));
            if (!isLegalPass(playersChoices[i]))
            {
                throw new IllegalPassException("Player " + i 
                		+ " attempted to pass illegal cards.");
            }
        }
        
        // sort the player's choices from lowest to highest index
        for (int i = 0; i < playersChoices.length; i++)
        {
            Arrays.sort(playersChoices[i]);
        }
        // Store cards to pass for each player
        Card[][] tempCards = new Card[4][3];
        for (int i = 0; i < NUM_PLAYERS; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                tempCards[i][j] = hands.get(i).get(playersChoices[i][j]);
            }
        }
        
        // Remove the cards each player chose from their hand
        for (int i = 0; i < NUM_PLAYERS; i++)
        {
            for (int j = 2; j >= 0; j--) 
            {
                hands.get(i).remove(playersChoices[i][j]);
            }
        }
        
        // Add the cards each passer chose to each passee
        int d = game.getPassDirection();
        for (int i = 0; i < NUM_PLAYERS; i++)
        {
            // from = the player who passed to player i
            int from = (int) ((i + d + (3.0 / 2) 
            		  * (d * d) - (9.0 / 2) * d + 3) % 4);
            for (int j = 0; j < 3; j++) 
            {
                hands.get(i).add(tempCards[from][j]);
            }
        }
    }
    /**
     * Helper method for the playRound() which specifically
     * plays the first round.
     * @return the HeartsPlayer that won the Trick
     * @throws TwoOfClubsException  
     * @throws FirstTrickException 
     */
    private int playFirstTrick() throws FirstTrickException, TwoOfClubsException
    {
        int[] cardAndPlayer = findCardAndPlayerWithTwoClubs();
        int cardPos = cardAndPlayer[0];
        int currPlayer = cardAndPlayer[1];
        ArrayList<Card> currTrick = new ArrayList<Card>();
        
        print("Player " + currPlayer + " leads with a " 
            + hands.get(currPlayer).get(cardPos));
        
        currTrick.add(hands.get(currPlayer).get(cardPos));
        Suit leadSuit = hands.get(currPlayer).get(cardPos).getSuit();
        hands.get(currPlayer).remove(cardPos);
        
        for (int i = 0; i < 3; i++)
        {
            currPlayer = advancePlayer(currPlayer);
            //Clone Hand to pass
            ArrayList<Card> handTemp = hands.get(currPlayer);
            ArrayList<Card> handCopy = new ArrayList<Card>();
            for (Card c : handTemp)
            {
                handCopy.add(c.clone());
            }
            //Clone Trick to pass
            ArrayList<Card> trickTemp = currTrick;
            ArrayList<Card> trickCopy = new ArrayList<Card>();
            for (Card c : trickTemp)
            {
                trickCopy.add(c.clone());
            }
            int choice = players.get(currPlayer).play(currPlayer, handCopy, 
            		  trickCopy, leadSuit, new GameStateViewer(game));
            
            print("Player " + currPlayer + " plays a " 
                + hands.get(currPlayer).get(choice));
            
            if (!isLegalPlay(choice, hands.get(currPlayer), 
            		  currTrick, leadSuit, 1))
            {
                throw new FirstTrickException("Player " + currPlayer 
                		+ " tried to play an illegal card on the first trick");
            }
            if (hands.get(currPlayer).get(choice).getSuit() == Suit.Hearts)
            {
                game.setHeartsBroken(true);
            }
            currTrick.add(hands.get(currPlayer).get(choice));
            hands.get(currPlayer).remove(choice);
        }
        
        int winningCard = findWinningCard(currTrick);
        
        //Returns the HeartsPlayer that won the Trick
        int winningPlayer = (currPlayer + winningCard + 1) % 4;
        game.addTrickToCurrentRound(new Trick(currTrick.get(0), 
        		  currTrick.get(1), currTrick.get(2), 
        		  currTrick.get(3), winningPlayer));
        print("Player " + winningPlayer + " takes the trick");
        print("");
        return winningPlayer;
    }
    
    /**
     * Helper method for the playRound()
     * which plays a single Trick.
     * @param currPlayer the HeartsPlayer to start the Trick
     * @return the winning HeartsPlayer
     * @throws IllegalPlayException 
     */
    private int playTrick(int currPlayer) throws IllegalPlayException
    {
        ArrayList<Card> currTrick = new ArrayList<Card>();
        //Clone Hand to pass
        ArrayList<Card> handTemp = hands.get(currPlayer);
        ArrayList<Card> handCopy = new ArrayList<Card>();
        for (Card c : handTemp)
        {
            handCopy.add(c.clone());
        }
        //Clone Trick to pass
        ArrayList<Card> trickTemp = currTrick;
        ArrayList<Card> trickCopy = new ArrayList<Card>();
        for (Card c : trickTemp)
        {
            trickCopy.add(c.clone());
        }
        int choice = players.get(currPlayer).lead(currPlayer, 
        		  handCopy, new GameStateViewer(game));
        print("Player " + currPlayer + " leads with a " 
        		    + hands.get(currPlayer).get(choice));
        
        if (!isLegalLead(choice, hands.get(currPlayer), game.heartsBroken()))
        {
            throw new IllegalPlayException("Player " + currPlayer 
            		+ " tried to lead an illegal card on trick " 
            		+ game.getCurrentTrickNumber());
        }
        
        if (hands.get(currPlayer).get(choice).getSuit() == Suit.Hearts)
        {
            game.setHeartsBroken(true);
        }
        
        currTrick.add(hands.get(currPlayer).get(choice));
        Suit leadSuit = hands.get(currPlayer).get(choice).getSuit();
        hands.get(currPlayer).remove(choice);
        
        for (int i = 0; i < 3; i++)
        {
            currPlayer = advancePlayer(currPlayer);
            //Clone Hand to pass
            ArrayList<Card> handTemp1 = hands.get(currPlayer);
            ArrayList<Card> handCopy1 = new ArrayList<Card>();
            for (Card c : handTemp1)
            {
                handCopy1.add(c.clone());
            }
            //Clone Trick to pass
            ArrayList<Card> trickTemp1 = currTrick;
            ArrayList<Card> trickCopy1 = new ArrayList<Card>();
            for (Card c : trickTemp1)
            {
                trickCopy1.add(c.clone());
            }
            choice = players.get(currPlayer).play(currPlayer, handCopy1,
            		trickCopy1, leadSuit, new GameStateViewer(game));
            
            print("Player " + currPlayer + " plays a " 
                + hands.get(currPlayer).get(choice));
            
            if (!isLegalPlay(choice, hands.get(currPlayer), currTrick, 
            		  leadSuit, game.getCurrentTrickNumber()))
            {
                throw new IllegalPlayException("Player " + currPlayer 
                		+ " tried to play an illegal card on trick "
                		+ game.getCurrentTrickNumber());
            }
            
            if (hands.get(currPlayer).get(choice).getSuit() == Suit.Hearts)
            {
                game.setHeartsBroken(true);
            }
            currTrick.add(hands.get(currPlayer).get(choice));
            hands.get(currPlayer).remove(choice);
        }
        
        int winningCard = findWinningCard(currTrick);
        
        // Returns the HeartsPlayer who won the Trick.
        int winningPlayer = (currPlayer + winningCard + 1) % 4;
        game.addTrickToCurrentRound(new Trick(currTrick.get(0), 
        		  currTrick.get(1), currTrick.get(2), 
        		  currTrick.get(3), winningPlayer));
        print("Player " + winningPlayer + " takes the trick");
        print("");
        return winningPlayer;
    }
    
    /**
     * Finds the winning Card in a Trick.
     * @param trick the Trick to search for the winning Card
     * @return the index of the winning Card
     */
    private int findWinningCard(ArrayList<Card> trick)
    {
        Suit leadSuit = trick.get(0).getSuit();
        Rank highestRank = trick.get(0).getRank();
        int winningCard = 0;
        
        for (int i = 1; i < NUM_PLAYERS; i++)
        {
            if ((trick.get(i).getSuit() == leadSuit) 
                && (trick.get(i).getRank().compareTo(highestRank) > 0))
            {
                highestRank = trick.get(i).getRank();
                winningCard = i;
            }
        }
        
        return winningCard;
    }
    
    /**
     * Prints the scores from the scores[] Array.
     */
    private void printScores()
    {
        for (int i = 0; i < NUM_PLAYERS; i++)
        {
            print("Player " + i + " Scored: " + game.getScores()[i]);
        }
    }
    
    /**
     * Finds the HeartsPlayer whose hand 
     * contains has the Card: Two of Clubs.
     * @return an Array of length two where
     * [0] = Two of Clubs index and [1] = Player index
     * @throws TwoOfClubsException 
     */
    private int[] findCardAndPlayerWithTwoClubs() throws TwoOfClubsException
    {
        for (int i = 0; i < NUM_PLAYERS; i++)
        {
            for (int j = 0; j < NUM_CARDS_IN_HANDS; j++)
            {
                if ((hands.get(i).get(j).getSuit() == Suit.Clubs) 
                    && (hands.get(i).get(j).getRank() == Rank.Two))
                {
                    int[] cardAndPlayer = {j, i};
                    return cardAndPlayer;
                }
            }
        }
        throw new TwoOfClubsException("Could not find the two of clubs");
    }
    
    /**
     * Advances current HeartsPlayer in a clockwise Direction.
     * @param currPlayer the current HeartsPlayer from which to advance
     * @return the next current HeartsPlayer.
     */
    private int advancePlayer(int currPlayer)
    {
        return (currPlayer + 1) % 4;
    }
    
    /**
     * Shuffles the deck ArrayList
     * by swapping at random locations.
     */
    private void shuffleDeck()
    {
        Random rnd = new Random();
        for (int i = deck.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            Card a = deck.get(index);
            deck.set(index, deck.get(i));
            deck.set(i, a);
        }
    }
    
    /**
     * Fills the deck ArrayList with all 52 Cards.
     */
    private void fillDeck()
    {
        Suit[] suits = Suit.values();
        Rank[] values = Rank.values();
        
        for (int suit = 0; suit < suits.length; suit++)
        {
            for (int value = 0; value < values.length; value++)
            {
                int points = 0;
                if (suits[suit] == Suit.Hearts)
                {
                    points = 1;
                }
                if ((suits[suit] == Suit.Spades) 
                    && (values[value] == Rank.Queen))
                {
                    points = 13;
                }
                deck.add(new Card(suits[suit], values[value], points));
            }
        }
    }
    
    /**
     * Print method which allows a run through of a Hearts game
     * to be printed or not, simply controlled by the print field.
     * @param string the string to print
     */
    private void print(String string)
    {
        if (print)
        {
            System.out.println(string);
        }
    }
}
