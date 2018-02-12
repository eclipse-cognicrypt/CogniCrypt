package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;

public class ClaferFeaturePatternDialog extends Dialog {

	private Composite compositeOptions;
	private ScrolledComposite compositeScrolledOptions;
	
	private String patternName;
	private ArrayList<StringBuilder> options;

	public ClaferFeaturePatternDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);

		patternName = "";
		options = new ArrayList<>();
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
		txtName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				patternName = txtName.getText();
				
			}
		});

		Button btnAddOption = new Button(container, SWT.BORDER);
		btnAddOption.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnAddOption.setText("Add option");
		btnAddOption.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StringBuilder strOption = new StringBuilder();
				options.add(strOption);

				Text txtOption = new Text(compositeOptions, SWT.BORDER);
				txtOption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				txtOption.setText("Text");
				txtOption.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent arg0) {
						strOption.delete(0, strOption.length());
						strOption.append(txtOption.getText());

					}
				});

				Button btnRemove = new Button(compositeOptions, SWT.NONE);
				btnRemove.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
				btnRemove.setText("Remove");
				btnRemove.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						txtOption.dispose();
						btnRemove.dispose();
						options.remove(strOption);

						compositeOptions.layout();
						super.widgetSelected(e);
					}
				});

				compositeOptions.layout();
				compositeScrolledOptions.setMinSize(compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				super.widgetSelected(e);
			}
		});

		compositeScrolledOptions = new ScrolledComposite(container, SWT.BORDER | SWT.V_SCROLL);
		compositeScrolledOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		compositeScrolledOptions.setLayout(new GridLayout(1, false));

		compositeOptions = new Composite(compositeScrolledOptions, SWT.NONE);
		compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compositeOptions.setLayout(new GridLayout(2, false));
		compositeScrolledOptions.setContent(compositeOptions);

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

	public ClaferModel getResultModel() {
		ClaferModel resultModel = new ClaferModel();
		resultModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, patternName, "Enum"));
		for (StringBuilder sb : options) {
			resultModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, sb.toString(), patternName));
		}

		return resultModel;
	}

}
