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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.utilities.Utils;
import de.cognicrypt.codegenerator.utilities.XMLParser;

public class DefaultAlgorithmPage extends WizardPage {

	private Composite control;
	private Group codePreviewPanel;
	private TaskSelectionPage taskSelectionPage;
	private Button defaultAlgorithmCheckBox;
	private Text code;
	private final InstanceGenerator instanceGenerator;
	private InstanceClafer value;
	private ConfiguratorWizard configuratorWizard;
	private String provider;

	/**
	 * This class is responsible for displaying an algorithm as the best solution, based on the answers given by the user for the previous questions. It also allows the users to
	 * view other possible algorithms matching their requirements, by the selection of the check box.
	 * 
	 */

	public DefaultAlgorithmPage(final InstanceGenerator inst, final TaskSelectionPage taskSelectionPage, ConfiguratorWizard confWizard) {
		super(Constants.DEFAULT_ALGORITHM_PAGE);
		setTitle("Best solution for task: " + taskSelectionPage.getSelectedTask().getDescription());
		setDescription(Constants.DESCRIPTION_DEFAULT_ALGORITHM_PAGE);
		this.instanceGenerator = inst;
		this.taskSelectionPage = taskSelectionPage;
		this.configuratorWizard = confWizard;
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
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.control, "de.cognicrypt.codegenerator.help_id_2");

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

		this.code = new Text(this.codePreviewPanel, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		Display display = Display.getCurrent();
		this.code.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.code.setBounds(10, 20, 520, 146);
		this.code.setEditable(false);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		this.code.setBackground(white);
		new Label(control, SWT.NONE);
		this.code.setText(getCodePreview());
		this.code.setToolTipText(Constants.DEFAULT_CODE_TOOLTIP);

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
	 * Preview of the code, for the default algorithm configuration/instance is displayed by calling this method .
	 * 
	 * @return preview of the code that will be generated in the Java project for provided default algorithm configuration
	 */
	public String getCodePreview() {
		XSLBasedGenerator codeGenerator = new XSLBasedGenerator(this.taskSelectionPage.getSelectedProject(), this.getProviderFromInstance());
		final String claferPreviewPath = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile;
		final XMLParser xmlparser = new XMLParser();
		xmlparser.displayInstanceValues(this.getValue(), this.configuratorWizard.getConstraints());
		try {
			xmlparser.writeClaferInstanceToFile(claferPreviewPath);
		} catch (IOException e) {
			Activator.getDefault().logError(e, Constants.WritingInstanceClaferErrorMessage);
			return "";
		}

		File claferPreviewFile = new File(claferPreviewPath);

		// Check whether directories and templates/model exist
		final File claferOutputFiles = claferPreviewFile != null && claferPreviewFile.exists() ? claferPreviewFile
			: Utils.getResourceFromWithin(Constants.pathToClaferInstanceFolder + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
		final File xslFile = Utils.getResourceFromWithin(this.taskSelectionPage.getSelectedTask().getXslFile());
		if (!claferOutputFiles.exists() || !xslFile.exists()) {
			Activator.getDefault().logError(Constants.FilesDoNotExistErrorMessage);
			return "";
		}
		// Perform actual transformation by calling XSLT processor.

		final String temporaryOutputFile = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.CodeGenerationCallFile;

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		final TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer(new StreamSource(xslFile));
		} catch (TransformerConfigurationException e) {
			Activator.getDefault().logError(e, Constants.TransformerConfigurationErrorMessage);
			return "";
		}
		File outputFile = new File(temporaryOutputFile);
		try {
			transformer.transform(new StreamSource(claferPreviewFile), new StreamResult(outputFile));
		} catch (TransformerException e) {
			Activator.getDefault().logError(e, Constants.TransformerErrorMessage);
			return "";
		}

		Path file = outputFile.toPath();
		try (InputStream in = Files.newInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("import")) {
					sb.append(line);
					sb.append(Constants.lineSeparator);
				}
			}
			return sb.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
		} catch (IOException e) {
			Activator.getDefault().logError(e, Constants.CodePreviewErrorMessage);
		} finally {
			File outputFolder = outputFile.getParentFile();
			outputFolder.delete();
			claferPreviewFile.delete();
		}

		return "";
	}

	public TaskSelectionPage getTaskSelectionPage() {
		return taskSelectionPage;
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
				if (ClaferModelUtils.removeScopePrefix(in.getType().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2")).equals("Provider")) {
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
		return this.provider.replace("\n", "");
	}

	public boolean isDefaultAlgorithm() {
		return this.defaultAlgorithmCheckBox.getSelection();
	}

	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}

	public InstanceClafer getValue() {
		return this.value;
	}

	@Override
	public void setPageComplete(final boolean complete) {
		super.setPageComplete(complete);
	}

	@Override
	public boolean canFlipToNextPage() {
		//Can go to next page only if the check box is checked
		if (this.defaultAlgorithmCheckBox.getSelection() != true) {
			return this.defaultAlgorithmCheckBox.getSelection();
		}
		return true;

	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			control.setFocus();
		}
	}

}
