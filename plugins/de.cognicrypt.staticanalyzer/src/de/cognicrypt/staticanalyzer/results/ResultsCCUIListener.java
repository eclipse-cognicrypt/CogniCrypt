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

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLValueConstraint;
import crypto.typestate.CallSiteWithParamIndex;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.Utils;
import soot.SootClass;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
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
	public void constraintViolation(AnalysisSeedWithSpecification arg0, ISLConstraint brokenConstraint, Statement location) {
		StringBuilder msg = new StringBuilder();
		msg.append("The constraint ");
		evaluateBrokenConstraint(brokenConstraint, msg);
		msg.append(" was violated.");
		markerGenerator.addMarker(unitToResource(location), location.getUnit().get().getJavaSourceStartLineNumber(), msg.toString());		
	}


	private void evaluateBrokenConstraint(ISLConstraint brokenConstraint, StringBuilder msg) {
		if (brokenConstraint instanceof CryptSLValueConstraint) {
			evaluateValueConstraint(brokenConstraint, msg);
		} else if (brokenConstraint instanceof CryptSLComparisonConstraint) {
			CryptSLArithmeticConstraint brokenArthConstraint = (CryptSLArithmeticConstraint) brokenConstraint;
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
	public void missingPredicates(AnalysisSeedWithSpecification spec, Set<CryptSLPredicate> missingPred) {
		for (CryptSLPredicate pred : missingPred) {
			StringBuilder msg = new StringBuilder();
			msg.append("Predicate ");
			msg.append(pred.getPredName());
			msg.append(" is missing.");
			final Statement stmt = spec.stmt();
			markerGenerator.addMarker(unitToResource(stmt), stmt.getUnit().get().getJavaSourceStartLineNumber(), msg.toString());
		}
	}

	private IResource unitToResource(Statement stmt) {
		SootClass className = stmt.getMethod().getDeclaringClass();
		final IProject currentProject = Utils.getCurrentProject();
		try {
			return Utils.findClassByName(className, currentProject);
		} catch (ClassNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		//Fall-back path when retrieval of actual path fails. If it does, the statement below should be left untouched and the actual bug should be fixed.
		return currentProject.getFile("src/" + className.getName().replace(".", "/") + ".java");
		
	}

	@Override
	public void boomerangQueryFinished(Query arg0, BackwardQuery arg1) {
		// Nothing
		
	}

	@Override
	public void boomerangQueryStarted(Query arg0, BackwardQuery arg1) {
		// Nothing
		
	}

	@Override
	public void callToForbiddenMethod(ClassSpecification arg0, Statement location, List<CryptSLMethod> alternatives) {
		StringBuilder msg = new StringBuilder();
		msg.append("Call to forbidden method ");
		msg.append(location.getMethod());
		if (!alternatives.isEmpty()) {
			msg.append(". Instead, call to method ");
			for (CryptSLMethod alt : alternatives) {
				final String methodName = alt.getMethodName();
				msg.append(methodName.substring(methodName.lastIndexOf(".") + 1));
				msg.append("(");
				for (Entry<String, String> pars : alt.getParameters()) {
					msg.append(pars.getValue());
					msg.append(", ");
				}
				msg.replace(msg.length() - 2, msg.length(), ")");
			}
			msg.append(".");
		}
		markerGenerator.addMarker(unitToResource(location), location.getUnit().get().getJavaSourceStartLineNumber(), msg.toString());
	
		
	}

	@Override
	public void collectedValues(AnalysisSeedWithSpecification arg0, Multimap<CallSiteWithParamIndex, Statement> arg1) {
		// Nothing
		
	}

	@Override
	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> arg0, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg1, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg2) {
		// Nothing
		
	}

	@Override
	public void onSeedFinished(IAnalysisSeed arg0, WeightedBoomerang<TransitionFunction> arg1) {
		// Nothing
		
	}

	@Override
	public void onSeedTimeout(Node<Statement, Val> arg0) {
		//Nothing
	}

	@Override
	public void predicateContradiction(Node<Statement, Val> location, Entry<CryptSLPredicate, CryptSLPredicate> arg1) {
		markerGenerator.addMarker(unitToResource(location.stmt()), location.stmt().getUnit().get().getJavaSourceStartColumnNumber(), "Predicate mismatch");
	}

	@Override
	public void typestateErrorAt(AnalysisSeedWithSpecification arg0, Statement location, Collection<SootMethod> expectedCalls) {
		StringBuilder msg = new StringBuilder();

		msg.append("Unexpected Method Call to");
		msg.append(location.getMethod());
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
		markerGenerator.addMarker(unitToResource(location), location.getUnit().get().getJavaSourceStartLineNumber(), msg.toString());
		
	}

	@Override
	public void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification seed, Statement location) {
		StringBuilder msg = new StringBuilder();

		msg.append("Operation with ");
		msg.append(seed.getSpec().getRule().getClassName());
		msg.append(" object not completed.");
		markerGenerator.addMarker(unitToResource(location), seed.stmt().getUnit().get().getJavaSourceStartLineNumber(), msg.toString());
	}

	@Override
	public void afterAnalysis() {
		// Nothing
		
	}

	@Override
	public void afterConstraintCheck(AnalysisSeedWithSpecification arg0) {
		// Nothing
		
	}

	@Override
	public void afterPredicateCheck(AnalysisSeedWithSpecification arg0) {
		// Nothing
		
	}

	@Override
	public void beforeAnalysis() {
		// Nothing
		
	}

	@Override
	public void beforeConstraintCheck(AnalysisSeedWithSpecification arg0) {
		// Nothing
		
	}

	@Override
	public void beforePredicateCheck(AnalysisSeedWithSpecification arg0) {
		// Nothing
		
	}

	@Override
	public void seedStarted(IAnalysisSeed arg0) {
		// Nothing
		
	}

	@Override
	public void checkedConstraints(AnalysisSeedWithSpecification arg0, Collection<ISLConstraint> arg1) {
		// Nothing
		
	}

	@Override
	public void discoveredSeed(IAnalysisSeed arg0) {
		// Nothing
		
	}
}
