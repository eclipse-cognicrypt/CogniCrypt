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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.ClaferModelUtils;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

public class ValueSelectionPage extends WizardPage implements Labels {

	private List<Spinner> taskCombo;
	private Composite container;
	private List<AstConcreteClafer> label;
	private List<AstConcreteClafer> mainClafer;
	private List<ComboViewer> options;
	private HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> userOptions;
	private HashMap<String, AstConcreteClafer> userGroupOptions;
	private HashMap<AstAbstractClafer, ArrayList<Integer>> groupPropertiesMap = new HashMap<AstAbstractClafer, ArrayList<Integer>>();
	private List<ComplexWidget> userConstraints = new ArrayList<ComplexWidget>();
	private static boolean statusPage = false;

	public ValueSelectionPage(List<AstConcreteClafer> items, ClaferModel claferModel) {
		super(Labels.SELECT_PROPERTIES);
		setTitle(Labels.PROPERTIES);
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		userOptions = new HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>>();
		userGroupOptions = new HashMap<String, AstConcreteClafer>();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void createControl(Composite parent) {

		taskCombo = new ArrayList<Spinner>();
		label = new ArrayList<AstConcreteClafer>();
		options = new ArrayList<ComboViewer>();
		mainClafer = new ArrayList<AstConcreteClafer>();

		container = new Composite(parent, SWT.NONE);

		container.setBounds(10, 10, 450, 200);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		try {
			Group titledPanel = new Group(container, SWT.NONE);
			titledPanel.setText("Global Constraints");
			Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 12, SWT.BOLD));
			titledPanel.setFont(boldFont);
			GridLayout layout2 = new GridLayout();

			layout2.numColumns = 4;
			titledPanel.setLayout(layout2);
			for (AstAbstractClafer groupPropertiesKey : PropertiesMapperUtil.getenumMap().keySet()) {

				userConstraints.add(
						new ComplexWidget(titledPanel, groupPropertiesKey, PropertiesMapperUtil.getenumMap().get(groupPropertiesKey)));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (AstConcreteClafer clafer : PropertiesMapperUtil.getPropertiesMap().keySet()) {
			System.out.println(PropertiesMapperUtil.getPropertiesMap());
			// Label label3 = new Label(container, SWT.NONE);
			// Font boldFont = new Font( label3.getDisplay(), new FontData(
			// "Arial", 10, SWT.BOLD ));
			// label3.setFont(boldFont);
			// label3.setText(parser.trim(parser.trim(clafer.getName())));
			// Label label4 = new Label(container, SWT.NONE);
			// label4.setText("");

			for (AstClafer claf : PropertiesMapperUtil.getPropertiesMap().get(clafer)) {

				if (PropertiesMapperUtil.getenumMap()
						.containsKey((AstAbstractClafer) ((AstConcreteClafer) claf).getParent()))
					System.out.println("TRUE");

			}
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
			System.out.println("prop" + claferProperties.toString());

			for (AstConcreteClafer property : claferProperties) {
				userConstraints.add(new ComplexWidget(titledPanel, clafer, property,
						ClaferModelUtils.removeScopePrefix(property.getName()), 1, 0, 1024, 0, 1, 1));
			}

		}
		setControl(container);
	}

	// void getWidget(Composite container, AstConcreteClafer key1,
	// AstConcreteClafer key2, String label, int selection,
	// int min, int max, int digits, int increment, int pageincrement) {
	// List<String> values = new ArrayList<String>();
	// values.add(Labels.LESS_THAN);
	// values.add(Labels.GREATER_THAN);
	// values.add(Labels.EQUALS);
	// values.add(Labels.LESS_THAN_EQUAL);
	// values.add(Labels.GREATER_THAN_EQUAL);
	// Label label5 = new Label(container, SWT.NONE);
	// label5.setText(" ");
	// Label label1 = new Label(container, SWT.NONE);
	// label1.setText(label);
	//
	// ComboViewer option = new ComboViewer(container, SWT.NONE);
	// option.setContentProvider(ArrayContentProvider.getInstance());
	// option.setInput(values);
	// option.setSelection(new StructuredSelection(values.get(2)));
	//
	// Spinner taskComb = new Spinner(container, SWT.BORDER | SWT.SINGLE);
	// taskComb.setValues(selection, min, max, digits, increment,
	// pageincrement);
	// taskComb.addModifyListener(new ModifyListener() {
	//
	// @Override
	// public void modifyText(ModifyEvent arg0) {
	// setComplete(true);
	//
	// }
	//
	// });
	//
	// this.mainClafer.add(key1);
	// this.label.add(key2);
	// this.options.add(option);
	// this.taskCombo.add(taskComb);
	// }

//	void getWidget(Composite container, AstAbstractClafer claferMain, List<AstClafer> claferProperty) {
//
//		List<String> values = new ArrayList<String>();
//		values.add(Labels.LESS_THAN);
//		values.add(Labels.GREATER_THAN);
//		values.add(Labels.EQUALS);
//		values.add(Labels.LESS_THAN_EQUAL);
//		values.add(Labels.GREATER_THAN_EQUAL);
//		Label label5 = new Label(container, SWT.NONE);
//		label5.setText("	");
//
//		Label label1 = new Label(container, SWT.NONE);
//		label1.setText(ClaferModelUtils.removeScopePrefix(claferMain.getName()));
//
//		ComboViewer option = new ComboViewer(container, SWT.NONE);
//		option.setContentProvider(ArrayContentProvider.getInstance());
//		option.setInput(values);
//		option.setSelection(new StructuredSelection(values.get(2)));
//		ArrayList<String> comboValues = new ArrayList<String>();
//		for (AstClafer comboValue : claferProperty) {
//			comboValues.add(ClaferModelUtils.removeScopePrefix(comboValue.getName()));
//		}
//
//		ComboViewer valuesCombo = new ComboViewer(container, SWT.NONE);
//		valuesCombo.setContentProvider(ArrayContentProvider.getInstance());
//		valuesCombo.setInput(comboValues);
//		valuesCombo.setSelection(new StructuredSelection(comboValues.get(0)));
//
//	}

	public boolean getPageStatus() {
//		setMap();
		return !userConstraints.isEmpty();
	}

	public List<ComplexWidget> getConstraints() {
		return userConstraints;
	}


//	public Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> getMap() {
//		return userOptions;
//	}

	/**
	 * Set user selected values to the clafer properties
	 */
//	public void setMap() {
////		ArrayList<Integer> values;
////		for (int i = 0; i < label.size(); i++) {
////			if (taskCombo.get(i) == null) {
////				values = new ArrayList<Integer>();
////				ArrayList<AstConcreteClafer> keys = new ArrayList<AstConcreteClafer>();
////				keys.add(mainClafer.get(i));
////				keys.add(label.get(i));
////				String test = mainClafer.get(i).getName()
////						+ options.get(i).getSelection().toString().replace("[", "").replace("]", "");
////				keys.add(userGroupOptions.get(test));
////				values.add(6);
////				values.add(label.get(i).getGroupCard().getLow());
////				userOptions.put(keys, values);
////			} else {
////				values = new ArrayList<Integer>();
////				ArrayList<AstConcreteClafer> keys = new ArrayList<AstConcreteClafer>();
////				keys.add(mainClafer.get(i));
////				keys.add(label.get(i));
////				values.add(toNumber(options.get(i).getSelection().toString()));
////				values.add(taskCombo.get(i).getSelection());
////				userOptions.put(keys, values);
////			}
////		}
//		
//		for(ComplexWidget widget: userConstraints){
//			if(widget.isGroupConstraint()){
//				
//			}else{
//				
//			}
//		}
//	}

	
//
//	public Map<AstAbstractClafer, ArrayList<Integer>> getGroupMap() {
//
//		for (AstAbstractClafer property : PropertiesMapperUtil.getenumMap().keySet()) {
//			Integer value = 1;
//			Integer operator = 2;
//			ArrayList<Integer> list = new ArrayList<Integer>();
//			list.add(value);
//			list.add(operator);
//			groupPropertiesMap.put(property, list);
//		}
//		return groupPropertiesMap;
//	}
}
