package de.cognicrypt.staticanalyzer.sootbridge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.AbstractJimpleBasedICFG;
import soot.jimple.toolkits.pointer.LocalMayAliasAnalysis;
import soot.jimple.toolkits.pointer.LocalMustAliasAnalysis;
import soot.toolkits.graph.UnitGraph;

/**
 * This class is used to cache must-alias analyses.
 */
public class AliasAnalysisManager {
	
	private final Map<SootMethod,LocalMustAliasAnalysis> methodToMustAlias;
	private final Map<SootMethod,LocalMayAliasAnalysis> methodToMayAlias;
	private final AbstractJimpleBasedICFG icfg;

	public AliasAnalysisManager(AbstractJimpleBasedICFG icfg) {
		this.icfg = icfg;
		this.methodToMustAlias = new HashMap<SootMethod, LocalMustAliasAnalysis>();
		this.methodToMayAlias = new HashMap<SootMethod, LocalMayAliasAnalysis>();
	}
	
	public boolean mustAlias(Stmt stmt, Local l1, Stmt stmt2, Local l2) {
		if(l1.equals(l2)) return true;
		SootMethod methodOf = icfg.getMethodOf(stmt);
		SootMethod methodOf2 = icfg.getMethodOf(stmt2);
		if(!methodOf.equals(methodOf2)) return false;
		LocalMustAliasAnalysis mustAliasAnalysis = getOrCreateMustAliasAnalysis(methodOf);
		return mustAliasAnalysis.mustAlias(l1, stmt, l2, stmt2);
	}

	protected LocalMustAliasAnalysis getOrCreateMustAliasAnalysis(SootMethod m) {
		LocalMustAliasAnalysis analysis = methodToMustAlias.get(m);
		if(analysis==null) {
			analysis = new LocalMustAliasAnalysis(getUnitGraph(m));
			methodToMustAlias.put(m, analysis);
		}
		return analysis;
	}

	protected LocalMayAliasAnalysis getOrCreateMayAliasAnalysis(SootMethod m) {
		LocalMayAliasAnalysis analysis = methodToMayAlias.get(m);
		if(analysis==null) {
			analysis = new LocalMayAliasAnalysis(getUnitGraph(m));
			methodToMayAlias.put(m, analysis);
		}
		return analysis;
	}

	private UnitGraph getUnitGraph(SootMethod m) {
		return (UnitGraph) icfg.getOrCreateUnitGraph(m.getActiveBody());
	}	
	
	public boolean mayAlias(Value v1, Value v2, Unit u) {
		if(v1.equals(v2)) return true;
		SootMethod methodOf = icfg.getMethodOf(u);
		LocalMayAliasAnalysis mayAliasAnalysis = getOrCreateMayAliasAnalysis(methodOf);
		return mayAliasAnalysis.mayAlias(v1, v2, u);
	}
	
	/**
	 * Returns all values that may-alias with v before u. 
	 */
	public Set<Value> mayAliases(Value v, Unit u) {
		SootMethod methodOf = icfg.getMethodOf(u);
		LocalMayAliasAnalysis mayAliasAnalysis = getOrCreateMayAliasAnalysis(methodOf);
		return mayAliasAnalysis.mayAliases(v, u);
	}

	/**
	 * Returns all values that may-alias with v at the end of the procedure. 
	 */
	public Set<Value> mayAliasesAtExit(Value v, SootMethod owner) {
		LocalMayAliasAnalysis mayAliasAnalysis = getOrCreateMayAliasAnalysis(owner);
		return mayAliasAnalysis.mayAliasesAtExit(v);
	}	
}
