package by.alesnax.qanda.service;

/**
 * Thrown to indicate that normal processing of SQL statement was
 * interrupted or SQL connection problems were occurred.
 *
 * @author  Aliaksandr Nakhankou
 * @see Exception
 */
public class ServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a <code>ServiceException</code> with the
	 * specified detail message.
	 *
	 * @param   message   the detail message.
	 */
	public ServiceException(String message) {
		super(message);
	}

	/**
	 * Constructs a <code>ServiceException</code> with the
	 * specified detail message and caught exception.
	 *
	 * @param   message   the detail message.
	 * @param e is thrown exception
	 */
	public ServiceException(String message, Exception e) {
		super(message, e);
	}

	/**
	 * Constructs a <code>ServiceException</code> with caught exception.
	 *
	 * @param e is thrown exception
	 */
	public ServiceException(Exception e){super(e);}

}
