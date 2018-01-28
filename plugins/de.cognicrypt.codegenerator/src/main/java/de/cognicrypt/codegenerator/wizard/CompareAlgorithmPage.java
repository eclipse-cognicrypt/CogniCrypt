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

import org.clafer.instance.InstanceClafer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;

public class CompareAlgorithmPage extends WizardPage {

	private Composite container;
	private IProject selectedProject = null;
	Text text1;
	private Composite control;
	private Group instancePropertiesPanel;
	private Group instancePropertiesPanel1;
	private Text instanceDetails;
	private InstanceClafer value;
	
	private TaskSelectionPage taskSelectionPage;
	private ConfiguratorWizard configuratorWizard;
	private CompareWizard CompareWizard;
	private InstanceGenerator instanceGenerator;


	public CompareAlgorithmPage() {
		// TODO Auto-generated constructor stub
		super("abc");
		setTitle("ABC");
		setDescription("abc");

	}

	public CompareAlgorithmPage(final InstanceGenerator inst, final TaskSelectionPage taskSelectionPage, CompareWizard compWizard) {
		// TODO Auto-generated constructor stub
		super("abc");
		setTitle("ABC");
		setDescription("abc");
		this.instanceGenerator = inst;
		this.taskSelectionPage = taskSelectionPage;
		this.CompareWizard = compWizard;
	}

	@Override
	public void createControl(final Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ComboViewer algorithmClass;
		Label labelInstanceList;
		this.control = new Composite(sc, SWT.NONE);
		GridLayout layout = new GridLayout();
		this.control.setLayout(layout);

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.control, "de.cognicrypt.codegenerator.help_id_3");
		
		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		labelInstanceList = new Label(compositeControl, SWT.NONE);
		labelInstanceList.setText(Constants.instanceList);
//		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();
		algorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
//		String firstInstance = inst.keySet().toArray()[0].toString();
		Combo combo = algorithmClass.getCombo();
//		String key = instanceGenerator.getAlgorithmName();
//		int count = instanceGenerator.getAlgorithmCount();
//		combo.setToolTipText("There are " + String.format("%d", count) + " variations of the algorithm " + key);
//
//		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
//		algorithmClass.setInput(inst.keySet());
		
