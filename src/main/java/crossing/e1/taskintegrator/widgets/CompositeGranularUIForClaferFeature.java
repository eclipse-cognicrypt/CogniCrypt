package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;

import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.FeatureProperty;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;


public class CompositeGranularUIForClaferFeature extends Composite {
	private ClaferFeature claferFeature;
	private Text txtFeatureName;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeGranularUIForClaferFeature(Composite parent, int style, ClaferFeature claferFeatureParam) {
		super(parent, SWT.BORDER);
		// set the clafer feature first.
		this.setClaferFeature(claferFeatureParam);
		setLayout(null);
		
		Group grpClaferFeatureProperties = new Group(this, SWT.BORDER);
		grpClaferFeatureProperties.setBounds(3, 40, 733, 82);
		grpClaferFeatureProperties.setText("Clafer feature");
		grpClaferFeatureProperties.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblType = new Label(grpClaferFeatureProperties, SWT.NONE);
		lblType.setText(claferFeature.getFeatureType().toString());
		
		txtFeatureName = new Text(grpClaferFeatureProperties, SWT.BORDER);
		txtFeatureName.setEnabled(false);
		txtFeatureName.setEditable(false);
		txtFeatureName.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtFeatureName.setText(claferFeature.getFeatureName());
		
		Label lblInheritsFrom = new Label(grpClaferFeatureProperties, SWT.NONE);
		lblInheritsFrom.setText("Inherits from");
		GroupFeatureProperty grpClaferFeatureInheritance = new GroupFeatureProperty(
																grpClaferFeatureProperties, 
																SWT.BORDER, 
																claferFeature.getFeatureInheritsFromForAbstract()
																);
		
		Group grpClaferFeatureConstraints = new Group(this, SWT.NONE);
		grpClaferFeatureConstraints.setBounds(3, 128, 733, 100);
		grpClaferFeatureConstraints.setText("Clafer feature properties");
		
		Group grpClaferFeature = new Group(this, SWT.NONE);
		grpClaferFeature.setBounds(3, 234, 733, 74);
		grpClaferFeature.setText("Clafer feature constraints");
		grpClaferFeature.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button btnModify = new Button(this, SWT.NONE);
		btnModify.setBounds(572, 3, 79, 31);
		btnModify.setText("Modify");
		
		Button btnDelete = new Button(this, SWT.NONE);
		btnDelete.setBounds(657, 3, 79, 31);
		btnDelete.setText("Delete");
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the claferFeature
	 */
	public ClaferFeature getClaferFeature() {
		return claferFeature;
	}

	/**
	 * @param claferFeature the claferFeature to set
	 */
	private void setClaferFeature(ClaferFeature claferFeature) {
		this.claferFeature = claferFeature;
	}
}
