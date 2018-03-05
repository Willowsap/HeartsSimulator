package hearts.players;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import hearts.game.Card;
import hearts.game.Direction;
import hearts.game.GameStateViewer;
import hearts.game.HeartsException;
import hearts.game.IllegalPlayException;
import hearts.game.Game;
import hearts.game.Rank;
import hearts.game.Suit;
import hearts.game.Trick;

/**
 * 
 * @author Kira Willow Vashaw
 * @version 0.2
 *
 */
public class ExtremePlayer implements HeartsPlayer
{
    public static final String FILE = "log.txt";
    public static final boolean APPEND = true;
    public static final boolean WRITE = true;
    public static final boolean PRINT = true;
    public static final int NUM_SIMULATIONS = 1;
    public static final int NUM_TRICKS_IN_ROUND = 13;
    public static final int NUM_CARDS_IN_DECK = 52;
    /*
     * Variables saved each turn
     */
    private ArrayList<Card> hand;
    private ArrayList<Card> currTrick;
    private Suit leadSuit;
    private int numCardsAlreadyInTrick;
    private GameStateViewer game;
    private int trickNo;
    private ArrayList<Integer> legalPlays;
    private int[] scores;
    private PrintWriter pw;
    
    /*
     * Variables created at the beginning of a round
     */
    private ArrayList<Card> unknownCards;
    private ArrayList<Card> playedCards;
    private int roundNum = 0;
    
    /*
     * Variables created once per game
     */
    private int myId;
    private Player[] players;
    private boolean firstRound = true;
    
    @Override
    public int play(int playerId, ArrayList<Card> hand, 
           ArrayList<Card> currTrick, Suit leadSuit, GameStateViewer game) 
    {
    	print("Playing on trick: " + game.getCurrentTrickNumber());
    	this.hand = hand;
        openWriter();
        if (game.getRoundsSoFar().size() > roundNum)
        {
            newRound();
        }
        this.roundNum = game.getRoundsSoFar().size();
        this.currTrick = currTrick;
        this.leadSuit = leadSuit;
        this.numCardsAlreadyInTrick = currTrick.size();
        this.game = game;
        this.trickNo = game.getCurrentTrickNumber();
        if (game.getCurrentTrickNumber() == 2 && playedCards.size() == 0)
        {
            leadOnFirstTrick();
        }
        processTurn();
        getLegalPlays();
        
        if (legalPlays.size() == 1)
        {
            print("Only option: " + hand.get(legalPlays.get(0)).toString());
            return legalPlays.get(0);
        }
        
        int answer = 0;
        try 
        {
            write("Starting a simulation on round " + game.getRoundsSoFar()
                .size() + " and trick " + game.getCurrentTrickNumber());
            answer = simulation();
            print("Used a simulation to choose: " 
                + hand.get(answer).toString());
        }
        catch (HeartsException e) 
        {
            System.out.println("Error in the Simulation");
            closeWriter();
            e.printStackTrace();
        } 
        closeWriter();
        return answer;
    }

    /**
     * I have not implemented a strategy for this method yet. 
     * Eventually it will also simulate.
     */
    @Override
    public int lead(int playerId, ArrayList<Card> hand, GameStateViewer game) 
    {
    	print("Leading on trick: " + game.getCurrentTrickNumber());
    	this.roundNum = game.getRoundsSoFar().size();
        this.hand = hand;
        this.numCardsAlreadyInTrick = 0;
        this.game = game;
        this.trickNo = game.getCurrentTrickNumber();
        currTrick.clear();
        if (trickNo == 1)
        {
            newRound();
        }
        if (trickNo == 2)
        {
            leadOnFirstTrick();
        }
        processTurn();
        if (!game.heartsBroken()) 
        {
            for (int i = 0; i < hand.size(); i++)
            {
                if (hand.get(i).getSuit() != Suit.Hearts)
                {
                    print("Chose to lead: " + hand.get(i).toString());
                    return i;
                }
            }
        }
        return 0;
    }
    