		//Display help assist for the first instance in the combo box
		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		Text infoText = new Text(control, SWT.BORDER | SWT.WRAP );
		infoText.setText(Constants.DEFAULT_ALGORITHM_NOTIFICATION);
		infoText.setEditable(false);
		infoText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));		
		final ControlDecoration deco = new ControlDecoration(infoText, SWT.RIGHT);
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage();		
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);	
		
		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		this.instancePropertiesPanel = new Group(this.control, SWT.NONE);
		this.instanceDetails = new Text(this.instancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		
		Composite composite_Control = new Composite(this.instancePropertiesPanel, SWT.BOTTOM | SWT.CENTER);
		composite_Control.setLayoutData(new GridData(SWT.CENTER, GridData.FILL, true, false));
		composite_Control.setLayout(new GridLayout(3, true)); 
//		
//		//Back button to go to the previous algorithm in the combo box
//				Button backIcon = new Button(composite_Control, SWT.CENTER | SWT.BOTTOM);
//				backIcon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//				backIcon.setText("<");		
//				backIcon.addSelectionListener(new SelectionAdapter() {			
//					@Override
//					public void widgetSelected(SelectionEvent e) {				
//						int temp = combo.getSelectionIndex();
//						if (temp != 0){							
//							temp = temp - 1;
//						    final ISelection selection = new StructuredSelection(inst.keySet().toArray()[temp]);
//							algorithmClass.setSelection(selection);
//						    }
//					}
//				});
//				
//				//Label that displays the current algorithm variation and the total number of variations
//				Label algorithmVariation = new Label(composite_Control, SWT.CENTER | SWT.BOTTOM);
//				algorithmVariation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
//				
//				//Button to go to the next algorithm in the combo box
//				Button nextIcon = new Button(composite_Control, SWT.CENTER | SWT.BOTTOM);
//				nextIcon.setText(">");
//				nextIcon.addSelectionListener(new SelectionAdapter() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						int temp = combo.getSelectionIndex();
//						if (temp != (count-1)){
//							temp = temp + 1;
//					        final ISelection selection = new StructuredSelection(inst.keySet().toArray()[temp]);
//						    algorithmClass.setSelection(selection);				    
//						}
//						
//					}
//				});
//				
//				algorithmClass.setLabelProvider(new LabelProvider() {
//
//					@Override
//					public String getText(final Object element) {
//						return element.toString();
//					}
//				});
//				algorithmClass.addSelectionChangedListener(event -> {
//					final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
//					CompareAlgorithmPage.this.instancePropertiesPanel.setVisible(true);
//					final String selectedAlgorithm = selection.getFirstElement().toString();	
//					setValue(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm));
//					CompareAlgorithmPage.this.instanceDetails.setText(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithm)));
//					int index = combo.getSelectionIndex();
//					algorithmVariation.setText("  Variation  " + (index + 1) + " / " + String.format("%d  ",count ));
//					if (!selectedAlgorithm.equals(firstInstance)) {
//						//hide the help assist and the text if the selected algorithm is not the default algorithm
//						deco.hide();
//						infoText.setVisible(false);	
//						backIcon.setEnabled(true);
//					} else {
//						infoText.setVisible(true);
//						deco.show();
//						//disable back button if the selected algorithm in the combo box is the first instance
//						backIcon.setEnabled(false);
//					}
//					if (combo.getSelectionIndex() == count-1){
//						//disable next button if the selected algorithm in the combo box is the last instance
//						nextIcon.setEnabled(false);
//					} else {
//						nextIcon.setEnabled(true);
//					}
//					if (selection.size() > 0) {
//						setPageComplete(true);
//					}
//				});		
//		
		
		
		
		
		instancePropertiesPanel = new Group(this.control, SWT.NONE);
		instancePropertiesPanel.setText(Constants.INSTANCE_DETAILS);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		instancePropertiesPanel.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.NONE, SWT.NONE, true, true);
		gridData.horizontalSpan = 1;
		gridData.heightHint = 200;
		instancePropertiesPanel.setLayoutData(gridData);

//		//SecondPart
//		final Composite compositeControl1 = new Composite(this.control, SWT.NONE);
//		setPageComplete(false);
//		compositeControl1.setLayout(new GridLayout(2, false));
//		labelInstanceList = new Label(compositeControl1, SWT.NONE);
//		labelInstanceList.setText(Constants.instanceList);
////				final Map<String, InstanceClafer> inst = instanceGenerator.getInstances();
//		algorithmClass = new ComboViewer(compositeControl1, SWT.DROP_DOWN | SWT.READ_ONLY);
//		//		String firstInstance = inst.keySet().toArray()[0].toString();
//		//		Combo combo = algorithmClass.getCombo();
//		//		String key = instanceGenerator.getAlgorithmName();
//		//		int count = instanceGenerator.getAlgorithmCount();
//		//		combo.setToolTipText("There are " + String.format("%d", count) + " variations of the algorithm " + key);
//
////				algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
////				algorithmClass.setInput("abc");
//
//		instancePropertiesPanel1 = new Group(this.control, SWT.NONE);
//		instancePropertiesPanel1.setText(Constants.INSTANCE_DETAILS);
//		GridLayout gridLayout1= new GridLayout();
//		gridLayout1.numColumns = 1;
//		instancePropertiesPanel1.setLayout(gridLayout1);
//		GridData gridData1 = new GridData(SWT.NONE, SWT.NONE, true, true);
//		gridData1.horizontalSpan = 1;
//		gridData1.heightHint = 200;
//		instancePropertiesPanel1.setLayoutData(gridData1);

		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
		//        setControl(container);
		//        setPageComplete(false);

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
