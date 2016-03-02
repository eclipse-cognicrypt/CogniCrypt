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
	private final HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> userOptions;
	private final HashMap<String, AstConcreteClafer> userGroupOptions;
	private boolean statusPage = false;

	public ValueSelectionPage(final List<AstConcreteClafer> items, final ClaferModel claferModel) {
		super(Labels.SELECT_PROPERTIES);
		setTitle(Labels.PROPERTIES);
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.userOptions = new HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>>();
		this.userGroupOptions = new HashMap<String, AstConcreteClafer>();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void createControl(final Composite parent) {
		this.taskCombo = new ArrayList<Spinner>();
		this.label = new ArrayList<AstConcreteClafer>();
		this.options = new ArrayList<ComboViewer>();
		this.mainClafer = new ArrayList<AstConcreteClafer>();
		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 450, 200);
		final GridLayout layout = new GridLayout();
		this.container.setLayout(layout);
		layout.numColumns = 1;

		for (final AstConcreteClafer clafer : PropertiesMapperUtil.getPropertiesMap().keySet()) {

			// Label label3 = new Label(container, SWT.NONE);
			// Font boldFont = new Font( label3.getDisplay(), new FontData(
			// "Arial", 10, SWT.BOLD ));
			// label3.setFont(boldFont);
			// label3.setText(parser.trim(parser.trim(clafer.getName())));
			// Label label4 = new Label(container, SWT.NONE);
			// label4.setText("");

			final Group titledPanel = new Group(this.container, SWT.NONE);
			titledPanel.setText(ClaferModelUtils.removeScopePrefix(clafer.getName()));
			final Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 12, SWT.BOLD));
			titledPanel.setFont(boldFont);
			final GridLayout layout2 = new GridLayout();

			layout2.numColumns = 4;
			titledPanel.setLayout(layout2);
			@SuppressWarnings("unchecked")
			final ArrayList<AstConcreteClafer> x = new ArrayList<AstConcreteClafer>(
					new LinkedHashSet(PropertiesMapperUtil.getPropertiesMap().get(clafer)));
			for (final AstConcreteClafer claf : x) {

				if (claf.getGroupCard().getLow() >= 1) {
					getWidget(titledPanel, clafer, claf, claf.getGroupCard().getHigh());
				} else {
					getWidget(titledPanel, clafer, claf, ClaferModelUtils.removeScopePrefix(claf.getName()), 1, 0, 1024, 0, 1, 1);
				}
			}
		}
		setControl(this.container);
	}

	public Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> getMap() {
		return this.userOptions;
	}

	public boolean getPageStatus() {
		setMap();
		return this.statusPage;
	}

	/**
	 * Set user selected values to the clafer properties
	 */
	public void setMap() {
		ArrayList<Integer> values;
		for (int i = 0; i < this.label.size(); i++) {
			if (this.taskCombo.get(i) == null) {
				values = new ArrayList<Integer>();
				final ArrayList<AstConcreteClafer> keys = new ArrayList<AstConcreteClafer>();
				keys.add(this.mainClafer.get(i));
				keys.add(this.label.get(i));
				final String test = this.mainClafer.get(i).getName() + this.options.get(i).getSelection().toString().replace("[", "")
						.replace("]", "");
				keys.add(this.userGroupOptions.get(test));
				values.add(6);
				values.add(this.label.get(i).getGroupCard().getLow());
				this.userOptions.put(keys, values);
			} else {
				values = new ArrayList<Integer>();
				final ArrayList<AstConcreteClafer> keys = new ArrayList<AstConcreteClafer>();
				keys.add(this.mainClafer.get(i));
				keys.add(this.label.get(i));
				values.add(toNumber(this.options.get(i).getSelection().toString()));
				values.add(this.taskCombo.get(i).getSelection());
				this.userOptions.put(keys, values);
			}
		}
	}

	private void getsubPanel() {
		// TODO method takes 2 widget as an input and adds an X mark
		throw new UnsupportedOperationException("TODO method takes 2 widget as an input and adds an X mark");
	}

	private void getWidget(final Composite container, final AstConcreteClafer claferMain, final AstConcreteClafer claferProperty,
			final int groupCard) {

		final Label label5 = new Label(container, SWT.NONE);
		label5.setText("	");
		final ArrayList<String> optionLables = new ArrayList<String>();
		final Label label1 = new Label(container, SWT.NONE);
		label1.setText(ClaferModelUtils.removeScopePrefix(claferProperty.getName()));
		for (final AstConcreteClafer astClafer : claferProperty.getChildren()) {
			this.userGroupOptions.put(claferMain.getName() + ClaferModelUtils.removeScopePrefix(astClafer.getName()), astClafer);
			optionLables.add(ClaferModelUtils.removeScopePrefix(astClafer.getName()));
		}
		final ComboViewer option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(optionLables);
		option.setSelection(new StructuredSelection(optionLables.get(0)));
		// Button button = new Button(composite, SWT.TOGGLE);
		// button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
		// false));
		// button.setText("X");
		// button.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// composite.setEnabled(!composite.isEnabled());
		// ;
		// container.redraw();
		//
		// }
		// });
		final Label label6 = new Label(container, SWT.NONE);
		label6.setText("	");
		this.mainClafer.add(claferMain);
		this.label.add(claferProperty);
		this.options.add(option);
		this.taskCombo.add(null);

	}

	private void getWidget(final Composite container, final AstConcreteClafer key1, final AstConcreteClafer key2, final String label,
			final int selection, final int min, final int max, final int digits, final int incement, final int pageincrement) {
		final List<String> values = new ArrayList<String>();
		values.add(Labels.LESS_THAN);
		values.add(Labels.GREATER_THAN);
		values.add(Labels.EQUALS);
		values.add(Labels.LESS_THAN_EQUAL);
		values.add(Labels.GREATER_THAN_EQUAL);
		final Label label5 = new Label(container, SWT.NONE);
		label5.setText("	");
		final Label label1 = new Label(container, SWT.NONE);
		label1.setText(label);
		final ComboViewer option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(values);
		option.setSelection(new StructuredSelection(values.get(2)));

		final Spinner taskComb = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		taskComb.setValues(selection, min, max, digits, incement, pageincrement);
		taskComb.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				setComplete(true);
			}
		});

		this.mainClafer.add(key1);
		this.label.add(key2);
		this.options.add(option);
		this.taskCombo.add(taskComb);
	}

	private void setComplete(final boolean b) {

		this.statusPage = b;
	}

	/**
	 * @param selection
	 * @return Map quantifier to integer
	 */
	private Integer toNumber(final String selection) {
		if (selection.contains(Labels.LESS_THAN_EQUAL)) {
			return 4;
		}
		if (selection.contains(Labels.GREATER_THAN_EQUAL)) {
			return 5;
		}
		if (selection.contains(Labels.EQUALS)) {
			return 1;
		}
		if (selection.contains(Labels.LESS_THAN)) {
			return 2;
		}
		if (selection.contains(Labels.GREATER_THAN)) {
			return 3;
		}

		return 999;
	}
}
