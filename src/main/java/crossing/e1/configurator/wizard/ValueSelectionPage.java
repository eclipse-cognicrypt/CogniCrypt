package crossing.e1.configurator.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;

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

import crossing.e1.configurator.Lables;
import crossing.e1.configurator.ReadConfig;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.ParseClafer;
import crossing.e1.featuremodel.clafer.StringLableMapper;

/**
 * @author Ram
 *
 */

public class ValueSelectionPage extends WizardPage implements Lables {
	private ClaferModel model;
	private List<Spinner> taskCombo;
	private Composite container;
	private List<AstConcreteClafer> label;
	private List<AstConcreteClafer> mainClafer;
	private List<ComboViewer> options;
	private HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> userOptions;
	private HashMap<String, AstConcreteClafer> userGroupOptions;
	private ParseClafer parser = new ParseClafer();
	List<Composite> widgets = new ArrayList<Composite>();
	boolean statusPage = false;

	public ValueSelectionPage(List<AstConcreteClafer> items,
			ClaferModel claferModel) {
		super(Lables.SELECT_PROPERTIES);
		setTitle(Lables.PROPERTIES);
		setDescription(Lables.DESCRIPTION_VALUE_SELECTION_PAGE);
		userOptions = new HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>>();
		userGroupOptions = new HashMap<String, AstConcreteClafer>();
		model = claferModel;

	}

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

		for (AstConcreteClafer clafer : StringLableMapper.getPropertiesLables()
				.keySet()) {

			// Label label3 = new Label(container, SWT.NONE);
			// Font boldFont = new Font( label3.getDisplay(), new FontData(
			// "Arial", 10, SWT.BOLD ));
			// label3.setFont(boldFont);
			// label3.setText(parser.trim(parser.trim(clafer.getName())));
			// Label label4 = new Label(container, SWT.NONE);
			// label4.setText("");
			// Label label5 = new Label(container, SWT.NONE);
			// label5.setText("");
			//

			Group ownerInfo = new Group(container, SWT.NONE);
			ownerInfo.setText(parser.trim(clafer.getName()));
			Font boldFont = new Font(ownerInfo.getDisplay(), new FontData(
					"Arial", 9, SWT.BOLD));
			ownerInfo.setFont(boldFont);
			GridLayout layout2 = new GridLayout();

			layout2.numColumns = 4;
			ownerInfo.setLayout(layout2);
			for (AstConcreteClafer claf : StringLableMapper
					.getPropertiesLables().get(clafer)) {

				 if (claf.getGroupCard().getLow() >= 1) {
//				 getWidget(container, clafer, claf, claf.getGroupCard()
//				 .getHigh());
				 } else

				getWidget(ownerInfo, clafer, claf, parser.trim(claf.getName()),
						1, 0, 1024, 0, 1, 1);
			}
		}
		setControl(container);
	}

	void getWidget(Composite container, AstConcreteClafer key1,
			AstConcreteClafer key2, String label, int selection, int min,
			int max, int digits, int incement, int pageincrement) {
		List<String> values = new ArrayList<String>();
		values.add(Lables.LESS_THAN);
		values.add(Lables.GREATER_THAN);
		values.add(Lables.EQUALS);
		values.add(Lables.LESS_THAN_EQUAL);
		values.add(Lables.GREATER_THAN_EQUAL);
		Label label5 = new Label(container, SWT.NONE);
		label5.setText("	");
		Label label1 = new Label(container, SWT.NONE);
		label1.setText(label);
		ComboViewer option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(values);
		option.setSelection(new StructuredSelection(values.get(2)));

		Spinner taskComb = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		taskComb.setValues(selection, min, max, digits, incement, pageincrement);
		taskComb.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				setComplete(true);

			}

		});
		this.mainClafer.add(key1);
		this.label.add(key2);
		this.options.add(option);
		this.taskCombo.add(taskComb);
	}

	void getWidget(Composite container, AstConcreteClafer claferMain,
			AstConcreteClafer claferProperty, int groupCard) {
		ArrayList<String> optionLables = new ArrayList<String>();
		Label label1 = new Label(container, SWT.NONE);
		label1.setText(parser.trim(claferProperty.getName()));
		for (AstConcreteClafer astClafer : claferProperty.getChildren()) {
			userGroupOptions.put(
					claferMain.getName() + parser.trim(astClafer.getName()),
					astClafer);
			optionLables.add(parser.trim(astClafer.getName()));
		}
		ComboViewer option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(optionLables);
		option.setSelection(new StructuredSelection(optionLables.get(0)));
		Label label2 = new Label(container, SWT.NONE);
		label2.setText("");

		this.mainClafer.add(claferMain);
		this.label.add(claferProperty);
		this.options.add(option);
		this.taskCombo.add(null);

	}

	private void setComplete(boolean b) {

		statusPage = b;
	}

	boolean getPageStatus() {
		return statusPage;
	}

	public Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> getMap() {
		return userOptions;
	}

	/**
	 * @return Validation method which will be invoked upon clicking next on the
	 *         valueList page Next widgetPage is only accessible if there are
	 *         more than 0 instances for a given clafer and the chosen values
	 */
	public boolean validate(InstanceGenerator gen, ClaferModel claferModel) {
		setMap();
		gen.generateInstances(
				new ClaferModel(new ReadConfig().getClaferPath()),
				this.getMap());
		if (gen.getNoOfInstances() > 0) {
			return true;
		} else {
			setErrorMessage(Lables.INSTANCE_ERROR_MESSGAE);
			return false;
		}
	}

	/**
	 * Set user selected values to the clafer properties
	 */
	private void setMap() {
		ArrayList<Integer> values;
		for (int i = 0; i < label.size(); i++) {
			if (taskCombo.get(i) == null) {
				values = new ArrayList<Integer>();
				ArrayList<AstConcreteClafer> keys = new ArrayList<AstConcreteClafer>();
				keys.add(mainClafer.get(i));
				keys.add(label.get(i));
				String test = mainClafer.get(i).getName()
						+ options.get(i).getSelection().toString()
								.replace("[", "").replace("]", "");
				keys.add(userGroupOptions.get(test));
				values.add(6);
				values.add(label.get(i).getGroupCard().getLow());
				userOptions.put(keys, values);
			} else {
				values = new ArrayList<Integer>();
				ArrayList<AstConcreteClafer> keys = new ArrayList<AstConcreteClafer>();
				keys.add(mainClafer.get(i));
				keys.add(label.get(i));
				values.add(toNumber(options.get(i).getSelection().toString()));
				values.add(taskCombo.get(i).getSelection());
				userOptions.put(keys, values);
			}
		}
	}

	/**
	 * @param selection
	 * @return Map quantifier to integer
	 */
	private Integer toNumber(String selection) {
		if (selection.contains(Lables.EQUALS))
			return 1;
		if (selection.contains(Lables.LESS_THAN))
			return 2;
		if (selection.contains(Lables.GREATER_THAN))
			return 3;
		if (selection.contains(Lables.LESS_THAN_EQUAL))
			return 4;
		if (selection.contains(Lables.GREATER_THAN_EQUAL))
			return 5;
		return 999;
	}
}
