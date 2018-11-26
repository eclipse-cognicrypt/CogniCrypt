/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;
import de.cognicrypt.codegenerator.taskintegrator.models.XSLAttribute;
import de.cognicrypt.codegenerator.taskintegrator.wizard.ClaferConstraintDialog;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.XSLTags;

public class CompositeToHoldSmallerUIElements extends ScrolledComposite {

	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private ArrayList<ClaferProperty> featureProperties;
	private ArrayList<ClaferConstraint> featureConstraints;
	private Composite composite;
	public ArrayList<GroupAnswer> groupAnswers;
	private ArrayList<XSLAttribute> XSLAttributes; // <attributeName, actualAttributeString>

	private ArrayList<Answer> arrayAnswer;
	private SortedSet<String> possibleCfrFeatures;

	private ArrayList<Button> btnList;

	private HashMap<ClaferProperty, GroupFeatureProperty> propertiesMap;

	private ClaferModel claferModel;

	private ClaferFeature currentClaferFeature;

	/**
	 * Create the composite. Warnings suppressed for casting array lists.
	 * 
	 * @param parent
	 * @param style
	 *        TODO
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 */
	public CompositeToHoldSmallerUIElements(Composite parent, int style, ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton, ClaferModel claferModel) {
		super(parent, style | SWT.V_SCROLL);

		setExpandVertical(true);
		setExpandHorizontal(true);

		arrayAnswer = new ArrayList<Answer>();
		groupAnswers = new ArrayList<GroupAnswer>();
		btnList = new ArrayList<Button>();

		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		composite.setLayout(new GridLayout(1, false));

		setContent(composite);
		setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		featureProperties = new ArrayList<ClaferProperty>();
		featureConstraints = new ArrayList<ClaferConstraint>();

		propertiesMap = new HashMap<>();
		XSLAttributes = new ArrayList<XSLAttribute>();

		this.claferModel = claferModel;

		addData(targetArrayListOfDataToBeDisplayed, showRemoveButton, claferModel);

	}

	public CompositeToHoldSmallerUIElements(Composite parent, int style, ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton, ClaferModel claferModel, ClaferFeature currentClaferFeature) {
		this(parent, style, targetArrayListOfDataToBeDisplayed, showRemoveButton, claferModel);
		this.currentClaferFeature = currentClaferFeature;
	}

