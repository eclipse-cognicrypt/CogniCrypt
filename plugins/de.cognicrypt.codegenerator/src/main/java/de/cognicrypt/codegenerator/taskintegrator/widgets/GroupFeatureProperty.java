package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;


public class GroupFeatureProperty extends Group {
	private FeatureProperty featureProperty;
	private Text txtPropertyName;
	private Text txtPropertyType;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @param showRemoveButton TODO
	 */
	public GroupFeatureProperty(Composite parent, int style, FeatureProperty featurePropertyParam, boolean showRemoveButton, boolean editable) {
		
		super(parent, SWT.BORDER);
		// Set the model for use first.
		this.setFeatureProperty(featurePropertyParam);
		
		
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblName = new Label(this, SWT.NONE);
		lblName.setText("Name ");
		
		txtPropertyName = new Text(this, SWT.BORDER);
		txtPropertyName.setEditable(editable);
		txtPropertyName.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtPropertyName.setText(featureProperty.getPropertyName());
		txtPropertyName.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				featureProperty.setPropertyName(txtPropertyName.getText());
				super.focusLost(e);
			}
		});
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText("Inherits from ");
		// inherits 
		
		txtPropertyType = new Text(this, SWT.BORDER);
		txtPropertyType.setEditable(editable);
		txtPropertyType.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtPropertyType.setText(featureProperty.getPropertyType());
		txtPropertyType.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				featureProperty.setPropertyType(txtPropertyType.getText());
				super.focusLost(e);
			}
		});
		
		if (showRemoveButton) {
			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setText("Remove");
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					((CompositeToHoldSmallerUIElements) getParent().getParent())
						.removeFeatureProperty(getFeatureProperty());
					((CompositeToHoldSmallerUIElements) getParent().getParent()).updateClaferContainer();
				}
			});
		}
	}

	public GroupFeatureProperty(Composite parent, int style, FeatureProperty featurePropertyParam, boolean showRemoveButton) {
		this(parent, style, featurePropertyParam, showRemoveButton, false);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the featureProperty
	 */
	public FeatureProperty getFeatureProperty() {
		return featureProperty;
	}

	/**
	 * @param featureProperty the featureProperty to set
	 */
	private void setFeatureProperty(FeatureProperty featureProperty) {
		this.featureProperty = featureProperty;
	}

}
