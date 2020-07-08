/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

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
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldSmallerUIElements;

public class AddDependenciesDialog extends Dialog {

	private Question question;
	private Text variableTxtBoxForCodeTab;

	public AddDependenciesDialog(final Shell parentShell, final Question question) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
		setQuestion(question);
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		getShell().setMinimumSize(900, 400);

		final TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		final TabItem tbtmLinkClaferFeatures = new TabItem(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setText("Link to variability constructs");

		final Composite compositeForClaferTab = new Composite(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setControl(compositeForClaferTab);
		compositeForClaferTab.setLayout(new GridLayout(2, false));

		if (this.question != null) {

			if (this.question.getElement().equals(Constants.GUIElements.text)) {
				final Label lblLinkFeatureTabMessage = new Label(compositeForClaferTab, SWT.NONE);
				lblLinkFeatureTabMessage.setText("This type of question does not need to link to variability constructs");

			} else if (this.question.getAnswers().size() == 0) {
				final Label lblMessageToAddAnswerForClaferTab = new Label(compositeForClaferTab, SWT.NONE);
				lblMessageToAddAnswerForClaferTab.setText("Please add answer to link variablilty constructs \nYou can do so by clicking the modify button of the current question");
			} else {
				final Label lblQuestion_2 = new Label(compositeForClaferTab, SWT.NONE);
				lblQuestion_2.setText("Question:");

				final Label qstnTxt_1 = new Label(compositeForClaferTab, SWT.None);
				qstnTxt_1.setText(this.question.getQuestionText());

				// Group containing the headers
				final Composite compositeHeaderClaferTab = new Composite(compositeForClaferTab, SWT.NONE);
				final GridData gd_groupHeaderClaferTab = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				compositeHeaderClaferTab.setLayoutData(gd_groupHeaderClaferTab);

				final Label ansLabel = new Label(compositeHeaderClaferTab, SWT.NONE);
				ansLabel.setBounds(5, 5, 130, 25);
				ansLabel.setText("Answers");

				final Label lblForAlgorithm = new Label(compositeHeaderClaferTab, SWT.NONE);
				lblForAlgorithm.setBounds(240, 5, 135, 25);
				lblForAlgorithm.setText("Variability construct");

				final Label lblForOperand = new Label(compositeHeaderClaferTab, SWT.NONE);
				lblForOperand.setBounds(410, 5, 100, 25);
				lblForOperand.setText("Property");

				final Label lblForValue = new Label(compositeHeaderClaferTab, SWT.NONE);
				lblForValue.setBounds(520, 5, 130, 25);
				lblForValue.setText("Operator");

				final Label lblForOperator = new Label(compositeHeaderClaferTab, SWT.NONE);
				lblForOperator.setBounds(655, 5, 130, 25);
				lblForOperator.setText("Set Value");

				// widgets for answer and clafer depenedencies are added in ansScrollCompositeForClaferTab
				final CompositeToHoldSmallerUIElements ansScrollCompositeForClaferTab = new CompositeToHoldSmallerUIElements(compositeForClaferTab, SWT.NONE, null, false);
				final GridData gd_LinkCode = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				gd_LinkCode.heightHint = 200;
				gd_LinkCode.widthHint = 890;
				ansScrollCompositeForClaferTab.setLayoutData(gd_LinkCode);
				ansScrollCompositeForClaferTab.setLayout(new GridLayout(3, false));

				for (final Answer answer : this.question.getAnswers()) {
					ansScrollCompositeForClaferTab.getListOfAllAnswer().add(answer);
				}

			}
		}

		final TabItem tbtmLink = new TabItem(tabFolder, SWT.NONE);
		tbtmLink.setText("Link to variables to use in code");

		final Composite compositeForLinkCodeTab = new Composite(tabFolder, SWT.None);
		tbtmLink.setControl(compositeForLinkCodeTab);
		compositeForLinkCodeTab.setLayout(new GridLayout(2, false));

		if (this.question != null) {

			if (this.question.getElement().equals(Constants.GUIElements.text)) {

				final Label question_3 = new Label(compositeForLinkCodeTab, SWT.None);
				question_3.setText("Question: ");

				final Label question_3Txt = new Label(compositeForLinkCodeTab, SWT.None);
				question_3Txt.setText(this.question.getQuestionText());

				final Label lblOption = new Label(compositeForLinkCodeTab, SWT.None);
				lblOption.setText("Variable Name");

				final Label lblText = new Label(compositeForLinkCodeTab, SWT.NONE);
				lblText.setText("Set Value");

				this.variableTxtBoxForCodeTab = new Text(compositeForLinkCodeTab, SWT.BORDER);
				setTextOfVariableTxtBox();

				final Text txtValue = new Text(compositeForLinkCodeTab, SWT.BORDER);
				txtValue.setVisible(true);
				final GridData gd_txtValue = new GridData(/* SWT.FILL, SWT.CENTER, true, true */);
				gd_txtValue.widthHint = 200;
				txtValue.setLayoutData(gd_txtValue);
				txtValue.setText("");
				txtValue.setEditable(false);

				final CodeDependency codeDependency = new CodeDependency();
				for (final Answer answer : this.question.getAnswers()) {
					if (answer.getCodeDependencies() != null) {
						for (final CodeDependency cd : answer.getCodeDependencies()) {
							if (cd.getOption() != null) {
								this.variableTxtBoxForCodeTab.setText(cd.getOption());
								codeDependency.setOption(this.variableTxtBoxForCodeTab.getText());
							}
							if (cd.getValue() != null) {
								txtValue.setText(cd.getValue());
								codeDependency.setValue("");
							}
						}
					}

					this.variableTxtBoxForCodeTab.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(final FocusEvent e) {
							codeDependency.setOption(AddDependenciesDialog.this.variableTxtBoxForCodeTab.getText());
						}
					});

					txtValue.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(final FocusEvent e) {
							codeDependency.setValue("");
						}
					});
					codeDependency.setValue("");

					final ArrayList<CodeDependency> codeDependencies = new ArrayList<CodeDependency>();
					codeDependencies.add(codeDependency);

					answer.setCodeDependencies(codeDependencies);

				}
			} else if (this.question.getAnswers().size() == 0) {
				final Label lblMessageToAddAnswerForCodeTab = new Label(compositeForLinkCodeTab, SWT.NONE);
				lblMessageToAddAnswerForCodeTab.setText("Please add answer to link variables to use in code \nYou can do so by clicking the modify button of the current question");
			} else {
				final Label question_3 = new Label(compositeForLinkCodeTab, SWT.None);
				question_3.setText("Question: ");

				final Label question_3Txt = new Label(compositeForLinkCodeTab, SWT.None);
				question_3Txt.setText(this.question.getQuestionText());

				final Label variableName = new Label(compositeForLinkCodeTab, SWT.None);
				variableName.setText("Variable Name:");

				this.variableTxtBoxForCodeTab = new Text(compositeForLinkCodeTab, SWT.BORDER);
				setTextOfVariableTxtBox();

				// Composite answersCompositeToLinkCode containing the headers of the table
				final Composite answersCompositeToLinkCode = new Composite(compositeForLinkCodeTab, SWT.NONE);
				final GridData gd_answersCompositeToLinkCode = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				answersCompositeToLinkCode.setLayoutData(gd_answersCompositeToLinkCode);

				final Label lblAnswersLink = new Label(answersCompositeToLinkCode, SWT.None);
				lblAnswersLink.setBounds(5, 5, 210, 25);
				lblAnswersLink.setText("Answers");

				final Label lblText = new Label(answersCompositeToLinkCode, SWT.NONE);
				lblText.setBounds(225, 5, 200, 25);
				lblText.setText("Set Value");

				// To create a scrollable Composite to display all the answers with the required input fields
				final CompositeToHoldSmallerUIElements answerCompositeForLinkCodeTab = new CompositeToHoldSmallerUIElements(compositeForLinkCodeTab, SWT.NONE, null, false);
				final GridData gd_LinkCode = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				gd_LinkCode.heightHint = 200;
				gd_LinkCode.widthHint = 700;
				answerCompositeForLinkCodeTab.setLayoutData(gd_LinkCode);
				answerCompositeForLinkCodeTab.setLayout(new GridLayout(3, false));

				for (final Answer answer : this.question.getAnswers()) {
					// To add the widgets and data inside answerCompositeForLinkCodeTab
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
		for (final Answer answer : this.question.getAnswers()) {
			if (answer.getCodeDependencies() != null) {
				for (final CodeDependency cd : answer.getCodeDependencies()) {
					if (cd.getOption() != null) {
						this.variableTxtBoxForCodeTab.setText(cd.getOption());
						valueSet = true;
						break;
					}

				}
			}
			break;
		}
		if (!valueSet) {
			this.variableTxtBoxForCodeTab.setText(getCapitaliseQuestionText(this.question.getQuestionText()));
		}
	}

	/**
	 * Capitalize the first letter of each word of question text
	 *
	 * @param questionText the question text
	 * @return the capitalize text
	 */
	private String getCapitaliseQuestionText(final String questionText) {
		// TODO Auto-generated method stub
		final String trimmedQuestionText = questionText.trim().replaceAll(" +", " ");
		final String[] arr = trimmedQuestionText.split(" ");
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1));
		}

		return sb.toString();

	}

	/**
	 * @return the question
	 */
	public Question getQuestion() {
		return this.question;
	}

	/**
	 * @param question sets the question
	 */
	public void setQuestion(final Question question) {
		this.question = question;
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
		for (final Answer answer : this.question.getAnswers()) {
			if (answer.getClaferDependencies() != null) {
				if (!this.question.getElement().equals(Constants.GUIElements.text)) {
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
				if (!this.question.getElement().equals(Constants.GUIElements.text)) {
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
				for (final CodeDependency cd : answer.getCodeDependencies()) {
					if (this.variableTxtBoxForCodeTab.getText().equals("")) {
						cd.setOption(getCapitaliseQuestionText(this.question.getQuestionText()));
					} else {
						if (cd.getValue() != null) {
							cd.setOption(this.variableTxtBoxForCodeTab.getText());
						}
					}
					if (this.question.getElement().equals(Constants.GUIElements.text)) {
						cd.setValue("");
					}
				}
			}
		}
	}

}
