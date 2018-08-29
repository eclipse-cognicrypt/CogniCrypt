package de.cognicrypt.staticanalyzer.performance;

import java.util.Set;

import org.eclipse.core.resources.IProject;

import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.ICrySLPerformanceListener;
import crypto.rules.CryptSLPredicate;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.handlers.AnalysisKickOff;


public class PerformanceListener implements ICrySLPerformanceListener{

	private AnalysisKickOff analysis;
	
	private PerformanceListener(AnalysisKickOff analysis) {
		this.analysis = analysis;
	}
	
	public static PerformanceListener createListener(AnalysisKickOff analysis) {
		PerformanceListener listener = new PerformanceListener(analysis);
		Activator.registerPerformanceListener(listener);
		return listener;
	}
	
	@Override
	public void afterAnalysis() {
		analysis.tearDown();
	}

	@Override
	public void afterConstraintCheck(AnalysisSeedWithSpecification arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPredicateCheck(AnalysisSeedWithSpecification arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeAnalysis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeConstraintCheck(AnalysisSeedWithSpecification arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforePredicateCheck(AnalysisSeedWithSpecification arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boomerangQueryFinished(Query arg0, BackwardQuery arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boomerangQueryStarted(Query arg0, BackwardQuery arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> arg0,
			Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg1,
			Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seedStarted(IAnalysisSeed arg0) {
		// TODO Auto-generated method stub
		
	}

}
