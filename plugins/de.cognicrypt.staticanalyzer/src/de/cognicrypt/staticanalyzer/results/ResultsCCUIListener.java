/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.results;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Node;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLPredicate;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.Severities;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.view.AnalysisData;
import de.cognicrypt.staticanalyzer.view.ResultsUnit;
import de.cognicrypt.staticanalyzer.view.StatisticsView;
import de.cognicrypt.staticanalyzer.view.Stats;
import de.cognicrypt.utils.Utils;
import de.cognicrypt.utils.XMLParser;
import soot.SootClass;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.internal.JimpleLocalBox;
import soot.tagkit.AbstractHost;
import typestate.TransitionFunction;

/**
 * This listener is notified of any misuses the analysis finds. It also reports
 * the results of the analysis to the Statistics View
 *
 * @author Stefan Krueger
 * @author Andre Sonntag
 * @author Adnan Manzoor
 */
public class ResultsCCUIListener extends CrySLAnalysisListener {

	private final ErrorMarkerGenerator markerGenerator;
	private final IProject currentProject;
	private ArrayList<String> suppressedWarningIds;
	private String warningFilePath;
	private XMLParser xmlParser;
	private Boolean depOnly = false;
	private static Stats stat;

	private int totalSeeds;
	private int processedSeeds;
	private int percentCompleted;
	private int workUnitsCompleted;
	private boolean cgGenComplete;
	private int work = 0;
	private int tempWork = 0;
	
	private ResultsCCUIListener(final IProject curProj, final ErrorMarkerGenerator gen) {
		this.currentProject = curProj;
		this.markerGenerator = gen;
		this.suppressedWarningIds = new ArrayList<>();
	}

	public static ResultsCCUIListener createListener(final IProject project) {
		final ResultsCCUIListener listener = new ResultsCCUIListener(project, new ErrorMarkerGenerator());
		Activator.registerResultsListener(listener);
		return listener;
	}

	/**
	 * @return the currentProject
	 */
	public IProject getReporterProject() {
		return this.currentProject;
	}

	public void analyzeDependenciesOnly(final Boolean depOnly) {
		this.depOnly = depOnly;
	}

