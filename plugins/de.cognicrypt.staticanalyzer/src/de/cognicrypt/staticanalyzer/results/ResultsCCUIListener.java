/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.results;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.ICrySLResultsListener;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;
import soot.SootClass;
import soot.tagkit.AbstractHost;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

/**
 * This listener is notified of any misuses the analysis finds.
 *
 * @author Stefan Krueger
 *
 */
public class ResultsCCUIListener implements ICrySLResultsListener {

	private final ErrorMarkerGenerator markerGenerator;
	private final IProject currentProject;

	private ResultsCCUIListener(final IProject curProj, final ErrorMarkerGenerator gen) {
		this.currentProject = curProj;
		this.markerGenerator = gen;
	}

	public static ResultsCCUIListener createListener(IProject project) {
		ResultsCCUIListener listener = new ResultsCCUIListener(project, new ErrorMarkerGenerator());
		Activator.registerResultsListener(listener);
		return listener;
	}

	/**
	 * @return the currentProject
	 */
	public IProject getReporterProject() {
		return currentProject;
	}

	@Override
	public void reportError(AbstractError error) {
		String errorMessage = error.toErrorMarkerString();
		Statement errorLocation = error.getErrorLocation();
		IResource sourceFile = unitToResource(errorLocation);
		int lineNumber = ((AbstractHost) errorLocation.getUnit().get()).getJavaSourceStartLineNumber();
		if (error instanceof ImpreciseValueExtractionError) {
			this.markerGenerator.addMarker(sourceFile, lineNumber, errorMessage, true);
		} else {
			this.markerGenerator.addMarker(sourceFile, lineNumber, errorMessage);
		}
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
	}

	@Override
	public void onSeedTimeout(final Node<Statement, Val> arg0) {
		// Nothing
	}

	@Override
	public void collectedValues(AnalysisSeedWithSpecification arg0,
			Multimap<CallSiteWithParamIndex, ExtractedValue> arg1) {
		// Nothing
	}

	@Override
	public void onSeedFinished(IAnalysisSeed arg0, ForwardBoomerangResults<TransitionFunction> arg1) {
		// Nothing
	}

	public ErrorMarkerGenerator getMarkerGenerator() {
		return markerGenerator;
	}

}
