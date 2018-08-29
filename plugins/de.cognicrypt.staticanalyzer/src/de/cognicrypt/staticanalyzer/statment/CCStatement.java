package de.cognicrypt.staticanalyzer.statment;

import boomerang.jimple.Statement;
import soot.SootMethod;
import soot.jimple.Stmt;

/**
 * This class calculates the hashCode/id for the Markers.
 * 
 * @author André Sonntag
 */
public class CCStatement {

	private Statement stmt;
	private String method;
	private String type;
	private String var;

	public CCStatement(Statement stmt) {
		this.stmt = stmt;
		this.method = stmt.getUnit().get().getInvokeExpr().getMethod().toString();
		this.type = stmt.getUnit().get().getInvokeExpr().getType().toString();
		this.var = stmt.getUnit().get().getUseAndDefBoxes().get(0).getValue().toString();
	}
	
	

	public Statement getStmt() {
		return stmt;
	}



	public String getMethod() {
		return method;
	}



	public String getType() {
		return type;
	}



	public String getVar() {
		return var;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.method == null) ? 0 : this.method.hashCode());
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		result = prime * result + ((this.var == null) ? 0 : this.var.hashCode());
		return result;
	}

}
