package de.cognicrypt.integrator.task.exceptions;

public class ErrorMessageException extends Exception {
	private static final long serialVersionUID = 1L;

	private String text; 
	
	public ErrorMessageException(String msg) {
		super(msg);
	}
	
	public ErrorMessageException(String msg, String text) {
		super(msg);
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
