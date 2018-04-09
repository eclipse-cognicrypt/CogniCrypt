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
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLComparisonConstraint.CompOp;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import crypto.typestate.CallSiteWithParamIndex;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.Constants;
import de.cognicrypt.staticanalyzer.Utils;
import soot.ArrayType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

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
	public void reportError(AbstractError error) {
		String message = "";
		if (error instanceof ConstraintError) {
			ConstraintError consError = (ConstraintError) error;
			message = evaluateBrokenConstraint(consError.getExtractedValues(), consError.getBrokenConstraint(), consError.getErrorLocation());
		} else if (error instanceof ForbiddenMethodError) {
			ForbiddenMethodError err = (ForbiddenMethodError) error;
			message = callToForbiddenMethod(err.getErrorLocation().getUnit().get().getInvokeExpr().getMethod(), err.getAlternatives());
		} else if (error instanceof TypestateError) {
			TypestateError err = (TypestateError) error;
			message = typestateErrorAt(err.getErrorLocation().getMethod(), err.getExpectedMethodCalls());
		} else if (error instanceof IncompleteOperationError) {
			IncompleteOperationError err = (IncompleteOperationError) error;
			message = typestateErrorEndOfLifeCycle(err.getErrorVariable(), err.getExpectedMethodCalls());
		} else if (error instanceof RequiredPredicateError) {
			RequiredPredicateError predErr = (RequiredPredicateError) error;
			message = missingPredicates(predErr.getContradictedPredicate(), predErr.getExtractedValues());
		}
		this.markerGenerator.addMarker(unitToResource(error.getErrorLocation()), error.getErrorLocation().getUnit().get().getJavaSourceStartLineNumber(), message);
	}

	public String callToForbiddenMethod(final SootMethod foundCall, final Collection<SootMethod> alternatives) {
		final StringBuilder msg = new StringBuilder();
		msg.append("Detected call to forbidden method");
		String methodDecl = foundCall.getSubSignature();
		msg.append(methodDecl.substring(methodDecl.indexOf(" ")).replace("<init>", foundCall.getDeclaringClass().getShortJavaStyleName()));
		if (!alternatives.isEmpty()) {
			msg.append(". Instead, call method ");
			for (final SootMethod alt : alternatives) {
				String altMethodDecl = alt.getSubSignature();
				msg.append(altMethodDecl.substring(altMethodDecl.indexOf(" ")).replace("<init>", alt.getDeclaringClass().getShortJavaStyleName()));
			}
			msg.append(".");
		}
		return msg.toString();
	}

	private String evaluateBrokenConstraint(Multimap<CallSiteWithParamIndex, Statement> extractedValues, final ISLConstraint brokenConstraint, Statement location) {
		StringBuilder msg = new StringBuilder();
		if (brokenConstraint instanceof CryptSLPredicate) {
			CryptSLPredicate brokenPred = (CryptSLPredicate) brokenConstraint;

			switch (brokenPred.getPredName()) {
				case "neverTypeOf":
					if (location.getUnit().get() instanceof JAssignStmt) {
						msg.append("Variable ");
						msg.append(((JAssignStmt) location.getUnit().get()).getLeftOp());
					} else {
						msg.append("This variable");
					}
					msg.append(" must not be of type ");
					msg.append(brokenPred.getParameters().get(1).getName());
					msg.append(".");
					break;
			}
		} else if (brokenConstraint instanceof CryptSLValueConstraint) {
			return evaluateValueConstraint(extractedValues, (CryptSLValueConstraint) brokenConstraint, location);
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
			final ISLConstraint leftSide = cryptSLConstraint.getLeft();
			final ISLConstraint rightSide = cryptSLConstraint.getRight();
			switch (cryptSLConstraint.getOperator()) {
				case and:
					msg.append(evaluateBrokenConstraint(extractedValues, leftSide, location));
					msg.append(" or ");
					msg.append(evaluateBrokenConstraint(extractedValues, rightSide, location));
					break;
				case implies:
					msg.append(evaluateBrokenConstraint(extractedValues, rightSide, location));
					break;
				case or:
					msg.append(evaluateBrokenConstraint(extractedValues, leftSide, location));
					msg.append(" and ");
					msg.append(evaluateBrokenConstraint(extractedValues, rightSide, location));
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

	private String evaluateValueConstraint(Multimap<CallSiteWithParamIndex, Statement> extractedValues, final CryptSLValueConstraint brokenConstraint, Statement location) {
		CryptSLObject crySLVar = brokenConstraint.getVar();
		StringBuilder msg = new StringBuilder(extractVarName(extractedValues, location, crySLVar));

		msg.append(" should be any of ");
		CryptSLSplitter splitter = brokenConstraint.getVar().getSplitter();
		if (splitter != null) {
			Stmt stmt = location.getUnit().get();
			String[] splitValues = new String[] { "" };
			if (stmt instanceof AssignStmt) {
				Value rightSide = ((AssignStmt) stmt).getRightOp();
				if (rightSide instanceof Constant) {
					splitValues = Utils.filterQuotes(rightSide.toString()).split(splitter.getSplitter());
				} else if (rightSide instanceof AbstractInvokeExpr) {
					List<Value> args = ((AbstractInvokeExpr) rightSide).getArgs();
					for (Value arg : args) {
						if (arg.getType().toQuotedString().equals(brokenConstraint.getVar().getJavaType())) {
							splitValues = Utils.filterQuotes(arg.toString()).split(splitter.getSplitter());
							break;
						}
					}
				}
			} else {
				splitValues = Utils.filterQuotes(stmt.getInvokeExpr().getUseBoxes().get(0).getValue().toString()).split(splitter.getSplitter());
			}
			if (splitValues.length >= splitter.getIndex()) {
				for (int i = 0; i < splitter.getIndex(); i++) {
					msg.append(splitValues[i]);
					msg.append(splitter.getSplitter());
				}
			}
		}
		msg.append("{");
		for (final String val : brokenConstraint.getValueRange()) {
			if (val.isEmpty()) {
				msg.append("Empty String");
			} else {
				msg.append(val);
			}
			msg.append(", ");
		}
		msg.delete(msg.length() - 2, msg.length());
		return msg.append('}').toString();
	}

	private String extractVarName(Multimap<CallSiteWithParamIndex, Statement> extractedValues, Statement location, CryptSLObject crySLVar) {
		StringBuilder msg = new StringBuilder();
		Stmt allocSite = location.getUnit().get();
		if (allocSite instanceof AssignStmt && ((AssignStmt) allocSite).getLeftOp().getType().toQuotedString().equals(crySLVar.getJavaType())) {
			AssignStmt as = (AssignStmt) allocSite;
			msg.append(Constants.VAR);
			msg.append(as.getLeftOp().toString());
			return msg.toString();
		}

		for (ValueBox par : allocSite.getInvokeExpr().getUseBoxes()) {
			Value value = par.getValue();
			if (!(value instanceof Constant)) {
				boolean neverFound = true;
				for (CallSiteWithParamIndex a : extractedValues.keySet()) {
					if (a.getVarName().equals(crySLVar.getVarName())) {
						if (a.fact().value().getType().equals(par.getValue().getType())) {
							String varName = par.getValue().toString();
							if (varName.matches("\\$[a-z][0-9]+")) {
								msg.append(Constants.OBJECT_OF_TYPE);
								msg.append(par.getValue().getType().toQuotedString());
								neverFound = false;
							} else {
								msg.append(Constants.VAR);
								msg.append(varName);
								neverFound = false;
							}
							break;
						}
					}
					if (neverFound) {
						Type valueType = value.getType();
						String type = (valueType instanceof ArrayType) ? ((ArrayType) valueType).getArrayElementType().toQuotedString() : valueType.toQuotedString();
						if (crySLVar.getJavaType().equals(type)) {
							String varName = par.getValue().toString();
							if (varName.matches("\\$[a-z][0-9]+")) {
								msg.append(Constants.OBJECT_OF_TYPE);
								msg.append(par.getValue().getType().toQuotedString());
								neverFound = false;
							} else {
								msg.append(Constants.VAR);
								msg.append(varName);
								neverFound = false;
							}
							break;
						}
					}
				}
			} else {
				if (((Constant) value).getType().toQuotedString().equals(crySLVar.getJavaType())) {
					msg.append(value);
				}
			}
		}
		return msg.toString();
	}

	public String missingPredicates(final CryptSLPredicate missingPred, Multimap<CallSiteWithParamIndex, Statement> extractedValues) {
		final StringBuilder msg = new StringBuilder();
		Statement stmt = missingPred.getLocation();
		msg.append(extractVarName(extractedValues, stmt, (CryptSLObject) missingPred.getParameters().get(0)));
		msg.append(" was not properly ");
		String predName = missingPred.getPredName();
		int index = Utils.getFirstIndexofUCL(predName);

		if (index == -1) {
			msg.append(predName);
		} else {
			msg.append(predName.substring(0, index));
			msg.append(" as ");
			msg.append(predName.substring(index).toLowerCase());
		}
		msg.append(".");
		return msg.toString();
	}

	@Override
	public void predicateContradiction(final Node<Statement, Val> location, final Entry<CryptSLPredicate, CryptSLPredicate> arg1) {
		this.markerGenerator.addMarker(unitToResource(location.stmt()), location.stmt().getUnit().get().getJavaSourceStartColumnNumber(), "Predicate mismatch");
	}

	public String typestateErrorAt(final SootMethod foundMethod, final Collection<SootMethod> expectedCalls) {
		final StringBuilder msg = new StringBuilder();

		msg.append("Unexpected method call to");
		msg.append(foundMethod);
		msg.append(". Expected a call to  one of the following methods ");
		final Set<String> altMethods = new HashSet<>();
		for (final SootMethod expectedCall : expectedCalls) {
			altMethods.add(expectedCall.getName());
		}
		for (final String methName : altMethods) {
			msg.append(methName);
			msg.append(", ");
		}
		msg.deleteCharAt(msg.length() - 2);
		msg.append(" here.");
		return msg.toString();
	}

	public String typestateErrorEndOfLifeCycle(final Val value, Collection<SootMethod> expectedCalls) {
		final StringBuilder msg = new StringBuilder();

		msg.append("Missing call ");
		final Iterator<SootMethod> expectedIterator = expectedCalls.iterator();
		while (expectedIterator.hasNext()) {
			SootMethod expCall = expectedIterator.next();
			String methodDecl = expCall.getSubSignature();
			msg.append(methodDecl.substring(methodDecl.indexOf(" ")).replace("<init>", expCall.getDeclaringClass().getShortJavaStyleName()));
			if (expectedIterator.hasNext()) {
				msg.append(" or ");
			}
		}
		msg.append(" on object ");
		msg.append(value.value());
		msg.append(".");
		return msg.toString();
	}

	private IResource unitToResource(final Statement stmt) {
		final SootClass className = stmt.getMethod().getDeclaringClass();
		try {
			return Utils.findClassByName(className, this.currentProject);
		} catch (final ClassNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		//Fall-back path when retrieval of actual path fails. If the statement below fails, it should be left untouched as the actual bug is above.
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
