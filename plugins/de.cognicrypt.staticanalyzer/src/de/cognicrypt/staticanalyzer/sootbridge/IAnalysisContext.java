package de.cognicrypt.staticanalyzer.sootbridge;

import java.util.Set;

import soot.Local;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Stmt;

public interface IAnalysisContext {

	public SootMethod getSootMethod();
	
	public boolean mustAlias(Stmt stmt, Local l, Stmt stmt2, Local l2);
	
	public Set<Value> mayAliasesAtExit(Value v, SootMethod owner);
	
	public IAnalysisConfiguration getAnalysisConfiguration();
	
	public void reportError(ErrorMarker... result);


}