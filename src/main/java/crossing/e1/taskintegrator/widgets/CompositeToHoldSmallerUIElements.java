package crossing.e1.taskintegrator.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import crossing.e1.configurator.Constants;
import crossing.e1.taskintegrator.models.FeatureProperty;
import crossing.e1.taskintegrator.models.XSLAttribute;




public class CompositeToHoldSmallerUIElements extends ScrolledComposite {
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private ArrayList<FeatureProperty> featureProperties;
	private ArrayList<String> featureConstraints;
	private Composite composite;
	private ArrayList<XSLAttribute> XSLAttributes; // <attributeName, actualAttributeString>
	


	/**
	 * Create the composite.
	 * Warnings suppressed for casting array lists.
	 * @param parent
	 * @param style
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton TODO
	 */
	@SuppressWarnings("unchecked")
	public CompositeToHoldSmallerUIElements(Composite parent, int style, ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);
		setExpandVertical(true);
		setExpandHorizontal(true);
		
		composite = new Composite(this, SWT.NONE);
		setContent(composite);
		setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.setLayout(null);
		
		featureProperties = new ArrayList<FeatureProperty>();
		featureConstraints = new ArrayList<String>();

		XSLAttributes = new ArrayList<XSLAttribute>();
		
		addData(targetArrayListOfDataToBeDisplayed, showRemoveButton);

	}

	@SuppressWarnings("unchecked")
	private void addData(ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton) {
		if (targetArrayListOfDataToBeDisplayed != null) {

			if (featureProperties == null && featureConstraints == null) {
				if (targetArrayListOfDataToBeDisplayed.get(0) instanceof FeatureProperty) {
					featureProperties = new ArrayList<FeatureProperty>();
				} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof String) {
					featureConstraints = new ArrayList<String>();
				}
			}

			if (targetArrayListOfDataToBeDisplayed.get(0) instanceof FeatureProperty) {

				featureProperties.addAll((ArrayList<FeatureProperty>) targetArrayListOfDataToBeDisplayed);

				for (FeatureProperty featureUnderConsideration : (ArrayList<FeatureProperty>) targetArrayListOfDataToBeDisplayed) {
					featureProperties.add(featureUnderConsideration);
					addFeatureProperty(featureUnderConsideration, showRemoveButton);
				}
			} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof String) {
				featureConstraints.addAll((ArrayList<String>) targetArrayListOfDataToBeDisplayed);

				for (String featureConstraintUnderConsideration : (ArrayList<String>) targetArrayListOfDataToBeDisplayed) {
					featureConstraints.add(featureConstraintUnderConsideration);
					addFeatureConstraint(featureConstraintUnderConsideration, false);
				}
			}

		}
	}

	public void addFeatureConstraint(String featureConstraintUnderConsideration, boolean showRemoveButton) {
		featureConstraints.add(featureConstraintUnderConsideration);
		addFeatureConstraintUI(featureConstraintUnderConsideration, showRemoveButton);
	}

	private void addFeatureConstraintUI(String featureConstraintUnderConsideration, boolean showRemoveButton) {
		GroupConstraint groupConstraint = new GroupConstraint((Composite) getContent(), SWT.NONE, featureConstraintUnderConsideration, showRemoveButton);
		groupConstraint.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT + 200, 29);

		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 29);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	public void addFeatureProperty(FeatureProperty featureProperty, boolean showRemoveButton) {
		featureProperties.add(featureProperty);
		addFeaturePropertyUI(featureProperty, showRemoveButton);
	}

	private void addFeaturePropertyUI(FeatureProperty featureProperty, boolean showRemoveButton) {
		GroupFeatureProperty groupForFeatureProperty = new GroupFeatureProperty((Composite) getContent(), SWT.NONE, featureProperty, showRemoveButton);
		groupForFeatureProperty.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT + 200,
			Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setMinHeight(getLowestWidgetYAxisValue());
	}
	
	public void addXSLAttribute(XSLAttribute XSLAttrubuteParam, boolean showRemoveButton, ArrayList<String> listOfPossibleAttributes ){
		XSLAttributes.add(XSLAttrubuteParam);
		addXSLAttributeUI(XSLAttrubuteParam,showRemoveButton,listOfPossibleAttributes);
	}
	
	private void addXSLAttributeUI(XSLAttribute XSLAttrubuteParam, boolean showRemoveButton, ArrayList<String> listOfPossibleAttributes){
		GroupXSLTagAttribute groupforXSLTagAttribute = new GroupXSLTagAttribute((Composite) getContent(), SWT.NONE, showRemoveButton, listOfPossibleAttributes, XSLAttrubuteParam);
		groupforXSLTagAttribute.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT,
			Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);
		
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	public void removeFeatureProperty(FeatureProperty featureProperty) {
		featureProperties.remove(featureProperty);
	}

	public void removeFeatureConstraint(String featureConstraint) {
		featureConstraints.remove(featureConstraint);
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

	public void updateClaferContainer() {
		Composite compositeContentOfThisScrolledComposite = (Composite) this.getContent();

		// first dispose all the UI elements (which includes the deleted one).
		for (Control uiRepresentationOfClaferFeatures : compositeContentOfThisScrolledComposite.getChildren()) {
			uiRepresentationOfClaferFeatures.dispose();
		}

		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		// add all the feature properties excluding the deleted one.
		if (featureProperties.size() > 0) {
			for (FeatureProperty fp : featureProperties) {
				addFeaturePropertyUI(fp, true);
			}
		} else if (featureConstraints.size() > 0) {
			for (String fc : featureConstraints) {
				addFeatureConstraintUI(fc, true);
			}
		} else if(XSLAttributes.size() > 0){
			for(XSLAttribute attribute : XSLAttributes){
				//TODO this is for the remove functionality.
			}
		}

	}

	/**
	 * @return the xSLAttributes
	 */
	public ArrayList<XSLAttribute> getXSLAttributes() {
		return XSLAttributes;
	}

	/**
	 * @param xSLAttributes the xSLAttributes to set
	 */
	// TODO use this somewhere
	private void setXSLAttributes(ArrayList<XSLAttribute> xSLAttributes) {
		XSLAttributes = xSLAttributes;
	}

}
