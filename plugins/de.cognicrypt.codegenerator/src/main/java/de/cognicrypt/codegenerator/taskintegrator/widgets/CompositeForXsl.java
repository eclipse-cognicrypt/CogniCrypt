/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.cognicrypt.codegenerator.taskintegrator.controllers.XSLStringGenerationAndManipulation;
import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegion;
import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegionAnalyzer;

public class CompositeForXsl extends Composite {

	private StyledText xslTxtBox; // this text box needs to be accessible on the page as well.

	public CompositeForXsl(Composite parent, int style) {
		super(parent, SWT.BORDER);
		setLayout(new GridLayout(2, false));

		//UI Widgets for xslPage
		setXslTxtBox(new StyledText(this, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL));
		xslTxtBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		xslTxtBox.setCursor(null);
	}

	/**
	 * Read the text from the file provided and update the {@link StyledText} with the processed data.
	 * 
	 * @param filePath
	 *        the path that is chosen from the FileDialog.
	 */
	public void updateTheTextFieldWithFileData(String filePath) {

		getXslTxtBox().setText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(filePath, getXslTxtBox().getText(), getXslTxtBox().getSelection(), null));

		colorizeTextBox();
	}

	/**
	 * Analyzes the {@link StyledText}, calculates the {@link XmlRegion} and {@link StyleRange} and applies the syntax highlight.
	 */
	public void colorizeTextBox() {
		Point selection = getXslTxtBox().getSelection();

		List<XmlRegion> regions = new XmlRegionAnalyzer().analyzeXml(xslTxtBox.getText());
		List<StyleRange> ranges = XSLStringGenerationAndManipulation.computeStyleForXMLRegions(regions);

		for (StyleRange styleRange : ranges) {
			getXslTxtBox().setStyleRange(styleRange);
		}

		getXslTxtBox().setSelection(selection);
	}

	/**
	 * @return the xslTxtBox
	 */
	public StyledText getXslTxtBox() {
		return xslTxtBox;
	}

	/**
	 * @param xslTxtBox
	 *        the xslTxtBox to set
	 */
	private void setXslTxtBox(StyledText xslTxtBox) {
		this.xslTxtBox = xslTxtBox;
	}

}
