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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.configurator.Lables;
import crossing.e1.configurator.tasks.beginner.CryptoTask;
import crossing.e1.configurator.tasks.beginner.TaskUtils;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.ParseClafer;
import crossing.e1.featuremodel.clafer.StringLabelMapper;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button advancedModeCheckBox;
	private Label label2;
	private String value = "";
	private ClaferModel model;
	//private Map<String, String> tasks;

	ParseClafer parser = new ParseClafer();

	public TaskSelectionPage(ClaferModel claferModel) {
	
		super(Lables.SELECT_TASK);
		setTitle(Lables.TASK_LIST);
		setDescription(Lables.DESCRIPTION_TASK_SELECTION_PAGE);
		this.model = claferModel;

	}

	@Override
	public void createControl(Composite parent) {
		//tasks = new HashMap<String, String>();
		HashSet<CryptoTask> availableTasks = TaskUtils.getAvailableTasks();
		container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 200, 200);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setLayout(layout);

		label2 = new Label(container, SWT.NONE);
		label2.setText(Lables.LABEL2);
//		for (CryptoTask task : TaskUtils.getAvailableTasks())
//			tasks.put(task.getDisplayText(), task.getClaferTaskName());
		
		taskComboSelection = new ComboViewer(container, SWT.COMPOSITION_SELECTION);
		taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());
		taskComboSelection.setInput(TaskUtils.getAvailableTasks());
		if (availableTasks.size() > 0) {
			taskComboSelection.setSelection(new StructuredSelection(availableTasks.iterator().next()));
		} else {
			setPageComplete(false);
			taskComboSelection
					.setSelection(new StructuredSelection(Lables.NO_TASK));

		}
		taskComboSelection.setLabelProvider((new LabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof CryptoTask){
					return ((CryptoTask) element).getDisplayText();
				}
				return element.toString();
			}
		}));
		taskComboSelection
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();

						CryptoTask selectedTask = (CryptoTask) selection.getFirstElement();
					
						setValue(selectedTask.getClaferTaskName());

					}

				});
		
		advancedModeCheckBox = new Button(container,SWT.CHECK);
		advancedModeCheckBox.setText("Advanced Mode");
		advancedModeCheckBox.setSelection(false);
		setControl(container);
	}

	public boolean canProceed() {
		CryptoTask selectedTask = (CryptoTask) ((IStructuredSelection)taskComboSelection.getSelection()).getFirstElement();
		
		
		if (selectedTask != null){
			StringLabelMapper.resetProperties();
			parser.setConstraintClafers(StringLabelMapper.getTaskLabels().get(selectedTask.getClaferTaskName()));
			setValue(selectedTask.getClaferTaskName());
			return true;
		}else
			return false;
	}
	
	public CryptoTask getSelectedTask(){
		return (CryptoTask) ((IStructuredSelection)taskComboSelection.getSelection()).getFirstElement();
	}
	public boolean isAdvancedMode(){
		return advancedModeCheckBox.getSelection();
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
