package de.cognicrypt.staticanalyzer.results;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
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
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLValueConstraint;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import de.cognicrypt.staticanalyzer.Activator;
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

	private final ErrorMarkerGenerator markerGenerator;
	private final IProject currentProject;

	public ResultsCCUIListener(final IProject curProj, final ErrorMarkerGenerator gen) {
		this.currentProject = curProj;
		this.markerGenerator = gen;
	}

	@Override
	public void constraintViolation(final AnalysisSeedWithSpecification spec, final ISLConstraint brokenConstraint, final StmtWithMethod location) {
		final StringBuilder msg = new StringBuilder();
		evaluateBrokenConstraint(brokenConstraint, msg);
		this.markerGenerator.addMarker(unitToResource(location), location.getStmt().getJavaSourceStartLineNumber(), msg.toString());
	}

	private void evaluateBrokenConstraint(final ISLConstraint brokenConstraint, final StringBuilder msg) {
		if (brokenConstraint instanceof CryptSLValueConstraint) {
			evaluateValueConstraint(brokenConstraint, msg);
		} else if (brokenConstraint instanceof CryptSLArithmeticConstraint) {
			final CryptSLArithmeticConstraint brokenArthConstraint = (CryptSLArithmeticConstraint) brokenConstraint;
			msg.append(brokenArthConstraint.getLeft());
			msg.append(" ");
			msg.append(brokenArthConstraint.getOperator());
			msg.append(" ");
			msg.append(brokenArthConstraint.getRight());
		} else if (brokenConstraint instanceof CryptSLConstraint) {
			final CryptSLConstraint cryptSLConstraint = (CryptSLConstraint) brokenConstraint;
			switch (cryptSLConstraint.getOperator()) {
				case and:
					evaluateValueConstraint(cryptSLConstraint.getLeft(), msg);
					msg.append(" or ");
					evaluateValueConstraint(cryptSLConstraint.getRight(), msg);
					break;
				case implies:
					evaluateValueConstraint(cryptSLConstraint.getRight(), msg);
					break;
				case or:
					evaluateValueConstraint(cryptSLConstraint.getLeft(), msg);
					msg.append(" or ");
					evaluateValueConstraint(cryptSLConstraint.getRight(), msg);
					break;
				default:
					break;
			}

		}
	}

	private void evaluateValueConstraint(final ISLConstraint brokenConstraint, final StringBuilder msg) {
		final CryptSLValueConstraint valCons = (CryptSLValueConstraint) brokenConstraint;
		msg.append(valCons.getVarName());
		msg.append(" should be any of {");
		for (final String val : valCons.getValueRange()) {
			msg.append(val);
			msg.append(", ");
		}
		msg.deleteCharAt(msg.length() - 3);
		msg.append('}');
	}

	@Override
	public void typestateErrorAt(final AnalysisSeedWithSpecification classSpecification, final StmtWithMethod location, final Collection<SootMethod> expectedCalls) {
		final StringBuilder msg = new StringBuilder();

		msg.append("Unexpected Method Call to");
		msg.append(((JInvokeStmt) location.getStmt()).getInvokeExpr().getMethod().toString());
		msg.append(". Expected a Call to  one of the Following Methods ");
		final Set<String> altMethods = new HashSet<>();
		for (final SootMethod expectedCall : expectedCalls) {
			altMethods.add(expectedCall.getName());
		}
		for (final String methName : altMethods) {
			msg.append(methName);
			msg.append(", ");
		}
		msg.deleteCharAt(msg.length() - 2);
		msg.append(" Here.");
		this.markerGenerator.addMarker(unitToResource(location), location.getStmt().getJavaSourceStartLineNumber(), msg.toString());
	}

	@Override
	public void callToForbiddenMethod(final ClassSpecification classSpecification, final StmtWithMethod location, final List<CryptSLMethod> alternatives) {
		final StringBuilder msg = new StringBuilder();
		msg.append("Call to forbidden method ");
		msg.append(((JInvokeStmt) location.getStmt()).getInvokeExpr().getMethod().toString());
		if (!alternatives.isEmpty()) {
			msg.append(". Instead, call method ");
			for (final CryptSLMethod alt : alternatives) {
				final String methodName = alt.getMethodName();
				msg.append(methodName.substring(methodName.lastIndexOf(".") + 1));
				msg.append("(");
				for (final Entry<String, String> pars : alt.getParameters()) {
					msg.append(pars.getValue());
					msg.append(", ");
				}
				msg.replace(msg.length() - 2, msg.length(), ")");
			}
			msg.append(".");
		}
		this.markerGenerator.addMarker(unitToResource(location), location.getStmt().getJavaSourceStartLineNumber(), msg.toString());
	}

	@Override
	public void missingPredicates(final AnalysisSeedWithSpecification spec, final Set<CryptSLPredicate> missingPred) {
		for (final CryptSLPredicate pred : missingPred) {
			final StringBuilder msg = new StringBuilder();
			msg.append("Predicate ");
			msg.append(pred.getPredName());
			msg.append(" is missing.");
			this.markerGenerator.addMarker(unitToResource(new StmtWithMethod(spec.getStmt(), spec.getMethod())), spec.getStmt().getJavaSourceStartLineNumber(), msg.toString());
		}
	}

	@Override
	public void predicateContradiction(final StmtWithMethod location, final AccessGraph accessGraph, final Entry<CryptSLPredicate, CryptSLPredicate> mismatchedPreds) {
		this.markerGenerator.addMarker(unitToResource(location), location.getStmt().getJavaSourceStartColumnNumber(), "Predicate mismatch");
	}

	private IResource unitToResource(final StmtWithMethod stmt) {
		final SootClass className = stmt.getMethod().getDeclaringClass();
		try {
			return Utils.findClassByName(className, this.currentProject);
		} catch (final ClassNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		//Fall-back path when retrieval of actual path fails. If it does, the statement below should be left untouched and the actual bug should be fixed.
		return this.currentProject.getFile("src/" + className.getName().replace(".", "/") + ".java");

	}

	@Override
	public void typestateErrorEndOfLifeCycle(final AnalysisSeedWithSpecification classSpecification, final StmtWithMethod stmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterAnalysis() {
		// nothing
	}

	@Override
	public void afterConstraintCheck(final AnalysisSeedWithSpecification arg0) {
		// nothing

	}

	@Override
	public void afterPredicateCheck(final AnalysisSeedWithSpecification arg0) {
		// nothing

	}

	@Override
	public void beforeAnalysis() {
		// nothing

	}

	@Override
	public void beforeConstraintCheck(final AnalysisSeedWithSpecification arg0) {
		// nothing

	}

	@Override
	public void beforePredicateCheck(final AnalysisSeedWithSpecification arg0) {
		// nothing

	}

	@Override
	public void boomerangQueryFinished(final IFactAtStatement arg0, final AdditionalBoomerangQuery arg1) {
		// nothing

	}

	@Override
	public void boomerangQueryStarted(final IFactAtStatement arg0, final AdditionalBoomerangQuery arg1) {
		// nothing

	}

	@Override
	public void seedFinished(final IAnalysisSeed arg0) {
		// nothing

	}

	@Override
	public void seedStarted(final IAnalysisSeed arg0) {
		// nothing

	}

	@Override
	public void checkedConstraints(final AnalysisSeedWithSpecification arg0, final Collection<ISLConstraint> arg1) {
		// nothing

	}

	@Override
	public void collectedValues(final AnalysisSeedWithSpecification arg0, final Multimap<CallSiteWithParamIndex, Unit> arg1) {
		// nothing

	}

	@Override
	public void discoveredSeed(final IAnalysisSeed arg0) {
		// nothing

	}

	@Override
	public void ensuredPredicates(final Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> arg0, final Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> arg1, final Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> arg2) {
		// nothing

	}

	@Override
	public void onSeedFinished(final IFactAtStatement arg0, final AnalysisSolver<TypestateDomainValue<StateNode>> arg1) {
		// nothing

	}

	@Override
	public void onSeedTimeout(final IFactAtStatement arg0) {
		// nothing
	}
}
