package crossing.e1.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import crossing.e1.configurator.Constants;
import crossing.e1.taskintegrator.models.ClaferFeature;


public class CompositeToHoldGranularUIElements extends ScrolledComposite {
	private String targetPageName;
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	private ArrayList<ClaferFeature> listOfAllClaferFeatures;
	
	/**
	 * Create the composite.  
	 * @param parent
	 * @param style
	 */
	public CompositeToHoldGranularUIElements(Composite parent, int style, String pageName) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);
		
		listOfAllClaferFeatures = new ArrayList<ClaferFeature>();
		setTargetPageName(pageName);
		
		setExpandHorizontal(true);		
		setExpandVertical(true);
		setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		// All the granular UI elements will be added to this composite for the ScrolledComposite to work.
		Composite contentComposite = new Composite(this, SWT.NONE);
		setContent(contentComposite);
		setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
		contentComposite.setLayout(null);	

	}
	
	public void addGranularClaferUIElements(ClaferFeature claferFeature){
		// Update the array list.
		//listOfAllClaferFeatures.add(claferFeature);
		
		CompositeGranularUIForClaferFeature granularClaferFeature = new CompositeGranularUIForClaferFeature
			((Composite) this.getContent(), // the content composite of ScrolledComposite.
			SWT.NONE, 
			claferFeature);
		granularClaferFeature.setBounds(
			Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS, 
			getLowestWidgetYAxisValue(), 
			Constants.WIDTH_FOR_GRANULAR_CLAFER_UI_ELEMENT, 
			//Constants.HEIGHT_FOR_GRANULAR_CLAFER_UI_ELEMENT
			granularClaferFeature.getSize().y);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + granularClaferFeature.getSize().y);
		setMinHeight(getLowestWidgetYAxisValue());
	}

	public void deleteClaferFeature(ClaferFeature featureToBeDeleted) {
		listOfAllClaferFeatures.remove(featureToBeDeleted);
		updateClaferContainer();
	}

	private void updateClaferContainer() {
		Composite compositeContentOfThisScrolledComposite = (Composite)this.getContent();
		
		// first dispose all the granular UI elements (which includes the deleted one).
		for(Control uiRepresentationOfClaferFeatures : compositeContentOfThisScrolledComposite.getChildren()){
			uiRepresentationOfClaferFeatures.dispose();
		}
		
		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());
		
		// add all the clafer features excluding the deleted one.
		for(ClaferFeature featureUnderConsideration : listOfAllClaferFeatures){
			addGranularClaferUIElements(featureUnderConsideration);
		}
	}
	
	public void modifyClaferFeature(ClaferFeature originalClaferFeature, ClaferFeature modifiedClaferFeature ){
		for(ClaferFeature featureUnderConsideration:listOfAllClaferFeatures){
			if(featureUnderConsideration.equals(originalClaferFeature)){
				featureUnderConsideration = modifiedClaferFeature;
				break;
			}
		}
		
		updateClaferContainer();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the targetPageName
	 */
	public String getTargetPageName() {
		return targetPageName;
	}

	/**
	 * @param targetPageName the targetPageName to set
	 */
	private void setTargetPageName(String targetPageName) {
		this.targetPageName = targetPageName;
	}

	/**
	 * @return the lowestWidgetYAxisValue
	 */
	public int getLowestWidgetYAxisValue() {
		return lowestWidgetYAxisValue;
	}

	/**
	 * @param lowestWidgetYAxisValue the lowestWidgetYAxisValue to set
	 */
	public void setLowestWidgetYAxisValue(int lowestWidgetYAxisValue) {
		this.lowestWidgetYAxisValue = lowestWidgetYAxisValue + Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	}
	
	/**
	 * @return the listOfAllClaferFeatures
	 */
	public ArrayList<ClaferFeature> getListOfAllClaferFeatures() {
		return listOfAllClaferFeatures;
	}

}
