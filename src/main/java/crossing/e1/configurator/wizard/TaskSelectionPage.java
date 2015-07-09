package crossing.e1.configurator.wizard;

import java.util.List;
import java.util.stream.Collectors;

import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.common.Check;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.*;

import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class TaskSelectionPage extends WizardPage {
	private ClaferModel model;
	private Spinner taskCombo;
	private Composite container;
	private List<AstConcreteClafer> tasks;
	private List<Integer> performanceLevel;
	private Button securityLevelSecured;
	private Button securityLevelInSecured;
	private Spinner outPutSize;
	private Label label1;
	private Label label2;
	private Label label3;

	public TaskSelectionPage(List<AstConcreteClafer> items,
			ClaferModel claferModel) {
		super("Select Task");
		setTitle("Chonfigure");
		setDescription("Here the user selects her options and security levels");
		this.tasks = items;
		this.model = claferModel;

	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		securityLevelSecured = new Button(container, SWT.RADIO);
		securityLevelSecured.setToolTipText("Secured Encryption");
		securityLevelSecured.setText("Secure");
		securityLevelSecured.setEnabled(true);
		securityLevelInSecured = new Button(container, SWT.RADIO);
		securityLevelInSecured.setToolTipText("Insecure");
		securityLevelInSecured.setText("Do Not Secure");
		securityLevelInSecured.setEnabled(true);

		securityLevelSecured.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				securityLevelInSecured.setSelection(false);
				securityLevelSecured.setSelection(true);
				outPutSize.setVisible(true);
				taskCombo.setVisible(true);
				label1.setVisible(true);
				label2.setVisible(true);
				label3.setVisible(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

		});

		securityLevelInSecured.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				securityLevelSecured.setSelection(false);
				securityLevelInSecured.setSelection(true);
				outPutSize.setVisible(false);
				taskCombo.setVisible(false);
				label1.setVisible(false);
				label2.setVisible(false);
				label3.setVisible(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

		});
		label1 = new Label(container, SWT.NONE);
		label1.setText("Select Performance");
		taskCombo = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		taskCombo.setValues(4, 0, 4, 0, 1, 1);
		
		label2 = new Label(container, SWT.NONE);
		label2.setText("Select Key length");

		outPutSize = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		outPutSize.setValues(100, 100,2048, 0, 24, 10);
		outPutSize.setToolTipText("key leangth");

		label3 = new Label(container, SWT.NONE);
		label3.setVisible(true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		setControl(container);
		canFlipToNextPage();

	}
private void onNext() {
	System.out.println("clicked Next");
}
	
	public Integer getKeyLengthSelction() {
		return taskCombo.getSelection();
	}

	public Integer getOutPutSelection() {
		return outPutSize.getSelection();
	}

	public boolean isSecure() {
		if(getOutPutSelection().intValue() > 100)
		return true;
		
		return false;
	}

}
