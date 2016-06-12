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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.tasks.TaskJSONReader;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button advancedModeCheckBox;
	private Label label2;
//	private Task selectedTask = null;
	//private final ClaferModel model;
	private boolean status = true;
	private boolean canProceed = false;

	public TaskSelectionPage() {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TASK_LIST);
		setDescription(Labels.DESCRIPTION_TASK_SELECTION_PAGE);
		//this.model = claferModel;
	}
	
	public Task getSelectedTask(){
		return (Task) ((IStructuredSelection) taskComboSelection.getSelection()).getFirstElement();
	}

	public boolean canProceed() {
		return canProceed;
	}

	@Override
	public void createControl(final Composite parent) {
		
		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 200, 200);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		this.container.setLayout(layout);

		this.label2 = new Label(this.container, SWT.NONE);
		this.label2.setText(Labels.LABEL2);

		this.taskComboSelection = new ComboViewer(this.container, SWT.COMPOSITION_SELECTION);
		this.taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		final List<Task> tasks = TaskJSONReader.getTasks();

	    /* if the current person is selected, show text */
		taskComboSelection.setLabelProvider(new LabelProvider() {
	        @Override
	        public String getText(Object element) {
	            if (element instanceof Task) {
	                Task current = (Task) element;
	                return current.getDescription();
	            }
	            return super.getText(element);
	        }
	    });
		 
	    
		taskComboSelection.setInput(tasks);
	    
		taskComboSelection.addSelectionChangedListener(new ISelectionChangedListener() {
	        @Override
	        public void selectionChanged(SelectionChangedEvent event) {
	            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
	            Task selectedTask = (Task)selection.getFirstElement();

	            taskComboSelection.refresh();
	            
	            if(selectedTask != null){
	            	canProceed = true;
	            }
	        }
	    });
		
		taskComboSelection.setSelection(new StructuredSelection(tasks.get(0)));

		this.advancedModeCheckBox = new Button(this.container, SWT.CHECK);
		this.advancedModeCheckBox.setText(Constants.ADVANCED_MODE);
		this.advancedModeCheckBox.setSelection(false);
		setControl(this.container);
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
