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

import java.util.HashMap;
import java.util.Map;

import org.clafer.instance.InstanceClafer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;

public class CompareAlgorithmPage extends WizardPage {

	private IProject selectedProject = null;
	Text text1;
	private Composite control;
	private Group firstInstancePropertiesPanel;
	private Group secondInstancePropertiesPanel;
	private InstanceListPage instanceListPage;
	private InstanceGenerator instanceGenerator;
	private Text firstInstanceDetails;
	private Text secondInstanceDetails;
	private InstanceClafer value;

	public CompareAlgorithmPage(InstanceListPage instanceListPage, InstanceGenerator instanceGenerator) {
		super(Constants.COMPARE_ALGORITHM_PAGE);
		setTitle(Constants.COMPARE_TITLE);
		setDescription(Constants.COMPARE_DESCRIPTION);
		this.instanceListPage = instanceListPage;
		this.instanceGenerator = instanceGenerator;
	}

	@Override
	public void createControl(final Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ComboViewer firstAlgorithmClass;
		Label firstLabelInstanceList;
		this.control = new Composite(sc, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.control.setLayout(layout);

		//Second Set
		ComboViewer secondAlgorithmClass;
		Label secondLabelInstanceList;

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.control, "de.cognicrypt.codegenerator.help_id_3");

		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();

		//First set of Algorithm Combinations
		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		firstLabelInstanceList = new Label(compositeControl, SWT.NONE);
		firstLabelInstanceList.setText(Constants.COMPARE_LABEL);

		firstAlgorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
		Object algorithmCombination = instanceListPage.getAlgorithmCombinations();
		firstAlgorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		firstAlgorithmClass.setInput(algorithmCombination);

		//Second set of Algorithm Combinations
		final Composite compositeControl1 = new Composite(this.control, SWT.NONE);
		compositeControl1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		setPageComplete(false);
		compositeControl1.setLayout(new GridLayout(2, false));
		secondLabelInstanceList = new Label(compositeControl1, SWT.NONE);
		secondLabelInstanceList.setText(Constants.COMPARE_LABEL);

		secondAlgorithmClass = new ComboViewer(compositeControl1, SWT.DROP_DOWN | SWT.READ_ONLY);
		secondAlgorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		secondAlgorithmClass.setInput(algorithmCombination);

		//First set of Instance details
		this.firstInstancePropertiesPanel = new Group(this.control, SWT.NONE);
		firstInstancePropertiesPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		this.firstInstanceDetails = new Text(this.firstInstancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

		firstAlgorithmClass.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			CompareAlgorithmPage.this.firstInstancePropertiesPanel.setVisible(true);
			final String selectedAlgorithm = selection.getFirstElement().toString();
			setValue(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm));
			CompareAlgorithmPage.this.firstInstanceDetails.setText(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm)));
		});

		GridLayout gridLayout = new GridLayout();
		this.firstInstancePropertiesPanel.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.widthHint = 60;
		gridData.horizontalSpan = 1;
		gridData.heightHint = 89;
		this.firstInstancePropertiesPanel.setLayoutData(gridData);
		this.firstInstancePropertiesPanel.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);

		Display display = Display.getCurrent();
		this.firstInstanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.firstInstanceDetails.setBounds(10, 20, 400, 180);
		this.firstInstanceDetails.setEditable(false);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		this.firstInstanceDetails.setBackground(white);

		//Second set of Instance details
		this.secondInstancePropertiesPanel = new Group(this.control, SWT.NONE);
		secondInstancePropertiesPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.secondInstanceDetails = new Text(this.secondInstancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

		secondAlgorithmClass.addSelectionChangedListener(event -> {
			final IStructuredSelection selection1 = (IStructuredSelection) event.getSelection();
			CompareAlgorithmPage.this.secondInstancePropertiesPanel.setVisible(true);
			final String selectedAlgorithm1 = selection1.getFirstElement().toString();
			setValue(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm1));
			CompareAlgorithmPage.this.secondInstanceDetails.setText(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm1)));
		});

		this.secondInstancePropertiesPanel.setLayout(gridLayout);
		this.secondInstancePropertiesPanel.setLayoutData(gridData);
		this.secondInstancePropertiesPanel.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);

		this.secondInstanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.secondInstanceDetails.setBounds(10, 20, 400, 180);
		this.secondInstanceDetails.setEditable(false);
		this.secondInstanceDetails.setBackground(white);

		final ISelection defaultAlgorithm = new StructuredSelection(inst.keySet().toArray()[0]);
		firstAlgorithmClass.setSelection(defaultAlgorithm);
		secondAlgorithmClass.setSelection(defaultAlgorithm);

		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	private String getInstanceProperties(final InstanceClafer inst) {
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
		return output.toString();
	}

	private void getInstanceDetails(final InstanceClafer inst, final Map<String, String> algorithms) {
		String value;

		if (!inst.getType().getRef().getTargetType().isPrimitive()) {
			String algo = Constants.ALGORITHM + " :" + ClaferModelUtils
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
				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo) + value);
			}
			// Above for loop over children hasn't been executed, then following if
			if (!instan.hasChildren()) {
				value = "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2")) + " : " + inst.getRef().toString()
					.replace("\"", "");
				algo = algorithms.keySet().iterator().next();
				algorithms.put(algo, algorithms.get(algo) + value);
			}
		}
	}

	public String getText1() {
		return text1.getText();
	}

	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.control.setFocus();
		}
	}

	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}

	public InstanceClafer getValue() {
		return this.value;
	}
}
