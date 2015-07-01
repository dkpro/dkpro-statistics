package org.dkpro.statistics.agreement;

/**
 * Exception type for indicating missing data. The exception is raised for
 * computing agreement for empty annotation studies and for studies with
 * only one annotation category.
 */
public class InsufficientDataException extends RuntimeException {

	public InsufficientDataException() {
		super();
	}

	public InsufficientDataException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InsufficientDataException(final String message) {
		super(message);
	}

	public InsufficientDataException(final Throwable cause) {
		super(cause);
	}

}
