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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;


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
		setLayout(new FormLayout());
		
		Group grpClaferFeatureProperties = new Group(this, SWT.BORDER);
		FormData fd_grpClaferFeatureProperties = new FormData();
		fd_grpClaferFeatureProperties.bottom = new FormAttachment(0, 122);
		fd_grpClaferFeatureProperties.right = new FormAttachment(0, 736);
		fd_grpClaferFeatureProperties.top = new FormAttachment(0, 40);
		fd_grpClaferFeatureProperties.left = new FormAttachment(0, 3);
		grpClaferFeatureProperties.setLayoutData(fd_grpClaferFeatureProperties);
		grpClaferFeatureProperties.setText("Clafer feature");
		grpClaferFeatureProperties.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblType = new Label(grpClaferFeatureProperties, SWT.NONE);
		lblType.setText(claferFeature.getFeatureType().toString());
		
		txtFeatureName = new Text(grpClaferFeatureProperties, SWT.BORDER);
		txtFeatureName.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtFeatureName.setEnabled(false);
		txtFeatureName.setEditable(false);
		txtFeatureName.setText(claferFeature.getFeatureName());
		
		Label lblInheritsFrom = new Label(grpClaferFeatureProperties, SWT.NONE);
		lblInheritsFrom.setText("Inherits from");
		GroupFeatureProperty grpClaferFeatureInheritance = new GroupFeatureProperty(
																grpClaferFeatureProperties, 
																SWT.BORDER, 
																claferFeature.getFeatureInheritsFromForAbstract()
																);
		
		Group grpClaferFeatureConstraints = new Group(this, SWT.NONE);
		FormData fd_grpClaferFeatureConstraints = new FormData();
		fd_grpClaferFeatureConstraints.bottom = new FormAttachment(0, 198);
		fd_grpClaferFeatureConstraints.right = new FormAttachment(0, 736);
		fd_grpClaferFeatureConstraints.top = new FormAttachment(0, 128);
		fd_grpClaferFeatureConstraints.left = new FormAttachment(0, 3);
		grpClaferFeatureConstraints.setLayoutData(fd_grpClaferFeatureConstraints);
		grpClaferFeatureConstraints.setText("Clafer feature properties");
		
		Group grpClaferFeature = new Group(this, SWT.NONE);
		FormData fd_grpClaferFeature = new FormData();
		fd_grpClaferFeature.bottom = new FormAttachment(0, 274);
		fd_grpClaferFeature.right = new FormAttachment(0, 736);
		fd_grpClaferFeature.top = new FormAttachment(0, 204);
		fd_grpClaferFeature.left = new FormAttachment(0, 3);
		grpClaferFeature.setLayoutData(fd_grpClaferFeature);
		grpClaferFeature.setText("Clafer feature constraints");
		grpClaferFeature.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button btnModify = new Button(this, SWT.NONE);
		FormData fd_btnModify = new FormData();
		fd_btnModify.right = new FormAttachment(0, 651);
		fd_btnModify.top = new FormAttachment(0, 3);
		fd_btnModify.left = new FormAttachment(0, 572);
		btnModify.setLayoutData(fd_btnModify);
		btnModify.setText("Modify");
		
		Button btnDelete = new Button(this, SWT.NONE);
		FormData fd_btnDelete = new FormData();
		fd_btnDelete.right = new FormAttachment(0, 736);
		fd_btnDelete.top = new FormAttachment(0, 3);
		fd_btnDelete.left = new FormAttachment(0, 657);
		btnDelete.setLayoutData(fd_btnDelete);
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
