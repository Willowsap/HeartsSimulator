package hearts.game;

/**
 * An exception for when the game fails
 * to find the two of clubs.
 * @author Willow Sapphire
 * @version 1.0
 */
public class TwoOfClubsException extends HeartsException {
    /**
     * Default Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified
     * detail message.
     * @param message the detail message
     */
    public TwoOfClubsException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified
     * detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public TwoOfClubsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     * @param cause the cause
     */
    public TwoOfClubsException(Throwable cause) {
        super(cause);
    }
}
