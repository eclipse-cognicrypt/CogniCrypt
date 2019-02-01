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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Node;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
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
import crypto.rules.CryptSLPredicate;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.Severities;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.statment.CCStatement;
import de.cognicrypt.staticanalyzer.view.AnalysisData;
import de.cognicrypt.staticanalyzer.view.ResultsUnit;
import de.cognicrypt.staticanalyzer.view.StatisticsView;
import de.cognicrypt.staticanalyzer.view.Stats;
import de.cognicrypt.utils.Utils;
import de.cognicrypt.utils.XMLParser;
import soot.SootClass;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.internal.JimpleLocalBox;
import soot.tagkit.AbstractHost;
import typestate.TransitionFunction;

/**
 * This listener is notified of any misuses the analysis finds.
 * It also reports the results of the analysis to the Statistics View
 *
 * @author Stefan Krueger
 * @author André Sonntag
 * @author Adnan Manzoor
 */
public class ResultsCCUIListener extends CrySLAnalysisListener {

	private final ErrorMarkerGenerator markerGenerator;
	private final IProject currentProject;
	private ArrayList<String> suppressedWarningIds;
	private String warningFilePath;
	private XMLParser xmlParser;
	private static Stats stat;

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

	@Override
	public void reportError(final AbstractError error) {
		final String errorMessage = error.toErrorMarkerString();
		final Statement errorLocation = error.getErrorLocation();
		final IResource sourceFile = unitToResource(errorLocation);
		final int lineNumber = ((AbstractHost) errorLocation.getUnit().get()).getJavaSourceStartLineNumber();
		final CCStatement stmt = new CCStatement(errorLocation);
		final int stmtId = stmt.hashCode();
		
		if(stat.getClassesAnalysed().containsKey(sourceFile.getName())){
			AnalysisData data = stat.getClassesAnalysed().get(sourceFile.getName());
			data.addError(errorMessage);
			data.setHealth("Unhealthy");
		} else {
			AnalysisData data = new AnalysisData();
			data.addError(errorMessage);
			data.setHealth("Unhealthy");
			Map<String, AnalysisData> classesAnalysedMap = stat.getClassesAnalysed();
			classesAnalysedMap.put(sourceFile.getName(), data);
			
		}

		/*
		 * Adding of new marker types for new errors: 1) add new ErrorMarker extension
		 * point in plugin.xml 2) add new markerResolutionGenerator tag in plugin.xml 3)
		 * add new Marker constant in Constants.java (CogniCrypt Core) 4) add new else
		 * if in the following query
		 */

		String markerType;
		if (error instanceof ForbiddenMethodError) {
			markerType = Constants.FORBIDDEN_METHOD_MARKER_TYPE;
		} else if (error instanceof PredicateContradictionError) {
			markerType = Constants.PREDICATE_CONTRADICTION_MARKER_TYPE;
		} else if (error instanceof RequiredPredicateError) {
			markerType = Constants.REQUIRED_PREDICATE_MARKER_TYPE;
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

		final Severities sev = (markerType != Constants.IMPRECISE_VALUE_EXTRACTION_MARKER_TYPE) ? Severities.Problem
				: Severities.Warning;

		this.warningFilePath = sourceFile.getProject().getLocation().toOSString() + Constants.outerFileSeparator
				+ Constants.SUPPRESSWARNING_FILE;
		final File warningsFile = new File(this.warningFilePath);

		if (!warningsFile.exists()) {
			this.markerGenerator.addMarker(markerType, stmtId, sourceFile, lineNumber, errorMessage, sev);
		} else {
			this.xmlParser = new XMLParser(warningsFile);
			this.xmlParser.useDocFromFile();
			if (!this.xmlParser.getAttrValuesByAttrName(Constants.SUPPRESSWARNING_ELEMENT, Constants.ID_ATTR)
					.contains(stmtId + "")) {
				this.markerGenerator.addMarker(markerType, stmtId, sourceFile, lineNumber, errorMessage, sev);
			} else {

				// update existing LineNumber
				final Node suppressWarningNode = this.xmlParser.getNodeByAttrValue(Constants.SUPPRESSWARNING_ELEMENT,
						Constants.ID_ATTR, stmtId + "");
				final Node lineNumberNode = this.xmlParser.getChildNodeByTagName(suppressWarningNode,
						Constants.LINENUMBER_ELEMENT);
				this.xmlParser.updateNodeValue(lineNumberNode, lineNumber + "");
				this.xmlParser.writeXML();

				try {
					this.currentProject.refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (final CoreException e) {
					Activator.getDefault().logError(e);
				}
				this.suppressedWarningIds.add(stmtId + "");
			}
		}

	}

	@Override
	public void onSecureObjectFound(final IAnalysisSeed secureObject) {
		final Statement stmt = secureObject.stmt();
		final Stmt unit = stmt.getUnit().get();
		final List<ValueBox> useAndDefBoxes = unit.getUseAndDefBoxes();
		final Optional<ValueBox> varOpt = useAndDefBoxes.stream().filter(e -> e instanceof JimpleLocalBox).findFirst();
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
		this.markerGenerator
				.addMarker(Constants.CC_MARKER_TYPE, -1, unitToResource(stmt),  unit.getJavaSourceStartLineNumber(),
						"Object " + (varName.toString().startsWith("$r")
								? " of Type " + var.getValue().getType().toQuotedString()
								: varName) + " is secure.",
						Severities.Secure);
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
	public void discoveredSeed(final IAnalysisSeed arg0) {
		// Nothing
		System.out.println("\ndiscovered Seed\n");
		String seedClass = arg0.getMethod().getDeclaringClass().getName() + ".java";
		if(stat.getClassesAnalysed().containsKey(seedClass)){
			AnalysisData data = stat.getClassesAnalysed().get(seedClass);
			data.addSeed("Method: " +arg0.getMethod().getName() + " , Variable: " + arg0.var().value());
		} else {
			AnalysisData data = new AnalysisData();
			data.addSeed("Method: " +arg0.getMethod().getName() + " , Variable: " + arg0.var().value());
			
			Map<String, AnalysisData> classesAnalysedMap = stat.getClassesAnalysed();
			classesAnalysedMap.put(seedClass, data);
		}
			
		
		/*//String seedClass = arg0.getClass().getName();
		System.out.println("-------------------------------------------------------------------------");
		System.out.println("Seed Name: " + seedName);
		System.out.println("Seed Method: " + arg0.getMethod().toString());
		System.out.println("Object Id: " + arg0.var().toString());
		System.out.println("Java Class: " + seedClass);
		//System.out.println("Class: " + seedClass);
		System.out.println("-------------------------------------------------------------------------");
		*/
		
	
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
		// TODO Auto-generated method stub
		System.out.println("\nBefore Analysis\n");
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime currentTime = LocalDateTime.now();
		stat = new Stats();
		stat.setProjectName(currentProject.getName());
		stat.setTimeOfAnalysis(dateTimeFormat.format(currentTime));
		//stat = new Stats();
	}

	@Override
	public void afterAnalysis() {
		removeUndetectableWarnings();
		System.out.println("\nInside After Analysis\n");
		/*System.out.println("------------------------------ Inside afterAnalysis method: ----------------------------------");
		System.out.println("Project Name: " + stat.getProjectName());
		System.out.println("Time : " + stat.getTimeOfAnalysis());*/
		Set<String> keys = stat.getClassesAnalysed().keySet();
		List<ResultsUnit> units = new ArrayList<ResultsUnit>();
		for(String key:keys) {
			//System.out.println(key + " has :" + stat.getClassesAnalysed().get(key).getErrors().size() +" errors & " + "Seeds analyzed = " + stat.getClassesAnalysed().get(key).getSeeds().size() + " Health = " + stat.getClassesAnalysed().get(key).getHealth() );
			AnalysisData data = stat.getClassesAnalysed().get(key);
			ArrayList<String> seeds = data.getSeeds();
			ArrayList<String> errors = data.getErrors();
			String firstSeed;
			String firstError;
			int seedsSize = seeds.size();
			int errorsSize = errors.size();
			int seedsIndex = 1, errorsIndex = 1;
			if(seedsSize > 0) {
				firstSeed = seeds.get(0);
			} else {
				firstSeed = "";
			}
			if(errorsSize > 0) {
				firstError = errors.get(0);
			} else {
				firstError = "";
			}
			
			units.add(new ResultsUnit(key, firstSeed, firstError, data.getHealth()));
			
			while(seedsIndex < seedsSize && errorsIndex < errorsSize) {
				units.add(new ResultsUnit("", seeds.get(seedsIndex), errors.get(errorsIndex), ""));
				errorsIndex++;
				seedsIndex++;
			}
			
			while(seedsIndex < seedsSize) {
				units.add(new ResultsUnit("", seeds.get(seedsIndex), "", ""));
				seedsIndex++;
			}
			
			while( errorsIndex < errorsSize) {
				units.add(new ResultsUnit("", "", errors.get(errorsIndex), ""));
				errorsIndex++;
			}
			
			
			
		}
		/*System.out.println("------------------------------ Inside afterAnalysis method: ----------------------------------");
		System.out.println("Total Units Created: " + units.size());*/
		IWorkbenchWindow workbenchWindow = null;
		IWorkbenchWindow[] allWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : allWindows) {
                workbenchWindow = window;
                if (workbenchWindow != null) {
                	System.out.println("Found workbench");
                    break;
                }
            }
        if(workbenchWindow != null) {
        	IWorkbenchPage activePage = workbenchWindow.getActivePage();
        	IViewPart viewPart = activePage.findView("de.cognicrypt.staticanalyzer.view.StatisticsView");
        	if(viewPart != null) {
	        	StatisticsView myView = (StatisticsView)viewPart;
        		Display.getDefault().asyncExec(new Runnable() {

        			public void run() {
        				myView.updateData(stat.getProjectName(), stat.getTimeOfAnalysis(), units);
        			}

        		});
        	}
        }
	}

	@Override
	public void beforeConstraintCheck(final AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterConstraintCheck(final AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforePredicateCheck(final AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterPredicateCheck(final AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void seedStarted(final IAnalysisSeed analysisSeedWithSpecification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void boomerangQueryStarted(final Query seed, final BackwardQuery q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void boomerangQueryFinished(final Query seed, final BackwardQuery q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ensuredPredicates(final Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates,
			final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates,
			final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {
		// TODO Auto-generated method stub
	}
}
