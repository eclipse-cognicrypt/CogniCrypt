package de.cognicrypt.staticanalyzer.sootbridge;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

public interface IIFDSAnalysisContext extends IAnalysisContext {
	
	BiDiInterproceduralCFG<Unit,SootMethod> getICFG();
	
	BiDiInterproceduralCFG<Unit,SootMethod> getBackwardICFG();

}