	@Override
	public void reportError(final AbstractError error) {
		IResource sourceFile = null;
		if (this.depOnly) {
			return;
		}
		final String errorMessage = error.toErrorMarkerString();
		final Statement errorLocation = error.getErrorLocation();
		final String errorJimpleBody = errorLocation.getMethod().getActiveBody().toString();
		final String errorCrySLRuleName = error.getRule().getClassName();
		
		sourceFile = unitToResource(errorLocation);
		final int lineNumber = ((AbstractHost) errorLocation.getUnit().get()).getJavaSourceStartLineNumber();

		final int stmtId = error.hashCode();
		HashMap<String, String> errorInfoMap = new HashMap<>();

		ICompilationUnit javaFile = (ICompilationUnit) JavaCore.create(sourceFile);
		String className = "";
		try {
			for (IPackageDeclaration decl : javaFile.getPackageDeclarations()) {
				className += decl.getElementName() + ".";
			}
		} catch (JavaModelException e1) {
		}
		className += javaFile.getElementName().substring(0, javaFile.getElementName().lastIndexOf("."));
		
		if (stat.getClassesAnalysed().containsKey(className)) {
			AnalysisData data = stat.getClassesAnalysed().get(className);
			data.addError(error);
			data.setHealth(false);
		} else {
			AnalysisData data = new AnalysisData();
			data.addError(error);
			data.setHealth(false);
			Map<String, AnalysisData> classesAnalysedMap = stat.getClassesAnalysed();
			classesAnalysedMap.put(className, data);
		}

		/*
		 * Adding of new marker types for new errors: 1) add new ErrorMarker extension point in plugin.xml 2) add new markerResolutionGenerator tag in plugin.xml 3) add new Marker
		 * constant in Constants.java (CogniCrypt Core) 4) add new else if in the following query
		 */

		String markerType;
		if (error instanceof ForbiddenMethodError) {
			markerType = Constants.FORBIDDEN_METHOD_MARKER_TYPE;
		} else if (error instanceof PredicateContradictionError) {
			markerType = Constants.PREDICATE_CONTRADICTION_MARKER_TYPE;
		} else if (error instanceof RequiredPredicateError) {
			markerType = Constants.REQUIRED_PREDICATE_MARKER_TYPE;
			errorInfoMap.put("predicate", ((RequiredPredicateError) error).getContradictedPredicate().getPredName());
			errorInfoMap.put("predicateParamCount", ((RequiredPredicateError) error).getContradictedPredicate().getParameters().size()+"");
			int errorIndex = ((RequiredPredicateError) error).getExtractedValues().getCallSite().getIndex();
			errorInfoMap.put("errorParamIndex", errorIndex+"");
			if(errorLocation.getUnit().get().containsInvokeExpr()) {
				InvokeExpr invoke = errorLocation.getUnit().get().getInvokeExpr();
				String errorParam = invoke.getArg(errorIndex).toString();
				errorInfoMap.put("errorParam", errorParam);
			}
		} else if (error instanceof ConstraintError) {
			markerType = Constants.CONSTRAINT_ERROR_MARKER_TYPE;
		} else if (error instanceof NeverTypeOfError) {
			markerType = Constants.NEVER_TYPEOF_MARKER_TYPE;
		} else if (error instanceof IncompleteOperationError) {
			markerType = Constants.INCOMPLETE_OPERATION_MARKER_TYPE;
		} else if (error instanceof TypestateError) {
			markerType = Constants.TYPESTATE_ERROR_MARKER_TYPE;
		} else if (error instanceof ImpreciseValueExtractionError) {
			markerType = Constants.IMPRECISE_VALUE_EXTRACTION_MARKER_TYPE;
		} else {
			markerType = Constants.CC_MARKER_TYPE;
		}

		int selectedSeverity = Activator.getDefault().getPreferenceStore().getInt(markerType);
		if (selectedSeverity == -1) {
			selectedSeverity = Activator.getDefault().getPreferenceStore().getDefaultInt(markerType);
		}
		Severities sev = Severities.get(selectedSeverity);
		if (sev == Severities.Ignored) {
			return;
		}
		
		this.warningFilePath = sourceFile.getProject().getLocation().toOSString() + Constants.outerFileSeparator + Constants.SUPPRESSWARNING_FILE;
		final File warningsFile = new File(this.warningFilePath);

		if (!warningsFile.exists()) {
			this.markerGenerator.addMarker(markerType, stmtId, sourceFile, lineNumber, errorMessage, errorCrySLRuleName, errorJimpleBody, sev, errorInfoMap, false);
		} else {
			this.xmlParser = new XMLParser(warningsFile);
			this.xmlParser.useDocFromFile();
			String idAsString = String.valueOf(stmtId);
			if (!this.xmlParser.getAttrValuesByAttrName(Constants.SUPPRESSWARNING_ELEMENT, Constants.ID_ATTR).contains(idAsString)) {
				this.markerGenerator.addMarker(markerType, stmtId, sourceFile, lineNumber, errorMessage, errorCrySLRuleName, errorJimpleBody, sev, errorInfoMap, false);
			} else {

				// update existing line number
				final Node suppressWarningNode = this.xmlParser.getNodeByAttrValue(Constants.SUPPRESSWARNING_ELEMENT, Constants.ID_ATTR, idAsString);
				final Node lineNumberNode = this.xmlParser.getChildNodeByTagName(suppressWarningNode, Constants.LINENUMBER_ELEMENT);
				this.xmlParser.updateNodeValue(lineNumberNode, lineNumber + "");
				this.xmlParser.writeXML();
				// last parameter(true) implies that the error was suppressed and info marker has to be shown.
				this.markerGenerator.addMarker(markerType, stmtId, sourceFile, lineNumber, errorMessage, errorCrySLRuleName, errorJimpleBody, sev, errorInfoMap, true);

				try {
					this.currentProject.refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (final CoreException e) {
					Activator.getDefault().logError(e);
				}
				this.suppressedWarningIds.add(idAsString);
			}
		}

	}

	// It only works when the secure object checkbox in preference page is checked
	@Override
	public void onSecureObjectFound(final IAnalysisSeed secureObject) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		if (!store.getBoolean(Constants.SHOW_SECURE_OBJECTS) || this.depOnly) {
			return;
		} else {
			final Statement stmt = secureObject.stmt();
			final Stmt unit = stmt.getUnit().get();
			final List<ValueBox> useAndDefBoxes = unit.getUseAndDefBoxes();
			final Optional<ValueBox> varOpt = useAndDefBoxes.stream().filter(e -> e instanceof JimpleLocalBox)
					.findFirst();
			ValueBox var = null;
			if (varOpt.isPresent()) {
				var = varOpt.get();
			} else {
				for (final ValueBox box : useAndDefBoxes) {
					if (box.getValue() instanceof JimpleLocal) {
						var = box;
						break;
					}
				}
			}
			final Value varName = var.getValue();
			this.markerGenerator.addMarker(Constants.CC_MARKER_TYPE, -1, unitToResource(stmt), unit.getJavaSourceStartLineNumber(),
					"Object " + (varName.toString().startsWith("$r") ? " of Type " + var.getValue().getType().toQuotedString() : varName) + " is secure.","",secureObject.getMethod().getActiveBody().toString()
					,Severities.Info, new HashMap<>(),
					false);	}
	}

