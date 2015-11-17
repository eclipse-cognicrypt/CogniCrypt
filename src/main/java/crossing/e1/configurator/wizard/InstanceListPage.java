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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.configurator.Labels;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class InstanceListPage extends WizardPage implements Labels {

	private Composite container;

	private InstanceGenerator instance;
	InstanceClafer value;
	boolean val = false;

	public InstanceListPage(InstanceGenerator inst) {
		super(Labels.SECOND_PAGE);
		setTitle(Labels.AVAILABLE_OPTIONS);
		setDescription(Labels.DESCRIPTION_INSTANCE_LIST_PAGE);
		this.instance = inst;
	}

	@Override
	public void createControl(Composite parent) {
		ComboViewer algorithmClass;
		Label lableInstanceList;
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setLayout(layout);
		setPageComplete(false);

		lableInstanceList = new Label(container, SWT.NONE);
		lableInstanceList.setText(Labels.instanceList);
		Map<String, InstanceClafer> inst = instance.getInstances();
		algorithmClass = new ComboViewer(container, SWT.COMPOSITION_SELECTION);
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());
		algorithmClass.setLabelProvider((new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}
		}));
		algorithmClass.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();

				String b = (String) selection.getFirstElement().toString();
				setValue(instance.getInstances().get(b));
				if (selection.size() > 0) {
					val = true;
					setPageComplete(true);
				}
			}

		});

		setControl(container);
		;
	}

	public InstanceClafer getValue() {
		return value;
	}

	public void setValue(InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}

	@Override
	public boolean canFlipToNextPage() {

		return val;
	}
}
