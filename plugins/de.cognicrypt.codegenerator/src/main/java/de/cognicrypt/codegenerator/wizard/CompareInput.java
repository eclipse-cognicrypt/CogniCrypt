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

	public CompareInput(final String left, final String right) {
		super(new CompareConfiguration());
		setTitle("Compare Code");
		this.left = left;
		this.right = right;
	}

	@Override
	protected Object prepareInput(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		final String leftLabel = "Old Source";
		final String rightLabel = "Modified Source";

		getCompareConfiguration().setLeftLabel(leftLabel);
		getCompareConfiguration().setRightLabel(rightLabel);

		final Differencer d = new Differencer();
		this.fRoot = d.findDifferences(false, monitor, null, null, new CompareItem(leftLabel, this.left), new CompareItem(rightLabel, this.right));
		return this.fRoot;
	}

}

class CompareItem implements IStreamContentAccessor, ITypedElement {

	private final String contents, name;

	CompareItem(final String name, final String contents) {
		this.name = name;
		this.contents = contents;
	}

	@Override
	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(this.contents.getBytes());
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getString() {
		return this.contents;
	}

	@Override
	public String getType() {
		return "Java";
	}
}
