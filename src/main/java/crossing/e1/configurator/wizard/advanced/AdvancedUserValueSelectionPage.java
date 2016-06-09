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
import java.util.LinkedHashSet;
import java.util.List;

import org.clafer.ast.AstAbstractClafer;
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
	

	public AdvancedUserValueSelectionPage(List<AstConcreteClafer> items, ClaferModel claferModel) {
		super(Labels.SELECT_PROPERTIES);
		setTitle(Labels.PROPERTIES);
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);

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
		for (AstConcreteClafer clafer : PropertiesMapperUtil.getPropertiesMap().keySet()) {
			Group titledPanel = new Group(container, SWT.NONE);
			titledPanel.setText(ClaferModelUtils.removeScopePrefix(clafer.getName()));
			Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 12, SWT.BOLD));
			titledPanel.setFont(boldFont);
			GridLayout layout2 = new GridLayout();

			layout2.numColumns = 4;
			titledPanel.setLayout(layout2);
			
			@SuppressWarnings("unchecked")
			ArrayList<AstConcreteClafer> claferProperties = new ArrayList<AstConcreteClafer>(
					new LinkedHashSet(PropertiesMapperUtil.getPropertiesMap().get(clafer)));

			for (AstConcreteClafer property : claferProperties) {
				userConstraints.add(new PropertyWidget(titledPanel, clafer, property,
						ClaferModelUtils.removeScopePrefix(property.getName()), 1, 0, 1024, 0, 1, 1));
			}

		}
		setControl(container);
	}

	public boolean getPageStatus() {
		return PropertyWidget.status;
	}

	public List<PropertyWidget> getConstraints() {
		return userConstraints;
	}

}
