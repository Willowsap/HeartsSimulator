package hearts.game;

/**
 * An exception for when a player attempts
 * to play a card that is not legal in the game context.
 * @author Willow Sapphire
 * @version 1.0
 */
public class IllegalPlayException extends HeartsException
{
    /**
     * Default Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified
     * detail message.
     * @param message the detail message
     */
    public IllegalPlayException(String message)
    {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified
     * detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public IllegalPlayException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     * @param cause the cause
     */
    public IllegalPlayException(Throwable cause)
    {
        super(cause);
    }
}