	/**
	 * This method removes superfluous suppressed warning entries from the
	 * SuppressWarnings.xml file.
	 */
	public void removeUndetectableWarnings() {
		if (this.suppressedWarningIds.size() > 0) {

			final ArrayList<String> allSuppressedWarningIds = this.xmlParser
					.getAttrValuesByAttrName(Constants.SUPPRESSWARNING_ELEMENT, Constants.ID_ATTR);

			final ArrayList<String> difference = new ArrayList<>(allSuppressedWarningIds.size());
			difference.addAll(allSuppressedWarningIds);
			difference.removeAll(this.suppressedWarningIds);

			for (int i = 0; i < difference.size(); i++) {
				this.xmlParser.removeNodeByAttrValue(Constants.SUPPRESSWARNING_ELEMENT, Constants.ID_ATTR,
						difference.get(i));
			}
			this.xmlParser.writeXML();
			try {
				this.currentProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (final CoreException e) {
				Activator.getDefault().logError(e);
			}
		}
		this.suppressedWarningIds = new ArrayList<>();
	}

	private IResource unitToResource(final Statement stmt) {
		final SootClass className = stmt.getMethod().getDeclaringClass();
		try {
			return Utils.findClassByName(className.getName(), this.currentProject);
		} catch (final ClassNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		// Fall-back path when retrieval of actual path fails. If the statement below
		// fails, it should be left untouched as the actual bug is above.
		return this.currentProject.getFile("src/" + className.getName().replace(".", "/") + ".java");
	}

	@Override
	public void checkedConstraints(final AnalysisSeedWithSpecification arg0, final Collection<ISLConstraint> arg1) {
		// Nothing
	}

	@Override
	public void discoveredSeed(final IAnalysisSeed seed) {
		String seedClass = seed.getMethod().getDeclaringClass().getName();
		if (stat.getClassesAnalysed().containsKey(seedClass)) {
			AnalysisData data = stat.getClassesAnalysed().get(seedClass);
			data.addSeed(seed);
		} else {
			AnalysisData data = new AnalysisData();
			data.addSeed(seed);

			Map<String, AnalysisData> classesAnalysedMap = stat.getClassesAnalysed();
			classesAnalysedMap.put(seedClass, data);
		}
	}

	@Override
	public void onSeedTimeout(final sync.pds.solver.nodes.Node<Statement, Val> arg0) {
		// Nothing
	}

	@Override
	public void collectedValues(final AnalysisSeedWithSpecification arg0,
			final Multimap<CallSiteWithParamIndex, ExtractedValue> arg1) {
		// Nothing
	}

	@Override
	public void onSeedFinished(final IAnalysisSeed arg0, final ForwardBoomerangResults<TransitionFunction> arg1) {
		// Nothing
	}

	public ErrorMarkerGenerator getMarkerGenerator() {
		return this.markerGenerator;
	}

	@Override
	public void beforeAnalysis() {
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime currentTime = LocalDateTime.now();
		stat = new Stats();
		stat.setProject(currentProject);
		stat.setTimeOfAnalysis(dateTimeFormat.format(currentTime));
		StatisticsView.allowAnalysisRerun(false);
	}

	@Override
	public void afterAnalysis() {
		removeUndetectableWarnings();
		List<ResultsUnit> units = new ArrayList<ResultsUnit>();
		for (Entry<String, AnalysisData> result : stat.getClassesAnalysed().entrySet()) {
			String className = result.getKey();
			AnalysisData findings = result.getValue();
			boolean first = true;
			for (AbstractError err : findings.getErrors()) {
				units.add(new ResultsUnit(className, null, err, false, first));
				first = false;
			}

			for (IAnalysisSeed seed : findings.getSeeds()) {
				if (units.parallelStream().noneMatch(e -> e.doesSeedmatchWithError(seed))) {
					units.add(new ResultsUnit(className, seed, null, true));
				}
			}
		}

		StatisticsView.allowAnalysisRerun(true);
		StatisticsView.updateView(stat.getProject(), stat.getTimeOfAnalysis(), units);
	}

	@Override
	public void beforeConstraintCheck(final AnalysisSeedWithSpecification analysisSeedWithSpecification) {

	}

	@Override
	public void afterConstraintCheck(final AnalysisSeedWithSpecification analysisSeedWithSpecification) {

	}

	@Override
	public void beforePredicateCheck(final AnalysisSeedWithSpecification analysisSeedWithSpecification) {

	}

	@Override
	public void afterPredicateCheck(final AnalysisSeedWithSpecification analysisSeedWithSpecification) {

	}

	@Override
	public void seedStarted(final IAnalysisSeed analysisSeedWithSpecification) {

	}

	@Override
	public void boomerangQueryStarted(final Query seed, final BackwardQuery q) {

	}

	@Override
	public void boomerangQueryFinished(final Query seed, final BackwardQuery q) {

	}

	@Override
	public void ensuredPredicates(final Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates,
			final Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> expectedPredicates,
			final Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> missingPredicates) {
	}

	@Override
	public void addProgress(final int processSeeds, final int workListsize) {
		setProcessedSeeds(processSeeds);
		setTotalSeeds(workListsize + processSeeds);
		setPercentCompleted((int) Math.round((float) processedSeeds * 100 / totalSeeds));
		tempWork = getPercentCompleted() - work;
		if (tempWork > 0) {
			setWorkUnitsCompleted(tempWork);
			work = getPercentCompleted();
		} else {
			setWorkUnitsCompleted(0);
		}
	}

	public int getPercentCompleted() {
		return percentCompleted;
	}

	public void setPercentCompleted(int percentCompleted) {
		this.percentCompleted = percentCompleted;
	}

	public int getTotalSeeds() {
		return totalSeeds;
	}

	public void setTotalSeeds(int totalSeeds) {
		this.totalSeeds = totalSeeds;
	}

	public int getProcessedSeeds() {
		return processedSeeds;
	}

	public void setProcessedSeeds(int processedSeeds) {
		this.processedSeeds = processedSeeds;
	}

	public boolean isCgGenComplete() {
		return cgGenComplete;
	}

	public void setCgGenComplete(boolean cgGenComplete) {
		this.cgGenComplete = cgGenComplete;
	}

	public int getWorkUnitsCompleted() {
		return workUnitsCompleted;
	}

	public void setWorkUnitsCompleted(int workUnitsCompleted) {
		this.workUnitsCompleted = workUnitsCompleted;
	}

	public void setWork(int work) {
		this.work = work;
	}
}