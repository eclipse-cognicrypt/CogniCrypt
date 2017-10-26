package de.cognicrypt.codegenerator.taskintegrator.models;

public class ClaferConstraint {

	private String constraint;

	public ClaferConstraint(String constraint) {
		this.constraint = constraint;
	}

	public ClaferConstraint() {
		this("");
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

}
