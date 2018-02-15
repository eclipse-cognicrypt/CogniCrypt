/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cognicrypt.codegenerator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.utilities.JavaLineStyler;

/**
 * This class is responsible for displaying the preview of the code for the algorithm combination selected by the user.
 *
 */

public class CodePreviewPage extends WizardPage {

	private InstanceListPage instanceListPage;
	private Composite control;
	private Group codePreviewPanel;
	private StyledText code;

	public CodePreviewPage(InstanceListPage instanceListPage) {
		super(Constants.CODE_PREVIEW_PAGE);
		setTitle(Constants.CODE_PREVIEW_PAGE_TITLE);
		setDescription(Constants.CODE_PREVIEW_PAGE_DESCRIPTION);
		this.instanceListPage = instanceListPage;
	}

	@Override
	public void createControl(Composite parent) {
		//Adding scroll bars to the page
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.control = new Composite(sc, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		this.control.setLayout(layout);
		setPageComplete(false);

		JavaLineStyler lineStyler = new JavaLineStyler();
		
		//Preview of the code for the selected solution, which will be generated in to the Java project
		this.codePreviewPanel = new Group(this.control, SWT.NONE);
		this.codePreviewPanel.setText(Constants.CODE_PREVIEW);
		GridLayout gridLayout = new GridLayout();
		this.codePreviewPanel.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		gridData.heightHint = 200;
		this.codePreviewPanel.setLayoutData(gridData);
		final Font boldFont = new Font(this.codePreviewPanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
		this.codePreviewPanel.setFont(boldFont);
		setControl(this.control);

		this.code = new StyledText(this.codePreviewPanel, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		Display display = Display.getCurrent();
		this.code.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.code.setBounds(10, 20, 520, 146);
		this.code.setEditable(false);
		//change font style of the code in the preview panel
		final Font Styledfont = new Font(this.codePreviewPanel.getDisplay(), new FontData("Courier New", 10, SWT.WRAP ));
		this.code.setFont(Styledfont);
		//Parsing the block comments to highlight them in the code preview		
		lineStyler.parseBlockComments(instanceListPage.getCodePreview());
		//syntax highlighting in the code preview
		this.code.addLineStyleListener(lineStyler);
		//setting the background color of the code
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		this.code.setBackground(white);
		new Label(control, SWT.NONE);
		//Display the formatted code
		Display displayedCode = this.code.getDisplay();
		displayedCode.asyncExec(new Runnable() {

			public void run() {
				code.setText(instanceListPage.getCodePreview());
			}
		});
		this.code.setText(instanceListPage.getCodePreview());
		this.code.setToolTipText(Constants.DEFAULT_CODE_TOOLTIP);

		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

}
