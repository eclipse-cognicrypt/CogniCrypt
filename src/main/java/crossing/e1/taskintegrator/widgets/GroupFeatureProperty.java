package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import crossing.e1.taskintegrator.models.FeatureProperty;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;


public class GroupFeatureProperty extends Group {
	private FeatureProperty featureProperty;
	private Text txtPropertyName;
	private Text txtPropertyType;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupFeatureProperty(Composite parent, int style, FeatureProperty featurePropertyParam) {
		
		super(parent, SWT.BORDER);
		// Set the model for use first.
		this.setFeatureProperty(featurePropertyParam);
		
		
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		txtPropertyName = new Text(this, SWT.BORDER);
		txtPropertyName.setEnabled(false);
		txtPropertyName.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtPropertyName.setText(featureProperty.getPropertyName());
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText("Type of");
		
		txtPropertyType = new Text(this, SWT.BORDER);
		txtPropertyType.setEnabled(false);
		txtPropertyType.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtPropertyType.setText(featureProperty.getPropertyType());
		

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
