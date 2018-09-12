package de.cognicrypt.staticanalyzer.statment;

import java.util.List;

import boomerang.jimple.Statement;
import soot.ValueBox;
import soot.jimple.internal.JimpleLocalBox;

/**
 * This class calculates the hashCode/id for the Markers.
 *
 * @author André Sonntag
 */
public class CCStatement {

	private final Statement stmt;
	private final String method;
	private final String params;
	private final String type;
	private String var;

	public CCStatement(final Statement stmt) {
		this.stmt = stmt;
		this.method = stmt.getUnit().get().getInvokeExpr().getMethod().toString();
		this.params = stmt.getUnit().get().getInvokeExpr().getArgs().toString();
		this.type = stmt.getUnit().get().getInvokeExpr().getType().toString();
		final List<ValueBox> boxes = stmt.getUnit().get().getUseAndDefBoxes();
		for (final ValueBox box : boxes) {
			// LinkedVariableBox is a privat local inner class of ValueBox. "instanceof" is
			// not usable in this case.
			if (box.getClass().getSimpleName().equals("LinkedVariableBox")) {
				this.var = box.getValue().toString();
				break;
			} else if (box instanceof JimpleLocalBox) {
				this.var = box.getValue().toString();
				break;
			}
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CCStatement other = (CCStatement) obj;
		if (this.method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!this.method.equals(other.method)) {
			return false;
		}
		if (this.params == null) {
			if (other.params != null) {
				return false;
			}
		} else if (!this.params.equals(other.params)) {
			return false;
		}
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!this.type.equals(other.type)) {
			return false;
		}
		if (this.var == null) {
			if (other.var != null) {
				return false;
			}
		} else if (!this.var.equals(other.var)) {
			return false;
		}
		return true;
	}

	public String getMethod() {
		return this.method;
	}

	public Statement getStmt() {
		return this.stmt;
	}

	public String getType() {
		return this.type;
	}

	public String getVar() {
		return this.var;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.method == null) ? 0 : this.method.hashCode());
		result = prime * result + ((this.params == null) ? 0 : this.params.hashCode());
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		result = prime * result + ((this.var == null) ? 0 : this.var.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "{hashCode()= " + hashCode() + " } CCStatement [stmt=" + this.stmt + ", method=" + this.method
				+ ", params=" + this.params + ", type=" + this.type + ", var=" + this.var + "]";
	}

}
