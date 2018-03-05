package hearts;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import hearts.game.Game;
import hearts.players.HeartsPlayer;

/**
 * This class is used to simulate
 * games of hearts. I
 * @author 3460 HN
 * @version 1.3
 */
public class HeartsSimulator 
{
    /**
     * The number of players in a game of hearts.
     */
    public static final int NUM_PLAYERS = 4;

    /**
     * Plays games using input from the command line.
     * The user will be asked to choose a number of games,
     * a checkpoint value, whether or not to print, the names of the
     * four player classes and names for the players.
     */
    public void simulate()
    {
        HeartsPlayer[] players = new HeartsPlayer[NUM_PLAYERS];
        String[] playerNames = new String[NUM_PLAYERS];
        boolean print = false;
        int numGames = 0;
        int checkpoint = 0;
        Scanner s = new Scanner(System.in);
        while (true) 
        {
            System.out.println("How many games would you "
            		  + "like to play? (Enter #)");
            try 
            {
                numGames = s.nextInt();
                break;
            }
            catch (InputMismatchException e) 
            {
                System.out.println("Please enter an integer");
            }
        }
        while (true) 
        {
            System.out.println("Checkpoints should print every "
            	     + "_____ number of games. (Enter #)");
            try 
            {
                checkpoint = s.nextInt();
                break;
            }
            catch (InputMismatchException e) 
            {
                System.out.println("Please enter an integer");
            }
        }
        while (true) 
        {
            System.out.println("Would you like to print details "
            		  + "about each individual game? (y/n)");
            String printString = s.next();
            if (printString.equals("y") || printString.equals("Y")) 
            {
                print = true;
                break;
            }
            else if (printString.equals("n") || printString.equals("N"))
            {
                print = false;
                break;
            }
            else 
            {
                System.out.println("Please enter y or n.");
            }
        }
        for (int i = 0; i < NUM_PLAYERS; i++)
        {
            while (true) 
            {
                System.out.println("Enter the class name of the player " 
                		  + i + 1 + ".");
                System.out.println("Remember to type the "
                		  + "package name . the class name");
                System.out.println("Alternatively, enter 'd' "
                		  + "to pick the default player");
                String player = s.next();
                try 
                {
                    if (player.equals("d")) 
                    {
                        player = "Players.RandomPlayer";
                    }
                    Class<?> pclass = Class.forName(player);
                    players[i] = (HeartsPlayer) pclass.newInstance();
                    System.out.println("Enter a recognizable "
                    		  + "name for this player.");
                    playerNames[i] = s.next();
                    break;
                }
                catch (ClassNotFoundException | InstantiationException 
                		  | IllegalAccessException e)
                {
                    System.out.println("Invalid Class Name.");
                    System.out.println("Remember to type the package "
                    		  + "name . the class name");
                }
            }
        }
        s.close();
        playGames(numGames, checkpoint, print, players, playerNames);
    }

    /**
     * Plays games using input from a file in the following format.
     * player1
     * player2
     * player3
     * player4
     * player1name
     * player2name
     * player3name
     * player4name
     * numGames
     * checkpoint
     * print
     * 
     * @param file
     *         The file from which to read
     * @throws FileNotFoundException 
     */
    public void simulate(String file) throws FileNotFoundException 
    {
        HeartsPlayer[] players = new HeartsPlayer[NUM_PLAYERS];
        String[] playerNames = new String[NUM_PLAYERS];
        boolean print = false;
        int numGames = 0;
        int checkpoint = 0;
        Scanner s = new Scanner(new File(file));
        try 
        {
            for (int i = 0; i < NUM_PLAYERS; i++) 
            {
                String player = s.nextLine();
                Class<?> pclass = Class.forName(player);
                players[i] = (HeartsPlayer) pclass.newInstance();
            }
            for (int i = 0; i < NUM_PLAYERS; i++) 
            {
                playerNames[i] = s.nextLine();
            }
            numGames = s.nextInt();
            checkpoint = s.nextInt();
            int printnum = s.nextInt();
            print = printnum == 0 ? false : true;
        }
        catch (ClassNotFoundException | InstantiationException 
        		  | IllegalAccessException | NoSuchElementException e)
        {
            System.out.println("File in incorrect format");
            e.printStackTrace();
            System.exit(0);
        }
        s.close();
        playGames(numGames, checkpoint, print, players, playerNames);
    }

