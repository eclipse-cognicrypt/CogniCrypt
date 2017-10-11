package crossing.e1.taskintegrator.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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

import crossing.e1.configurator.Constants.FeatureType;
import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.FeatureConstraint;
import crossing.e1.taskintegrator.models.FeatureProperty;
import crossing.e1.taskintegrator.widgets.CompositeToHoldSmallerUIElements;

public class ClaferFeatureDialog extends Dialog {

	private Text txtFeatureName;
	private CompositeToHoldSmallerUIElements featuresComposite;
	private CompositeToHoldSmallerUIElements constraintsComposite;
	private int globalFeaturesCounter = 0; // TODO Debugging purposes (name dummy values meaningfully)
	private int globalConstraintsCounter = 0; // TODO Debugging purposes (name dummy values meaningfully)
	private Button btnRadioAbstract;
	private Button btnRadioConcrete;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ClaferFeatureDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));

		Label lblType = new Label(container, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Select the type");

		btnRadioAbstract = new Button(container, SWT.RADIO);
		btnRadioAbstract.setSelection(true);
		btnRadioAbstract.setText("Abstract");

		btnRadioConcrete = new Button(container, SWT.RADIO);
		btnRadioConcrete.setText("Concrete");

		Label lblFeatureName = new Label(container, SWT.NONE);
		lblFeatureName.setText("Type in the name");

		txtFeatureName = new Text(container, SWT.BORDER);
		txtFeatureName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblInheritance = new Label(container, SWT.NONE);
		lblInheritance.setText("Choose inheritance");

		Combo comboInheritance = new Combo(container, SWT.NONE);
		comboInheritance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Button btnAddProperty = new Button(container, SWT.NONE);
		btnAddProperty.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addClaferProperty();
			}
		});
		btnAddProperty.setText("Add property");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		featuresComposite = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null, true);
		featuresComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		featuresComposite.setMinHeight(200);

		Button btnAddConstraint = new Button(container, SWT.NONE);
		btnAddConstraint.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addClaferConstraint();
			}
		});
		btnAddConstraint.setText("Add constraint");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		constraintsComposite = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null, true);
		constraintsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		constraintsComposite.setMinHeight(200);

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

	private void addClaferProperty() {
		FeatureProperty featureProperty = new FeatureProperty("", "");
		featuresComposite.addFeatureProperty(featureProperty, true);
	}

	private void addClaferConstraint() {
		ClaferConstraintDialog cfrConstraintDialog = new ClaferConstraintDialog(getShell());

		// blocking call to Dialog.open() the dialog
		// it returns 0 on success
		if (cfrConstraintDialog.open() == 0) {
			constraintsComposite.addFeatureConstraint(cfrConstraintDialog.getResult(), true);
		}
	}

	public ClaferFeature getResult() {
		// prepare fields of ClaferFeature
		FeatureType featureType;
		String featureName;
		FeatureProperty featureInheritsFromForAbstract;
		FeatureConstraint featureInheritsFromForConcrete;

		// fill ClaferFeature from user input
		if (btnRadioAbstract.getSelection()) {
			featureType = FeatureType.ABSTRACT;
		} else {
			featureType = FeatureType.CONCRETE;
		}
		
		featureName = txtFeatureName.getText();
		featureInheritsFromForAbstract = null;
		featureInheritsFromForConcrete = null;

		// TODO instantiate appropriately, should another constructor be created?
		ClaferFeature resultClafer = new ClaferFeature(featureType, featureName, featureInheritsFromForAbstract, featureInheritsFromForConcrete);

		resultClafer.getfeatureProperties().addAll(featuresComposite.getFeatureProperties());
		resultClafer.getFeatureConstraints().addAll(constraintsComposite.getFeatureConstraints());

		return resultClafer;
	}
}
