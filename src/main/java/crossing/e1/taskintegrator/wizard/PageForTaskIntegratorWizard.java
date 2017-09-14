/**
 * 
 */
package crossing.e1.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.FillLayout;

import crossing.e1.configurator.Constants;
import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.FeatureProperty;
import crossing.e1.taskintegrator.widgets.CompositeChoiceForModeOfWizard;
import crossing.e1.taskintegrator.widgets.CompositeGranularUIForClaferFeature;
import crossing.e1.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import org.eclipse.swt.widgets.Button;


/**
 * @author rajiv
 *
 */
public class PageForTaskIntegratorWizard extends WizardPage {
	private CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard = null;
	private CompositeToHoldGranularUIElements compositeToHoldGranularUIElements = null;
	
	
	
	
	// TODO for testing only.
	int counter = 0;
	
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
				btnAddFeature.setBounds(Constants.RECTANGLE_FOR_FIRST_BUTTON_FOR_NON_MODE_SELECTION_PAGES);
				btnAddFeature.setText("Add Feature");
				
				//TODO for testing only
				Button btnDeleteFeature = new Button(container, SWT.NONE);
				btnDeleteFeature.setBounds(Constants.RECTANGLE_FOR_SECOND_BUTTON_FOR_NON_MODE_SELECTION_PAGES);
				btnDeleteFeature.setText("Delete Feature");
				btnDeleteFeature.addSelectionListener(new SelectionAdapter() {	
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						//deleteClaferFeature(listOfAllClaferFeatures.get(listOfAllClaferFeatures.size() - 1));
					}
				});
				
				
				btnAddFeature.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						CompositeToHoldGranularUIElements forTestingOnly = (CompositeToHoldGranularUIElements) ((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getCompositeToHoldGranularUIElements(); 
						
						// TODO for testing only
						counter++;
						ClaferFeature tempFeature = new ClaferFeature(
							Constants.FeatureType.ABSTRACT,
							Integer.toString(counter), // TODO for testing only. Counter as the name.
							new FeatureProperty("Enum", "integer"),
							null);
						
						//listOfAllClaferFeatures.add(tempFeature);
						
						compositeToHoldGranularUIElements.addGranularClaferUIElements(tempFeature);
						
						
						
						// TODO for testing only.
						//for(ClaferFeature feature:claferFeature){
							
							
							Control test = forTestingOnly.getContent();
							System.out.println(test.getSize());
							//Control[] cont = ((Composite) test).getChildren();
						//}
						//forTestingOnly.setMinSize(forTestingOnly.computeSize(SWT.DEFAULT, SWT.DEFAULT));
						//forTestingOnly.update();
						
						//System.out.println(forTestingOnly.getMinHeight());
						forTestingOnly.setMinHeight(forTestingOnly.getLowestWidgetYAxisValue());
						//forTestingOnly.setMinSize(forTestingOnly.getContent().computeSize(SWT.DEFAULT, forTestingOnly.getLowestWidgetYAxisValue()));
						//System.out.println(forTestingOnly.getMinHeight());
						System.out.println(test.getSize());
						forTestingOnly.layout();
						
						
						//forTestingOnly.getVerticalBar().setMaximum(forTestingOnly.getMinHeight());
						//forTestingOnly.setMinSize(forTestingOnly.computeSize(SWT.DEFAULT, SWT.DEFAULT));
						//System.out.println(forTestingOnly.getMinHeight());
						//	forTestingOnly.setSize(744, forTestingOnly.getLowestWidgetYAxisValue());
						//forTestingOnly.getVerticalBar();
						
						
					}
					
				});
				break;
			case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION:
				this.setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));
				//this.compositeToHoldGranularUIElements.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);				
				Button btnAddXSLTag = new Button(container, SWT.NONE);
				btnAddXSLTag.setBounds(Constants.RECTANGLE_FOR_FIRST_BUTTON_FOR_NON_MODE_SELECTION_PAGES);
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
				btnAddQuestion.setBounds(Constants.RECTANGLE_FOR_FIRST_BUTTON_FOR_NON_MODE_SELECTION_PAGES);
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
