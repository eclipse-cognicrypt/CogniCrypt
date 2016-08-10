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

package crossing.e1.configurator.wizard.advanced;

import java.util.ArrayList;
import java.util.List;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import crossing.e1.configurator.utilities.Labels;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.ClaferModelUtils;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

public class AdvancedUserValueSelectionPage extends WizardPage implements Labels {

	private Composite container;
	private List<PropertyWidget> userConstraints = new ArrayList<PropertyWidget>();
	private AstConcreteClafer taskClafer;

	public AdvancedUserValueSelectionPage(ClaferModel claferModel, AstConcreteClafer taskClafer) {
		super(Labels.SELECT_PROPERTIES);
		setTitle(Labels.PROPERTIES);
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.taskClafer = taskClafer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		container.setBounds(10, 10, 450, 200);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		//		try {
		//			Group titledPanel = new Group(container, SWT.NONE);
		//			titledPanel.setText("Global Constraints");
		//			Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 12, SWT.BOLD));
		//			titledPanel.setFont(boldFont);
		//			GridLayout layout2 = new GridLayout();
		//			// sent number of columns in a widget
		//			layout2.numColumns = 4;
		//			titledPanel.setLayout(layout2);
		//			// List constraints from ENUMmap as group properties, under single
		//			// titled panel
		//			for (AstAbstractClafer groupPropertiesKey : PropertiesMapperUtil.getenumMap().keySet()) {
		//
		//				userConstraints.add(new ComplexWidget(titledPanel, groupPropertiesKey,
		//						PropertiesMapperUtil.getenumMap().get(groupPropertiesKey)));
		//
		//			}
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}

		// Add every constraints to its parent and group it as a separate titled
		// panel

		for (AstClafer taskAlgorithm : taskClafer.getChildren()) {
			Group titledPanel = createPanel(ClaferModelUtils.removeScopePrefix(taskAlgorithm.getRef().getTargetType().getName()), container);
			createConstraints(taskClafer, taskAlgorithm, titledPanel);
		}

		setControl(container);
	}

	public void createConstraints(AstClafer parent, AstClafer inputClafer, Group titledPanel) {

		if (inputClafer.hasChildren()) {
			if (inputClafer.getGroupCard() != null && inputClafer.getGroupCard().getLow() >= 1) {
				userConstraints
					.add(new PropertyWidget(titledPanel, parent, (AstConcreteClafer) inputClafer, ClaferModelUtils.removeScopePrefix(inputClafer.getName()), 1, 0, 1024, 0, 1, 1));
			} else
				for (AstConcreteClafer childClafer : inputClafer.getChildren()) {
					createConstraints(parent, childClafer, titledPanel);
				}
		}

		if (inputClafer.hasRef()) {
			if (inputClafer.getRef().getTargetType().isPrimitive() && !(inputClafer.getRef().getTargetType().getName().contains("string"))) {
				if (!ClaferModelUtils.isAbstract(inputClafer)) {
					userConstraints.add(
						new PropertyWidget(titledPanel, parent, (AstConcreteClafer) inputClafer, ClaferModelUtils.removeScopePrefix(inputClafer.getName()), 1, 0, 1024, 0, 1, 1));
				}
			} else if (PropertiesMapperUtil.getenumMap().containsKey(inputClafer.getRef().getTargetType())) {
				createConstraints(inputClafer, inputClafer.getRef().getTargetType(), titledPanel);
			} else if (!inputClafer.getRef().getTargetType().isPrimitive()) {
				if (!ClaferModelUtils.removeScopePrefix(inputClafer.getRef().getTargetType().getName()).equals(titledPanel.getText())) {
					Group childPanel = createPanel(ClaferModelUtils.removeScopePrefix(inputClafer.getRef().getTargetType().getName()), titledPanel);
					createConstraints(inputClafer, inputClafer.getRef().getTargetType(), childPanel);
				} else {
					//same panel as main algorithm type (e.g., kda in secure pwd storage)
					createConstraints(inputClafer, inputClafer.getRef().getTargetType(), titledPanel);
				}
			}
		}

		if (inputClafer.getSuperClafer() != null) {
			createConstraints(parent, inputClafer.getSuperClafer(), titledPanel);
		}

	}

	public void createConstraints(AstClafer parent, AstAbstractClafer inputClafer, Group titledPanel) {

		if (inputClafer.hasChildren()) {
			for (AstConcreteClafer in : inputClafer.getChildren()) {
				createConstraints(parent, in, titledPanel);
			}
		}
		if (inputClafer.hasRef()) {
			createConstraints(parent, inputClafer.getRef().getTargetType(), titledPanel);
		}

		if (inputClafer.getSuperClafer() != null) {
			createConstraints(parent, inputClafer.getSuperClafer(), titledPanel);
		}
	}

	private Group createPanel(String name, Composite parent) {
		Group titledPanel = new Group(parent, SWT.LEFT);
		titledPanel.setText(name);
		Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 12, SWT.BOLD));
		titledPanel.setFont(boldFont);
		GridLayout layout2 = new GridLayout();

		layout2.numColumns = 4;
		titledPanel.setLayout(layout2);

		return titledPanel;
	}

	public boolean getPageStatus() {
		return PropertyWidget.status;
	}

	public List<PropertyWidget> getConstraints() {
		return userConstraints;
	}

}
