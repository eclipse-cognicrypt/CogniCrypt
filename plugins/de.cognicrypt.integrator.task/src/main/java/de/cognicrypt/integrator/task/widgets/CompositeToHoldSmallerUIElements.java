/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

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
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.XSLTags;
import de.cognicrypt.integrator.task.models.ClaferConstraint;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;
import de.cognicrypt.integrator.task.models.XSLAttribute;
import de.cognicrypt.integrator.task.wizard.ClaferConstraintDialog;

public class CompositeToHoldSmallerUIElements extends ScrolledComposite {

	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private ArrayList<ClaferProperty> featureProperties;
	private ArrayList<ClaferConstraint> featureConstraints;
	private final Composite composite;
	public ArrayList<GroupAnswer> groupAnswers;
	private final ArrayList<XSLAttribute> XSLAttributes; // <attributeName, actualAttributeString>

	private final ArrayList<Answer> arrayAnswer;
	private SortedSet<String> possibleCfrFeatures;

	private final ArrayList<Button> btnList;

	private final HashMap<ClaferProperty, GroupFeatureProperty> propertiesMap;

	private final ClaferModel claferModel;

	private ClaferFeature currentClaferFeature;

	/**
	 * Create the composite. Warnings suppressed for casting array lists.
	 *
	 * @param parent
	 * @param style TODO
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 */
	public CompositeToHoldSmallerUIElements(final Composite parent, final int style, final ArrayList<?> targetArrayListOfDataToBeDisplayed, final boolean showRemoveButton, final ClaferModel claferModel) {
		super(parent, style | SWT.V_SCROLL);

		setExpandVertical(true);
		setExpandHorizontal(true);

		this.arrayAnswer = new ArrayList<Answer>();
		this.groupAnswers = new ArrayList<GroupAnswer>();
		this.btnList = new ArrayList<Button>();

		this.composite = new Composite(this, SWT.NONE);
		this.composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		this.composite.setLayout(new GridLayout(1, false));

		setContent(this.composite);
		setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		this.featureProperties = new ArrayList<ClaferProperty>();
		this.featureConstraints = new ArrayList<ClaferConstraint>();

		this.propertiesMap = new HashMap<>();
		this.XSLAttributes = new ArrayList<XSLAttribute>();

		this.claferModel = claferModel;

		addData(targetArrayListOfDataToBeDisplayed, showRemoveButton, claferModel);

	}

