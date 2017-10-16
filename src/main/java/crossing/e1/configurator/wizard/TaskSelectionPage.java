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
package crossing.e1.configurator.wizard;

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

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.tasks.TaskJSONReader;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.configurator.utilities.Utils;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button guidedModeCheckBox;
	private Label selectTaskLabel;
	private Label selectTaskLabel_1;
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
		container.setLayout(null);

		this.selectTaskLabel = new Label(this.container, SWT.NONE);
		this.selectTaskLabel.setBounds(5, 9, 111, 15);
		this.selectTaskLabel.setText(Constants.SELECT_JAVA_PROJECT);

		ComboViewer projectComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		Combo combo = projectComboSelection.getCombo();
		combo.setEnabled(true);
        combo.setBounds(153, 5, 393, 23);
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

		this.selectTaskLabel_1 = new Label(this.container, SWT.NONE);
		selectTaskLabel_1.setBounds(5, 37, 73, 15);
		this.selectTaskLabel_1.setText(Constants.SELECT_TASK);

		this.taskComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		Combo combo_1 = taskComboSelection.getCombo();
		combo_1.setEnabled(true);
		combo_1.setBounds(153, 33, 393, 23);
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

		this.taskComboSelection.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			final Task selectedTask = (Task) selection.getFirstElement();
			//final String b = selection.getFirstElement().toString();
			TaskSelectionPage.this.taskComboSelection.refresh();
			setPageComplete(selectedTask != null && this.selectedProject != null);
		});

		this.taskComboSelection.setSelection(new StructuredSelection(tasks.get(0)));
		setControl(this.container);
		
		this.guidedModeCheckBox = new Button(container, SWT.CHECK);
		this.guidedModeCheckBox.setEnabled(true);
		this.guidedModeCheckBox.setBounds(5, 181, 261, 16);
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
}
