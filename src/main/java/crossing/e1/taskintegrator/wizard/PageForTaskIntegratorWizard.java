/**
 * 
 */
package crossing.e1.taskintegrator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;

import crossing.e1.configurator.Constants;
import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.FeatureProperty;
import crossing.e1.taskintegrator.widgets.CompisiteGranularUIForClaferFeature;
import crossing.e1.taskintegrator.widgets.CompositeChoiceForModeOfWizard;
import crossing.e1.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import org.eclipse.swt.widgets.Button;


/**
 * @author rajiv
 *
 */
public class PageForTaskIntegratorWizard extends WizardPage {
	private CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard = null;
	private CompositeToHoldGranularUIElements compositeToHoldGranularUIElements = null;
	
	/**
	 * Create the wizard.
	 */
	public PageForTaskIntegratorWizard(String name, String title, String description) {
		super(name);
		setTitle(title);
		setDescription(description);
		// TODO improve the next button selection functionality.
		//this.setPageComplete(false);
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER);
				
		setControl(container);
						
		switch(this.getName()){
			case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD:
				container.setLayout(new FillLayout(SWT.HORIZONTAL));
				this.setCompositeChoiceForModeOfWizard(new CompositeChoiceForModeOfWizard(container, SWT.NONE));
				//this.compositeChoiceForModeOfWizard.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
				break;
			case Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION:
				this.setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));				
				this.compositeToHoldGranularUIElements.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
				Button btnAddFeature = new Button(container, SWT.NONE);
				btnAddFeature.setBounds(Constants.RECTANGLE_FOR_BUTTONS_FOR_NON_MODE_SELECTION_PAGES);
				btnAddFeature.setText("Add Feature");
				btnAddFeature.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						//TODO : https://stackoverflow.com/questions/20204367/how-to-dynamically-add-swt-widgets-to-a-composite
						ClaferFeature claferFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, 
							"Security", 
							new FeatureProperty("Enum", "integer"), 
							null);
						//new CompisiteGranularUIForClaferFeature(
						//	((PageForTaskIntegratorWizard) btnAddFeature.getParent().).getCompositeToHoldGranularUIElements(), 
						//	SWT.NONE, 
						//	claferFeature);
						
						CompisiteGranularUIForClaferFeature granularClaferFeature = new CompisiteGranularUIForClaferFeature(
							((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getCompositeToHoldGranularUIElements(), 
							SWT.NONE, 
							claferFeature);
						granularClaferFeature.setBounds(10, 10, 744, 272);
						//((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getCompositeToHoldGranularUIElements().redraw();
					}
				});
				break;
			case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION:
				this.setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));
				//this.compositeToHoldGranularUIElements.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);				
				Button btnAddXSLTag = new Button(container, SWT.NONE);
				btnAddXSLTag.setBounds(Constants.RECTANGLE_FOR_BUTTONS_FOR_NON_MODE_SELECTION_PAGES);
				btnAddXSLTag.setText("Add XSL tag");
				btnAddXSLTag.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						
					}
				});
				break;
			case Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS:
				this.setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));
				//this.compositeToHoldGranularUIElements.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
				Button btnAddQuestion = new Button(container, SWT.NONE);
				btnAddQuestion.setBounds(Constants.RECTANGLE_FOR_BUTTONS_FOR_NON_MODE_SELECTION_PAGES);
				btnAddQuestion.setText("Add Question");
				btnAddQuestion.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						
					}
				});
				break;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {

		// each case needs to be handled separately. By default all cases will return false. 
		/*switch(this.getName()){
			case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD:
				if(((boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN) == true ||
				(boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED) == true) &&
					!this.isPageComplete()){
					
					return true;
					
					}
			case Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION:
				return false;
			case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION:
				return false;
			case Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS:
				return false;
			default:
				return false;				
		}*/
		return true;
		
	}

	/**
	 * @return the compositeChoiceForModeOfWizard
	 */
	public Composite getCompositeChoiceForModeOfWizard() {
		return compositeChoiceForModeOfWizard;
	}

	
	/**
	 * @param compositeChoiceForModeOfWizard the compositeChoiceForModeOfWizard to set
	 */
	private void setCompositeChoiceForModeOfWizard(CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard) {
		this.compositeChoiceForModeOfWizard = compositeChoiceForModeOfWizard;
	}

	/**
	 * @return the compositeToHoldGranularUIElements
	 */
	public Composite getCompositeToHoldGranularUIElements() {
		return compositeToHoldGranularUIElements;
	}

	/**
	 * @param compositeToHoldGranularUIElements the compositeToHoldGranularUIElements to set
	 */
	public void setCompositeToHoldGranularUIElements(CompositeToHoldGranularUIElements compositeToHoldGranularUIElements) {
		this.compositeToHoldGranularUIElements = compositeToHoldGranularUIElements;
	}
}
