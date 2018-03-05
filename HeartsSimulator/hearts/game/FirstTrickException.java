package hearts.game;

/**
 * An exception for when a player attempts to play
 * an illegal card on the first trick of a round.
 * 
 * @author Willow Sapphire
 * @version 1.0
 */
public class FirstTrickException extends HeartsException {
    /**
     * Default Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified
     * detail message.
     * @param message the detail message
     */
    public FirstTrickException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified
     * detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public FirstTrickException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     * @param cause the cause
     */
    public FirstTrickException(Throwable cause) {
        super(cause);
    }
}
