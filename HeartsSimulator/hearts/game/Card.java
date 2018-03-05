package hearts.game;

/**
 * Card class which represents a single card in 
 * the game Hearts which has a Suit, Value, and Point value.
 * @author Willow Sapphire
 * @version 1.0
 */
public class Card implements Cloneable, Comparable<Card>
{

    /**
     * The Suit of the Card represented by an enumerated value.
     */
    private Suit suit;
    
    /**
     * The Value of the Card represented by an enumerated value.
     */
    private Rank rank;
    
    /**
     * The Point value of the Card.
     */
    private int points;
    
    /**
     * Constructor for the Card object 
     * which initializes each field to the value passed in.
     * @param s the Suit of the new Card
     * @param r the Rank of the new Card
     * @param p the Point value of the new Card
     */
    public Card(Suit s, Rank r, int p) {
        suit = s;
        rank = r;
        points = p;
    }
    
    /**
     * Accessor which retrieves the Suit of the Card.
     * @return the Suit of the Card
     */
    public Suit getSuit() {
        return suit;
    }
    
    /**
     * Accessor which retrieves the Value of the Card.
     * @return the Value of the Card
     */
    public Rank getRank() {
        return rank;
    }
    
    /**
     * Accessor which retrieves the Point value of the Card.
     * @return the point value of the card
     */
    public int getPoints() {
        return points;
    }
    
    /**
     * To String method which returns the Value and Suit of the Card.
     * Exp: "Queen of Hearts"
     */
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
    
    /**
     * Compares an object with this card to see if they
     * are the same.
     * @return true if the object is a card and 
     * its rank and suit are equal to this card's
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Card && suit.equals(((Card) o).getSuit()) 
        		  && rank.equals(((Card) o).getRank());
    }
    
    /**
     * Clones this card.
     * @return a precise copy of this card
     */
    @Override
    public Card clone() {
        return new Card(this.suit, this.rank, this.points);
    }

    /**
     * Compares two cards to see which is greater
     * Value is based on the standard deck order
     * Spades > Hearts > Diamonds > Clubs
     * Ace > King > Queen > Jack > 10...2
     * @return 
	 *	0 this equals c
	 *	less than 0 if this is less than c
	 *	greater than 0 this is greater than c
     */
    @Override
    public int compareTo(Card c) {
        return Integer.compare(((suit.ordinal() * 100) + (rank.ordinal())), 
        		  ((suit.ordinal() * 100) + (rank.ordinal())));
    }
    
    /**
     * Creates a hash code for the card
     * @return a hash code unique to the card
     */
    @Override
    public int hashCode() {
        return (suit.ordinal() * 100) + (rank.ordinal());
    }
}
