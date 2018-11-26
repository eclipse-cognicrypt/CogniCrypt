/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

/**
 * This class is responsible for displaying the comparison between the code of the user's file before and after code generation.
 *
 */
class CompareInput extends CompareEditorInput {

	private final String left;
	private final String right;
	private Object fRoot;

	public CompareInput(String left, String right) {
		super(new CompareConfiguration());
		setTitle("Compare Code");
		this.left = left;
		this.right = right;
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		String leftLabel = "Old Source";
		String rightLabel = "Modified Source";

		getCompareConfiguration().setLeftLabel(leftLabel);
		getCompareConfiguration().setRightLabel(rightLabel);

		Differencer d = new Differencer();
		fRoot = d.findDifferences(false, monitor, null, null, new CompareItem(leftLabel, left), new CompareItem(rightLabel, right));
		return fRoot;
	}

}

class CompareItem implements IStreamContentAccessor, ITypedElement {

	private String contents, name;

	CompareItem(String name, String contents) {
		this.name = name;
		this.contents = contents;
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(contents.getBytes());
	}

	public Image getImage() {
		return null;
	}

	public String getName() {
		return this.name;
	}

	public String getString() {
		return this.contents;
	}

	public String getType() {
		return "Java";
	}
}
