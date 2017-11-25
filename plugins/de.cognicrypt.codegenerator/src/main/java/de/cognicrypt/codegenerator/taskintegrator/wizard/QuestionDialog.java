package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;
import java.util.Objects;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldSmallerUIElements;

public class QuestionDialog extends Dialog {

	public Text textQuestion;
	private Label lblQuestionContent;
	private String questionText;
	private String questionType;
	private Combo combo;
	private CompositeToHoldSmallerUIElements compositeToHoldAnswers;
	private Question question;
	private Question questionDetails;
	private ArrayList<ClaferFeature> claferFeatures;
	private ArrayList<Question> listOfAllQuestions;
	int counter = 0;
	private String featureSelected;
	private ArrayList<String> operandItems;
	private String currentQuestionType = null;
	private MessageBox linkAnswersTabMessageBox;
	private MessageBox linkFeaturesMessageBox;
	private MessageBox linkCodeMessageBox;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public QuestionDialog(Shell parentShell) {
		this(parentShell, null, null, null);
	}

	public QuestionDialog(Shell parentShell, Question question, ArrayList<ClaferFeature> claferFeatures, ArrayList<Question> listOfAllQuestions) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
		this.question = question;
		this.claferFeatures = claferFeatures;
		this.listOfAllQuestions = listOfAllQuestions;
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
		getShell().setMinimumSize(700, 400);

		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				if (tabFolder.getSelectionIndex() == 1) {
					System.out.println(tabFolder.getSelectionIndex());
					//lblQuestionContent.setText(textQuestion.getText());

					if (question == null) {
						linkAnswersTabMessageBox.open();
					}
				}

				if (tabFolder.getSelectionIndex() == 2) {
					if (question == null) {
						linkFeaturesMessageBox.open();
					}
				}

