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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.statment.CCStatement;
import de.cognicrypt.utils.Utils;
import de.cognicrypt.utils.XMLParser;
import soot.SootClass;
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
	private ArrayList<String> suppressWarningsIds;
	private String warningFilePath;

	private ResultsCCUIListener(final IProject curProj, final ErrorMarkerGenerator gen) {
		this.currentProject = curProj;
		this.markerGenerator = gen;
		this.suppressWarningsIds = new ArrayList<>();
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
		int lineNumber = errorLocation.getUnit().get().getJavaSourceStartLineNumber();
		CCStatement stmt = new CCStatement(errorLocation);
		int stmtId = stmt.hashCode();
		String stmtVar = stmt.getVar();

		warningFilePath = sourceFile.getProject().getLocation().toOSString() + "\\SuppressWarnings.xml";
		File warningsFile = new File(warningFilePath);
		Document doc;

		if (!warningsFile.exists()) {
			if (error instanceof ImpreciseValueExtractionError) {
				this.markerGenerator.addMarker(stmtId, sourceFile, lineNumber, stmtVar, errorMessage, true);
			} else {
				this.markerGenerator.addMarker(stmtId, sourceFile, lineNumber, stmtVar, errorMessage);
			}
		} else {
			try {
				doc = XMLParser.getDocFromFile(warningsFile);
				suppressWarningsIds = XMLParser.getAttrValuesByAttrName(doc, "SuppressWarning", "ID");
				if (!XMLParser.getAttrValuesByAttrName(doc, "SuppressWarning", "ID").contains(stmtId + "")) {
					if (error instanceof ImpreciseValueExtractionError) {
						this.markerGenerator.addMarker(stmtId, sourceFile, lineNumber, stmtVar, errorMessage, true);
					} else {
						this.markerGenerator.addMarker(stmtId, sourceFile, lineNumber, stmtVar, errorMessage);
					}
				}
				else {
					suppressWarningsIds.remove(stmtId+"");
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void removeUndetectableWarnings() {

		Activator.getDefault().logInfo("---------->>> removeUndetectableWarnings invoke");
		
		if (suppressWarningsIds.size() > 0) {

			try {
				File warningsFile = new File(warningFilePath);
				Document doc = XMLParser.getDocFromFile(warningsFile);
				for(int i = 0; i < suppressWarningsIds.size(); i++) {
					XMLParser.removeNodeByAttrValue(doc, "SuppressWarning", "ID", suppressWarningsIds.get(i));
				}
				XMLParser.writeXML(doc, warningsFile);
			} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		suppressWarningsIds = new ArrayList<>();
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
