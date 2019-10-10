/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import de.cognicrypt.integrator.task.controllers.XSLStringGenerationAndManipulation;
import de.cognicrypt.integrator.task.controllers.XmlRegion;
import de.cognicrypt.integrator.task.controllers.XmlRegionAnalyzer;

public class CompositeForXsl extends Composite {

	private StyledText xslTxtBox; // this text box needs to be accessible on the page as well.

	public CompositeForXsl(final Composite parent, final int style) {
		super(parent, SWT.BORDER);
		setLayout(new GridLayout(2, false));

		// UI Widgets for xslPage
		setXslTxtBox(new StyledText(this, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL));
		this.xslTxtBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		this.xslTxtBox.setCursor(null);
	}

	/**
	 * Read the text from the file provided and update the {@link StyledText} with the processed data.
	 *
	 * @param filePath the path that is chosen from the FileDialog.
	 */
	public void updateTheTextFieldWithFileData(final String filePath) {

		getXslTxtBox().setText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(filePath, getXslTxtBox().getText(), getXslTxtBox().getSelection(), null));

		colorizeTextBox();
	}

	/**
	 * Analyzes the {@link StyledText}, calculates the {@link XmlRegion} and {@link StyleRange} and applies the syntax highlight.
	 */
	public void colorizeTextBox() {
		final Point selection = getXslTxtBox().getSelection();

		final List<XmlRegion> regions = new XmlRegionAnalyzer().analyzeXml(this.xslTxtBox.getText());
		final List<StyleRange> ranges = XSLStringGenerationAndManipulation.computeStyleForXMLRegions(regions);

		for (final StyleRange styleRange : ranges) {
			getXslTxtBox().setStyleRange(styleRange);
		}

		getXslTxtBox().setSelection(selection);
	}

	/**
	 * @return the xslTxtBox
	 */
	public StyledText getXslTxtBox() {
		return this.xslTxtBox;
	}

	/**
	 * @param xslTxtBox the xslTxtBox to set
	 */
	private void setXslTxtBox(final StyledText xslTxtBox) {
		this.xslTxtBox = xslTxtBox;
	}

}
