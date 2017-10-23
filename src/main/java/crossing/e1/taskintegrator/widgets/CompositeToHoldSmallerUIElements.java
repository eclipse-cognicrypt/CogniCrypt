package crossing.e1.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.ClaferConstraint;
import crossing.e1.taskintegrator.models.FeatureProperty;
import crossing.e1.taskintegrator.wizard.QuestionDialog;

public class CompositeToHoldSmallerUIElements extends ScrolledComposite {

	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private ArrayList<FeatureProperty> featureProperties;
	private ArrayList<ClaferConstraint> featureConstraints;
	private Composite composite;
	public ArrayList<GroupAnswer> groupAnswers;

	private ArrayList<Answer> arrayAnswer;

	/**
	 * Create the composite. Warnings suppressed for casting array lists.
	 * 
	 * @param parent
	 * @param style
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 *        TODO
	 */
	@SuppressWarnings("unchecked")
	public CompositeToHoldSmallerUIElements(Composite parent, int style, ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);
		setExpandVertical(true);
		setExpandHorizontal(true);

		arrayAnswer = new ArrayList<Answer>();
		groupAnswers = new ArrayList<GroupAnswer>();

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
			if (targetArrayListOfDataToBeDisplayed.size() > 0) {
				if (targetArrayListOfDataToBeDisplayed.get(0) instanceof FeatureProperty) {
					for (FeatureProperty featureUnderConsideration : (ArrayList<FeatureProperty>) targetArrayListOfDataToBeDisplayed) {
						addFeatureProperty(featureUnderConsideration, showRemoveButton);
					}
				} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferConstraint) {
					for (ClaferConstraint featureConstraintUnderConsideration : (ArrayList<ClaferConstraint>) targetArrayListOfDataToBeDisplayed) {
						addFeatureConstraint(featureConstraintUnderConsideration, showRemoveButton);
					}
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

	public void addAnswer(Answer answer, boolean showRemoveButton) {
		GroupAnswer groupForAnswer = new GroupAnswer((Composite) getContent(), SWT.NONE, answer, showRemoveButton);
		groupForAnswer.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), 651, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * To delete the answer
	 * 
	 * @param answerToBeDeleted
	 */
	public void deleteAnswer(Answer answerToBeDeleted) {
		arrayAnswer.remove(answerToBeDeleted);
		updateAnswerContainer();
	}

	public void updateAnswerContainer() {
		Composite contentOfThisScrolledComposite = (Composite) this.getContent();

		for (Control answerToDelete : contentOfThisScrolledComposite.getChildren()) {
			answerToDelete.dispose();
		}

		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		for (Answer answer : arrayAnswer) {
			addAnswer(answer, true);
		}
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
	 * @param lowestWidgetYAxisValue
	 *        the lowestWidgetYAxisValue to set
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

	public void setFeatureProperties(ArrayList<FeatureProperty> featureProperties) {
		this.featureProperties = featureProperties;
	}

	/**
	 * @return the featureConstraints
	 */
	public ArrayList<ClaferConstraint> getFeatureConstraints() {
		return featureConstraints;
	}

	/**
	 * @return the listOfAllAnswer
	 */
	public ArrayList<Answer> getListOfAllAnswer() {
		return arrayAnswer;
	}

	public void setFeatureConstraints(ArrayList<ClaferConstraint> featureConstraints) {
		this.featureConstraints = featureConstraints;
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
