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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.tasks.TaskJSONReader;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.configurator.utilities.Utils;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button advancedModeCheckBox;
	private Label selectTaskLabel;
	private boolean canProceed = false;
	private Button createJavaProject;
	public static IProject selectedProject = null;

	public TaskSelectionPage() {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TASK_LIST);
		setDescription(Labels.DESCRIPTION_TASK_SELECTION_PAGE);
	}

	public boolean canProceed() {
		return this.canProceed;
	}

	@Override
	public void createControl(final Composite parent) {

		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 200, 300);
		final GridLayout layout = new GridLayout(3, false);
		//layout.numColumns = 4;
		this.container.setLayout(layout);
		
		

		this.selectTaskLabel = new Label(this.container, SWT.NONE);
		this.selectTaskLabel.setText(Constants.SELECT_JAVA_PROJECT);

		this.taskComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<IProject> javaProjects = new ArrayList<IProject>();
		if (projects.length > 0) {
			for (int i = 0; i < projects.length; i++) {
				if (Boolean.TRUE.equals(Utils.checkIfJavaProjectSelected(projects[i]))) {
					javaProjects.add(projects[i]);
				}
			}

		}
		this.taskComboSelection.setInput(javaProjects);

		this.taskComboSelection.addSelectionChangedListener(event -> {
			final IStructuredSelection selected = (IStructuredSelection) event.getSelection();
			selectedProject = (IProject) selected.getFirstElement();

			TaskSelectionPage.this.taskComboSelection.refresh();

		});
		if (javaProjects.indexOf(Utils.ProjectSelection()) >= 0)
			this.taskComboSelection.setSelection(
					new StructuredSelection(javaProjects.get(javaProjects.indexOf(Utils.ProjectSelection()))));
		else
			this.taskComboSelection.setSelection(new StructuredSelection(javaProjects.get(0)));
		
		this.createJavaProject = new Button(this.container, SWT.BUTTON1);
		this.createJavaProject.setText("Create Java Project");
		setControl(this.container);
		
		this.selectTaskLabel = new Label(this.container, SWT.NONE);
		this.selectTaskLabel.setText(Constants.SELECT_TASK);

		this.taskComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
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

			TaskSelectionPage.this.taskComboSelection.refresh();

			if (selectedTask != null && selectedProject !=null) {
				TaskSelectionPage.this.canProceed = true;
			}
		});

		this.taskComboSelection.setSelection(new StructuredSelection(tasks.get(0)));

		this.advancedModeCheckBox = new Button(this.container, SWT.CHECK);
		this.advancedModeCheckBox.setText(Constants.ADVANCED_MODE);
		this.advancedModeCheckBox.setSelection(false);
		setControl(this.container);
		
		
	}

	public Task getSelectedTask() {
		return (Task) ((IStructuredSelection) this.taskComboSelection.getSelection()).getFirstElement();
	}

	/**
	 * Helper method to UI , this flag decides the second page of the wizard
	 *
	 * @return
	 */
	public boolean isAdvancedMode() {
		return this.advancedModeCheckBox.getSelection();
	}
}
