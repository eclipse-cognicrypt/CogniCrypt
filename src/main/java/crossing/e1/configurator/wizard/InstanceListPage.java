/**
 * Copyright 2015-2016 Technische Universitaet Darmstadt
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
package crossing.e1.configurator.wizard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.clafer.ast.AstConcreteClafer;
import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.featuremodel.clafer.ClaferModelUtils;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

public class InstanceListPage extends WizardPage implements Labels {

	private Composite control;
	private Text instanceDetails;
	private final InstanceGenerator instanceGenerator;
	private InstanceClafer value;
	private Group instancePropertiesPanel;
	private Task selectedTask;

	public InstanceListPage(final InstanceGenerator inst, final Task selectedTask) {
		super(Labels.SECOND_PAGE);
		setTitle("Possible solutions for task: " + selectedTask.getDescription());
		setDescription(Labels.DESCRIPTION_INSTANCE_LIST_PAGE);
		this.instanceGenerator = inst;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	public void checkNumberOfInstances() {
		if (this.instanceGenerator.getNoOfInstances() == 0) {
			MessageDialog.openError(new Shell(), "Error", Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE);
		}
	}

	@Override
	public void createControl(final Composite parent) {

		//checkNumberOfInstances();

		ComboViewer algorithmClass;
		Label labelInstanceList;
		this.control = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(1, false);
		this.control.setLayout(layout);

		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		labelInstanceList = new Label(compositeControl, SWT.NONE);
		labelInstanceList.setText(Labels.instanceList);
		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();
		algorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());
		algorithmClass.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				return element.toString();
			}
		});
		algorithmClass.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			InstanceListPage.this.instancePropertiesPanel.setVisible(true);
			final String b = selection.getFirstElement().toString();
			setValue(InstanceListPage.this.instanceGenerator.getInstances().get(b));
			InstanceListPage.this.instanceDetails.setText(getInstanceProperties(InstanceListPage.this.instanceGenerator.getInstances().get(b)));

			if (selection.size() > 0) {
				setPageComplete(true);
			}
		});
		this.instancePropertiesPanel = new Group(this.control, SWT.NONE);
		this.instancePropertiesPanel.setText("Instance Details");
		final Font boldFont = new Font(this.instancePropertiesPanel.getDisplay(), new FontData("Arial", 12, SWT.BOLD));
		this.instancePropertiesPanel.setFont(boldFont);

		this.instanceDetails = new Text(this.instancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		this.instanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.instanceDetails.setBounds(10, 10, 400, 200);
		/*
		 * Initially instance properties panel will be hidden
		 */
		this.instancePropertiesPanel.setVisible(false);
		setControl(this.control);
		;
	}

	/**
	 *
	 * @param inst
	 * @return
	 */
	public String getInstanceProperties(final InstanceClafer inst) {
		String output = "";
		
		Map<String, String> algorithms = new HashMap<String, String>();
		InstanceClafer[] children = inst.getChildren();
		for(int i=0; i < children.length; i++)
		{
			getInstanceDetails(children[i], algorithms);
		}
		
		for (Map.Entry<String, String> entry : algorithms.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    if(!value.equals("")){
		    	output += key + value + Constants.lineSeparator;
		    }
		}
		
		return output;
	}
	
	/**
	 * 
	 * @param inst
	 * @param algorithms
	 */
	void getInstanceDetails(final InstanceClafer inst, Map<String, String> algorithms) {
		String value = "";
		
		if (!inst.getType().getRef().getTargetType().isPrimitive()) {
			String algo = Constants.ALGORITHM + " :" + ClaferModelUtils.removeScopePrefix(inst.getType().getRef().getTargetType().getName()) + Constants.lineSeparator;
			algorithms.put(algo, "");
			
			InstanceClafer instan = (InstanceClafer) inst.getRef();
			for (final InstanceClafer in : instan.getChildren()) {
				if (!in.getType().getRef().getTargetType().isPrimitive()){
					String superName = ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getSuperClafer().getName());
					if(!superName.equals("Enum")){
						getInstanceDetails(in, algorithms);
						continue;
					}
				}
				value = "\t" + ClaferModelUtils.removeScopePrefix(in.getType().getName()) + " : " + in.getRef().toString().replace("\"", "");
				if(value.indexOf("->") > 0) {	// VeryFast -> 4 or Fast -> 3	removing numerical value and "->"
					value = value.substring(0, value.indexOf("->")-1);
				}
				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo)+value);
			}
		}
	}

	public Task getTask() {
		return this.selectedTask;
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
}
