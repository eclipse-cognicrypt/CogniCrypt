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

/**
 * @author Ram Kamath
 *
 */
package de.cognicrypt.codegenerator.wizard;

import java.util.HashMap;
import java.util.Map;

import org.clafer.collection.Cons;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import org.eclipse.swt.widgets.Combo;

public class CompareAlgorithmPage extends WizardPage {

	private IProject selectedProject = null;
	Text text1;
	private Composite control;
	private Group instancePropertiesPanel;
	private Group instancePropertiesPanel1;
	private InstanceListPage instanceListPage;
	private InstanceGenerator instanceGenerator;
	private Text instanceDetails;
	private Text instanceDetails1;
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

		ComboViewer algorithmClass;
		Label labelInstanceList;
		this.control = new Composite(sc, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.control.setLayout(layout);
		
		//Second Set
		ComboViewer algorithmClass1;
		Label labelInstanceList1;
		
		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.control, "de.cognicrypt.codegenerator.help_id_3");

		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();
		
		//First set of Algorithm Combinations
		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		labelInstanceList = new Label(compositeControl, SWT.NONE);
		labelInstanceList.setText(Constants.instanceList);
		
		algorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
		Object algorithmCombination=instanceListPage.getAlgorithmCombinations();
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(algorithmCombination);
		
		//Second set of Algorithm Combinations
		final Composite compositeControl1 = new Composite(this.control, SWT.NONE);
		compositeControl1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		setPageComplete(false);
		compositeControl1.setLayout(new GridLayout(2, false));
		labelInstanceList1 = new Label(compositeControl1, SWT.NONE);
		labelInstanceList1.setText(Constants.instanceList);
		
		algorithmClass1 = new ComboViewer(compositeControl1, SWT.DROP_DOWN| SWT.READ_ONLY);
		algorithmClass1.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass1.setInput(algorithmCombination);
		
		//First set of Instance details
		this.instancePropertiesPanel = new Group(this.control, SWT.NONE);
		instancePropertiesPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		this.instanceDetails = new Text(this.instancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
	
		algorithmClass.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			CompareAlgorithmPage.this.instancePropertiesPanel.setVisible(true);
			final String selectedAlgorithm = selection.getFirstElement().toString();	
			setValue(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm));
			CompareAlgorithmPage.this.instanceDetails.setText(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm)));
		});
		
		this.instancePropertiesPanel.setText(Constants.INSTANCE_DETAILS);
		GridLayout gridLayout = new GridLayout();
		this.instancePropertiesPanel.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		gridData.heightHint=200;
		this.instancePropertiesPanel.setLayoutData(gridData);
		this.instancePropertiesPanel.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);

		//Hide scroll bar in instance details text box
		Listener scrollBarListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				Text t = (Text) event.widget;
				Rectangle r1 = t.getClientArea();
				Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
				Point p = t.computeSize(r1.x, SWT.DEFAULT, true);
				t.getVerticalBar().setVisible(r2.height <= p.y);
				if (event.type == SWT.Modify) {
					t.getParent().layout(true);
					t.showSelection();
				}
			}
		};
		Display display = Display.getCurrent();
		this.instanceDetails.addListener(SWT.Resize, scrollBarListener);
		this.instanceDetails.addListener(SWT.Modify, scrollBarListener);
		GridData gd_instanceDetails = new GridData(GridData.FILL_BOTH);
		gd_instanceDetails.widthHint = 157;
		this.instanceDetails.setLayoutData(gd_instanceDetails);
		this.instanceDetails.setBounds(10, 20, 400, 180);
		this.instanceDetails.setEditable(false);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		this.instanceDetails.setBackground(white);
		
		//Second set of Instance details
		this.instancePropertiesPanel1 = new Group(this.control, SWT.NONE);
		instancePropertiesPanel1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.instanceDetails1 = new Text(this.instancePropertiesPanel1, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		
		algorithmClass1.addSelectionChangedListener(event -> {
			final IStructuredSelection selection1 = (IStructuredSelection) event.getSelection();
			CompareAlgorithmPage.this.instancePropertiesPanel1.setVisible(true);
			final String selectedAlgorithm1 = selection1.getFirstElement().toString();	
			setValue(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm1));
			CompareAlgorithmPage.this.instanceDetails1.setText(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm1)));
		});
		
		this.instancePropertiesPanel1.setText(Constants.INSTANCE_DETAILS);
		GridLayout gridLayout1 = new GridLayout();
		this.instancePropertiesPanel1.setLayout(gridLayout1);
		GridData gridData1 = new GridData(SWT.FILL, GridData.FILL, true, true);
		gridData1.widthHint = 60;
		gridData1.horizontalSpan = 1;
		gridData1.heightHint=89;
		this.instancePropertiesPanel.setLayoutData(gridData1);
		this.instancePropertiesPanel1.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);

		//Hide scroll bar in instance details text box
		Listener scrollBarListener1 = new Listener() {

			@Override
			public void handleEvent(Event event) {
				Text t = (Text) event.widget;
				Rectangle r1 = t.getClientArea();
				Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
				Point p = t.computeSize(r1.x, SWT.DEFAULT, true);
				t.getVerticalBar().setVisible(r2.height <= p.y);
				if (event.type == SWT.Modify) {
					t.getParent().layout(true);
					t.showSelection();
				}
			}
		};
		Display display1 = Display.getCurrent();
		this.instanceDetails1.addListener(SWT.Resize, scrollBarListener1);
		this.instanceDetails1.addListener(SWT.Modify, scrollBarListener1);
		this.instanceDetails1.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.instanceDetails1.setBounds(10, 20, 400, 180);
		this.instanceDetails1.setEditable(false);
		Color white1 = display1.getSystemColor(SWT.COLOR_WHITE);
		this.instanceDetails1.setBackground(white1);
		

		final ISelection defaultAlgorithm = new StructuredSelection(inst.keySet().toArray()[0]);
		algorithmClass.setSelection(defaultAlgorithm);
		algorithmClass1.setSelection(defaultAlgorithm);
		
		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
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
			String algo = Constants.ALGORITHM + " :" + ClaferModelUtils.removeScopePrefix(inst.getType().getRef().getTargetType().getName()) + Constants.lineSeparator;
			algorithms.put(algo, "");

			final InstanceClafer instan = (InstanceClafer) inst.getRef();
			for (final InstanceClafer in : instan.getChildren()) {
				if (in.getType().getRef() != null && !in.getType().getRef().getTargetType().isPrimitive()) {
					final String superName = ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getSuperClafer().getName());
					if (!superName.equals("Enum")) {
						getInstanceDetails(in, algorithms);
						continue;
					}
				}
				value = "\t" + ClaferModelUtils.removeScopePrefix(in.getType().getName()) + " : " + ((in.getRef() != null) ? in.getRef().toString().replace("\"", "") : "");
				if (value.indexOf("->") > 0) {	// VeryFast -> 4 or Fast -> 3	removing numerical value and "->"
					value = value.substring(0, value.indexOf("->") - 1);
				}
				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo) + value);
			}
			// Above for loop over children hasn't been executed, then following if
			if (!instan.hasChildren()) {
				value = "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName()) + " : " + inst.getRef().toString().replace("\"", "");
				algo = algorithms.keySet().iterator().next();
				algorithms.put(algo, algorithms.get(algo) + value);
			}
		}
	}
}
