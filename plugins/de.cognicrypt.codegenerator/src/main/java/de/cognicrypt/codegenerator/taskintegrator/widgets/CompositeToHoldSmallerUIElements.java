package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.Constants.XSLTags;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;
import de.cognicrypt.codegenerator.taskintegrator.models.XSLAttribute;

public class CompositeToHoldSmallerUIElements extends ScrolledComposite {

	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private ArrayList<FeatureProperty> featureProperties;
	private ArrayList<ClaferConstraint> featureConstraints;
	private Composite composite;
	public ArrayList<GroupAnswer> groupAnswers;
	private ArrayList<XSLAttribute> XSLAttributes; // <attributeName, actualAttributeString>

	private ArrayList<Answer> arrayAnswer;
	private ArrayList<String> possibleCfrFeatures;

	private ArrayList<ClaferFeature> listOfExistingClaferFeatures;

	/**
	 * Create the composite. Warnings suppressed for casting array lists.
	 * 
	 * @param parent
	 * @param style
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 */
	public CompositeToHoldSmallerUIElements(Composite parent, int style, ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton, ArrayList<ClaferFeature> listOfExistingClaferFeatures) {
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

		XSLAttributes = new ArrayList<XSLAttribute>();

		this.listOfExistingClaferFeatures = listOfExistingClaferFeatures;

		addData(targetArrayListOfDataToBeDisplayed, showRemoveButton, listOfExistingClaferFeatures);

	}

