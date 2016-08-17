package de.uulm.einhoernchen.flashcardsapp.Util;

/**
 * @author Jonas Kraus
 * @author Fabian Widmann
 */
public class ParameterNotSupportedException extends Throwable {

    public ParameterNotSupportedException()
    {
    }

    public ParameterNotSupportedException(String message)
    {
        super(message);
    }

    public ParameterNotSupportedException(Throwable cause)
    {
        super(cause);
    }

    public ParameterNotSupportedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ParameterNotSupportedException(String message, Throwable cause,
                           boolean enableSuppression, boolean writableStackTrace)
    {
        // TODO nicht für alle api level geeignet
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
