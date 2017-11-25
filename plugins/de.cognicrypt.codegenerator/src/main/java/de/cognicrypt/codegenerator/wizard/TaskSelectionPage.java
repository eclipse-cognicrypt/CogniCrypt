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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.Labels;
import de.cognicrypt.codegenerator.utilities.Utils;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button guidedModeCheckBox;
	private Label selectProjectLabel;
	private Label selectTaskLabel;
	private Label taskDescription;
	private Text descriptionText;
	private IProject selectedProject = null;

	public TaskSelectionPage() {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TASK_LIST);
		setDescription(Labels.DESCRIPTION_TASK_SELECTION_PAGE);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {

		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 200, 300);
		this.container.setLayout(null);
		
		/** To display the Help view after clicking the help icon
		 * @param help_id_1 
		 *        This id refers to HelpContexts_1.xml
		 */
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, "de.cognicrypt.codegenerator.help_id_1");
		
		this.selectProjectLabel = new Label(this.container, SWT.NONE);
		this.selectProjectLabel.setBounds(5, 5, 111, 15);
		this.selectProjectLabel.setText(Constants.SELECT_JAVA_PROJECT);

		ComboViewer projectComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		Combo combo = projectComboSelection.getCombo();
		combo.setEnabled(true);
        combo.setBounds(153, 5, 385, 23);
		projectComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		Map<String, IProject> javaProjects = new HashMap<String, IProject>();
		for (IProject project : Utils.createListOfJavaProjectsInCurrentWorkspace()) {
			javaProjects.put(project.getName(), project);
		}

		if (javaProjects.isEmpty()) {
			String[] errorMessage = { Constants.ERROR_MESSAGE_NO_PROJECT };
			projectComboSelection.setInput(errorMessage);
			projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
		} else {
			projectComboSelection.setInput(javaProjects.keySet().toArray());
			projectComboSelection.addSelectionChangedListener(event -> {
				final IStructuredSelection selected = (IStructuredSelection) event.getSelection();
				this.selectedProject = javaProjects.get((String) selected.getFirstElement());
				projectComboSelection.refresh();

			});

			IProject currentProject = Utils.getCurrentProject();
			if (currentProject == null) {
				projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
			} else {
				projectComboSelection.setSelection(new StructuredSelection(currentProject.getName()));
			}
		}

		this.selectTaskLabel = new Label(this.container, SWT.NONE);
		selectTaskLabel.setBounds(5, 45, 73, 15);
		this.selectTaskLabel.setText(Constants.SELECT_TASK);

		this.taskComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		Combo combo_1 = taskComboSelection.getCombo();
		combo_1.setEnabled(true);
		combo_1.setBounds(153, 45, 385, 23);
		this.taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		final List<Task> tasks = TaskJSONReader.getTasks();

		this.taskComboSelection.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object task) {
				if (task instanceof Task) {
					final Task current = (Task) task;
					return current.getDescription();
					
				}
				return super.getText(task);			
			}
		});

		this.taskComboSelection.setInput(tasks);
		
		// Adding description text for the cryptographic task that has been selected from the combo box
		this.descriptionText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		this.descriptionText.setToolTipText(Constants.DESCRIPTION_BOX_TOOLTIP);
		this.descriptionText.setEditable(false);
		this.descriptionText.setBounds(153, 85, 385, 95);
		
		this.taskComboSelection.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			final Task selectedTask = (Task) selection.getFirstElement();
			TaskSelectionPage.this.taskComboSelection.refresh();
			setPageComplete(selectedTask != null && this.selectedProject != null);
			// To display the description text
			this.descriptionText.setText(selectedTask.getTaskDescription())	;
		});

		this.taskComboSelection.setSelection(new StructuredSelection(tasks.get(0)));
		setControl(this.container);
		
		//Label for task description
		this.taskDescription = new Label(this.container, SWT.NONE);
		this.taskDescription.setBounds(5, 85, 93, 15);
		this.taskDescription.setText(Constants.TASK_DESCRIPTION);
		
		//Check box for going to guided mode
		this.guidedModeCheckBox = new Button(container, SWT.CHECK);
		this.guidedModeCheckBox.setEnabled(true);
		this.guidedModeCheckBox.setBounds(5, 205, 261, 16);
		this.guidedModeCheckBox.addSelectionListener(new SelectionAdapter() {
		    @Override
			public void widgetSelected(SelectionEvent e) {
				
		    }
		});
		this.guidedModeCheckBox.setText(Constants.GUIDED_MODE);
		this.guidedModeCheckBox.setSelection(true);
		
	}

	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	public Task getSelectedTask() {
		return (Task) ((IStructuredSelection) this.taskComboSelection.getSelection()).getFirstElement();
	}

	/**
	 * Helper method to UI , this flag decides the second page of the wizard
	 *
	 * @return
	 */
	public boolean isGuidedMode() {
			return this.guidedModeCheckBox.getSelection();
	}

	@Override
	public void setVisible( boolean visible ) {
	  super.setVisible( visible );
	  if( visible ) {
	    container.setFocus();
	  }
	}
}
