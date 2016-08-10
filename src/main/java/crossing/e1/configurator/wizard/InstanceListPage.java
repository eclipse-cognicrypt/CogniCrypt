/**
 * Copyright 2015 Technische Universität Darmstadt
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

import java.util.Map;

import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.configurator.utilities.XMLParser;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class InstanceListPage extends WizardPage implements Labels {

	private Composite control;
	private Text instanceDetails;
	private final InstanceGenerator instanceGenerator;
	private InstanceClafer value;
	private boolean val = false;
	private final XMLParser publisher;
	private Group instancePropertiesPanel;
	private Task selectedTask;

	public InstanceListPage(final InstanceGenerator inst, Task selectedTask) {
		super(Labels.SECOND_PAGE);
		setTitle("Possible solutions for task: " + selectedTask.getDescription());
		setDescription(Labels.DESCRIPTION_INSTANCE_LIST_PAGE);
		this.instanceGenerator = inst;
		this.publisher = new XMLParser();
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void createControl(final Composite parent) {

		//checkNumberOfInstances();

		ComboViewer algorithmClass;
		Label lableInstanceList;
		this.control = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(1, false);
		this.control.setLayout(layout);

		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		lableInstanceList = new Label(compositeControl, SWT.NONE);
		lableInstanceList.setText(Labels.instanceList);
		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();
		algorithmClass = new ComboViewer(compositeControl, SWT.COMPOSITION_SELECTION);
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());
		algorithmClass.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				return element.toString();
			}
		});
		algorithmClass.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				InstanceListPage.this.instancePropertiesPanel.setVisible(true);
				final String b = selection.getFirstElement().toString();
				setValue(InstanceListPage.this.instanceGenerator.getInstances().get(b));
				InstanceListPage.this.instanceDetails
					.setText(InstanceListPage.this.publisher.getInstanceProperties(InstanceListPage.this.instanceGenerator.getInstances().get(b), ""));
				if (selection.size() > 0) {
					InstanceListPage.this.val = true;
					setPageComplete(true);
				}
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

	public void checkNumberOfInstances() {
		if (instanceGenerator.getNoOfInstances() == 0) {
			MessageDialog.openError(new Shell(), "Error", Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE);
		}

	}

	public InstanceClafer getValue() {
		return this.value;
	}

	public Task getTask() {
		return selectedTask;
	}

	@Override
	public void setPageComplete(final boolean complete) {
		super.setPageComplete(complete);
	}

	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}
}
