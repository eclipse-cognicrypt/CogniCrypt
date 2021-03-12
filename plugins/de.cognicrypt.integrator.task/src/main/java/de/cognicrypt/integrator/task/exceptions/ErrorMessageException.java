package de.cognicrypt.integrator.task.exceptions;

public class ErrorMessageException extends Exception {
	private static final long serialVersionUID = 1L;

	public ErrorMessageException(String msg) {
		super(msg);
	}
}
