package hearts.game;

/**
 * An exception for when a player attempts to lead
 * with an illegal play.
 * @author Willow Sapphire
 * @version 1.0
 */
public class IllegalLeadException extends HeartsException
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
    public IllegalLeadException(String message)
    {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified
     * detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public IllegalLeadException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     * @param cause the cause
     */
    public IllegalLeadException(Throwable cause)
    {
        super(cause);
    }
}
