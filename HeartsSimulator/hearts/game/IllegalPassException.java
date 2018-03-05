package hearts.game;

/**
 * An exception for when a player attempts
 * to pass cards that are not legal in the game context.
 * @author Willow Sapphire
 * @version 1.0
 */
public class IllegalPassException extends HeartsException
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
    public IllegalPassException(String message)
    {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified
     * detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public IllegalPassException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     * @param cause the cause
     */
    public IllegalPassException(Throwable cause)
    {
        super(cause);
    }
}