    /**
     * Plays games and prints the scores.
     * @param numGames
     *         The number of games to play.
     * @param checkpoint
     *         Every checkpoint games there will be a prinout
     * @param print
     *         Whether or not to print details on each game
     * @param players
     *         The HeartsPlayers to compete
     * @param playerNames
     *         The name of each player
     */
    public void playGames(int numGames, int checkpoint, boolean print, 
    		  HeartsPlayer[] players, String[] playerNames) 
    {
        int[][] numPlayersWon = new int[NUM_PLAYERS][NUM_PLAYERS];
        int[] weights = new int[NUM_PLAYERS];
        // Start the games
        for (int i = 0; i < numGames; i++) 
        {
            Game game = new Game(players[0], players[1], 
            	    players[2], players[3], print);
            int[] scores = game.playGame();
            for (int j = 0; j < NUM_PLAYERS; j++)
            {
                numPlayersWon[scores[j]][j]++;
            }
            if (checkpoint != 0 && i % checkpoint == 0 && i != 0)
            {
                System.out.println(i + " game played so far");
            }
        }

        // Print the results
        for (int i = 0; i < NUM_PLAYERS; i++) 
        {
            System.out.println();
            System.out.println("========================================");
            System.out.println("    " + playerNames[i] + " Stats:        ");
            for (int j = 0; j < NUM_PLAYERS; j++) 
            {
                weights[i] += numPlayersWon[i][j] * (3 - j);
                String place = (j == 0) ? "1st" : (j == 1) 
                		  ? "2nd" : (j == 2) ? "3rd" : "4th";
                System.out.println("    Times coming in " + place 
                		  + ": " + numPlayersWon[i][j]);
            }
        }
        System.out.println();
        System.out.println("=========================================");
        System.out.println("     Weighted Player Scores        ");
        System.out.println("=========================================");
        System.out.println();
        int[] playerOrder = {-1, -1, -1, -1};
        for (int k = 0; k < NUM_PLAYERS; k++) 
        {
            int bestPlayer = -1;
            int bestScore = -1;
            outer:
                for (int i = 0; i < NUM_PLAYERS; i++)
                {
                    for (int j = 0; j < playerOrder.length; j++)
                    {
                        if (playerOrder[j] == i) 
                        {
                            continue outer;
                        }
                    }

                    if (weights[i] > bestScore)
                    {
                        bestPlayer = i;
                        bestScore = weights[i];
                    }
                }
            playerOrder[k] = bestPlayer;
        }

        for (int i = 0; i < NUM_PLAYERS; i++) 
        {
            String place = (i == 0) ? "1st" : (i == 1) 
            		  ? "2nd" : (i == 2) ? "3rd" : "4th";
            System.out.println("   " + place + ": " 
            		  + playerNames[playerOrder[i]] 
            		  + " with a score of " + (double) weights[playerOrder[i]]
            				  / (double) numGames);
        }
    }

    /**
     * Test Program.
     * @param args
     *         unused
     */
    public static void main(String[] args) 
    {
    	long start = System.currentTimeMillis();
        HeartsSimulator hs = new HeartsSimulator();
        int m = 0;
        Scanner s = new Scanner(System.in);
        System.out.println("Would you like to use an input file (1), "
        	      + "or enter information through the command line (2)?");
        while (true) 
        {
            try 
            {
                int t = s.nextInt();
                if (t == 1 || t == 2) 
                {
                    m = t;
                    break;
                }
                else
                {
                    System.out.println("Please enter a 1 or a 2.");
                }
            }
            catch (InputMismatchException e)
            {
                System.out.println("Please enter a 1 or a 2.");
                s.nextLine();
            }
        }
        if (m == 1) 
        {
            while (true)
            {
                System.out.println("Please enter the name of "
                		  + "the file you wish to use.");
                String fileName = s.next();
                try 
                {
                    hs.simulate(fileName);
                    break;
                } 
                catch (FileNotFoundException e) 
                {
                    System.out.println("The file does not exist");
                }
            }
        }
        else
        {
            hs.simulate();
        }
        s.close();
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("Program took " + time + " milliseconds.");
    }
}
