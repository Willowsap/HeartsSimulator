package hearts.game;

import java.util.Arrays;

/**
 * Trick object used to store each Trick in the game.
 * @author Willow Sapphire
 * @version 1.0
 */
public class Trick implements Cloneable {
    /**
     * Array of Cards included in the Trick.
     */
    private Card[] cards;
    
    /**
     * The index of the winning Card.
     */
    private int player;
    
    /**
     * The number of points contained in the trick.
     */
    private int points;
    
    /**
     * Constructor which fills the cards Array
     * with the Cards passed in.
     * @param c1 the first Card played
     * @param c2 the second Card played
     * @param c3 the third Card played
     * @param c4 the fourth Card played
     * @param p the index of the winning Card
     */
    public Trick(Card c1, Card c2, Card c3, Card c4, int p)
    {
        player = p;
        
        cards = new Card[4];
        cards[0] = c1;
        cards[1] = c2;
        cards[2] = c3;
        cards[3] = c4;
        
        for (int i = 0; i < 4; i++)
        {
            points += cards[i].getPoints();
        }
    }
    
    /**
     * Accessor for the cards field.
     * @return the array of Cards played
     */
    public Card[] getCards()
    {
        Card[] cs = new Card[this.cards.length];
        for (int i = 0; i < cs.length; i++)
        {
            cs[i] = cards[i].clone();
        }
        return cs; 
    }    
    
    /**
     * Accessor for the player field.
     * @return the index of the winning player.
     */
    public int getPlayer()
    {
        return player;
    }
    
    /**
     * Accessor for the player field.
     * @return the number of points in the trick.
     */
    public int getPoints()
    {
        return points;
    }
    
    /**
     * Creates a string representation of the trick.
     * @return the string representation.
     */
    @Override
    public String toString() 
    {
        return (cards[0].toString() + " was lead. "
              + cards[1].toString() + " was played next. "
              + cards[2].toString() + " was played next. "
              + cards[3].toString() + " was played last. ");
    }
    
    /**
     * Determines if a particular object is 
     * the same as this trick.
     * @return true if they are the same
     */
    @Override
    public boolean equals(Object o) 
    {
        return o instanceof Trick && (((Trick) o).getPlayer() == player) 
        		  && (((Trick) o).getPoints() == points) 
        		  && Arrays.equals((((Trick) o).getCards()), cards); 
    }
    
    /**
     * Creates a semi-unique integer for hashing.
     * @return the hashcode
     */
    @Override
    public int hashCode() 
    {
        return this.toString().hashCode();
    }
    
    /**
     * Clone method which creates a deep copy of Trick
     * including all fields.
     * @return the cloned Trick
     */
    @Override
    public Trick clone()
    {
        return new Trick(this.cards[0].clone(),
                            this.cards[1].clone(),
                            this.cards[2].clone(),
                            this.cards[3].clone(),
                            this.player);
    }
    
}
