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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.JavaLineStyler;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

public class DefaultAlgorithmPage extends WizardPage {

	private Composite control;
	private Group codePreviewPanel;
	private final TaskSelectionPage taskSelectionPage;
	private Button defaultAlgorithmCheckBox;
	private StyledText code;
	private final InstanceGenerator instanceGenerator;
	private Map<Question, Answer> constraints;
	private InstanceClafer value;
	private String provider;
	private JavaLineStyler lineStyler;

	/**
	 * Constructor for DefaultAlgorithmPage.
	 * 
	 * @param instGen
	 *        Instance Generator
	 * @param constraints
	 * @param taskSelectionPage
	 *        Page to select task
	 */
	public DefaultAlgorithmPage(final InstanceGenerator instGen, HashMap<Question, Answer> constraints, final TaskSelectionPage taskSelectionPage) {
		super(Constants.DEFAULT_ALGORITHM_PAGE);
		setTitle("Best solution for task: " + taskSelectionPage.getSelectedTask().getDescription());
		setDescription(Constants.DESCRIPTION_DEFAULT_ALGORITHM_PAGE);
		this.instanceGenerator = instGen;
		this.taskSelectionPage = taskSelectionPage;
		this.constraints = constraints;
	}

	@Override
	public void createControl(final Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label algorithmClass;
		Label labelDefaultAlgorithm;
		this.control = new Composite(sc, SWT.NONE);
		final GridLayout layout = new GridLayout(1, false);
		this.control.setLayout(layout);

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sc, "de.cognicrypt.codegenerator.DefaultAlgorithmHelp");

		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		compositeControl.setLayout(new GridLayout(2, false));
		labelDefaultAlgorithm = new Label(compositeControl, SWT.NONE);
		labelDefaultAlgorithm.setText(Constants.defaultAlgorithm);
		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();//Only the first Instance,which is the most secure one, will be displayed

		//display the default algorithm
		algorithmClass = new Label(compositeControl, SWT.NONE);
		String firstInstance = inst.keySet().toArray()[0].toString();
		algorithmClass.setText(firstInstance);
		setValue(DefaultAlgorithmPage.this.instanceGenerator.getInstances().get(firstInstance));
		getInstanceProperties(DefaultAlgorithmPage.this.instanceGenerator.getInstances().get(firstInstance));
		setPageComplete(true);

		algorithmClass.setToolTipText(Constants.DEFAULT_ALGORITHM_COMBINATION_TOOLTIP);