    @Override
    public int[] pass(int playerId, ArrayList<Card> hand, 
            Direction passDirection, GameStateViewer game) 
    {
    	print("Passing on round: " + game.getRoundsSoFar().size());
        this.hand = hand;
        this.game = game;
        if (firstRound)
        {
            initializations(playerId);
            firstRound = false;
        }
        Suit fewest = findFewestSuit();
        int numFound = 0;
        int[] passCards = new int[3];
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
     * Processes a new round having begun.
     */
    private void newRound() 
    {
    	unknownCards = new ArrayList<Card>(NUM_CARDS_IN_DECK);
        fillUnknownCards();
        removeHandFromUnknowns();
        playedCards = new ArrayList<Card>(NUM_CARDS_IN_DECK);
    }
    
    /**
     * Removes my current hand from the list of
     * cards whose place I don't know.
     */
    private void removeHandFromUnknowns() 
    {
        for (int i = 0; i < hand.size(); i++) 
        {
            for (int j = 0; j < unknownCards.size(); j++) 
            {
                if (hand.get(i).equals(unknownCards.get(j))) 
                {
                    unknownCards.remove(j);
                    break;
                }
            }
        }
    }
    
    /**
     * Opens the PrintWriter pw.
     */
    private void openWriter() 
    {
        try 
        {
            pw = new PrintWriter(new BufferedWriter(
                    new FileWriter(FILE, APPEND)));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Closes the PrintWriter pw.
     */
    private void closeWriter() 
    {
        pw.flush();
        pw.close();
    }
    
    /**
     * Writes my current hand to the log.
     */
    private void writeHand() 
    {
        if (WRITE) 
        {
            pw.print("My Hand: ");
            for (int i = 0; i < hand.size(); i++) 
            {
                pw.print(hand.get(i).toString() + ", ");
            }
            pw.println();
        }
    }
    
    /**
     * Writes a passed string to the log.
     * @param str the string to write
     */
    private void write(String str) 
    {
        if (WRITE) 
        {
            pw.println(str);
        }
    }
    
    /**
     * Writes a passed string to the log
     * without appending a new line.
     * @param str the string to write.
     */
    private void writeNoLine(String str) 
    {
        if (WRITE) 
        {
            pw.print(str);
        }
    }
    
    /**
     * Runs a monte carlo simulation.
     * @return the best card
     * @throws HeartsException 
     */
    private int simulation() throws HeartsException 
    {
        ArrayList<Double[]> simScores = 
                new ArrayList<Double[]>(legalPlays.size());
        ArrayList<Card> currTrick = new ArrayList<Card>(4);
        for (Card c : this.currTrick)
        {
            currTrick.add(c.clone());
        }
        writeNoLine("Trick so far: ");
        for (int i = 0; i < currTrick.size(); i++) 
        {
            writeNoLine(currTrick.get(i).toString() + ", ");
        }
        write("");
        writeNoLine("Played Cards: ");
        for (int i = 0; i < playedCards.size(); i++) 
        {
            writeNoLine(playedCards.get(i).toString() + ", ");
        }
        write("");
        writeNoLine("Unknown Cards: ");
        for (int i = 0; i < unknownCards.size(); i++) 
        {
            writeNoLine(unknownCards.get(i).toString() + ", ");
        }
        write("");
        writeHand();
        writeNoLine("Legal Plays: ");
        for (int i = 0; i < legalPlays.size(); i++) 
        {
            writeNoLine(legalPlays.get(i).toString() + ", ");
        }
        write("");
        
        for (int i = 0; i < legalPlays.size(); i++) 
        {
            Double[] someScores = {0.0, 0.0, 0.0, 0.0};
            simScores.add(someScores);
            SimRound sim = new SimRound(currTrick);
            for (int j = 0; j < NUM_SIMULATIONS; j++) 
            {
                int[] temp = sim.finishRound(legalPlays.get(i));
                for (int k = 0; k < 4; k++) 
                {
                    simScores.get(simScores.size() - 1)[k] += temp[k];
                }
                sim.reset(currTrick);
            }
            for (int j = 0; j < 4; j++) 
            {
                simScores.get(i)[j] /= NUM_SIMULATIONS;
                simScores.get(i)[j] = scores[j] - simScores.get(i)[j]; 
            }
            
        }
        double[] finals = new double[simScores.size()];
        for (int i = 0; i < simScores.size(); i++) 
        {
            for (int j = 0; j < simScores.get(i).length; j++) 
            {
                if (j == myId)
                {
                    finals[i] += simScores.get(i)[j];
                }
                else
                {
                    finals[i] += -simScores.get(i)[j] / 3;
                }
            }
        }
        double bestNum = Double.MIN_VALUE;
        int bestIndex = 0;
        for (int i = 0; i < finals.length; i++) 
        {
            if (finals[i] > bestNum) 
            {
                bestNum = finals[i];
                bestIndex = i;
            }
        }
        return legalPlays.get(bestIndex);
    }
    
    /**
     * prints a string to the command line.
     * @param str the string to print
     */
    private void print(String str) 
    {
        if (PRINT)
        {
            System.out.println(str);
        }
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
     * Processes the last turn.
     */
    private void processTurn() 
    {
        if (trickNo != 1)
        {
            Card[] cards = game.getCurrentRound()
                .getTrick(trickNo - 2).getCards();
            for (int i = numCardsAlreadyInTrick; i < 4; i++) 
            {
            	if (!playedCards.contains(cards[i]))
            	{
                    playedCards.add(cards[i]);
            	}
                unknownCards.remove(cards[i]);
            }
        }
        for (int i = 0; i < currTrick.size(); i++)
        {
            if (!playedCards.contains(currTrick.get(i)))
            {
                playedCards.add(currTrick.get(i));
            }
            unknownCards.remove(currTrick.get(i));
        }
    }
    
    /**
     * Initialize shit.
     * @param id my player id
     */
    private void initializations(int id) 
    {
    	newRound();
        legalPlays = new ArrayList<Integer>(13);
        scores = new int[4];
        this.myId = id;
        players = new Player[4];
        players[id]       = new Player(id, "me");
        players[(id + 1) % 4] = new Player((id + 1) % 4, "left");
        players[(id + 2) % 4] = new Player((id + 2) % 4, "across");
        players[(id + 3) % 4] = new Player((id + 3) % 4, "right");
    }
    
    /**
     * Find the suit of which I have the least.
     * @return the fewest suit.
     */
    private Suit findFewestSuit() 
    {
        Suit[] suits = Suit.values();
        int[] numOf = new int[4];
        for (int i = 0; i < hand.size(); i++)
        {
            numOf[hand.get(i).getSuit().ordinal()]++;
        }
        int lowest = 0;
        for (int i = 1; i < 4; i++)
        {
            if (numOf[lowest] > numOf[i])
            {
                lowest = i;
            }
        }
        return suits[lowest];
    }
    
    /**
     * Fills the unknown cards list with all 52 cards.
     */
    private void fillUnknownCards() 
    {
        // Fills with all 52 cards
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
                unknownCards.add(new Card(suits[suit], values[value], points));
            }
        }
    }
    
    /**
     * Gets all the legal plays from my hand and
     * puts them in the global legalPlays array.
     */
    private void getLegalPlays() 
    {
        legalPlays.clear();
        for (int i = 0; i < hand.size(); i++)
        {
            if (Game.isLegalPlay(i, hand, currTrick, leadSuit, trickNo))
            {
                legalPlays.add(i);
            }
        }
    }
    
    /**
     * Deal with it.
     */
    private void leadOnFirstTrick()
    {
        print("I lead on the first trick, processing it now");
        Card[] cards = game.getCurrentRound().getTrick(0).getCards();
        for (int i = 0; i < 4; i++) 
        {
            if (!playedCards.contains(cards[i]))
            {
                playedCards.add(cards[i]);
            }
            unknownCards.remove(cards[i]);
        }
    }
    /**
     * 
     * @author Kira Willow Vashaw
     * @version 0.1
     *
     */
    private class SimRound 
    {
        private ArrayList<Trick> thisRound;
        private ArrayList<ArrayList<Card>> hands;
        private ArrayList<Card> simCurrTrick;
        private ArrayList<Card> deck = new ArrayList<Card>(unknownCards.size());
        private ArrayList<ModHeartsPlayer> simPlayers;
        private int trickNo = game.getCurrentTrickNumber();
        private boolean heartsBroken = game.heartsBroken();
    
        /**
         * Constructor for simRound.
         * @param currTrick the current trick.
         */
        public SimRound(ArrayList<Card> currTrick) 
        {
            this.simCurrTrick = new ArrayList<Card>(4);
            for (int i = 0; i < currTrick.size(); i++)
            {
            	simCurrTrick.add(currTrick.get(i).clone());
            }
            this.simPlayers = new ArrayList<ModHeartsPlayer>(4);
            simPlayers.add(new MediumModHeartsPlayer());
            simPlayers.add(new MediumModHeartsPlayer());
            simPlayers.add(new MediumModHeartsPlayer());
            simPlayers.add(new MediumModHeartsPlayer());
            this.thisRound = new ArrayList<Trick>(13);
            for (int i = 0; i < game.getCurrentRound().getTricks().size(); i++)
            {
            	thisRound.add(game.getCurrentRound().getTrick(i).clone());
            }
        }
    
        /**
         * Resets the sim.
         * @param currTrick the current trick.
         */
        public void reset(ArrayList<Card> currTrick) 
        {
            this.simCurrTrick = new ArrayList<Card>(4);
            for (int i = 0; i < currTrick.size(); i++)
            {
            	simCurrTrick.add(currTrick.get(i).clone());
            }
            this.thisRound = new ArrayList<Trick>(13);
            for (int i = 0; i < game.getCurrentRound().getTricks().size(); i++)
            {
            	thisRound.add(game.getCurrentRound().getTrick(i).clone());
            }
        }
        /**
         * Finishes the current round.
         * @param choice
         *         The card I play
         * @return
         *         The scores
         * @throws HeartsException 
         */
        public int[] finishRound(int choice) throws HeartsException 
        {
            writeInSimRound("finishing round with choice: " + choice + "...");
            guessHands();
            int currPlayer = finishFirstTrick(choice);
            for (int i = thisRound.size(); i < NUM_TRICKS_IN_ROUND; i++) 
            {
                currPlayer = playTrick(currPlayer);
            }
            return countScores();
        }
        
        /**
         * Method for writing to the log in the sim round.
         * @param str the string to write
         */
        private void writeInSimRound(String str) 
        {
            if (PRINT) 
            {
                pw.println("----" + str);
            }
        }
        
        /**
         * Finishes the first trick.
         * @param myChoice
         *         The card I chose to play.
         * @return
         *         The winner.
         * @throws IllegalPlayException 
         */
        private int finishFirstTrick(int myChoice) throws IllegalPlayException 
        {
            writeInSimRound("Finishing the first trick...");
            int currPlayer = myId;
            simCurrTrick.add(hands.get(currPlayer).get(myChoice));
            writeInSimRound("Player " + currPlayer + " played " 
                + hands.get(currPlayer).get(myChoice));
            hands.get(currPlayer).remove(myChoice);
            if (simCurrTrick.size() < 4) 
            {
                for (int i = simCurrTrick.size(); i < 4; i++) 
                {
                    currPlayer = (currPlayer + 1) % 4;
                    int choice = simPlayers.get(currPlayer).play(hands
                            .get(currPlayer), simCurrTrick, leadSuit, trickNo);
                    if (!isLegalPlay(choice, hands.get(currPlayer), 
                            simCurrTrick, leadSuit, trickNo))
                    {
                        throw new IllegalPlayException("Player " + currPlayer 
                            + " tried to play an illegal card on trick " 
                            + game.getCurrentTrickNumber());
                    }
                    writeInSimRound("Player " + currPlayer + " plays " 
                            + hands.get(currPlayer).get(choice).toString());
                    if (hands.get(currPlayer).get(choice).getSuit() 
                            == Suit.Hearts)
                    {
                        heartsBroken = true;
                    }
                    simCurrTrick.add(hands.get(currPlayer).get(choice));
                    hands.get(currPlayer).remove(choice);
                }
            }
            trickNo++;
            int winningPlayer = trickWinner(simCurrTrick, currPlayer);
            thisRound.add(new Trick(simCurrTrick.get(0), simCurrTrick.get(1), 
                    simCurrTrick.get(2), simCurrTrick.get(3), winningPlayer));
            writeInSimRound("Player " + winningPlayer + " wins the round.");
            return winningPlayer;
        }    
    
        /**
         * Plays a trick in the sim round.
         * @param currPlayer the current player
         * @return the winning player
         * @throws IllegalPlayException 
         */
        private int playTrick(int currPlayer) throws IllegalPlayException 
        {
            simCurrTrick = new ArrayList<Card>();
            writeInSimRound("Starting trick " + trickNo);
            int choice = simPlayers.get(currPlayer)
                    .lead(hands.get(currPlayer), heartsBroken);
            if (!isLegalLead(choice, hands.get(currPlayer), heartsBroken))
            {
                throw new IllegalPlayException("Player " + currPlayer 
                        + " tried to lead an illegal card on trick " + trickNo);
            }
            writeInSimRound("Player " + currPlayer + " leads with " 
                + hands.get(currPlayer).get(choice).toString());
            if (hands.get(currPlayer).get(choice).getSuit() == Suit.Hearts)
            {
                heartsBroken = true;
            }
        
            simCurrTrick.add(hands.get(currPlayer).get(choice));
            Suit leadSuit = hands.get(currPlayer).get(choice).getSuit();
            hands.get(currPlayer).remove(choice);
        
            for (int i = 0; i < 3; i++) 
            {
                currPlayer = (currPlayer + 1) % 4;
                choice = simPlayers.get(currPlayer).play(hands
                         .get(currPlayer), simCurrTrick, leadSuit, trickNo);
                if (!isLegalPlay(choice, hands.get(currPlayer), 
                        simCurrTrick, leadSuit, trickNo))
                {
                    throw new IllegalPlayException("Player " + currPlayer 
                            + " tried to play an illegal card on trick " 
                            + game.getCurrentTrickNumber());
                }
                writeInSimRound("Player " + currPlayer + " plays " 
                    + hands.get(currPlayer).get(choice).toString());
                if (hands.get(currPlayer).get(choice).getSuit() == Suit.Hearts)
                {
                    heartsBroken = true;
                }
                simCurrTrick.add(hands.get(currPlayer).get(choice));
                hands.get(currPlayer).remove(choice);
            }
            trickNo++;
            int winningPlayer = trickWinner(simCurrTrick, currPlayer);
            thisRound.add(new Trick(simCurrTrick.get(0), simCurrTrick.get(1), 
                    simCurrTrick.get(2), simCurrTrick.get(3), winningPlayer));
            writeInSimRound("Player " + winningPlayer + " wins the round.");
            return winningPlayer;
        }
        
        /**
         * Checks if a lead is legal.
         * @param choice the players choice
         * @param hand the players hand
         * @param heartsBroken if hearts is broken
         * @return true if it is legal
         */
        private boolean isLegalLead(int choice, ArrayList<Card> hand, 
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
         * Checks is a play is legal.
         * @param choice the players choice
         * @param hand the players hand
         * @param simCurrTrick the current trick
         * @param leadSuit the lead suit
         * @param trickNo the trick number
         * @return true if it is legal
         */
        private boolean isLegalPlay(int choice, ArrayList<Card> hand, 
                ArrayList<Card> simCurrTrick, Suit leadSuit, int trickNo)
        {    
            if (trickNo == 1 && (hand.get(choice).getPoints() != 0))
            {
                for (int i = 0; i < hand.size(); i++)
                {
                    if (hand.get(i).getPoints() == 0)
                    {
                        return false;
                    }
                }
            }
            boolean hasTheSuit = false;
            for (int i = 0; i < hand.size(); i++)
            {
                if (hand.get(i).getSuit() == leadSuit)
                {
                    hasTheSuit = true;
                    break;
                }
            }
            return !((hand.get(choice).getSuit() != leadSuit) && hasTheSuit);
        }
        
        /**
         * Finds the winner of a trick.
         * @param trick the trick
         * @param currPlayer the player
         * @return the winner
         */
        private int trickWinner(ArrayList<Card> trick, int currPlayer)
        {
            Suit leadSuit = trick.get(0).getSuit();
            Rank highestRank = trick.get(0).getRank();
            int winningCard = 0;
        
            for (int i = 1; i < 4; i++)
            {
                if ((trick.get(i).getSuit() == leadSuit)
                    && (trick.get(i).getRank().compareTo(highestRank) > 0))
                {
                    highestRank = trick.get(i).getRank();
                    winningCard = i;
                }
            }
            return (currPlayer + winningCard + 1) % 4;
        }
        
        /**
         * Attempts to guess the hands of the other players.
         * @throws HeartsException 
         */
        private void guessHands() throws HeartsException 
        {
            deck.clear();
            deck.addAll(unknownCards);
            shuffleDeck();
            hands = new ArrayList<ArrayList<Card>>(4);
            for (int i = 0; i < 4; i++) 
            {
                if (i == myId) 
                {
                    hands.add(new ArrayList<Card>(13));
                    for (int j = 0; j < hand.size(); j++)
                    {
                    	hands.get(i).add(hand.get(j).clone());
                    }
                }
                else 
                { 
                    hands.add(new ArrayList<Card>(13));
                }
            }
            int[] handSizes = calcHandSizes();
            writeInSimRound("Calculated Hand Sizes: " + handSizes[0] + ", " 
                + handSizes[1] + ", " + handSizes[2] + ", " + handSizes[3]);
            boolean noChange = false;
            while (!noChange) 
            {
                noChange = true;
                for (int i = 0; i < 4; i++) 
                {
                    if (i == myId) 
                    {
                        continue;
                    }
                    if (hands.get(i).size() < handSizes[i]) 
                    {
                        dealCard(hands.get(i), players[i]);
                        noChange = false;
                    }
                }
            }
        }
        
        /**
         * Counts the scores.
         * @return the scores
         */
        private int[] countScores() 
        {
            int[] scores = new int[4];
            for (int i = 0; i < thisRound.size(); i++) 
            {
                scores[thisRound.get(i).getPlayer()] 
                    += thisRound.get(i).getPoints();
            }
            write("Scores: " + scores[0] + ", " + scores[1] + ", " 
                + scores[2] + ", " + scores[3]);
            return scores;
        }
        
        /**
         * Deal a card to a player.
         * @param hand
         *         The players hand
         * @param p
         *         The player
         */
        private void dealCard(ArrayList<Card> hand, Player p) 
        {
            if (deck.size() > 0)
            {
            	hand.add(deck.get(0));
                deck.remove(0);
            }
            else 
            {
            	pw.close();
            	System.out.println("Ran out of cards in the deck");
            	System.exit(0);
            }
            
        }
        
        /**
         * Calculates the hand sizes of each player.
         * @return the hand sizes
         * @throws HeartsException 
         */
        private int[] calcHandSizes() throws HeartsException 
        {
            int[] handSizes = new int[4];
            handSizes[myId] = hand.size();
            switch(simCurrTrick.size()) 
            {
                case 0:
                    handSizes[(myId + 1) % 4] = hand.size();
                    handSizes[(myId + 2) % 4] = hand.size();
                    handSizes[(myId + 3) % 4] = hand.size();
                    break;
                case 1:
                    handSizes[(myId + 1) % 4] = hand.size();
                    handSizes[(myId + 2) % 4] = hand.size();
                    handSizes[(myId + 3) % 4] = hand.size() - 1;
                    break;
                case 2:
                    handSizes[(myId + 1) % 4] = hand.size();
                    handSizes[(myId + 2) % 4] = hand.size() - 1;
                    handSizes[(myId + 3) % 4] = hand.size() - 1;
                    break;
                case 3:
                    handSizes[(myId + 1) % 4] = hand.size() - 1;
                    handSizes[(myId + 2) % 4] = hand.size() - 1;
                    handSizes[(myId + 3) % 4] = hand.size() - 1;
                    break;
                default:
                    throw new HeartsException("The size of the "
                            + "current trick is not between 0 and 3: Size of " 
                    		+ simCurrTrick.size());
            }
            return handSizes;
        }
        
        /**
         * Shuffles the deck.
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
    }    
    
    /**
     * 
     * @author Kira Willow Vashaw
     * @version 0.2
     */
    private class Player 
    {

        private int id;
        private String relationToMe;
        private ArrayList<Suit> voidSuits;
        private ArrayList<Card> thisRound;
        private ArrayList<Card> knownCards;
        private ArrayList<ArrayList<Card>> previousRounds;
        private ArrayList<Integer> pointsEachRound;
        
        /**
         * Constructor for the player class.
         * @param id the players id
         * @param relationToMe their relation to me
         */
        public Player(int id, String relationToMe) 
        {
            this.id = id;
            this.relationToMe = relationToMe;
            voidSuits = new ArrayList<Suit>(4);
            thisRound = new ArrayList<Card>(13);
            previousRounds = new ArrayList<ArrayList<Card>>();
            pointsEachRound = new ArrayList<Integer>();
            knownCards = new ArrayList<Card>();
        }
        
        /**
         * Adds cards to their list of cards I know.
         * @param a the cards to add
         */
        public void addKnownCards(ArrayList<Card> a) 
        {
            knownCards.addAll(a);
        }
        
        /**
         * Getter for the known cards.
         * @return the known cards
         */
        public ArrayList<Card> getKnownCards() 
        {
            return knownCards;
        }
        
        /**
         * Getter for the points received each round.
         * @return the points from each round.
         */
        public ArrayList<Integer> getPointsEachRound() 
        {
            return pointsEachRound;
        }
        
        /**
         * Getter for the points on a particular round.
         * @param r the round
         * @return the points
         */
        public int getPointsOnRound(int r) 
        {
            return pointsEachRound.get(r);
        }
        
        /**
         * Getter for the total points.
         * @return the total points the player has.
         */
        public int getTotalPoints() 
        {	
            int p = 0;
            for (Integer i : pointsEachRound)
            {
                p += i;
            }
            return p;
        }
        
        /**
         * Adds a new round and the points earned.
         * @param pointsEarned the points earned
         */
        public void newRound(int pointsEarned) 
        {
            pointsEachRound.add(pointsEarned);
            previousRounds.add(thisRound);
            thisRound = new ArrayList<Card>(13);
        }
        
        /**
         * Getter for the void suits.
         * @return the void suits.
         */
        public ArrayList<Suit> getVoidSuits() 
        {
            return voidSuits;
        }
        
        /**
         * Add a void suit.
         * @param voidSuit
         * 		The void suit
         */
        public void addVoid(Suit voidSuit) 
        {
            voidSuits.add(voidSuit);
        }
        
        /**
         * Getter for their relation to me.
         * @return their relation to me.
         */
        public String getRelationToMe() 
        {
            return relationToMe;
        }
        
        /**
         * Getter for the id.
         * @return the id
         */
        public int getId() 
        {
            return id;
        }
        @Override
        public String toString() 
        {
            return "Player " + id;
        }
    }
    
    /**
     * A private version of the hearts player.
     * @author Kira Willow Vashaw
     * @version 1.0
     */
    private interface ModHeartsPlayer 
    {
    	/**
    	 * Play a card.
    	 * @param hand the hand.
    	 * @param currTrick the current trick
    	 * @param leadSuit the lead suit.
    	 * @param trickNo the trick number.
    	 * @return the choice.
    	 */
        public int play(ArrayList<Card> hand, ArrayList<Card> currTrick, 
        	   Suit leadSuit, int trickNo);
        
        /**
         * Lead a card.
         * @param hand the hand.
         * @param heartsBroken if hearts is broken/
         * @return the choice.
         */
        public int lead(ArrayList<Card> hand, boolean heartsBroken);
    }
    
    /**
     * A private variation of the MediumModHeartsPlayer.
     * @author Kira Willow Vashaw
     *@version 1.0
     */
    private class MediumModHeartsPlayer implements ModHeartsPlayer 
    {
        @Override
        public int play(ArrayList<Card> hand, ArrayList<Card> currTrick, 
        	    Suit leadSuit, int trickNo) 
        {
            if (canPlayAnything(hand, leadSuit, trickNo)) 
            {
                int indexToPlay = 0;
                for (int i = 1; i < hand.size(); i++) 
                {
                    if (hand.get(i).getSuit() == Suit.Spades 
                    	    && hand.get(i).getRank() == Rank.Queen)
                    {
                        return i;
                    }
                    if (hand.get(i).getSuit() == Suit.Hearts 
                            && hand.get(indexToPlay).getSuit() != Suit.Hearts)
                    {
                        indexToPlay = i;
                    }
                    else if (hand.get(i).getSuit() == Suit.Hearts  && hand
                    	    .get(i).getRank().compareTo(hand
                    	    .get(indexToPlay).getRank()) > 0)
                    {
                        indexToPlay = i;
                    }
                                
                }
            }
            
            for (int i = 0; i < hand.size(); i++) 
            {
                if (hand.get(i).getSuit() == leadSuit)
                {
                    return i;
                }
            }
            if (trickNo == 1)
            {
                for (int i = 0; i < hand.size(); i++)
                {
                    if (!(hand.get(i).getSuit() == Suit.Hearts) 
                    	    && !((hand.get(i).getSuit() == Suit.Spades) 
                    	    && (hand.get(i).getRank() == Rank.Queen)))
                    {
                        return i;
                    }
                }
            }
            return 0;
        }
        
        @Override
        public int lead(ArrayList<Card> hand, boolean heartsBroken) 
        {
            if (!heartsBroken) 
            {
                for (int i = 0; i < hand.size(); i++) 
                {
                    if (hand.get(i).getSuit() != Suit.Hearts)
                    {
                        return i;
                    }
                }
            }
            return 0;
        }
        
        /**
         * Checks if any play is legal.
         * @param hand the hand
         * @param leadSuit the lead suit
         * @param trickNo the trick number
         * @return true if any play is legal
         */
        public boolean canPlayAnything(ArrayList<Card> hand, 
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
    }

}


