package common;

/**
 * The Whack A Mole Exception error when something goes wrong in the game for specific cases
 * @author Angela Hudak
 * @author Ryan Vay
 */
public class WAMException extends Exception {
    public WAMException(String message)
    {
        super(message);
    }
    public WAMException(Throwable cause)
    {
        super(cause);
    }
    public WAMException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