		//Preview of the code for the default algorithm, which will be generated in to the Java project
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
		this.code.setEditable(true);
		//change font style of the code in the preview panel
		final Font Styledfont = new Font(this.codePreviewPanel.getDisplay(), new FontData("Courier New", 10, SWT.WRAP));
		this.code.setFont(Styledfont);
		lineStyler = new JavaLineStyler();
		//Parsing the block comments to highlight them in the code preview			
		lineStyler.parseBlockComments(compileCodePreview());
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
				if (getCurrentEditorContent() == "") {
					code.setText(compileCodePreview());
				} else {
					//if there is open file, insert te new code in the same for the preview.
					String currentlyOpenPart = getCurrentEditorContent();
					int position = currentlyOpenPart.indexOf("{");
					currentlyOpenPart = new StringBuilder(currentlyOpenPart).insert(position + 1, "\n" + compileCodePreview()).toString();
					code.setText(currentlyOpenPart);
				}
			}
		});
		this.code.setToolTipText(Constants.DEFAULT_CODE_TOOLTIP);
		this.code.setAlwaysShowScrollBars(false);

		//this checkbox should be checked, to move to the next page.
		defaultAlgorithmCheckBox = new Button(control, SWT.CHECK);
		defaultAlgorithmCheckBox.setSelection(false);
		if (instanceGenerator.getNoOfInstances() == 1) {
			//if there is only one instance, then the user can generate the code only for the default algorithm combination. 
			//Thus, the check box will be disabled which prevents the user from moving to the next page. 
			defaultAlgorithmCheckBox.setEnabled(false);
		}
		defaultAlgorithmCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		defaultAlgorithmCheckBox.setText(Constants.DEFAULT_ALGORITHM_PAGE_CHECKBOX);
		defaultAlgorithmCheckBox.setToolTipText(Constants.DEFAULT_CHECKBOX_TOOLTIP);

		//Show the info icon if the check box is disabled
		final ControlDecoration deco = new ControlDecoration(defaultAlgorithmCheckBox, SWT.TOP | SWT.RIGHT);
		Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		if (defaultAlgorithmCheckBox.isEnabled()) {
			deco.hide();
		} else {
			deco.setDescriptionText(Constants.DEFAULT_ALGORITHM_CHECKBOX_DISABLE);
		}
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);

		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	/**
	 * Get the code from the user's open file.
	 * 
	 * @return code in the file
	 */
	public String getCurrentEditorContent() {
		IEditorPart currentlyOpenPart = Utils.getCurrentlyOpenEditor();
		//if there are no open files, then return an empty string for comparison.
		if (currentlyOpenPart == null || !(currentlyOpenPart instanceof AbstractTextEditor)) {
			Activator.getDefault().logInfo(
				"Could not open access the editor of the file or there are no files open. Therefore,  the 'Old Source' part remains empty and the newly generated code appears in the 'Modified Source' part.");
			return "";
		}
		ITextEditor currentlyOpenEditor = (ITextEditor) currentlyOpenPart;
		IDocument currentlyOpenDocument = currentlyOpenEditor.getDocumentProvider().getDocument(currentlyOpenEditor.getEditorInput());
		final String docContent = currentlyOpenDocument.get();
		return docContent;
	}

	/**
	 * Assembles code-preview text.
	 * 
	 * @return code snippet
	 * @throws BadLocationException
	 */
	private String compileCodePreview() {
		final CodeGenerator codeGenerator = new XSLBasedGenerator(this.taskSelectionPage.getSelectedProject(), this.taskSelectionPage.getSelectedTask().getXslFile());
		final String claferPreviewPath = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile;
		Configuration codePreviewConfig = new Configuration(value, this.constraints, claferPreviewPath);
		final String temporaryOutputFile = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.CodeGenerationCallFile;

		try {
			((XSLBasedGenerator) codeGenerator).transform(codePreviewConfig.persistConf(), temporaryOutputFile);
		} catch (TransformerException | IOException e) {
			Activator.getDefault().logError(e, Constants.TransformerErrorMessage);
			return "";
		}

		final Path file = new File(temporaryOutputFile).toPath();
		try (InputStream in = Files.newInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			final StringBuilder preview = new StringBuilder();
			String line = null;
			// If no file is open in user's editor, show the preview of newly generated class
			if (getCurrentEditorContent() == "") {
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("import")) {
						preview.append(line);
						preview.append(Constants.lineSeparator);
					}
				}
				return preview.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
			}
			// If a file is open in user's editor, show the preview of newly generated lines located inside the user's open file.
			else {
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("package") && !line.contains("class") && !line.startsWith("import")) {
						preview.append(line);
						preview.append(Constants.lineSeparator);
					}
				}
				String truncatedPreview = preview.toString();
				int truncateIndex = truncatedPreview.length();
				truncateIndex = truncatedPreview.lastIndexOf("}", truncateIndex - 1);
				truncatedPreview = truncatedPreview.substring(0, truncateIndex);
				return truncatedPreview.replaceAll("(?m)^[ \t]*\r?\n", "");
			}
		} catch (final IOException e) {
			Activator.getDefault().logError(e, Constants.CodePreviewErrorMessage);
		}
		return "";
	}

	public void getInstanceDetails(final InstanceClafer inst, final Map<String, String> algorithms) {
		String value;

		if (!inst.getType().getRef().getTargetType().isPrimitive()) {
			String algo = Constants.ALGORITHM + " : " + ClaferModelUtils
				.removeScopePrefix(inst.getType().getRef().getTargetType().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2")) + Constants.lineSeparator;
			algorithms.put(algo, "");

			final InstanceClafer instan = (InstanceClafer) inst.getRef();
			for (final InstanceClafer in : instan.getChildren()) {
				if (in.getType().getRef() != null && !in.getType().getRef().getTargetType().isPrimitive()) {
					final String superName = ClaferModelUtils
						.removeScopePrefix(in.getType().getRef().getTargetType().getSuperClafer().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2"));
					if (!superName.equals("Enum")) {
						getInstanceDetails(in, algorithms);
						continue;
					}
				}
				value = "\t" + ClaferModelUtils.removeScopePrefix(
					in.getType().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2")) + " : " + ((in.getRef() != null) ? in.getRef().toString().replace("\"", "") : "");

				if (value.indexOf("->") > 0) {	// VeryFast -> 4 or Fast -> 3	removing numerical value and "->"
					value = value.substring(0, value.indexOf("->") - 1);
					value = value.replaceAll("([a-z0-9])([A-Z])", "$1 $2");
				}
				// To get the provider of the instance
				if (ClaferModelUtils.removeScopePrefix(in.getType().getName()).equals("Provider")) {
					setProviderForInstance((in.getRef() != null) ? in.getRef().toString().replace("\"", "") : "");
				}

				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo) + value);
			}
			// Above for loop over children hasn't been executed, then following if
			if (!instan.hasChildren()) {
				value = "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2")) + " : " + inst.getRef().toString();
				algo = algorithms.keySet().iterator().next();
				algorithms.put(algo, algorithms.get(algo) + value);
			}
		}
	}

	String getInstanceProperties(final InstanceClafer inst) {
		final Map<String, String> algorithms = new HashMap<>();
		for (InstanceClafer child : inst.getChildren()) {
			getInstanceDetails(child, algorithms);
		}

		StringBuilder output = new StringBuilder();
		for (final Map.Entry<String, String> entry : algorithms.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			if (!value.isEmpty()) {
				output.append(key);
				output.append(value);
				output.append(Constants.lineSeparator);
			}
		}
		return output.toString().replaceAll("([a-z0-9])([A-Z])", "$1 $2");
	}

	private void setProviderForInstance(String provider) {
		this.provider = provider;
	}

	public String getProviderFromInstance() {
		if (this.provider != null) {
			return this.provider.replace("\n", "");
		} else {
			return "";
		}
	}

	public TaskSelectionPage getTaskSelectionPage() {
		return this.taskSelectionPage;
	}

	public boolean isDefaultAlgorithm() {
		return this.defaultAlgorithmCheckBox.getSelection();
	}

	public InstanceClafer getValue() {
		return this.value;
	}

	@Override
	public void setPageComplete(final boolean complete) {
		super.setPageComplete(complete);
	}

	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}

	@Override
	public boolean canFlipToNextPage() {
		//Can go to next page only if the check box is unchecked
		if (this.defaultAlgorithmCheckBox.getSelection() != true) {
			return this.defaultAlgorithmCheckBox.getSelection();
		}
		return true;
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.control.setFocus();
		}
	}

}
