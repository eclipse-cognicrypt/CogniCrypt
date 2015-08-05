package crossing.e1.configurator.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.ParseClafer;
import crossing.e1.featuremodel.clafer.StringLableMapper;

/**
 * @author Ram
 *
 */

public class ValueSelectionPage extends WizardPage {
	private ClaferModel model;
	private List<Spinner> taskCombo;
	private Composite container;
	private List<AstConcreteClafer> label;
	private List<AstConcreteClafer> mainClafer;
	private List<ComboViewer> options;
	private HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> userOptions;
	private ParseClafer parser = new ParseClafer();
	List<Composite> widgets = new ArrayList<Composite>();
	boolean statusPage=false;

	public ValueSelectionPage(List<AstConcreteClafer> items,
			ClaferModel claferModel) {
		super("Select Properties");
		setTitle("Configure");
		setDescription("Here the user configures values for properties");
		userOptions = new HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>>();
		this.model = claferModel;

	}

	@Override
	public void createControl(Composite parent) {
		taskCombo = new ArrayList<Spinner>();
		label = new ArrayList<AstConcreteClafer>();
		options = new ArrayList<ComboViewer>();
		mainClafer=  new ArrayList<AstConcreteClafer>();

		container = new Composite(parent, SWT.NONE); 
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		setControl(container);
		for (AstConcreteClafer clafer : StringLableMapper.getPropertiesLables().keySet()) {
			for(AstConcreteClafer claf: StringLableMapper.getPropertiesLables().get(clafer))
			getWidget(container, clafer,claf, parser.trim(claf.getName()), 1, 0, 1024, 0, 1,
					1);
		}
	}

	void getWidget(Composite container, AstConcreteClafer key1,AstConcreteClafer key2, String label,
			int selection, int min, int max, int digits, int incement,
			int pageincrement) {
		List<String> values = new ArrayList<String>();
		values.add("<=");
		values.add(">=");
		values.add("==");
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

	private void setComplete(boolean b) {
		
		statusPage=b;
	}
	
	boolean getPageStatus(){
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
		gen.generateInstances(claferModel, this.getMap());
		if (gen.getNoOfInstances() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set user selected values to the clafer properties
	 */
	private void setMap() {
		ArrayList<Integer> values;
		for (int i = 0; i < taskCombo.size(); i++) {
			values = new ArrayList<Integer>();
			ArrayList<AstConcreteClafer> keys= new ArrayList<AstConcreteClafer>();
			keys.add(mainClafer.get(i));
			keys.add(label.get(i));
			values.add(toNumber(options.get(i).getSelection().toString()));
			values.add(taskCombo.get(i).getSelection());
			userOptions.put(keys,
					values);
		}
	}

	/**
	 * @param selection
	 * @return Map quantifier to integer
	 */
	private Integer toNumber(String selection) {
		if (selection.contains("=="))
			return 1;
		if (selection.contains("<="))
			return 2;
		if (selection.contains(">="))
			return 3;
		return 999;
	}
}
