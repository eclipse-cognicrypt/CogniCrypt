package crossing.e1.configurator.codegeneration.exceptions;

public class CompilationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public CompilationFailedException(String errorMessage) {
		super(errorMessage);
	}

}
