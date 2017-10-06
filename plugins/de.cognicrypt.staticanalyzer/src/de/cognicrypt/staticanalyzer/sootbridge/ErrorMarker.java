package de.cognicrypt.staticanalyzer.sootbridge;

public class ErrorMarker {
	
	protected final String errorMessage;		
	protected final String className;	
	protected final int lineNumber;
	protected final String methodSignature;	
	public ErrorMarker(String errorMessage, String className, int lineNumber){
		this(errorMessage, className, null, lineNumber);
	}
	public ErrorMarker(String errorMessage, String className, String methodSignature, int lineNumber) {
		this.errorMessage = errorMessage;
		this.className = className;
		this.lineNumber = lineNumber;
		this.methodSignature = methodSignature; 
	}

	public String getMethodSignature() {
		return methodSignature;
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	public String getClassName() {
		return className;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + lineNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorMarker other = (ErrorMarker) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return className+":"+lineNumber+" "+errorMessage;
	}

}
