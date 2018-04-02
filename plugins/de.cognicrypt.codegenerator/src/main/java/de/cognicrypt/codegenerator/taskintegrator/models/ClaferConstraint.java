package de.cognicrypt.codegenerator.taskintegrator.models;

import java.io.Serializable;

public class ClaferConstraint implements Serializable {

	private static final long serialVersionUID = 466282011383992518L;

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

	@Override
	public String toString() {
		return "[ " + getConstraint() + " ]";
	}

}
