package de.cognicrypt.codegenerator.taskintegrator.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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

public class ClaferFeaturePatternDialog extends Dialog {

	
	public ClaferFeaturePatternDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		
		// restrict resizing the dialog below a minimum
		getShell().setMinimumSize(520, 600);

		Label lblPattern = new Label(container, SWT.NONE);
		lblPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblPattern.setText("Pattern");

		Combo comboPattern = new Combo(container, SWT.NONE);
		comboPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboPattern.add("Enum");

		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblName.setText("Name");

		Text txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button btnAddOption = new Button(container, SWT.NONE);
		btnAddOption.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnAddOption.setText("Add option");

		ScrolledComposite compositeScrolledOptions = new ScrolledComposite(container, SWT.BORDER);
		compositeScrolledOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		compositeScrolledOptions.setLayout(new GridLayout(1, false));

		Composite compositeOptions = new Composite(compositeScrolledOptions, SWT.NONE);
		compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compositeOptions.setLayout(new GridLayout(2, false));
		compositeScrolledOptions.setContent(compositeOptions);

		for (int i = 0; i < 4; i++) {
			Text firstOption = new Text(compositeOptions, SWT.NONE);
			firstOption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			firstOption.setText("Test");
		}

		Button testButton = new Button(compositeOptions, SWT.NONE);
		testButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		testButton.setText("Test");

		compositeScrolledOptions.setExpandHorizontal(true);
		compositeScrolledOptions.setExpandVertical(true);
		compositeScrolledOptions.setMinSize(compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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
		return new Point(800, 700);
	}

}
