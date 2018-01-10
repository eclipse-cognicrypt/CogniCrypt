package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.wizard.ClaferFeatureDialog;


public class CompositeGranularUIForClaferFeature extends Composite {
	private ClaferFeature claferFeature;
	private int yAxisValue = 40; // begin at 40 for the "top" value.

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeGranularUIForClaferFeature(Composite parent, int style, ClaferFeature claferFeatureParam) {
		super(parent, SWT.BORDER);
		// set the clafer feature first.
		setClaferFeature(claferFeatureParam);
		setLayout(new FormLayout());
		
		Group grpClaferFeature = new Group(this, SWT.BORDER);
		FormData fd_grpClaferFeature = new FormData();
		fd_grpClaferFeature.top = new FormAttachment(0, yAxisValue); //40
		yAxisValue = yAxisValue + 70;
		fd_grpClaferFeature.bottom = new FormAttachment(0, yAxisValue);//110
		fd_grpClaferFeature.right = new FormAttachment(0, Constants.RIGHT_VALUE_FOR_GRANULAR_CLAFER_UI_SUB_ELEMENT);		
		fd_grpClaferFeature.left = new FormAttachment(0, 3);
		grpClaferFeature.setLayoutData(fd_grpClaferFeature);
		//grpClaferFeature.setText("Clafer feature");
		grpClaferFeature.setText("Variability Construct");
		grpClaferFeature.setLayout(new RowLayout(SWT.HORIZONTAL));
		yAxisValue = yAxisValue + 6;
		
		Label lblType = new Label(grpClaferFeature, SWT.NONE);
		
		if(claferFeature.getFeatureType().toString().equals(Constants.FeatureType.ABSTRACT.toString().toLowerCase())){
			lblType.setText("Class");
		} else if(claferFeature.getFeatureType().toString().toLowerCase().equals(Constants.FeatureType.CONCRETE.toString().toLowerCase())){
			lblType.setText("Instance");
		}
		
		
		Text txtFeatureName;
		txtFeatureName = new Text(grpClaferFeature, SWT.BORDER);
		txtFeatureName.setEditable(false);
		txtFeatureName.setLayoutData(new RowData(160, SWT.DEFAULT));
		txtFeatureName.setText(claferFeature.getFeatureName());
		
		
		if (!claferFeature.getFeatureInheritance().isEmpty()) {
			Label lblInheritsFrom = new Label(grpClaferFeature, SWT.NONE);

			if (claferFeature.getFeatureType() == Constants.FeatureType.ABSTRACT) {
				lblInheritsFrom.setText("inherits from class");
			} else if (claferFeature.getFeatureType() == Constants.FeatureType.CONCRETE) {
				lblInheritsFrom.setText("implements");
			}

			Text txtFeatureInheritance;
			txtFeatureInheritance = new Text(grpClaferFeature, SWT.BORDER);
			txtFeatureInheritance.setEditable(false);
			txtFeatureInheritance.setLayoutData(new RowData(160, SWT.DEFAULT));
			txtFeatureInheritance.setText(claferFeature.getFeatureInheritance());
		}
		
		if(claferFeature.getfeatureProperties().size()!=0){
			Group grpClaferFeatureProperties = new Group(this, SWT.NONE);
			FormData fd_grpClaferFeatureProperties = new FormData();
			fd_grpClaferFeatureProperties.top = new FormAttachment(0, yAxisValue);//116
			yAxisValue = yAxisValue + 130;
			fd_grpClaferFeatureProperties.bottom = new FormAttachment(0, yAxisValue);//246
			fd_grpClaferFeatureProperties.right = new FormAttachment(0, Constants.RIGHT_VALUE_FOR_GRANULAR_CLAFER_UI_SUB_ELEMENT);			
			fd_grpClaferFeatureProperties.left = new FormAttachment(0, 3);
			grpClaferFeatureProperties.setLayoutData(fd_grpClaferFeatureProperties);
			grpClaferFeatureProperties.setLayout(new FillLayout(SWT.HORIZONTAL));
			grpClaferFeatureProperties.setText("Clafer feature properties");
			CompositeToHoldSmallerUIElements compositeToHoldClaferFeatureProperties = new CompositeToHoldSmallerUIElements(grpClaferFeatureProperties, SWT.NONE, claferFeature
				.getfeatureProperties(), false, null);
			yAxisValue = yAxisValue + 6;
		}
		
		
		if(claferFeature.getFeatureConstraints().size()!=0){
			Group grpClaferFeatureConstraints = new Group(this, SWT.NONE);
			FormData fd_grpClaferFeatureConstraints = new FormData();
			fd_grpClaferFeatureConstraints.top = new FormAttachment(0, yAxisValue);//252
			yAxisValue = yAxisValue + 130;
			fd_grpClaferFeatureConstraints.bottom = new FormAttachment(0, yAxisValue);//364
			fd_grpClaferFeatureConstraints.right = new FormAttachment(0, Constants.RIGHT_VALUE_FOR_GRANULAR_CLAFER_UI_SUB_ELEMENT);			
			fd_grpClaferFeatureConstraints.left = new FormAttachment(0, 3);
			grpClaferFeatureConstraints.setLayoutData(fd_grpClaferFeatureConstraints);
			grpClaferFeatureConstraints.setText("Clafer feature constraints");
			grpClaferFeatureConstraints.setLayout(new FillLayout(SWT.HORIZONTAL));
			CompositeToHoldSmallerUIElements compositeToHoldClaferFeatureConstraints = new CompositeToHoldSmallerUIElements(grpClaferFeatureConstraints, SWT.NONE, claferFeature
				.getFeatureConstraints(), false, null);
			yAxisValue = yAxisValue + 6;
		}
		
		
		Button btnModify = new Button(this, SWT.NONE);
		btnModify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ClaferModel claferModel = ((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent()).getClaferModel();
				ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(getShell(), claferFeature, claferModel);
				if (cfrFeatureDialog.open() == 0) {
					((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent()).modifyClaferFeature(claferFeature, cfrFeatureDialog.getResult());// (1) CompositeGranularUIForClaferFeature, (2) composite inside (3) CompositeToHoldGranularUIElements
				}
				

			}
		});
		FormData fd_btnModify = new FormData();
		fd_btnModify.right = new FormAttachment(0, 651);
		fd_btnModify.top = new FormAttachment(0, 3);
		fd_btnModify.left = new FormAttachment(0, 572);
		btnModify.setLayoutData(fd_btnModify);
		btnModify.setText("Modify");
		
		Button btnDelete = new Button(this, SWT.NONE);
		FormData fd_btnDelete = new FormData();
		fd_btnDelete.right = new FormAttachment(0, Constants.RIGHT_VALUE_FOR_GRANULAR_CLAFER_UI_SUB_ELEMENT);
		fd_btnDelete.top = new FormAttachment(0, 3);
		fd_btnDelete.left = new FormAttachment(0, 657);
		btnDelete.setLayoutData(fd_btnDelete);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING
		            | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
				confirmationMessageBox.setText("Deleting Clafer Feature");
		        int response = confirmationMessageBox.open();
		        if (response == SWT.YES){
		        	((CompositeToHoldGranularUIElements) btnDelete.getParent().getParent().getParent()).deleteClaferFeature(claferFeature);// (1) CompositeGranularUIForClaferFeature, (2) composite inside (3) CompositeToHoldGranularUIElements
		        }
		          
			}
		});
		btnDelete.setText("Delete");
		
		this.setSize(Constants.WIDTH_FOR_GRANULAR_CLAFER_UI_ELEMENT, yAxisValue);
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
