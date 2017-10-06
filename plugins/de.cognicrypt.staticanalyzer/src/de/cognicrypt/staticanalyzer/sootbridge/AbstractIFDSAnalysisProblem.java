package de.cognicrypt.staticanalyzer.sootbridge;

import heros.DefaultSeeds;
import heros.InterproceduralCFG;
import heros.template.DefaultIFDSTabulationProblem;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.Value;

public abstract class AbstractIFDSAnalysisProblem<D> extends DefaultIFDSTabulationProblem<Unit, D, SootMethod, InterproceduralCFG<Unit, SootMethod>> {

	public AbstractIFDSAnalysisProblem(InterproceduralCFG<Unit, SootMethod> icfg) {
		super(icfg);
	}
	public AbstractIFDSAnalysisProblem(IIFDSAnalysisContext context) {
		super(context.getICFG());
	}
	public boolean autoAddZero() {
		return false;
	}

	public boolean computeValues() {
		return false;
	}

	public boolean followReturnsPastSeeds() {
		return true;
	}

	protected abstract Set<Unit> getStartSeed();
	public Map<Unit, Set<D>> initialSeeds() {
		return DefaultSeeds.make(this.getStartSeed(), zeroValue());
	}

}