	public CompositeToHoldSmallerUIElements(final Composite parent, final int style, final ArrayList<?> targetArrayListOfDataToBeDisplayed, final boolean showRemoveButton, final ClaferModel claferModel, final ClaferFeature currentClaferFeature) {
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
	private void addData(final ArrayList<?> targetArrayListOfDataToBeDisplayed, final boolean showRemoveButton, final ClaferModel claferModel) {
		if (targetArrayListOfDataToBeDisplayed != null) {

			if (this.featureProperties == null && this.featureConstraints == null) {
				if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferProperty) {
					this.featureProperties = new ArrayList<ClaferProperty>();
				} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferConstraint) {
					this.featureConstraints = new ArrayList<ClaferConstraint>();
				}
			}
			if (targetArrayListOfDataToBeDisplayed.size() > 0) {
				if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferProperty) {
					for (final ClaferProperty featureUnderConsideration : (ArrayList<ClaferProperty>) targetArrayListOfDataToBeDisplayed) {
						addFeatureProperty(featureUnderConsideration, showRemoveButton, claferModel);
					}
				} else if (targetArrayListOfDataToBeDisplayed.get(0) instanceof ClaferConstraint) {
					for (final ClaferConstraint featureConstraintUnderConsideration : (ArrayList<ClaferConstraint>) targetArrayListOfDataToBeDisplayed) {
						addFeatureConstraint(featureConstraintUnderConsideration, showRemoveButton);
					}
				}
			}
		}
	}

	/**
	 * @param featureConstraintUnderConsideration
	 * @param showRemoveButton
	 */
	public void addFeatureConstraint(final ClaferConstraint featureConstraintUnderConsideration, final boolean showRemoveButton) {
		this.featureConstraints.add(featureConstraintUnderConsideration);
		addFeatureConstraintUI(featureConstraintUnderConsideration, showRemoveButton);
	}

	/**
	 * @param featureConstraintUnderConsideration
	 * @param showRemoveButton
	 */
	private void addFeatureConstraintUI(final ClaferConstraint featureConstraintUnderConsideration, final boolean showRemoveButton) {
		final GroupConstraint groupConstraint = new GroupConstraint(this.composite, SWT.NONE, featureConstraintUnderConsideration, showRemoveButton);
		groupConstraint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.composite.layout();
	}

	/**
	 * @param featureProperty
	 * @param showRemoveButton
	 */
	public void addFeatureProperty(final ClaferProperty featureProperty, final boolean showRemoveButton, final ClaferModel claferModel) {
		this.featureProperties.add(featureProperty);
		addFeaturePropertyUI(featureProperty, showRemoveButton, claferModel);
	}

	/**
	 * @param featureProperty
	 * @param showRemoveButton
	 */
	private void addFeaturePropertyUI(final ClaferProperty featureProperty, final boolean showRemoveButton, final ClaferModel claferModel) {
		final GroupFeatureProperty groupForFeatureProperty = new GroupFeatureProperty(this.composite, SWT.NONE, featureProperty, showRemoveButton, claferModel);
		groupForFeatureProperty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.composite.layout();

		this.propertiesMap.put(featureProperty, groupForFeatureProperty);
	}

	/**
	 * @param showRemoveButton
	 * @param selectedTag
	 */
	public void addXSLAttribute(final boolean showRemoveButton, final String selectedTag, final SortedSet<String> possibleCfrFeatures) {
		final ArrayList<String> possibleAttributes = getListOfPossibleAttributes(selectedTag);
		this.possibleCfrFeatures = possibleCfrFeatures;

		if (possibleAttributes.size() > 0) {
			// Add the first attribute on the list of possible attributes with empty tag data.
			final XSLAttribute xslAttribute = new XSLAttribute(possibleAttributes.get(0), "");
			this.XSLAttributes.add(xslAttribute);
			addXSLAttributeUI(xslAttribute, possibleCfrFeatures, showRemoveButton);
		} else {
			// Show a message if all the possible attributes are exhausted.
			final MessageBox headsUpMessageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
			headsUpMessageBox.setMessage("All possible attributes have been used up.");
			headsUpMessageBox.setText("Cannot add attributes");
			headsUpMessageBox.open();
		}

	}

	/**
	 * @param XSLAttrubuteParam
	 * @param showRemoveButton
	 */
	private void addXSLAttributeUI(final XSLAttribute XSLAttrubuteParam, final SortedSet<String> possibleCfrFeatures, final boolean showRemoveButton) {
		final GroupXSLTagAttribute groupforXSLTagAttribute = new GroupXSLTagAttribute((Composite) getContent(), SWT.NONE, showRemoveButton, XSLAttrubuteParam, possibleCfrFeatures);
		groupforXSLTagAttribute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.composite.layout();
	}

	/**
	 * @param property
	 */
	public void removeFeatureProperty(final ClaferProperty property) {
		this.featureProperties.remove(property);
		this.propertiesMap.remove(property);
	}

	/**
	 * @param featureConstraint
	 */
	public void removeFeatureConstraint(final ClaferConstraint constraint) {
		this.featureConstraints.remove(constraint);
	}

	/**
	 * @param xslAttribute
	 */
	public void removeXSLAttribute(final XSLAttribute xslAttribute) {
		getXSLAttributes().remove(xslAttribute);
	}

	/**
	 * Creates the widgets in which user can give answer details
	 *
	 * @param answer
	 * @param showRemoveButton
	 */
	public void addAnswer(final Answer answer, final boolean showRemoveButton) {
		final GroupAnswer groupForAnswer = new GroupAnswer((Composite) getContent(), SWT.NONE, answer, showRemoveButton);
		groupForAnswer.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), 890, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * To delete the answer
	 *
	 * @param answerToBeDeleted
	 */
	public void deleteAnswer(final Answer answerToBeDeleted) {
		this.arrayAnswer.remove(answerToBeDeleted);
		this.btnList.clear();
		updateAnswerContainer();
	}

	public void updateAnswerContainer() {
		final Composite contentOfThisScrolledComposite = (Composite) getContent();

		for (final Control answerToDelete : contentOfThisScrolledComposite.getChildren()) {
			answerToDelete.dispose();
		}

		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		for (final Answer answer : this.arrayAnswer) {
			addAnswer(answer, true);
		}
	}

	/**
	 * @return the btnList List of radio buttons for default Answer field
	 */
	public ArrayList<Button> getDefaulAnswerBtnList() {
		return this.btnList;
	}

	/**
	 * Add the widgets and data inside the scrollable composite for Link code tab
	 *
	 * @param answer
	 */
	public void addELementsInCodeTabQuestionDialog(final Answer answer) {
		final CompositeForCodeTab group = new CompositeForCodeTab((Composite) getContent(), SWT.NONE, answer);
		group.setBounds(5, getLowestWidgetYAxisValue(), 690, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);
		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * Add the widgets and data inside the scrollable composite for clafer dependency tab
	 *
	 * @param answer
	 * @param claferFeatures list of all clafer features created in the clafer page
	 */
	public void addElementsInClaferTabQuestionDialog(final Answer answer, final ClaferModel claferModel, final boolean showClaferWidgets) {
		final CompositeForClaferTab group = new CompositeForClaferTab((Composite) getContent(), SWT.NONE, answer, claferModel, showClaferWidgets);
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
	 * @param claferModel contains the list of all ClaferFeatures
	 */
	public void callAddElementsInClaferTabQuestionDialog(final ClaferModel claferModel) {
		for (final Answer answer : this.arrayAnswer) {
			addElementsInClaferTabQuestionDialog(answer, claferModel, false);
		}
	}

	/**
	 * Deletes the specific clafer dependency from the list of answer's clafer dependencies
	 *
	 * @param answer the Answer
	 * @param claferDependency to be deleted
	 * @param claferModel containing the list of clafer features
	 */
	public void deleteClaferDependency(final Answer answer, final ClaferDependency claferDependency, final ClaferModel claferModel) {
		answer.getClaferDependencies().remove(claferDependency);
		updateClaferTab(claferModel, answer);
	}

	/**
	 * Updates the clafer tab whenever a deletion or addition of clafer dependency takes place in clafer tab
	 *
	 * @param claferModel containing the list of clafer features
	 * @param showClaferWidgetsForAnswer the answer
	 */
	public void updateClaferTab(final ClaferModel claferModel, final Answer showClaferWidgetsForAnswer) {
		final Composite contentOfThisScrolledComposite = (Composite) getContent();

		for (final Control answerToDelete : contentOfThisScrolledComposite.getChildren()) {
			answerToDelete.dispose();
		}

		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		for (final Answer answer : this.arrayAnswer) {
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
	public void addElementsOfLinkAnswer(final Answer answer, final Question currentQuestion, final ArrayList<Question> listOfAllQuestions) {
		final GroupForLinkAnswer group = new GroupForLinkAnswer((Composite) getContent(), SWT.NONE, answer, currentQuestion, listOfAllQuestions);
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
		return this.lowestWidgetYAxisValue;
	}

	/**
	 * @param lowestWidgetYAxisValue the lowestWidgetYAxisValue to set
	 */
	public void setLowestWidgetYAxisValue(final int lowestWidgetYAxisValue) {
		this.lowestWidgetYAxisValue = lowestWidgetYAxisValue + Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	}

	/**
	 * @return the featureProperties
	 */
	public ArrayList<ClaferProperty> getFeatureProperties() {
		return this.featureProperties;
	}

	public void setFeatureProperties(final ArrayList<ClaferProperty> featureProperties) {
		this.featureProperties = featureProperties;
	}

	/**
	 * @return the featureConstraints
	 */
	public ArrayList<ClaferConstraint> getFeatureConstraints() {
		return this.featureConstraints;
	}

	/**
	 * @return the listOfAllAnswer
	 */
	public ArrayList<Answer> getListOfAllAnswer() {
		return this.arrayAnswer;
	}

	public void setFeatureConstraints(final ArrayList<ClaferConstraint> featureConstraints) {
		this.featureConstraints = featureConstraints;
	}

	/**
	 * TODO update the name of this method.
	 */
	public void updateClaferContainer() {
		final Composite compositeContentOfThisScrolledComposite = (Composite) getContent();

		// first dispose all the UI elements (which includes the deleted one).
		for (final Control uiRepresentationOfClaferFeatures : compositeContentOfThisScrolledComposite.getChildren()) {
			uiRepresentationOfClaferFeatures.dispose();
		}

		// add all the feature properties excluding the deleted one.
		if (this.featureProperties.size() > 0) {
			for (final ClaferProperty fp : this.featureProperties) {
				addFeaturePropertyUI(fp, true, this.claferModel);
			}
		} else if (this.featureConstraints.size() > 0) {
			for (final ClaferConstraint fc : this.featureConstraints) {
				addFeatureConstraintUI(fc, true);
			}
		} else if (this.XSLAttributes.size() > 0) {
			for (final XSLAttribute attribute : this.XSLAttributes) {
				addXSLAttributeUI(attribute, this.possibleCfrFeatures, true);
			}
			// The drop downs need to be updated to keep them consistent with the existing data in the attributes.
			updateDropDownsForXSLAttributes(getListOfPossibleAttributes(((Combo) getParent().getChildren()[0]).getText()));

		}

	}

	/**
	 * @param listOfPossibleAttributes
	 */
	public void updateDropDownsForXSLAttributes(final ArrayList<String> listOfPossibleAttributes) {
		for (final Control attribute : ((Composite) getContent()).getChildren()) {
			((GroupXSLTagAttribute) attribute).updateAttributeDropDown(listOfPossibleAttributes);
		}
	}

	/**
	 * @param selectionOnComboXSLTags
	 * @return
	 */
	public ArrayList<String> getListOfPossibleAttributes(final String selectionOnComboXSLTags) {

		final ArrayList<String> listOfPossibleAttributes = new ArrayList<String>();

		// Populate with all the possible attributes first.
		for (final XSLTags XSLTag : Constants.XSLTags.values()) {
			if (XSLTag.getXSLTagFaceName().equals(selectionOnComboXSLTags)) {
				for (final String attribute : XSLTag.getXSLAttributes()) {
					listOfPossibleAttributes.add(attribute);
				}
			}
		}
		// Remove the attributes that already exist in the list.
		for (final XSLAttribute attribute : this.XSLAttributes) {
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
		return this.XSLAttributes;
	}

	public void modifyFeature(final ClaferConstraint constraint) {
		final ClaferConstraintDialog cfrConstraintDialog = new ClaferConstraintDialog(getShell(), this.currentClaferFeature, this.claferModel, constraint);
		final int id = this.featureConstraints.lastIndexOf(constraint); // blocking call to Dialog.open() the dialog // it returns 0 on success
		if (cfrConstraintDialog.open() == 0) {
			this.featureConstraints.set(id, cfrConstraintDialog.getResult());
		}

	}

	public boolean validate() {
		boolean valid = true;

		for (final GroupFeatureProperty groupFeatureProperty : this.propertiesMap.values()) {
			valid &= groupFeatureProperty.validate();
		}

		return valid;
	}

}
