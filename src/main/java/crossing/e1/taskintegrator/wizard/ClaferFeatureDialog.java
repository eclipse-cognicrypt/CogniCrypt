package crossing.e1.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.FeatureProperty;
import crossing.e1.taskintegrator.widgets.CompositeToHoldSmallerUIElements;

public class ClaferFeatureDialog extends Dialog {

	private Text text;
	private ArrayList<FeatureProperty> propertyList;
	private Composite featureComposite;
	private CompositeToHoldSmallerUIElements smallComp;
	private ScrolledComposite scrolledComposite;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ClaferFeatureDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
		propertyList = new ArrayList<>();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));

		Label lblSelectTheType = new Label(container, SWT.NONE);
		lblSelectTheType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblSelectTheType.setText("Select the type");

		Button btnRadioButton = new Button(container, SWT.RADIO);
		btnRadioButton.setSelection(true);
		btnRadioButton.setText("Abstract");

		Button btnConcrete = new Button(container, SWT.RADIO);
		btnConcrete.setText("Concrete");

		Label lblTypeInThe = new Label(container, SWT.NONE);
		lblTypeInThe.setText("Type in the name");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblChooseInheritance = new Label(container, SWT.NONE);
		lblChooseInheritance.setText("Choose inheritance");

		Combo combo = new Combo(container, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addClaferProperty();
			}
		});
		btnNewButton.setText("Add property");

		propertyList.add(new FeatureProperty("name", "type"));

		smallComp = new CompositeToHoldSmallerUIElements(container, SWT.NONE, propertyList, true);
		smallComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		smallComp.setMinHeight(200);

		Button btnAddConstraint = new Button(container, SWT.NONE);
		btnAddConstraint.setText("Add constraint");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}

	private void addClaferProperty() {
		propertyList.add(new FeatureProperty("TestProp", "TestVal"));
		this.smallComp.addFeatureProperty(propertyList.get(propertyList.size() - 1), true);
	}

	public void deleteClaferProperty(ClaferFeature cfrFeature) {

	}
}