	/**
	 * If data is provided before hand, add it to the composite. This is specifically used for clafer.
	 * 
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 */
	@SuppressWarnings("unchecked")
	private void addData(ArrayList<?> targetArrayListOfDataToBeDisplayed, boolean showRemoveButton, ClaferModel claferModel) {
		if (targetArrayListOfDataToBeDisplayed != null) {

			if (featureProperties == null && featureConstraints == null) {
				if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferProperty) {
					featureProperties = new ArrayList<ClaferProperty>();
				} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferConstraint) {
					featureConstraints = new ArrayList<ClaferConstraint>();
				}
			}
			if (targetArrayListOfDataToBeDisplayed.size() > 0) {
				if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferProperty) {
					for (ClaferProperty featureUnderConsideration : (ArrayList<ClaferProperty>) targetArrayListOfDataToBeDisplayed) {
						addFeatureProperty(featureUnderConsideration, showRemoveButton, claferModel);
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
		GroupConstraint groupConstraint = new GroupConstraint(composite, SWT.NONE, featureConstraintUnderConsideration, showRemoveButton);
		groupConstraint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.layout();
	}

	/**
	 * 
	 * @param featureProperty
	 * @param showRemoveButton
	 */
	public void addFeatureProperty(ClaferProperty featureProperty, boolean showRemoveButton, ClaferModel claferModel) {
		featureProperties.add(featureProperty);
		addFeaturePropertyUI(featureProperty, showRemoveButton, claferModel);
	}

	/**
	 * 
	 * @param featureProperty
	 * @param showRemoveButton
	 */
	private void addFeaturePropertyUI(ClaferProperty featureProperty, boolean showRemoveButton, ClaferModel claferModel) {
		GroupFeatureProperty groupForFeatureProperty = new GroupFeatureProperty(composite, SWT.NONE, featureProperty, showRemoveButton, claferModel);
		groupForFeatureProperty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.layout();

		propertiesMap.put(featureProperty, groupForFeatureProperty);
	}

	/**
	 * 
	 * @param showRemoveButton
	 * @param selectedTag
	 */
	public void addXSLAttribute(boolean showRemoveButton, String selectedTag, SortedSet<String> possibleCfrFeatures) {
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
	private void addXSLAttributeUI(XSLAttribute XSLAttrubuteParam, SortedSet<String> possibleCfrFeatures, boolean showRemoveButton) {
		GroupXSLTagAttribute groupforXSLTagAttribute = new GroupXSLTagAttribute((Composite) getContent(), SWT.NONE, showRemoveButton, XSLAttrubuteParam, possibleCfrFeatures);
		groupforXSLTagAttribute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.layout();
	}

	/**
	 * 
	 * @param property
	 */
	public void removeFeatureProperty(ClaferProperty property) {
		featureProperties.remove(property);
		propertiesMap.remove(property);
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

	/**
	 * Creates the widgets in which user can give answer details
	 * 
	 * @param answer
	 * @param showRemoveButton
	 */
	public void addAnswer(Answer answer, boolean showRemoveButton) {
		GroupAnswer groupForAnswer = new GroupAnswer((Composite) getContent(), SWT.NONE, answer, showRemoveButton);
		groupForAnswer.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), 890, 39);
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
		btnList.clear();
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
	 * @return the btnList List of radio buttons for default Answer field
	 */
	public ArrayList<Button> getDefaulAnswerBtnList() {
		return btnList;
	}

	/**
	 * Add the widgets and data inside the scrollable composite for Link code tab
	 * 
	 * @param answer
	 * 
	 */
	public void addELementsInCodeTabQuestionDialog(Answer answer) {
		CompositeForCodeTab group = new CompositeForCodeTab((Composite) getContent(), SWT.NONE, answer);
		group.setBounds(5, getLowestWidgetYAxisValue(), 690, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);
		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * Add the widgets and data inside the scrollable composite for clafer dependency tab
	 * 
	 * @param answer
	 * @param claferFeatures
	 *        list of all clafer features created in the clafer page
	 * 
	 */
	public void addElementsInClaferTabQuestionDialog(Answer answer, ClaferModel claferModel, boolean showClaferWidgets) {
		CompositeForClaferTab group = new CompositeForClaferTab((Composite) getContent(), SWT.NONE, answer, claferModel, showClaferWidgets);
		/**
		 * case 1: if the showClaferWidgets value is true or the list of clafer Dependecies is not null then the following loop executes
		 */
		if (showClaferWidgets || answer.getClaferDependencies() != null) {
			/**
			 * following if else block decides the height of the group object depending on the size of clafer dependency list of answer object
			 */
			if (answer.getClaferDependencies().size() == 0) {
				group.setBounds(5, getLowestWidgetYAxisValue(), 890, 39);
				setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);
			} else {
				group.setBounds(5, getLowestWidgetYAxisValue(), 890, group.getLowestWidgetYAxisValue());
				setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + group.getLowestWidgetYAxisValue());
			}
		}
		/**
		 * executes when case 1 is false
		 */
		else {
			group.setBounds(5, getLowestWidgetYAxisValue(), 890, 39);
			setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);
		}
		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * calls the addElementsInclaferTabQuestionDialog method for each answer
	 * 
	 * @param claferModel
	 *        contains the list of all ClaferFeatures
	 */
	public void callAddElementsInClaferTabQuestionDialog(ClaferModel claferModel) {
		for (Answer answer : arrayAnswer) {
			addElementsInClaferTabQuestionDialog(answer, claferModel, false);
		}
	}

	/**
	 * Deletes the specific clafer dependency from the list of answer's clafer dependencies
	 * 
	 * @param answer
	 *        the Answer
	 * @param claferDependency
	 *        to be deleted
	 * @param claferModel
	 *        containing the list of clafer features
	 */
	public void deleteClaferDependency(Answer answer, ClaferDependency claferDependency, ClaferModel claferModel) {
		answer.getClaferDependencies().remove(claferDependency);
		updateClaferTab(claferModel, answer);
	}

	/**
	 * Updates the clafer tab whenever a deletion or addition of clafer dependency takes place in clafer tab
	 * 
	 * @param claferModel
	 *        containing the list of clafer features
	 * @param showClaferWidgetsForAnswer
	 *        the answer
	 */
	public void updateClaferTab(ClaferModel claferModel, Answer showClaferWidgetsForAnswer) {
		Composite contentOfThisScrolledComposite = (Composite) this.getContent();

		for (Control answerToDelete : contentOfThisScrolledComposite.getChildren()) {
			answerToDelete.dispose();
		}

		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		for (Answer answer : arrayAnswer) {
			if (answer.getValue().equals(showClaferWidgetsForAnswer.getValue())) {
				addElementsInClaferTabQuestionDialog(answer, claferModel, true);
			} else {
				addElementsInClaferTabQuestionDialog(answer, claferModel, false);
			}
		}

	}

	/**
	 * Add the widgets and data inside the scrollable composite for Link Answer
	 * 
	 * @param currentQuestion
	 * @param listOfAllQuestions
	 */
	public void addElementsOfLinkAnswer(Answer answer, Question currentQuestion, ArrayList<Question> listOfAllQuestions) {
		GroupForLinkAnswer group = new GroupForLinkAnswer((Composite) getContent(), SWT.NONE, answer, currentQuestion, listOfAllQuestions);
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
	public ArrayList<ClaferProperty> getFeatureProperties() {
		return featureProperties;
	}

	public void setFeatureProperties(ArrayList<ClaferProperty> featureProperties) {
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

		// add all the feature properties excluding the deleted one.
		if (featureProperties.size() > 0) {
			for (ClaferProperty fp : featureProperties) {
				addFeaturePropertyUI(fp, true, claferModel);
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

	public void modifyFeature(ClaferConstraint constraint) {
		ClaferConstraintDialog cfrConstraintDialog = new ClaferConstraintDialog(getShell(), currentClaferFeature, claferModel, constraint);
		int id = featureConstraints.lastIndexOf(constraint); // blocking call to Dialog.open() the dialog // it returns 0 on success 
		if (cfrConstraintDialog.open() == 0) {
			featureConstraints.set(id, cfrConstraintDialog.getResult());
		}

	}

	public boolean validate() {
		boolean valid = true;

		for (GroupFeatureProperty groupFeatureProperty : propertiesMap.values()) {
			valid &= groupFeatureProperty.validate();
		}

		return valid;
	}

}
