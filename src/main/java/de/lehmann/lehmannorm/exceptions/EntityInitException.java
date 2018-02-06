package de.lehmann.lehmannorm.exceptions;

/**
 * @author Tim Lehmann
 *
 */
public class EntityInitException extends IllegalArgumentException {

    private static final long serialVersionUID = -7733597311475060935L;

    public EntityInitException() {
        super();
    }

    public EntityInitException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EntityInitException(final String s) {
        super(s);
    }

    public EntityInitException(final Throwable cause) {
        super(cause);
    }
}
