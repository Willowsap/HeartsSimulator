package hearts.game;

/**
 * An blanket exception for errors in a game of hearts.
 * @author Willow Sapphire
 * @version 1.0
 */
public class HeartsException extends Exception
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
    public HeartsException(String message)
    {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified
     * detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public HeartsException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     * @param cause the cause
     */
    public HeartsException(Throwable cause)
    {
        super(cause);
    }
}
