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

import java.util.Set;

import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button advancedModeCheckBox;
	private Label label2;
	private String value = "";
	private final ClaferModel model;
	private boolean status = true;

	public TaskSelectionPage(final ClaferModel claferModel) {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TASK_LIST);
		setDescription(Labels.DESCRIPTION_TASK_SELECTION_PAGE);
		this.model = claferModel;
	}

	public boolean canProceed() {
		final String selectedTask = getValue();
		if (selectedTask.length() > 0) {
			// Special handling for TLS task
			if (!selectedTask.equals("Communicate over a secure channel")) {
				PropertiesMapperUtil.resetPropertiesMap();
				// PropertiesMapperUtil.resetEnumMap();
				final AstConcreteClafer claferSelected = PropertiesMapperUtil.getTaskLabelsMap().get(selectedTask);
				this.model.createClaferPropertiesMap(claferSelected);
			}
			setValue(selectedTask);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void createControl(final Composite parent) {
		final Set<String> availableTasks = PropertiesMapperUtil.getTaskLabelsMap().keySet();
		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 200, 200);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		this.container.setLayout(layout);

		this.label2 = new Label(this.container, SWT.NONE);
		this.label2.setText(Labels.LABEL2);

		this.taskComboSelection = new ComboViewer(this.container, SWT.COMPOSITION_SELECTION);
		this.taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());
		this.taskComboSelection.setInput(availableTasks);
		if (availableTasks.size() > 0) {
			// taskComboSelection.setSelection(new
			// StructuredSelection(availableTasks.iterator().next()));
		} else {
			this.taskComboSelection.setSelection(new StructuredSelection(Labels.NO_TASK));
		}

		this.taskComboSelection.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				final String selectedTask = selection.getFirstElement().toString();
				setValue(selectedTask);
			}
		});

		this.advancedModeCheckBox = new Button(this.container, SWT.CHECK);
		this.advancedModeCheckBox.setText(Constants.ADVANCED_MODE);
		this.advancedModeCheckBox.setSelection(false);
		setControl(this.container);
	}

	public boolean getStatus() {
		return this.status = !this.status;
	}

	public String getValue() {
		return this.value;
	}

	/**
	 * Helper method to UI , this flag decides the second page of the wizard
	 *
	 * @return
	 */
	public boolean isAdvancedMode() {
		return this.advancedModeCheckBox.getSelection();
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