				if (tabFolder.getSelectionIndex() == 3) {
					if (question == null) {
						linkCodeMessageBox.open();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		TabItem tbtmQuestion = new TabItem(tabFolder, SWT.NONE);
		tbtmQuestion.setText("Question");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmQuestion.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Label lblQuestion = new Label(composite, SWT.NONE);
		lblQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblQuestion.setText("Question");

		textQuestion = new Text(composite, SWT.BORDER);
		textQuestion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblType = new Label(composite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Answer type");

		String comboItem1 = "Drop down ";
		String comboItem2 = "text box";
		String comboItem3 = "itemSelection ( More than one answer selection possible )";
		String comboItem4 = "Radio Button";
		combo = new Combo(composite, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//combo.setItems(new String[] {comboItem1, comboItem2, comboItem3, comboItem4 });
		combo.setItems(new String[] {comboItem1, comboItem2, comboItem4 });
		combo.select(-1);

		Button btnAddAnswer = new Button(composite, SWT.None);
		btnAddAnswer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnAddAnswer.setText("Add Answer");
		//Visibility depends on question type
		btnAddAnswer.setVisible(false);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		boolean showRemoveButton = true;
		compositeToHoldAnswers = new CompositeToHoldSmallerUIElements(composite, SWT.NONE, null, showRemoveButton);
		GridData gd_compositeToHoldAnswers = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_compositeToHoldAnswers.heightHint = 125;
		gd_compositeToHoldAnswers.widthHint = 520;
		compositeToHoldAnswers.setLayoutData(gd_compositeToHoldAnswers);
		compositeToHoldAnswers.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeToHoldAnswers.setVisible(false);
		btnAddAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Answer tempAnswer = new Answer();
				compositeToHoldAnswers.getListOfAllAnswer().add(tempAnswer);
				compositeToHoldAnswers.addAnswer(tempAnswer, showRemoveButton);
				compositeToHoldAnswers.setVisible(true);
			}

		});
		currentQuestionType = combo.getText();
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				switch (combo.getText()) {
					case "text box":
						btnAddAnswer.setVisible(false);
						compositeToHoldAnswers.setVisible(false);
						compositeToHoldAnswers.getListOfAllAnswer().clear();
						compositeToHoldAnswers.updateAnswerContainer();
						currentQuestionType = "text box";
						Answer txtAnswer = new Answer();
						txtAnswer.setValue("");
						question.getAnswers().add(txtAnswer);
						break;
					case "Drop down ":
						boolean comboSelected = combo.getText().equalsIgnoreCase("Drop down ") ? true : false;
						btnAddAnswer.setVisible(comboSelected);
						if (!currentQuestionType.equalsIgnoreCase("combo (Drop down )")) {
							compositeToHoldAnswers.getListOfAllAnswer().clear();
							compositeToHoldAnswers.updateAnswerContainer();
							compositeToHoldAnswers.setVisible(false);
							currentQuestionType = "Drop down ";
						}
						break;
					case "itemSelection ( More than one answer selection possible )":
						boolean itemSelected = combo.getText().equalsIgnoreCase("itemSelection ( More than one answer selection possible )") ? true : false;
						btnAddAnswer.setVisible(itemSelected);
						if (!currentQuestionType.equalsIgnoreCase("itemselection")) {
							compositeToHoldAnswers.getListOfAllAnswer().clear();
							compositeToHoldAnswers.updateAnswerContainer();
							compositeToHoldAnswers.setVisible(false);
							currentQuestionType = "itemSelection ( More than one answer selection possible )";
						}
						break;
					case "Radio Button":
						boolean buttonSelected = combo.getText().equalsIgnoreCase("Radio Button") ? true : false;
						btnAddAnswer.setVisible(buttonSelected);
						if (!currentQuestionType.equalsIgnoreCase("Radio Button")) {
							compositeToHoldAnswers.getListOfAllAnswer().clear();
							compositeToHoldAnswers.updateAnswerContainer();
							compositeToHoldAnswers.setVisible(false);
							currentQuestionType = "Radio Button";
						}
						break;
					default:
						break;
				}
			}
		});

		if (question != null) {
			textQuestion.setText(question.getQuestionText());
			combo.setText(question.getQuestionType());
			for (Answer answer : question.getAnswers()) {
				compositeToHoldAnswers.getListOfAllAnswer().add(answer);
				compositeToHoldAnswers.addAnswer(answer, showRemoveButton);
				compositeToHoldAnswers.setVisible(true);
			}
			if (question.getQuestionType().equalsIgnoreCase("text box")) {
				compositeToHoldAnswers.setVisible(false);
			}

		}

		TabItem tbtmLinkAnswers = new TabItem(tabFolder, SWT.NONE);
		tbtmLinkAnswers.setText("Link to other questions");
		

		Composite compositeForLinkAnswerTab = new Composite(tabFolder, SWT.NONE);
		tbtmLinkAnswers.setControl(compositeForLinkAnswerTab);
		compositeForLinkAnswerTab.setLayout(new GridLayout(2, false));

		if (question == null) {
			linkAnswersTabMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
			linkAnswersTabMessageBox.setMessage(
				"Please at first completely fill the details of \"Question\" Tab and Click OK. " + " Then Click on \"modify\" Button to further fill the details in \"Link answers\" tab");
		}

		if (question != null) {

			if (question.getQuestionType().equalsIgnoreCase("text box")) {
				Label lblLinkAnswersTabMessage = new Label(compositeForLinkAnswerTab, SWT.NONE);
				lblLinkAnswersTabMessage.setText("This type of question does not need to link answers");

			} else {
				Label lblQuestion_1 = new Label(compositeForLinkAnswerTab, SWT.NONE);
				lblQuestion_1.setText("Question:");

				Label qstnTxt = new Label(compositeForLinkAnswerTab, SWT.NONE);
				qstnTxt.setText(question.getQuestionText());

				/*
				 * ScrolledComposite scroll = new ScrolledComposite( compositeForLinkAnswerTab, SWT.BORDER|SWT.V_SCROLL); scroll.setExpandVertical(true);
				 * scroll.setExpandHorizontal(true); GridData gd_scroll = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1); gd_scroll.horizontalSpan=2; gd_scroll.heightHint =
				 * 150; gd_scroll.widthHint = 600; // scroll.setLayoutData(gd_scroll); // scroll.setLayout(new GridLayout(2, false));
				 */

				Composite compositeForAnswers = new Composite(compositeForLinkAnswerTab, SWT.NONE);
				compositeForAnswers.setLayout(new GridLayout(2, false));
				GridData gd_compositeForAnswers = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				gd_compositeForAnswers.horizontalSpan = 2;
				/*
				 * gd_compositeForAnswers.heightHint=100; gd_compositeForAnswers.widthHint=400;
				 */compositeForAnswers.setLayoutData(gd_compositeForAnswers);

				Label lblAnswers = new Label(compositeForAnswers, SWT.NONE);
				lblAnswers.setText("Answers");

				Label lblSelectQuestion = new Label(compositeForAnswers, SWT.NONE);
				lblSelectQuestion.setText("Jump to question");

				/*
				 * CompositeForAnswers answerComposite=new CompositeForAnswers(compositeForLinkAnswerTab,question,listOfAllQuestions); GridData gd_answerComposite = new
				 * GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1); gd_answerComposite.heightHint = 125; gd_answerComposite.widthHint = 520; gd_answerComposite.horizontalSpan=2;
				 * answerComposite.setLayoutData(gd_answerComposite); answerComposite.setLayout(new FillLayout(SWT.HORIZONTAL)); for(Answer answer: question.getAnswers()){
				 * answerComposite.addAnswerUIElements(answer); } //answerComposite.addAnswerUIElements();
				 */

				for (Answer answer : question.getAnswers()) {

					Text answerTxt = new Text(compositeForAnswers, SWT.BORDER);
					GridData gd_answerTxt = new GridData(SWT.FILL, SWT.NONE, false, false);
					gd_answerTxt.widthHint = 120;
					answerTxt.setLayoutData(gd_answerTxt);
					answerTxt.setEditable(false);

					answerTxt.setText(answer.getValue());

					/*
					 * Label lblCurrentAnswer = new Label(compositeForAnswers, SWT.NONE); lblCurrentAnswer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
					 * lblCurrentAnswer.setText(answer.getValue());
					 */
					Combo comboForLinkAnswers = new Combo(compositeForAnswers, SWT.DROP_DOWN);
					comboForLinkAnswers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
					comboForLinkAnswers.add("Default");
					for (int i = 0; i < listOfAllQuestions.size(); i++) {
						if (listOfAllQuestions.size() == 1) {
							comboForLinkAnswers.removeAll();
							comboForLinkAnswers.add("Please add more questions to link the answers");
						}
						if (question.getId() != listOfAllQuestions.get(i).getId()) {
							comboForLinkAnswers.add(listOfAllQuestions.get(i).getQuestionText());
						}

					}

					if (answer.getNextID() != -2) {
						if(answer.getNextID()==-1){
							comboForLinkAnswers.setText("Default");
						} else {
							for (Question question : listOfAllQuestions) {
								if (question.getId() == answer.getNextID()) {
									comboForLinkAnswers.setText(question.getQuestionText());
								}
							}
						}
					}					

					comboForLinkAnswers.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							if(comboForLinkAnswers.getText().equalsIgnoreCase("Default")){
								answer.setNextID(-1);
							} else {
								for (Question question : listOfAllQuestions) {
									if (question.getQuestionText().equalsIgnoreCase(comboForLinkAnswers.getText())) {
										System.out.println(question.getId());
										answer.setNextID(question.getId());
										System.out.println(answer.getNextID());
									}
								}
							}
						}

					});

				}
				// scroll.setContent(compositeForAnswers);
				//scroll.setExpandHorizontal(true);
				/*
				 * scroll.setContent(compositeForAnswers); scroll.setExpandHorizontal(true); scroll.setExpandVertical(true);
				 * //scroll.setMinSize(compositeForAnswers.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				 */
			}
		}

		TabItem tbtmLinkClaferFeatures = new TabItem(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setText("Link to variability constructs");

		Composite compositeForClaferTab = new Composite(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setControl(compositeForClaferTab);
		compositeForClaferTab.setLayout(new GridLayout(2, false));

		if (question == null) {
			linkFeaturesMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
			linkFeaturesMessageBox.setMessage(
				"Please at first completely fill the details of \"Question\" Tab and Click OK. " + " Then Click on \"modify\" Button to further fill the details in \"Link Features\" tab");
		}

		if (question != null) {

			if (question.getQuestionType().equalsIgnoreCase("text box")) {
				Label lblLinkFeatureTabMessage = new Label(compositeForClaferTab, SWT.NONE);
				lblLinkFeatureTabMessage.setText("This type of question does not need to link features");

			} else {
				Label lblQuestion_2 = new Label(compositeForClaferTab, SWT.NONE);
				lblQuestion_2.setText("Question:");

				Label qstnTxt_1 = new Label(compositeForClaferTab, SWT.None);
				qstnTxt_1.setText(question.getQuestionText());

				Composite compositeForAnswers1 = new Composite(compositeForClaferTab, SWT.None);
				compositeForAnswers1.setLayout(new GridLayout(5, false));
				GridData gd_compositeAnswers = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				gd_compositeAnswers.horizontalSpan = 2;
				compositeForAnswers1.setLayoutData(gd_compositeAnswers);

				Label lblEmpty = new Label(compositeForAnswers1, SWT.NONE);
				lblEmpty.setText("Answers");

				Label lblForAlgorithm = new Label(compositeForAnswers1, SWT.NONE);
				lblForAlgorithm.setText("Variability construct");

				Label lblForOperand = new Label(compositeForAnswers1, SWT.NONE);
				lblForOperand.setText("Property");

				Label lblForValue = new Label(compositeForAnswers1, SWT.NONE);
				lblForValue.setText("Operator");

				Label lblForOperator = new Label(compositeForAnswers1, SWT.NONE);
				lblForOperator.setText("Set Value");

				//claferFeatures = getClaferFeatures();

				for (Answer answer : question.getAnswers()) {

					Text txtBoxCurrentAnswer = new Text(compositeForAnswers1, SWT.BORDER);
					GridData gd_txtBoxCurrentAnswer = new GridData(SWT.FILL, SWT.NONE, false, false);
					gd_txtBoxCurrentAnswer.widthHint = 120;
					txtBoxCurrentAnswer.setLayoutData(gd_txtBoxCurrentAnswer);
					txtBoxCurrentAnswer.setEditable(false);
					txtBoxCurrentAnswer.setText(answer.getValue());

					Combo comboForAlgorithm = new Combo(compositeForAnswers1, SWT.NONE);
					GridData gd_comboForAlgorithm = new GridData(SWT.FILL, SWT.NONE, true, true);
					gd_comboForAlgorithm.widthHint = 130;
					comboForAlgorithm.setLayoutData(gd_comboForAlgorithm);
					comboForAlgorithm.setVisible(true);
					comboForAlgorithm.add("none");

					for (int i = 0; i < claferFeatures.size(); i++) {
						comboForAlgorithm.add(claferFeatures.get(i).getFeatureName());
					}

					Combo comboForOperand = new Combo(compositeForAnswers1, SWT.NONE);
					comboForOperand.setVisible(true);
					GridData gd_comboForOperand = new GridData(SWT.FILL, SWT.NONE, true, true);
					gd_comboForOperand.widthHint = 130;
					comboForOperand.setLayoutData(gd_comboForOperand);

					//comboForOperand.add("none");

					Combo comboForOperator = new Combo(compositeForAnswers1, SWT.NONE);
					comboForOperator.setVisible(true);
					comboForOperator.add("none");
					GridData gd_Operator = new GridData(SWT.FILL, SWT.NONE, true, true);
					gd_Operator.widthHint = 100;
					comboForOperator.setLayoutData(gd_Operator);
					/*
					 * comboForOperator.setItems(Constants.FeatureConstraintRelationship.EQUAL.toString(), Constants.FeatureConstraintRelationship.NOTEQUAL.toString(),
					 * Constants.FeatureConstraintRelationship.LESSTHAN.toString(), Constants.FeatureConstraintRelationship.GREATERTHAN.toString(),
					 * Constants.FeatureConstraintRelationship.LESSTHANEQUALTO.toString(), Constants.FeatureConstraintRelationship.GREATERTHANEQUALTO.toString(),
					 * Constants.FeatureConstraintRelationship.AND.toString(), Constants.FeatureConstraintRelationship.OR.toString());
					 */

					comboForOperator.setItems("Equal" + "(" + Constants.FeatureConstraintRelationship.EQUAL.toString() + ")",
						"NOTEQUAL" + "(" + Constants.FeatureConstraintRelationship.NOTEQUAL.toString() + ")",
						"LESSTHAN" + "(" + Constants.FeatureConstraintRelationship.LESSTHAN.toString() + ")",
						"GREATERTHAN" + "(" + Constants.FeatureConstraintRelationship.GREATERTHAN.toString() + ")",
						"LESSTHANEQUALTO" + "(" + Constants.FeatureConstraintRelationship.LESSTHANEQUALTO.toString() + ")",
						"GREATERTHANEQUALTO" + "(" + Constants.FeatureConstraintRelationship.GREATERTHANEQUALTO.toString() + ")",
						"AND" + "(" + Constants.FeatureConstraintRelationship.AND.toString() + ")", "OR" + "(" + Constants.FeatureConstraintRelationship.OR.toString() + ")");

					Text txtBoxValue = new Text(compositeForAnswers1, SWT.BORDER);
					GridData gd_txtBoxValue = new GridData(SWT.FILL, SWT.NONE, true, true);
					gd_txtBoxValue.widthHint = 140;
					txtBoxValue.setLayoutData(gd_txtBoxValue);
					txtBoxValue.setVisible(true);

					ClaferDependency claferDependency = new ClaferDependency();

					if (answer.getClaferDependencies() != null) {
						for (ClaferDependency cf : answer.getClaferDependencies()) {
							if (cf.getAlgorithm() != null) {
								comboForAlgorithm.setText(cf.getAlgorithm());
								claferDependency.setAlgorithm(comboForAlgorithm.getText());
							}
							if (cf.getOperand() != null) {
								comboForOperand.setText(cf.getOperand());
								claferDependency.setOperand(comboForOperand.getText());
							}
							if (cf.getOperator() != null) {
								comboForOperator.setText(cf.getOperator());
								claferDependency.setOperator(comboForOperator.getText());
							}
							if (cf.getValue() != null) {
								txtBoxValue.setText(cf.getValue());
								claferDependency.setValue(txtBoxValue.getText());
							}
						}
					}
					//adding the items to comboForOperand box depending on the comboForAlgorithm box value 
					if (comboForAlgorithm.getText() != null) {
						operandItems = new ArrayList<String>();
						ArrayList<String> operandToAdd = itemsToAdd(comboForAlgorithm.getText());
						for (int i = 0; i < operandToAdd.size(); i++) {
							comboForOperand.add(operandToAdd.get(i));
						}

					}

					comboForAlgorithm.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							featureSelected = comboForAlgorithm.getText();
							comboForOperand.removeAll();
							operandItems = new ArrayList<String>();
							ArrayList<String> operandToAdd = itemsToAdd(featureSelected);
							for (int i = 0; i < operandToAdd.size(); i++) {
								comboForOperand.add(operandToAdd.get(i));
							}
							//to remove the previous operand selected of comboForOperand as value of comboForlgorithm is changed
							if (answer.getClaferDependencies() != null) {
								comboForOperand.setText("");
								claferDependency.setOperand(comboForOperand.getText());

							}
							claferDependency.setAlgorithm(featureSelected);
							//algorithmChanged=true;
						}
					});
					comboForOperand.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							claferDependency.setOperand(comboForOperand.getText());
						}
					});
					comboForOperator.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							claferDependency.setOperator(comboForOperator.getText());
						}
					});

					txtBoxValue.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(FocusEvent e) {
							claferDependency.setValue(txtBoxValue.getText());
						}
					});

					ArrayList<ClaferDependency> listOfClaferDependencies = new ArrayList<ClaferDependency>();
					listOfClaferDependencies.add(claferDependency);
					answer.setClaferDependencies(listOfClaferDependencies);
				}
			}
		}

		TabItem tbtmLink = new TabItem(tabFolder, SWT.NONE);
		tbtmLink.setText("Link to variables to use in code");
		
		Composite compositeForLinkCodeTab=new Composite(tabFolder,SWT.None);
		tbtmLink.setControl(compositeForLinkCodeTab);
		compositeForLinkCodeTab.setLayout(new GridLayout(2, false));

		if (question == null) {
			linkCodeMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
			linkCodeMessageBox.setMessage(
				"Please at first completely fill the details of \"Question\" Tab and Click OK. " + " Then Click on \"modify\" Button to further fill the details in \"Link Code\" tab");
		}

		if (question != null) {

			if (question.getQuestionType().equalsIgnoreCase("text box")) {
				Label question_3 = new Label(compositeForLinkCodeTab, SWT.None);
				question_3.setText("Question: ");

				Label question_3Txt = new Label(compositeForLinkCodeTab, SWT.None);
				question_3Txt.setText(question.getQuestionText());

				Label lblOption = new Label(compositeForLinkCodeTab, SWT.None);
				lblOption.setText("Set Name");

				Label lblText = new Label(compositeForLinkCodeTab, SWT.NONE);
				lblText.setText("Set Value");

				Text txtOption = new Text(compositeForLinkCodeTab, SWT.BORDER);
				txtOption.setVisible(true);
				GridData gd_txtOption = new GridData(/* SWT.FILL, SWT.CENTER, true, true */);
				gd_txtOption.widthHint = 100;
				txtOption.setLayoutData(gd_txtOption);

				Text txtValue = new Text(compositeForLinkCodeTab, SWT.BORDER);
				txtValue.setVisible(true);
				GridData gd_txtValue = new GridData(/* SWT.FILL, SWT.CENTER, true, true */);
				gd_txtValue.widthHint = 100;
				txtValue.setLayoutData(gd_txtValue);

				CodeDependency codeDependency = new CodeDependency();
				for (Answer answer : question.getAnswers()) {
					if (answer.getCodeDependencies() != null) {
						for (CodeDependency cd : answer.getCodeDependencies()) {
							if (cd.getOption() != null) {
								txtOption.setText(cd.getOption());
								codeDependency.setOption(txtOption.getText());
							}
							if (cd.getValue() != null) {
								txtValue.setText(cd.getValue());
								codeDependency.setValue(txtValue.getText());
							}
						}
					}

					txtOption.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(FocusEvent e) {
							codeDependency.setOption(txtOption.getText());
						}
					});

					txtValue.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(FocusEvent e) {
							codeDependency.setValue(txtValue.getText());
						}
					});

					ArrayList<CodeDependency> codeDependencies = new ArrayList<CodeDependency>();
					codeDependencies.add(codeDependency);

					answer.setCodeDependencies(codeDependencies);

				}
			} else {
				Label question_3 = new Label(compositeForLinkCodeTab, SWT.None);
				question_3.setText("Question: ");

				Label question_3Txt = new Label(compositeForLinkCodeTab, SWT.None);
				question_3Txt.setText(question.getQuestionText());

				Composite answersCompositeToLinkCode = new Composite(compositeForLinkCodeTab, SWT.None);
				answersCompositeToLinkCode.setLayout(new GridLayout(3, false));
				GridData gd_LinkCode = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				answersCompositeToLinkCode.setLayoutData(gd_LinkCode);

				Label lblAnswersLink = new Label(answersCompositeToLinkCode, SWT.None);
				lblAnswersLink.setText("Answers");

				Label lblOption = new Label(answersCompositeToLinkCode, SWT.None);
				lblOption.setText("Set Name");

				Label lblText = new Label(answersCompositeToLinkCode, SWT.NONE);
				lblText.setText("Set Value");

				for (Answer answer : question.getAnswers()) {

					Text txtBoxAnswers = new Text(answersCompositeToLinkCode, SWT.BORDER);
					GridData gd_txtBoxAnswers = new GridData(SWT.FILL, SWT.CENTER, true, true);
					gd_txtBoxAnswers.widthHint = 120;
					txtBoxAnswers.setLayoutData(gd_txtBoxAnswers);
					txtBoxAnswers.setEditable(false);
					txtBoxAnswers.setText(answer.getValue());

					Text txtOption = new Text(answersCompositeToLinkCode, SWT.BORDER);
					txtOption.setVisible(true);
					GridData gd_txtOption = new GridData(SWT.FILL, SWT.CENTER, true, true);
					gd_txtOption.widthHint = 100;
					txtOption.setLayoutData(gd_txtOption);

					Text txtValue = new Text(answersCompositeToLinkCode, SWT.BORDER);
					txtValue.setVisible(true);
					GridData gd_txtValue = new GridData(SWT.FILL, SWT.CENTER, true, true);
					gd_txtValue.widthHint = 100;
					txtValue.setLayoutData(gd_txtValue);

					CodeDependency codeDependency = new CodeDependency();

					if (answer.getCodeDependencies() != null) {
						for (CodeDependency cd : answer.getCodeDependencies()) {
							if (cd.getOption() != null) {
								txtOption.setText(cd.getOption());
								codeDependency.setOption(txtOption.getText());
							}
							if (cd.getValue() != null) {
								txtValue.setText(cd.getValue());
								codeDependency.setValue(txtValue.getText());
							}
						}
					}

					txtOption.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(FocusEvent e) {
							codeDependency.setOption(txtOption.getText());
						}
					});

					txtValue.addFocusListener(new FocusAdapter() {

						@Override
						public void focusLost(FocusEvent e) {
							codeDependency.setValue(txtValue.getText());
						}
					});

					ArrayList<CodeDependency> codeDependencies = new ArrayList<CodeDependency>();
					codeDependencies.add(codeDependency);
					answer.setCodeDependencies(codeDependencies);

				}

			}
		}
		return container;
	}

	private ArrayList<String> itemsToAdd(String featureSelected) {
		for (ClaferFeature claferFeature : claferFeatures) {
			if (claferFeature.getFeatureName().equalsIgnoreCase(featureSelected)) {
				System.out.println(featureSelected);
				for (FeatureProperty featureProperty : claferFeature.getfeatureProperties()) {
					operandItems.add(featureProperty.getPropertyName());
				}
				if (claferFeature.getFeatureInheritance() != null) {
					//FeatureProperty inheritProperty = claferFeature.getFeatureInheritsFromForAbstract();
					featureSelected = claferFeature.getFeatureInheritance();
					itemsToAdd(featureSelected);
				}
			}
		}
		return operandItems;
	}

	private ArrayList<ClaferFeature> getClaferFeatures() {
		ClaferFeature algorithm = new ClaferFeature(Constants.FeatureType.ABSTRACT, "algorithm", // Counter as the name to make each addition identifiable.
			null);

		algorithm.getfeatureProperties().add(new FeatureProperty("name", "string"));
		algorithm.getfeatureProperties().add(new FeatureProperty("description", "string"));
		algorithm.getfeatureProperties().add(new FeatureProperty("security", "Security"));
		algorithm.getfeatureProperties().add(new FeatureProperty("performance", "Performance"));
		algorithm.getfeatureProperties().add(new FeatureProperty("classPerformance", "Performance"));

		ClaferFeature cipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "cipher", // Counter as the name to make each addition identifiable.
			"algorithm");

		ClaferFeature symmetricCipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "symmetricCipher", // Counter as the name to make each addition identifiable.
			"cipher");

		symmetricCipher.getfeatureProperties().add(new FeatureProperty("keySize", "integer"));
		symmetricCipher.getFeatureConstraints().add(new ClaferConstraint("classPerformance = Fast"));

		ClaferFeature symmetricBlockCipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "symmetricBlockCipher", // Counter as the name to make each addition identifiable.
			"symmetricCipher");

		symmetricBlockCipher.getfeatureProperties().add(new FeatureProperty("mode", "Mode"));
		symmetricBlockCipher.getfeatureProperties().add(new FeatureProperty("padding", "Padding"));
		symmetricBlockCipher.getFeatureConstraints().add(new ClaferConstraint("mode !=ECB"));
		symmetricBlockCipher.getFeatureConstraints().add(new ClaferConstraint("padding !=NoPadding"));

		ClaferFeature AES = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", // Counter as the name to make each addition identifiable.
			"symmetricBlockCipher");
		AES.getFeatureConstraints().add(new ClaferConstraint("description = Advanced Encryption Standard (AES) cipher"));
		AES.getFeatureConstraints().add(new ClaferConstraint("name = AES"));
		AES.getFeatureConstraints().add(new ClaferConstraint("keySize = 128 || keySize = 192 || keySize = 256"));
		AES.getFeatureConstraints().add(new ClaferConstraint("keySize = 128 => performance = VeryFast && security = Medium"));
		AES.getFeatureConstraints().add(new ClaferConstraint("keySize > 128 => performance = Fast && security = Strong"));

		claferFeatures.add(algorithm);
		claferFeatures.add(cipher);
		claferFeatures.add(symmetricCipher);
		claferFeatures.add(symmetricBlockCipher);
		claferFeatures.add(AES);

		return claferFeatures;

	}

	//to save the question text and type
	private void saveInput() {
		setQuestionDetails();
		//claferFeatures.clear();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText() {
		textQuestion.setText(question.getQuestionText());
	}

	public String getquestionType() {
		return questionType;
	}

	public void setQuestionType(String type) {
		combo.setText(type);

	}

	//Saving question details
	public void setQuestionDetails() {
		Question questionDetails = new Question();
		questionDetails.setQuestionText(textQuestion.getText());
		questionDetails.setQuestionType(combo.getText());
		for (int i = 0; i < compositeToHoldAnswers.getListOfAllAnswer().size(); i++) {
			if (Objects.equals(compositeToHoldAnswers.getListOfAllAnswer().get(i).getValue(), null)) {
				compositeToHoldAnswers.deleteAnswer(compositeToHoldAnswers.getListOfAllAnswer().get(i));
				compositeToHoldAnswers.updateAnswerContainer();
				i--;
			}
		}
		//compositeToHoldAnswers.getListOfAllAnswer()
		questionDetails.setAnswers(compositeToHoldAnswers.getListOfAllAnswer());
		if (question != null) {
			System.out.println(question.getQuestionText());
			for (Answer answer : question.getAnswers()) {
				//answer.setClaferDependencies()
			}
		}
		//questionDetails.set
		this.questionDetails = questionDetails;
	}

	public Question getQuestionDetails() {
		return this.questionDetails;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(559, 351);
	}

}
