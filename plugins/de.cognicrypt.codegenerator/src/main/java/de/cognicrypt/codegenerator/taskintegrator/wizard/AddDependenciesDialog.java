/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldSmallerUIElements;
import de.cognicrypt.core.Constants;

public class AddDependenciesDialog extends Dialog {

	private Question question;
	private ClaferModel claferModel;
	private Text variableTxtBoxForCodeTab;

	public AddDependenciesDialog(Shell parentShell, Question question, ClaferModel claferModel) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
		setQuestion(question);
		setClaferModel(claferModel);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		getShell().setMinimumSize(900, 400);

		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		TabItem tbtmLinkClaferFeatures = new TabItem(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setText("Link to variability constructs");

		Composite compositeForClaferTab = new Composite(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setControl(compositeForClaferTab);
		compositeForClaferTab.setLayout(new GridLayout(2, false));

		if (question != null) {

			if (question.getElement().equals(Constants.GUIElements.text)) {
				Label lblLinkFeatureTabMessage = new Label(compositeForClaferTab, SWT.NONE);
				lblLinkFeatureTabMessage.setText("This type of question does not need to link to variability constructs");

			} else if (question.getAnswers().size() == 0) {
				Label lblMessageToAddAnswerForClaferTab = new Label(compositeForClaferTab, SWT.NONE);
				lblMessageToAddAnswerForClaferTab.setText("Please add answer to link variablilty constructs \nYou can do so by clicking the modify button of the current question");
			} else {
				Label lblQuestion_2 = new Label(compositeForClaferTab, SWT.NONE);
				lblQuestion_2.setText("Question:");

				Label qstnTxt_1 = new Label(compositeForClaferTab, SWT.None);
				qstnTxt_1.setText(question.getQuestionText());

				//Group containing the headers
				Composite compositeHeaderClaferTab = new Composite(compositeForClaferTab, SWT.NONE);
				GridData gd_groupHeaderClaferTab = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				compositeHeaderClaferTab.setLayoutData(gd_groupHeaderClaferTab);

				Label ansLabel = new Label(compositeHeaderClaferTab, SWT.NONE);
				ansLabel.setBounds(5, 5, 130, 25);
				ansLabel.setText("Answers");

				Label lblForAlgorithm = new Label(compositeHeaderClaferTab, SWT.NONE);
				lblForAlgorithm.setBounds(240, 5, 135, 25);
				lblForAlgorithm.setText("Variability construct");

				Label lblForOperand = new Label(compositeHeaderClaferTab, SWT.NONE);
				lblForOperand.setBounds(410, 5, 100, 25);
				lblForOperand.setText("Property");

				Label lblForValue = new Label(compositeHeaderClaferTab, SWT.NONE);
				lblForValue.setBounds(520, 5, 130, 25);
				lblForValue.setText("Operator");

				Label lblForOperator = new Label(compositeHeaderClaferTab, SWT.NONE);
				lblForOperator.setBounds(655, 5, 130, 25);
				lblForOperator.setText("Set Value");

				//widgets for answer and clafer depenedencies are added in ansScrollCompositeForClaferTab
				CompositeToHoldSmallerUIElements ansScrollCompositeForClaferTab = new CompositeToHoldSmallerUIElements(compositeForClaferTab, SWT.NONE, null, false, null);
				GridData gd_LinkCode = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				gd_LinkCode.heightHint = 200;
				gd_LinkCode.widthHint = 890;
				ansScrollCompositeForClaferTab.setLayoutData(gd_LinkCode);
				ansScrollCompositeForClaferTab.setLayout(new GridLayout(3, false));

				for (Answer answer : question.getAnswers()) {
					ansScrollCompositeForClaferTab.getListOfAllAnswer().add(answer);
				}
				//To add the widgets and data inside ansScrollCompositeForClaferTab
				ansScrollCompositeForClaferTab.callAddElementsInClaferTabQuestionDialog(claferModel);

			}
		}

		TabItem tbtmLink = new TabItem(tabFolder, SWT.NONE);
		tbtmLink.setText("Link to variables to use in code");

		Composite compositeForLinkCodeTab = new Composite(tabFolder, SWT.None);
		tbtmLink.setControl(compositeForLinkCodeTab);
		compositeForLinkCodeTab.setLayout(new GridLayout(2, false));

		if (question != null) {

			if (question.getElement().equals(Constants.GUIElements.text)) {

				Label question_3 = new Label(compositeForLinkCodeTab, SWT.None);
				question_3.setText("Question: ");

				Label question_3Txt = new Label(compositeForLinkCodeTab, SWT.None);
				question_3Txt.setText(question.getQuestionText());

				Label lblOption = new Label(compositeForLinkCodeTab, SWT.None);
				lblOption.setText("Variable Name");

				Label lblText = new Label(compositeForLinkCodeTab, SWT.NONE);
				lblText.setText("Set Value");

				variableTxtBoxForCodeTab = new Text(compositeForLinkCodeTab, SWT.BORDER);
				setTextOfVariableTxtBox();

				Text txtValue = new Text(compositeForLinkCodeTab, SWT.BORDER);
				txtValue.setVisible(true);
				GridData gd_txtValue = new GridData(/* SWT.FILL, SWT.CENTER, true, true */);
				gd_txtValue.widthHint = 200;
				txtValue.setLayoutData(gd_txtValue);
				txtValue.setText("");
				txtValue.setEditable(false);

				CodeDependency codeDependency = new CodeDependency();
				for (Answer answer : question.getAnswers()) {
					if (answer.getCodeDependencies() != null) {
						for (CodeDependency cd : answer.getCodeDependencies()) {
							if (cd.getOption() != null) {
								variableTxtBoxForCodeTab.setText(cd.getOption());
								codeDependency.setOption(variableTxtBoxForCodeTab.getText());
							}
							if (cd.getValue() != null) {
								txtValue.setText(cd.getValue());
								codeDependency.setValue("");
							}
						}
					}

					variableTxtBoxForCodeTab.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(FocusEvent e) {
							codeDependency.setOption(variableTxtBoxForCodeTab.getText());
						}
					});

					txtValue.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(FocusEvent e) {
							codeDependency.setValue("");
						}
					});
					codeDependency.setValue("");

					ArrayList<CodeDependency> codeDependencies = new ArrayList<CodeDependency>();
					codeDependencies.add(codeDependency);

					answer.setCodeDependencies(codeDependencies);

				}
			} else if (question.getAnswers().size() == 0) {
				Label lblMessageToAddAnswerForCodeTab = new Label(compositeForLinkCodeTab, SWT.NONE);
				lblMessageToAddAnswerForCodeTab.setText("Please add answer to link variables to use in code \nYou can do so by clicking the modify button of the current question");
			} else {
				Label question_3 = new Label(compositeForLinkCodeTab, SWT.None);
				question_3.setText("Question: ");

				Label question_3Txt = new Label(compositeForLinkCodeTab, SWT.None);
				question_3Txt.setText(question.getQuestionText());

				Label variableName = new Label(compositeForLinkCodeTab, SWT.None);
				variableName.setText("Variable Name:");

				variableTxtBoxForCodeTab = new Text(compositeForLinkCodeTab, SWT.BORDER);
				setTextOfVariableTxtBox();

				// Composite answersCompositeToLinkCode containing the headers of the table
				Composite answersCompositeToLinkCode = new Composite(compositeForLinkCodeTab, SWT.NONE);
				GridData gd_answersCompositeToLinkCode = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				answersCompositeToLinkCode.setLayoutData(gd_answersCompositeToLinkCode);

				Label lblAnswersLink = new Label(answersCompositeToLinkCode, SWT.None);
				lblAnswersLink.setBounds(5, 5, 210, 25);
				lblAnswersLink.setText("Answers");

				Label lblText = new Label(answersCompositeToLinkCode, SWT.NONE);
				lblText.setBounds(225, 5, 200, 25);
				lblText.setText("Set Value");

				//To create a scrollable Composite to display all the answers with the required input fields 
				CompositeToHoldSmallerUIElements answerCompositeForLinkCodeTab = new CompositeToHoldSmallerUIElements(compositeForLinkCodeTab, SWT.NONE, null, false, null);
				GridData gd_LinkCode = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				gd_LinkCode.heightHint = 200;
				gd_LinkCode.widthHint = 700;
				answerCompositeForLinkCodeTab.setLayoutData(gd_LinkCode);
				answerCompositeForLinkCodeTab.setLayout(new GridLayout(3, false));

				for (Answer answer : question.getAnswers()) {
					//To add the widgets and data inside answerCompositeForLinkCodeTab
					answerCompositeForLinkCodeTab.addELementsInCodeTabQuestionDialog(answer);
				}
			}
		}

		return container;

	}

	/**
	 * sets the vale of variable name text box in code tab
	 */
	private void setTextOfVariableTxtBox() {
		boolean valueSet = false;
		for (Answer answer : question.getAnswers()) {
			if (answer.getCodeDependencies() != null) {
				for (CodeDependency cd : answer.getCodeDependencies()) {
					if (cd.getOption() != null) {
						variableTxtBoxForCodeTab.setText(cd.getOption());
						valueSet = true;
						break;
					}

				}
			}
			break;
		}
		if (!valueSet) {
			variableTxtBoxForCodeTab.setText(getCapitaliseQuestionText(question.getQuestionText()));
		}
	}

	/**
	 * Capitalize the first letter of each word of question text
	 *
	 * @param questionText
	 *        the question text
	 * @return the capitalize text
	 */
	private String getCapitaliseQuestionText(String questionText) {
		// TODO Auto-generated method stub
		String trimmedQuestionText = questionText.trim().replaceAll(" +", " ");
		String[] arr = trimmedQuestionText.split(" ");
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1));
		}

		return sb.toString();

	}

	/**
	 * 
	 * @return the question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * 
	 * @param question
	 *        sets the question
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * 
	 * @return the clafer model
	 */
	public ClaferModel getClaferModel() {
		return claferModel;
	}

	/**
	 * 
	 * @param claferModel
	 *        sets the clafer model
	 */
	public void setClaferModel(ClaferModel claferModel) {
		this.claferModel = claferModel;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	/**
	 * Saves the dialog box details
	 */
	private void saveInput() {
		for (Answer answer : question.getAnswers()) {
			if (answer.getClaferDependencies() != null) {
				if (!question.getElement().equals(Constants.GUIElements.text)) {
					// removes the empty clafer dependency objects
					for (int i = 0; i < answer.getClaferDependencies().size();) {
						if (answer.getClaferDependencies().get(i).getAlgorithm() == null || answer.getClaferDependencies().get(i).getAlgorithm() == "") {
							answer.getClaferDependencies().remove(i);
						} else {
							i++;
						}
					}
					// sets the clafer Dependencies to null if the size is 0
					if (answer.getClaferDependencies().size() == 0) {
						answer.setClaferDependencies(null);
					}
				}
			}

			if (answer.getCodeDependencies() != null) {
				if (!question.getElement().equals(Constants.GUIElements.text)) {
					// removes the empty code dependency objects if question type is not text
					for (int i = 0; i < answer.getCodeDependencies().size();) {
						if (answer.getCodeDependencies().get(i).getValue() == null || answer.getCodeDependencies().get(i).getValue() == "") {
							answer.getCodeDependencies().remove(i);
						} else {
							i++;
						}
					}
					// sets the code Dependencies to null if the size is 0
					if (answer.getCodeDependencies().size() == 0) {
						answer.setCodeDependencies(null);
					}
				}
			}

			if (answer.getCodeDependencies() != null) {
				for (CodeDependency cd : answer.getCodeDependencies()) {
					if (variableTxtBoxForCodeTab.getText().equals("")) {
						cd.setOption(getCapitaliseQuestionText(question.getQuestionText()));
					} else {
						if (cd.getValue() != null) {
							cd.setOption(variableTxtBoxForCodeTab.getText());
						}
					}
					if (question.getElement().equals(Constants.GUIElements.text)) {
						cd.setValue("");
					}
				}
			}
		}
	}

}
