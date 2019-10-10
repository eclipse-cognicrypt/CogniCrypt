/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.view;

import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ErrorWithObjectAllocation;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.TypestateError;
import soot.jimple.InstanceInvokeExpr;

/**
 * This class contains the analysis Data to be shown in the Statistics View.
 * 
 * @author Adnan Manzoor
 */

public class ResultsUnit {

	private String className;
	private IAnalysisSeed seedLocation;
	private AbstractError error;
	private boolean isHealthy;
	private boolean showClassName;

	public ResultsUnit(String className, IAnalysisSeed location, AbstractError error, boolean isHealthy) {
		this(className, location, error, isHealthy, false);
	}

	public ResultsUnit(String className, IAnalysisSeed location, AbstractError error, boolean isHealthy, boolean showClassName) {
		this.className = className;
		this.seedLocation = location;
		this.error = error;
		this.isHealthy = isHealthy;
		this.showClassName = showClassName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public IAnalysisSeed getSeed() {
		return seedLocation;
	}

	public void setSeed(IAnalysisSeed seed) {
		this.seedLocation = seed;
	}

	public AbstractError getError() {
		return error;
	}

	public void setError(AbstractError error) {
		this.error = error;
	}

	public boolean isHealthy() {
		return isHealthy;
	}

	public void setHealthStatus(boolean healthStatus) {
		this.isHealthy = healthStatus;
	}

	public String toString() {
		return printClassName() + "\n" + printSeedDescription() + "\n" + printErrorString();
	}

	public String printClassName() {
		return showClassName ? className : "";
	}

	public String printErrorString() {
		if (error == null) {
			return "";
		}
		return error.toErrorMarkerString();
	}

	public String printSeedDescription() {
		if (seedLocation != null) {
			return printDescriptionOfSeed(seedLocation);
		} else {
			String seed = error.getErrorLocation().toString();
			if (error instanceof IncompleteOperationError || error instanceof TypestateError) {
				seed = printDescriptionOfSeed(((ErrorWithObjectAllocation) error).getObjectLocation());
			} else {
				seed = "Call to " + error.getErrorLocation().getUnit().get().getInvokeExpr().getMethodRef().getSignature() + " in Line "
						+ error.getErrorLocation().getUnit().get().getJavaSourceStartLineNumber() + " of Method " + error.getErrorLocation().getMethod().getName() + "()";
			}
			return seed;
		}
	}

	private String printDescriptionOfSeed(IAnalysisSeed seed) {
		String varName = seed.var().value().toString();
		if (varName.startsWith("$") || varName.contains("varMatcher")) {
			String fqn = seed.var().value().getType().toQuotedString();
			varName = "Object of type " + fqn.substring(fqn.lastIndexOf('.') + 1);
		} else {
			varName = "Object " + varName;
		}
		varName += " in Line " + seed.stmt().getUnit().get().getJavaSourceStartLineNumber();

		String methodName = seed.getMethod().getName();
		if ("<init>".equals(methodName)) {
			methodName = className.substring(className.lastIndexOf('.') + 1);
		}
		return varName + " of Method " + methodName + "()";
	}

	public boolean doesSeedmatchWithError(IAnalysisSeed seed) {
		if (error instanceof ErrorWithObjectAllocation) {
			return ((ErrorWithObjectAllocation) error).getObjectLocation().equals(seed);
		} else if (error == null) {
			return false;
		}
		return seed.var().value().equals(((InstanceInvokeExpr) error.getErrorLocation().getUnit().get().getInvokeExpr()).getBase());
	}

}
