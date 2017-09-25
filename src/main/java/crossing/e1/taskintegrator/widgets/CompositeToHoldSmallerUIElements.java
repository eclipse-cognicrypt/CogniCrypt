package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Constants;
import crossing.e1.taskintegrator.models.FeatureProperty;

import java.util.ArrayList;




public class CompositeToHoldSmallerUIElements extends ScrolledComposite {
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private ArrayList<FeatureProperty> featureProperties;
	private ArrayList<String> featureConstraints;
	private Text txtForFeatureConstraints;
	


	/**
	 * Create the composite.
	 * Warnings suppressed for casting array lists.
	 * @param parent
	 * @param style
	 * @param targetArrayListOfDataToBeDisplayed
	 */
	@SuppressWarnings("unchecked")
	public CompositeToHoldSmallerUIElements(Composite parent, int style, ArrayList<?> targetArrayListOfDataToBeDisplayed) {
		super(parent, SWT.V_SCROLL);
		setExpandVertical(true);
		setExpandHorizontal(true);
		
		Composite composite = new Composite(this, SWT.NONE);
		setContent(composite);
		setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.setLayout(null);		
		
		if(targetArrayListOfDataToBeDisplayed.get(0) instanceof FeatureProperty){			
		
			featureProperties = (ArrayList<FeatureProperty>) targetArrayListOfDataToBeDisplayed;
			
			for(FeatureProperty featureUnderConsideration : featureProperties){
				GroupFeatureProperty groupForFeatureProperty = new GroupFeatureProperty(composite, SWT.NONE, featureUnderConsideration);
				groupForFeatureProperty.setBounds(
					Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, 
					getLowestWidgetYAxisValue(), 
					Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT, 
					Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);
				
				setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);
				}		
		} else if(targetArrayListOfDataToBeDisplayed.get(0) instanceof String){
			featureConstraints = (ArrayList<String>) targetArrayListOfDataToBeDisplayed;
			
			for(String featureConstraintUnderConsideration : featureConstraints){
				
				txtForFeatureConstraints = new Text(composite, SWT.BORDER);
				txtForFeatureConstraints.setBounds(
					Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, 
					getLowestWidgetYAxisValue(), 
					Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT, 
					29);
				txtForFeatureConstraints.setEditable(false);
				txtForFeatureConstraints.setText(featureConstraintUnderConsideration);
				setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 29);
			}
		}
				
		setMinHeight(getLowestWidgetYAxisValue());

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
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
		this.lowestWidgetYAxisValue = lowestWidgetYAxisValue + Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	}
	/**
	 * @return the featureProperties
	 */
	public ArrayList<FeatureProperty> getFeatureProperties() {
		return featureProperties;
	}

	/**
	 * @return the featureConstraints
	 */
	public ArrayList<String> getFeatureConstraints() {
		return featureConstraints;
	}
}
