package de.cognicrypt.staticanalyzer.results;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
import crypto.rules.CryptSLComparisonConstraint.CompOp;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLValueConstraint;
import crypto.rules.TransitionEdge;
import crypto.typestate.CallSiteWithParamIndex;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.Utils;
import soot.SootClass;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

/**
 * This listener is notified of any misuses the analysis finds.
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

	/**
	 * @return the currentProject
	 */
	public IProject getReporterProject() {
		return currentProject;
	}

	@Override
	public void callToForbiddenMethod(final ClassSpecification arg0, final Statement location, final List<CryptSLMethod> alternatives) {
		final StringBuilder msg = new StringBuilder();
		msg.append("Call to forbidden method ");
		msg.append(location.getMethod());
		if (!alternatives.isEmpty()) {
			msg.append(". Instead, call to method ");
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
		this.markerGenerator.addMarker(unitToResource(location), location.getUnit().get().getJavaSourceStartLineNumber(), msg.toString());

	}

	@Override
	public void constraintViolation(final AnalysisSeedWithSpecification seed, final ISLConstraint brokenConstraint, final Statement location) {
		this.markerGenerator.addMarker(unitToResource(location), location.getUnit().get().getJavaSourceStartLineNumber(), evaluateBrokenConstraint(brokenConstraint));
	}

	private String evaluateBrokenConstraint(final ISLConstraint brokenConstraint) {
		StringBuilder msg = new StringBuilder();
		if (brokenConstraint instanceof CryptSLValueConstraint) {
			return evaluateValueConstraint((CryptSLValueConstraint) brokenConstraint);
		} else if (brokenConstraint instanceof CryptSLArithmeticConstraint) {
			final CryptSLArithmeticConstraint brokenArthConstraint = (CryptSLArithmeticConstraint) brokenConstraint;
			msg.append(brokenArthConstraint.getLeft());
			msg.append(" ");
			msg.append(brokenArthConstraint.getOperator());
			msg.append(" ");
			msg.append(brokenArthConstraint.getRight());
		} else if (brokenConstraint instanceof CryptSLComparisonConstraint) {
			final CryptSLComparisonConstraint brokenCompCons = (CryptSLComparisonConstraint) brokenConstraint;
			msg.append("Variable ");
			msg.append(brokenCompCons.getLeft().getLeft().getName());
			msg.append("must be ");
			msg.append(evaluateCompOp(brokenCompCons.getOperator()));
			msg.append(brokenCompCons.getRight().getLeft().getName());
		} else if (brokenConstraint instanceof CryptSLConstraint) {
			final CryptSLConstraint cryptSLConstraint = (CryptSLConstraint) brokenConstraint;
			final CryptSLValueConstraint leftSide = (CryptSLValueConstraint) cryptSLConstraint.getLeft();
			final CryptSLValueConstraint rightSide = (CryptSLValueConstraint) cryptSLConstraint.getRight();
			switch (cryptSLConstraint.getOperator()) {
				case and:
					msg.append(evaluateValueConstraint(leftSide));
					msg.append(" or ");
					msg.append(evaluateValueConstraint(rightSide));
					break;
				case implies:
					msg.append(evaluateValueConstraint(rightSide));
					break;
				case or:
					msg.append(evaluateValueConstraint(leftSide));
					msg.append(" and ");
					msg.append(evaluateValueConstraint(rightSide));
					break;
				default:
					break;
			}

		}
		return msg.toString();
	}

	private String evaluateCompOp(CompOp operator) {
		switch (operator) {
			case ge:
				return " at least ";
			case g:
				return " greater than ";
			case l:
				return " lesser than ";
			case le:
				return " at most ";
			default:
				return "equal to";
		}
	}

	private String evaluateValueConstraint(final CryptSLValueConstraint brokenConstraint) {
		StringBuilder msg = new StringBuilder();
		msg.append(brokenConstraint.getVarName());
		msg.append(" should be any of {");
		for (final String val : brokenConstraint.getValueRange()) {
			msg.append(val);
			msg.append(", ");
		}
		msg.deleteCharAt(msg.length() - 3);
		return msg.append('}').toString();
	}

	@Override
	public void missingPredicates(final AnalysisSeedWithSpecification spec, final Set<CryptSLPredicate> missingPred) {
		for (final CryptSLPredicate pred : missingPred) {
			final StringBuilder msg = new StringBuilder();
			msg.append("Predicate ");
			msg.append(pred.getPredName());
			msg.append(" is missing.");
			final Statement stmt = spec.stmt();
			this.markerGenerator.addMarker(unitToResource(stmt), stmt.getUnit().get().getJavaSourceStartLineNumber(), msg.toString());
		}
	}

	@Override
	public void predicateContradiction(final Node<Statement, Val> location, final Entry<CryptSLPredicate, CryptSLPredicate> arg1) {
		this.markerGenerator.addMarker(unitToResource(location.stmt()), location.stmt().getUnit().get().getJavaSourceStartColumnNumber(), "Predicate mismatch");
	}

	@Override
	public void typestateErrorAt(final AnalysisSeedWithSpecification seed, final Statement location, final Collection<SootMethod> expectedCalls) {
		final StringBuilder msg = new StringBuilder();

		msg.append("Unexpected Method Call to");
		msg.append(location.getMethod());
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
		this.markerGenerator.addMarker(unitToResource(location), location.getUnit().get().getJavaSourceStartLineNumber(), msg.toString());
	}

	@Override
	public void typestateErrorEndOfLifeCycle(final AnalysisSeedWithSpecification seed, final Val value, final Statement location, Set<TransitionEdge> expectedCalls) {
		final StringBuilder msg = new StringBuilder();

		msg.append("Operation with ");
		final String type = value.value().getType().getEscapedName();
		msg.append(type.substring(type.lastIndexOf('.') + 1));
		msg.append(" object not completed. Expected call to ");

		final Iterator<TransitionEdge> expectedIterator = expectedCalls.iterator();
		while (expectedIterator.hasNext()) {
			final String methodName = expectedIterator.next().getLabel().get(0).getMethodName();

			msg.append(methodName.substring(methodName.lastIndexOf('.') + 1));
			msg.append("()");
			if (expectedIterator.hasNext()) {
				msg.append(" or ");
			}
		}
		this.markerGenerator.addMarker(unitToResource(location), location.getUnit().get().getJavaSourceStartLineNumber(), msg.toString());
	}

	private IResource unitToResource(final Statement stmt) {
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
	public void unevaluableConstraint(AnalysisSeedWithSpecification seed, ISLConstraint con, Statement location) {
		final StringBuilder msg = new StringBuilder();

		msg.append("Constraint ");
		msg.append(con);
		msg.append(" could not be evaluted due to insufficient information.");
		this.markerGenerator.addMarker(unitToResource(location), seed.stmt().getUnit().get().getJavaSourceStartLineNumber(), msg.toString());
	}

	@Override
	public void afterAnalysis() {
		// Nothing
	}

	@Override
	public void afterConstraintCheck(final AnalysisSeedWithSpecification arg0) {
		// nothing
	}

	@Override
	public void afterPredicateCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void beforeAnalysis() {
		// Nothing

	}

	@Override
	public void beforeConstraintCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void beforePredicateCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void boomerangQueryFinished(final Query arg0, final BackwardQuery arg1) {
		// Nothing
	}

	@Override
	public void boomerangQueryStarted(final Query arg0, final BackwardQuery arg1) {
		// Nothing
	}

	@Override
	public void checkedConstraints(final AnalysisSeedWithSpecification arg0, final Collection<ISLConstraint> arg1) {
		// Nothing
	}

	@Override
	public void collectedValues(final AnalysisSeedWithSpecification arg0, final Multimap<CallSiteWithParamIndex, Statement> arg1) {
		// Nothing
	}

	@Override
	public void discoveredSeed(final IAnalysisSeed arg0) {
		// Nothing
	}

	@Override
	public void ensuredPredicates(final Table<Statement, Val, Set<EnsuredCryptSLPredicate>> arg0, final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg1, final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg2) {
		// Nothing
	}

	@Override
	public void onSeedFinished(final IAnalysisSeed arg0, final WeightedBoomerang<TransitionFunction> arg1) {
		// Nothing
	}

	@Override
	public void onSeedTimeout(final Node<Statement, Val> arg0) {
		//Nothing
	}

	@Override
	public void seedStarted(final IAnalysisSeed arg0) {
		// Nothing
	}
}
