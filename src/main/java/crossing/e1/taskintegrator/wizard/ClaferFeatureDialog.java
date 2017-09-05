package crossing.e1.taskintegrator.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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


public class ClaferFeatureDialog extends Dialog {

	private Text text;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ClaferFeatureDialog(Shell parentShell) {
		super(parentShell);
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
		btnNewButton.setText("Add property");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Button btnAddConstraint = new Button(container, SWT.NONE);
		btnAddConstraint.setText("Add constraint");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

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
}
