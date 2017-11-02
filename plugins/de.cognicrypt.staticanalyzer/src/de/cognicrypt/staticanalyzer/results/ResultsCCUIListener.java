package de.cognicrypt.staticanalyzer.results;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLValueConstraint;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import de.cognicrypt.staticanalyzer.Utils;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.internal.JInvokeStmt;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

/**
 * This listener is notified of any misuses the analyis finds.
 * 
 * @author Stefan Krueger
 *
 */
public class ResultsCCUIListener extends CrySLAnalysisListener {

	private ErrorMarkerGenerator markerGenerator;

	public ResultsCCUIListener(ErrorMarkerGenerator gen) {
		markerGenerator = gen;
	}

	@Override
	public void constraintViolation(AnalysisSeedWithSpecification spec, ISLConstraint brokenConstraint, StmtWithMethod location) {
		StringBuilder msg = new StringBuilder();
		msg.append("The constraint ");
		evaluateBrokenConstraint(brokenConstraint, msg);
		msg.append(" was violated.");
		markerGenerator.addMarker(unitToResource(location), location.getStmt().getJavaSourceStartLineNumber(), msg.toString());
	}

	private void evaluateBrokenConstraint(ISLConstraint brokenConstraint, StringBuilder msg) {
		//TODO: Add other constraint types
		if (brokenConstraint instanceof CryptSLValueConstraint) {
			evaluateValueConstraint(brokenConstraint, msg);
		} else if (brokenConstraint instanceof CryptSLConstraint) {
			final CryptSLConstraint cryptSLConstraint = (CryptSLConstraint) brokenConstraint;
			evaluateValueConstraint(cryptSLConstraint.getRight(), msg);
		}
	}

	private void evaluateValueConstraint(ISLConstraint brokenConstraint, StringBuilder msg) {
		final CryptSLValueConstraint valCons = (CryptSLValueConstraint) brokenConstraint;
		msg.append(valCons.getVarName());
		msg.append(" € ");
		for (String val : valCons.getValueRange()) {
			msg.append(val);
			msg.append(", ");
		}
		msg.deleteCharAt(msg.length() - 2);
	}

	@Override
	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, StmtWithMethod location, Collection<SootMethod> expectedCalls) {
		StringBuilder msg = new StringBuilder();

		msg.append("Unexpected Method Call to");
		msg.append(((JInvokeStmt) location.getStmt()).getInvokeExpr().getMethod().toString());
		msg.append(". Expected a Call to  one of the Following Methods ");
		Set<String> altMethods = new HashSet<String>();
		for (SootMethod expectedCall : expectedCalls) {
			altMethods.add(expectedCall.getName());
		}
		for (String methName : altMethods) {
			msg.append(methName);
			msg.append(", ");
		}
		msg.deleteCharAt(msg.length() - 2);
		msg.append(" Here.");
		markerGenerator.addMarker(unitToResource(location), location.getStmt().getJavaSourceStartLineNumber(), msg.toString());
	}
	
	@Override
	public void callToForbiddenMethod(ClassSpecification classSpecification, StmtWithMethod location, List<CryptSLMethod> alternatives) {
		StringBuilder msg = new StringBuilder();
		msg.append("Call to forbidden method ");
		msg.append(((JInvokeStmt) location.getStmt()).getInvokeExpr().getMethod().toString());
		if (!alternatives.isEmpty()) {
			msg.append(". Instead, call method ");
			for (CryptSLMethod alt : alternatives) {
				final String methodName = alt.getMethodName();
				msg.append(methodName.substring(methodName.lastIndexOf(".") + 1));
				msg.append("(");
				for (Entry<String, String> pars : alt.getParameters()) {
					msg.append(pars.getValue());
					msg.append(", ");
				}
				msg.replace(msg.length() -2, msg.length(), ")");
			}
			msg.append(".");
		}
		markerGenerator.addMarker(unitToResource(location), location.getStmt().getJavaSourceStartLineNumber(), msg.toString());
	}


	@Override
	public void missingPredicates(AnalysisSeedWithSpecification spec, Set<CryptSLPredicate> missingPred) {
		for (CryptSLPredicate pred : missingPred) {
			StringBuilder msg = new StringBuilder();
			msg.append("Predicate ");
			msg.append(pred.getPredName());
			msg.append(" is missing.");
			markerGenerator.addMarker(unitToResource(new StmtWithMethod(spec.getStmt(), spec.getMethod())), spec.getStmt().getJavaSourceStartLineNumber(), msg.toString());
		}
	}

	@Override
	public void predicateContradiction(StmtWithMethod location, AccessGraph accessGraph, Entry<CryptSLPredicate, CryptSLPredicate> mismatchedPreds) {
		markerGenerator.addMarker(unitToResource(location), location.getStmt().getJavaSourceStartColumnNumber(), "Predicate mismatch");
	}

	//Untested
	private IResource unitToResource(StmtWithMethod stmt) {
		SootClass className = stmt.getMethod().getDeclaringClass();
		return Utils.getCurrentProject().getFile("src/" + className.getName().replace(".", "/") + ".java");
	}

	@Override
	public void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt) {
		// TODO Auto-generated method stub

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
