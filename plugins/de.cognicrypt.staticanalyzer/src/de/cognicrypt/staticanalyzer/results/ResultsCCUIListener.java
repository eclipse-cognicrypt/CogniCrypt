package de.cognicrypt.staticanalyzer.results;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.accessgraph.AccessGraph;
import boomerang.util.StmtWithMethod;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import de.cognicrypt.staticanalyzer.Utils;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import soot.SootClass;
import soot.Unit;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public class ResultsCCUIListener extends CrySLAnalysisListener {

	private ErrorMarkerGenerator markerGenerator;
	
	
	public ResultsCCUIListener(ErrorMarkerGenerator gen) {
		markerGenerator = gen;
	}
	
	@Override
	public void constraintViolation(AnalysisSeedWithSpecification arg0, ISLConstraint arg1, StmtWithMethod arg2) {
		System.out.println("Constraint " + arg1.toString() + " is violated.");
		markerGenerator.addMarker(unitToResource(arg2), arg2.getStmt().getJavaSourceStartLineNumber(), "The constraint was violated");
	}

	@Override
	public void typestateErrorAt(AnalysisSeedWithSpecification arg0, StmtWithMethod arg1) {
		System.out.print("Typestate Error " + arg1.getMethod().toString());
		markerGenerator.addMarker(unitToResource(arg1), arg1.getStmt().getJavaSourceStartLineNumber(), "Typestate error");
		
	}

	@Override
	public void callToForbiddenMethod(ClassSpecification arg0, StmtWithMethod arg1) {
		System.out.print("Call to forbidden method " + arg1.toString());
		markerGenerator.addMarker(unitToResource(arg1), arg1.getStmt().getJavaSourceStartLineNumber(), "Call to forbidden method");
	}
	
	@Override
	public void missingPredicates(AnalysisSeedWithSpecification arg0, Set<CryptSLPredicate> arg1) {
		System.out.print("Predicate is missing.");
		markerGenerator.addMarker(null, 1, "Missing Predicate");
	}

	@Override
	public void predicateContradiction(StmtWithMethod arg0, AccessGraph arg1, Entry<CryptSLPredicate, CryptSLPredicate> arg2) {
		System.out.print("Predicate is missing.");
		markerGenerator.addMarker(unitToResource(arg0), 1, "Predicate mismatch");
	}

	//Untested
	private IResource unitToResource(StmtWithMethod stmt) {
		SootClass className = stmt.getMethod().getDeclaringClass();
		return Utils.getCurrentProject().getFile("src/" + className.getName().replace(".", "/") + ".java");
	}

	@Override
	public void afterAnalysis() {
		// nothing
	}

	@Override
	public void afterConstraintCheck(AnalysisSeedWithSpecification arg0) {
		// nothing
		
	}

	@Override
	public void afterPredicateCheck(AnalysisSeedWithSpecification arg0) {
		// nothing
		
	}

	@Override
	public void beforeAnalysis() {
		// nothing
		
	}

	@Override
	public void beforeConstraintCheck(AnalysisSeedWithSpecification arg0) {
		// nothing
		
	}

	@Override
	public void beforePredicateCheck(AnalysisSeedWithSpecification arg0) {
		// nothing
		
	}

	@Override
	public void boomerangQueryFinished(IFactAtStatement arg0, AdditionalBoomerangQuery arg1) {
		// nothing
		
	}

	@Override
	public void boomerangQueryStarted(IFactAtStatement arg0, AdditionalBoomerangQuery arg1) {
		// nothing
		
	}

	@Override
	public void seedFinished(IAnalysisSeed arg0) {
		// nothing
		
	}

	@Override
	public void seedStarted(IAnalysisSeed arg0) {
		// nothing
		
	}

	@Override
	public void checkedConstraints(AnalysisSeedWithSpecification arg0, Collection<ISLConstraint> arg1) {
		// nothing
		
	}

	@Override
	public void collectedValues(AnalysisSeedWithSpecification arg0, Multimap<CallSiteWithParamIndex, Unit> arg1) {
		// nothing
		
	}

	@Override
	public void discoveredSeed(IAnalysisSeed arg0) {
		// nothing
		
	}

	@Override
	public void ensuredPredicates(Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> arg0, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> arg1, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> arg2) {
		// nothing
		
	}

	@Override
	public void onSeedFinished(IFactAtStatement arg0, AnalysisSolver<TypestateDomainValue<StateNode>> arg1) {
		// nothing
		
	}

	@Override
	public void onSeedTimeout(IFactAtStatement arg0) {
		// nothing
	}

}