	/**
	 * If data is provided before hand, add it to the composite. This is specifically used for clafer.
	 * 
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 */
	@SuppressWarnings("unchecked")
	private void addData(ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton, ArrayList<ClaferFeature> listOfExistingClaferFeatures) {
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
						addFeatureProperty(featureUnderConsideration, showRemoveButton, listOfExistingClaferFeatures);
					}
				} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferConstraint) {
					for (ClaferConstraint featureConstraintUnderConsideration : (ArrayList<ClaferConstraint>) targetArrayListOfDataToBeDisplayed) {
						addFeatureConstraint(featureConstraintUnderConsideration, showRemoveButton);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param featureConstraintUnderConsideration
	 * @param showRemoveButton
	 */
	public void addFeatureConstraint(ClaferConstraint featureConstraintUnderConsideration, boolean showRemoveButton) {
		featureConstraints.add(featureConstraintUnderConsideration);
		addFeatureConstraintUI(featureConstraintUnderConsideration, showRemoveButton);
	}

	/**
	 * 
	 * @param featureConstraintUnderConsideration
	 * @param showRemoveButton
	 */
	private void addFeatureConstraintUI(ClaferConstraint featureConstraintUnderConsideration, boolean showRemoveButton) {
		GroupConstraint groupConstraint = new GroupConstraint((Composite) getContent(), SWT.NONE, featureConstraintUnderConsideration, showRemoveButton);
		groupConstraint.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT + 200, 39);

		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * 
	 * @param featureProperty
	 * @param showRemoveButton
	 */
	public void addFeatureProperty(FeatureProperty featureProperty, boolean showRemoveButton, ArrayList<ClaferFeature> listOfExistingClaferFeatures) {
		featureProperties.add(featureProperty);
		addFeaturePropertyUI(featureProperty, showRemoveButton, listOfExistingClaferFeatures);
	}

	/**
	 * 
	 * @param featureProperty
	 * @param showRemoveButton
	 */
	private void addFeaturePropertyUI(FeatureProperty featureProperty, boolean showRemoveButton, ArrayList<ClaferFeature> listOfExistingClaferFeatures) {
		GroupFeatureProperty groupForFeatureProperty = new GroupFeatureProperty((Composite) getContent(), SWT.NONE, featureProperty, showRemoveButton, true, listOfExistingClaferFeatures);
		groupForFeatureProperty.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT + 200,
			Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * 
	 * @param showRemoveButton
	 * @param selectedTag
	 */
	public void addXSLAttribute(boolean showRemoveButton, String selectedTag, ArrayList<String> possibleCfrFeatures) {
		ArrayList<String> possibleAttributes = getListOfPossibleAttributes(selectedTag);
		this.possibleCfrFeatures = possibleCfrFeatures;

		if (possibleAttributes.size() > 0) {
			// Add the first attribute on the list of possible attributes with empty tag data.
			XSLAttribute xslAttribute = new XSLAttribute(possibleAttributes.get(0), "");
			XSLAttributes.add(xslAttribute);
			addXSLAttributeUI(xslAttribute, possibleCfrFeatures, showRemoveButton);
		} else {
			// Show a message if all the possible attributes are exhausted. 
			MessageBox headsUpMessageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
			headsUpMessageBox.setMessage("All possible attributes have been used up.");
			headsUpMessageBox.setText("Cannot add attributes");
			headsUpMessageBox.open();
		}

	}

	/**
	 * 
	 * @param XSLAttrubuteParam
	 * @param showRemoveButton
	 */
	private void addXSLAttributeUI(XSLAttribute XSLAttrubuteParam, ArrayList<String> possibleCfrFeatures, boolean showRemoveButton) {
		GroupXSLTagAttribute groupforXSLTagAttribute = new GroupXSLTagAttribute((Composite) getContent(), SWT.NONE, showRemoveButton, XSLAttrubuteParam, possibleCfrFeatures);
		groupforXSLTagAttribute.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT,
			Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + Constants.HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * 
	 * @param featureProperty
	 */
	public void removeFeatureProperty(FeatureProperty featureProperty) {
		featureProperties.remove(featureProperty);
	}

	/**
	 * 
	 * @param featureConstraint
	 */
	public void removeFeatureConstraint(ClaferConstraint constraint) {
		featureConstraints.remove(constraint);
	}

	/**
	 * 
	 * @param xslAttribute
	 */
	public void removeXSLAttribute(XSLAttribute xslAttribute) {
		getXSLAttributes().remove(xslAttribute);
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

	/**
	 * Add the widgets and data inside the scrollable composite for Link code tab
	 * 
	 * @param answer
	 * 
	 */
	public void addELementsInCodeTabQuestionDialog(Answer answer) {
		GroupForCodeTab group = new GroupForCodeTab((Composite) getContent(), SWT.NONE, answer);
		group.setBounds(5, getLowestWidgetYAxisValue(), 690, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);
		setMinHeight(getLowestWidgetYAxisValue());
	}
	

	/**
	 * Add the widgets and data inside the scrollable composite for clafer dependency tab
	 * 
	 * @param answer
	 * @param claferFeatures list of all clafer features created in the clafer page
	 * 
	 */
	public void addElementsInClaferTabQuestionDialog(Answer answer, ArrayList<ClaferFeature> claferFeatures){
		GroupForClaferTab group = new GroupForClaferTab((Composite) getContent(), SWT.NONE, answer, claferFeatures);
		group.setBounds(5, getLowestWidgetYAxisValue(), 690, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);
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

	/**
	 * TODO update the name of this method.
	 */
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
				addFeaturePropertyUI(fp, true, listOfExistingClaferFeatures);
			}
		} else if (featureConstraints.size() > 0) {
			for (ClaferConstraint fc : featureConstraints) {
				addFeatureConstraintUI(fc, true);
			}
		} else if (XSLAttributes.size() > 0) {
			for (XSLAttribute attribute : XSLAttributes) {
				addXSLAttributeUI(attribute, possibleCfrFeatures, true);
			}
			// The drop downs need to be updated to keep them consistent with the existing data in the attributes.
			updateDropDownsForXSLAttributes(getListOfPossibleAttributes(((Combo) getParent().getChildren()[0]).getText()));

		}

	}

	/**
	 * 
	 * @param listOfPossibleAttributes
	 */
	public void updateDropDownsForXSLAttributes(ArrayList<String> listOfPossibleAttributes) {
		for (Control attribute : ((Composite) getContent()).getChildren()) {
			((GroupXSLTagAttribute) attribute).updateAttributeDropDown(listOfPossibleAttributes);
		}
	}

	/**
	 * 
	 * @param selectionOnComboXSLTags
	 * @return
	 */
	public ArrayList<String> getListOfPossibleAttributes(String selectionOnComboXSLTags) {

		ArrayList<String> listOfPossibleAttributes = new ArrayList<String>();

		// Populate with all the possible attributes first.
		for (XSLTags XSLTag : Constants.XSLTags.values()) {
			if (XSLTag.getXSLTagFaceName().equals(selectionOnComboXSLTags)) {
				for (String attribute : XSLTag.getXSLAttributes()) {
					listOfPossibleAttributes.add(attribute);
				}
			}
		}
		// Remove the attributes that already exist in the list.
		for (XSLAttribute attribute : XSLAttributes) {
			if (listOfPossibleAttributes.contains(attribute.getXSLAttributeName())) {
				listOfPossibleAttributes.remove(attribute.getXSLAttributeName());
			}
		}

		return listOfPossibleAttributes;
	}

	/**
	 * @return the xSLAttributes
	 */
	public ArrayList<XSLAttribute> getXSLAttributes() {
		return XSLAttributes;
	}

}
