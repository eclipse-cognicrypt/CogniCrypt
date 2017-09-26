package crossing.e1.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.FeatureProperty;


public class GroupFeatureProperty extends Group {
	private FeatureProperty featureProperty;
	private Text txtPropertyName;
	private Text txtPropertyType;
	private ArrayList<ClaferFeature> propertylist;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @param showRemoveButton TODO
	 */
	public GroupFeatureProperty(Composite parent, int style, FeatureProperty featurePropertyParam, boolean showRemoveButton) {
		
		super(parent, SWT.BORDER);
		// Set the model for use first.
		this.setFeatureProperty(featurePropertyParam);
		
		
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		txtPropertyName = new Text(this, SWT.BORDER);
		txtPropertyName.setEditable(false);
		txtPropertyName.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtPropertyName.setText(featureProperty.getPropertyName());
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText("Type of");
		
		txtPropertyType = new Text(this, SWT.BORDER);
		txtPropertyType.setEditable(false);
		txtPropertyType.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtPropertyType.setText(featureProperty.getPropertyType());
		
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
