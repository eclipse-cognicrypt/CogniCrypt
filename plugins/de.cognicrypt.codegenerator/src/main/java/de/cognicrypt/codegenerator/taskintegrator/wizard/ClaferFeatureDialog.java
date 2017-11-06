package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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

import de.cognicrypt.codegenerator.Constants.FeatureType;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldSmallerUIElements;

public class ClaferFeatureDialog extends Dialog {

	private Text txtFeatureName;
	private CompositeToHoldSmallerUIElements featuresComposite;
	private CompositeToHoldSmallerUIElements constraintsComposite;
	private Button btnRadioAbstract;
	private Button btnRadioConcrete;

	private ClaferFeature resultClafer;
	private ArrayList<ClaferFeature> listOfExistingClaferFeatures;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ClaferFeatureDialog(Shell parentShell) {
		this(parentShell, new ClaferFeature(FeatureType.ABSTRACT, "", ""));
	}

	public ClaferFeatureDialog(Shell parentShell, ClaferFeature modifiableClaferFeature) {
		super(parentShell);
		setShellStyle(SWT.CLOSE);

		resultClafer = modifiableClaferFeature;
	}

	public ClaferFeatureDialog(Shell shell, ArrayList<ClaferFeature> listOfExistingClaferFeatures) {
		this(shell);
		this.listOfExistingClaferFeatures = listOfExistingClaferFeatures;
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
		//btnRadioAbstract.setText("Abstract");
		btnRadioAbstract.setText("Class");
		btnRadioAbstract.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				resultClafer.setFeatureType(FeatureType.ABSTRACT);
				super.widgetSelected(e);
			}
		});

		btnRadioConcrete = new Button(container, SWT.RADIO);
		//btnRadioConcrete.setText("Concrete");
		btnRadioConcrete.setText("Instance");
		btnRadioConcrete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				resultClafer.setFeatureType(FeatureType.CONCRETE);
				super.widgetSelected(e);
			}
		});

		if (resultClafer.getFeatureType() == FeatureType.ABSTRACT) {
			btnRadioAbstract.setSelection(true);
			btnRadioConcrete.setSelection(false);
		} else {
			btnRadioAbstract.setSelection(false);
			btnRadioConcrete.setSelection(true);
		}

		Label lblFeatureName = new Label(container, SWT.NONE);
		lblFeatureName.setText("Type in the name");

		txtFeatureName = new Text(container, SWT.BORDER);
		txtFeatureName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtFeatureName.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				resultClafer.setFeatureName(txtFeatureName.getText());
				super.focusLost(e);
			}
		});

		txtFeatureName.setText(resultClafer.getFeatureName());

		Label lblInheritance = new Label(container, SWT.NONE);
		lblInheritance.setText("Choose inheritance");

		Combo comboInheritance = new Combo(container, SWT.NONE);
		comboInheritance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		comboInheritance.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				resultClafer.setFeatureInheritance(comboInheritance.getText());
				super.focusLost(e);
			}
		});

		comboInheritance.setText(resultClafer.getFeatureInheritance());

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

		featuresComposite = new CompositeToHoldSmallerUIElements(container, SWT.NONE, resultClafer.getfeatureProperties(), true);
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

		constraintsComposite = new CompositeToHoldSmallerUIElements(container, SWT.NONE, resultClafer.getFeatureConstraints(), true);
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
		resultClafer.setFeatureProperties(featuresComposite.getFeatureProperties());
	}

	private void addClaferConstraint() {
		ClaferConstraintDialog cfrConstraintDialog = new ClaferConstraintDialog(getShell(), resultClafer, listOfExistingClaferFeatures);

		// blocking call to Dialog.open() the dialog
		// it returns 0 on success
		if (cfrConstraintDialog.open() == 0) {
			constraintsComposite.addFeatureConstraint(cfrConstraintDialog.getResult(), true);
		}
	}

	public ClaferFeature getResult() {
		resultClafer.setFeatureProperties(featuresComposite.getFeatureProperties());
		resultClafer.setFeatureConstraints(constraintsComposite.getFeatureConstraints());

		return resultClafer;
	}
}
