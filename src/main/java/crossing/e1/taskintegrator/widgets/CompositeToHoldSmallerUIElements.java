package crossing.e1.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import crossing.e1.configurator.Constants;
import crossing.e1.taskintegrator.models.ClaferConstraint;
import crossing.e1.taskintegrator.models.FeatureProperty;




public class CompositeToHoldSmallerUIElements extends ScrolledComposite {
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private ArrayList<FeatureProperty> featureProperties;
	private ArrayList<ClaferConstraint> featureConstraints;
	private Composite composite;
	


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
		featureConstraints = new ArrayList<ClaferConstraint>();

		addData(targetArrayListOfDataToBeDisplayed, showRemoveButton);

	}

	@SuppressWarnings("unchecked")
	private void addData(ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton) {
		if (targetArrayListOfDataToBeDisplayed != null) {

			if (featureProperties == null && featureConstraints == null) {
				if (targetArrayListOfDataToBeDisplayed.get(0) instanceof FeatureProperty) {
					featureProperties = new ArrayList<FeatureProperty>();
				} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferConstraint) {
					featureConstraints = new ArrayList<ClaferConstraint>();
				}
			}

			if (targetArrayListOfDataToBeDisplayed.get(0) instanceof FeatureProperty) {

				featureProperties.addAll((ArrayList<FeatureProperty>) targetArrayListOfDataToBeDisplayed);

				for (FeatureProperty featureUnderConsideration : (ArrayList<FeatureProperty>) targetArrayListOfDataToBeDisplayed) {
					featureProperties.add(featureUnderConsideration);
					addFeatureProperty(featureUnderConsideration, showRemoveButton);
				}
			} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferConstraint) {
				featureConstraints.addAll((ArrayList<ClaferConstraint>) targetArrayListOfDataToBeDisplayed);

				for (ClaferConstraint featureConstraintUnderConsideration : (ArrayList<ClaferConstraint>) targetArrayListOfDataToBeDisplayed) {
					featureConstraints.add(featureConstraintUnderConsideration);
					addFeatureConstraint(featureConstraintUnderConsideration, false);
				}
			}

		}
	}

	public void addFeatureConstraint(ClaferConstraint featureConstraintUnderConsideration, boolean showRemoveButton) {
		featureConstraints.add(featureConstraintUnderConsideration);
		addFeatureConstraintUI(featureConstraintUnderConsideration, showRemoveButton);
	}

	private void addFeatureConstraintUI(ClaferConstraint featureConstraintUnderConsideration, boolean showRemoveButton) {
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
		GroupFeatureProperty groupForFeatureProperty = new GroupFeatureProperty((Composite) getContent(), SWT.NONE, featureProperty, showRemoveButton, true);
		groupForFeatureProperty.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT + 200,
			Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	public void removeFeatureProperty(FeatureProperty featureProperty) {
		featureProperties.remove(featureProperty);
	}

	public void removeFeatureConstraint(ClaferConstraint constraint) {
		featureConstraints.remove(constraint);
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
	public ArrayList<ClaferConstraint> getFeatureConstraints() {
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
			for (ClaferConstraint fc : featureConstraints) {
				addFeatureConstraintUI(fc, true);
			}
		}

	}

}
