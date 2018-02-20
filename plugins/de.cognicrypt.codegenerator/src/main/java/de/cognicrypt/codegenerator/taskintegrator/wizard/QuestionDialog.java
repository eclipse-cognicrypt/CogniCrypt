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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
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
	private ClaferModel claferModel;
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

	public QuestionDialog(Shell parentShell, Question question, ClaferModel claferModel, ArrayList<Question> listOfAllQuestions) {
		super(parentShell);
		this.question = question;
		this.claferModel = claferModel;
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
		getShell().setMinimumSize(900, 400);
		

		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				if (tabFolder.getSelectionIndex() == 1) {
					if(question==null){
						linkFeaturesMessageBox.open();
					}
				}

				if (tabFolder.getSelectionIndex() == 2) {
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

		combo = new Combo(composite, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.select(-1);
		combo.setItems(Constants.dropDown, Constants.textBox, Constants.radioButton);

		Button btnAddAnswer = new Button(composite, SWT.None);
		btnAddAnswer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnAddAnswer.setText("Add Answer");
		//Visibility depends on question type
		btnAddAnswer.setVisible(false);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		boolean showRemoveButton = true;
		compositeToHoldAnswers = new CompositeToHoldSmallerUIElements(composite, SWT.NONE, null, showRemoveButton, null);
		GridData gd_compositeToHoldAnswers = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_compositeToHoldAnswers.heightHint = 135;
		gd_compositeToHoldAnswers.widthHint = 890;
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
					case Constants.textBox:
						btnAddAnswer.setVisible(false);
						compositeToHoldAnswers.setVisible(false);
						compositeToHoldAnswers.getListOfAllAnswer().clear();
						compositeToHoldAnswers.updateAnswerContainer();
						Answer emptyAnswer=new Answer();
						emptyAnswer.setDefaultAnswer(true);
						emptyAnswer.setValue("");
						compositeToHoldAnswers.getListOfAllAnswer().add(emptyAnswer);
						currentQuestionType = Constants.textBox;
						break;
					case Constants.dropDown:
						boolean comboSelected = combo.getText().equalsIgnoreCase(Constants.dropDown) ? true : false;
						btnAddAnswer.setVisible(comboSelected);
						if (!currentQuestionType.equalsIgnoreCase(Constants.dropDown)) {
							compositeToHoldAnswers.getListOfAllAnswer().clear();
							compositeToHoldAnswers.updateAnswerContainer();
							compositeToHoldAnswers.setVisible(false);
							currentQuestionType = Constants.dropDown;
						}
						break;
					case Constants.radioButton:
						boolean buttonSelected = combo.getText().equalsIgnoreCase(Constants.radioButton) ? true : false;
						btnAddAnswer.setVisible(buttonSelected);
						if (!currentQuestionType.equalsIgnoreCase(Constants.radioButton)) {
							compositeToHoldAnswers.getListOfAllAnswer().clear();
							compositeToHoldAnswers.updateAnswerContainer();
							compositeToHoldAnswers.setVisible(false);
							currentQuestionType = Constants.radioButton;
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
			if (question.getQuestionType().equalsIgnoreCase(Constants.textBox)) {
				compositeToHoldAnswers.setVisible(false);
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
				"Please at first completely fill the details of \"Question\" Tab and Click OK. " + " Then Click on \"modify\" Button to further fill the details in \"Link to variability constructs\" tab");
		}

		if (question != null) {

			if (question.getQuestionType().equalsIgnoreCase(Constants.textBox)) {
				Label lblLinkFeatureTabMessage = new Label(compositeForClaferTab, SWT.NONE);
				lblLinkFeatureTabMessage.setText("This type of question does not need to link features");

			} else {
				Label lblQuestion_2 = new Label(compositeForClaferTab, SWT.NONE);
				lblQuestion_2.setText("Question:");

				Label qstnTxt_1 = new Label(compositeForClaferTab, SWT.None);
				qstnTxt_1.setText(question.getQuestionText());
				
				//Group containing the headers
				Group groupHeaderClaferTab = new Group(compositeForClaferTab, SWT.NONE);
				GridData gd_groupHeaderClaferTab = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				groupHeaderClaferTab.setLayoutData(gd_groupHeaderClaferTab);

				Label ansLabel = new Label(groupHeaderClaferTab, SWT.NONE);
				ansLabel.setBounds(5, 5, 130, 25);
				ansLabel.setText("Answers");

				Label lblForAlgorithm = new Label(groupHeaderClaferTab, SWT.NONE);
				lblForAlgorithm.setBounds(240, 5, 135, 25);
				lblForAlgorithm.setText("Variability construct");

				Label lblForOperand = new Label(groupHeaderClaferTab, SWT.NONE);
				lblForOperand.setBounds(410, 5, 100, 25);
				lblForOperand.setText("Property");

				Label lblForValue = new Label(groupHeaderClaferTab, SWT.NONE);
				lblForValue.setBounds(520, 5, 130, 25);
				lblForValue.setText("Operator");

				Label lblForOperator = new Label(groupHeaderClaferTab, SWT.NONE);
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

		if (question == null) {
			linkCodeMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
			linkCodeMessageBox.setMessage(
				"Please at first completely fill the details of \"Question\" Tab and Click OK. " + " Then Click on \"modify\" Button to further fill the details in \"Link to variables to use in code\" tab");
		}

		if (question != null) {

			if (question.getQuestionType().equalsIgnoreCase(Constants.textBox)) {
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
				gd_txtOption.widthHint = 200;
				txtOption.setLayoutData(gd_txtOption);
				if(question.getQuestionText().length()>20){
					txtOption.setText(question.getQuestionText().substring(0, 30)+"....");	
				}else{
					txtOption.setText(question.getQuestionText());
				}
				txtOption.setEditable(false);

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

				// Group answersCompositeToLinkCode containing the headers of the table
				Group answersCompositeToLinkCode = new Group(compositeForLinkCodeTab, SWT.NONE);
				GridData gd_answersCompositeToLinkCode = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
				answersCompositeToLinkCode.setLayoutData(gd_answersCompositeToLinkCode);

				Label lblAnswersLink = new Label(answersCompositeToLinkCode, SWT.None);
				lblAnswersLink.setBounds(5, 5, 210, 25);
				lblAnswersLink.setText("Answers");

				Label lblOption = new Label(answersCompositeToLinkCode, SWT.None);
				lblOption.setBounds(225, 5, 200, 25);
				lblOption.setText("Set Name");

				Label lblText = new Label(answersCompositeToLinkCode, SWT.NONE);
				lblText.setBounds(430, 5, 200, 25);
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

	private ArrayList<String> itemsToAdd(String featureSelected) {
		for (ClaferFeature claferFeature : claferModel) {
			if (claferFeature.getFeatureName().equalsIgnoreCase(featureSelected)) {
				System.out.println(featureSelected);
				for (FeatureProperty featureProperty : claferFeature.getFeatureProperties()) {
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

	private ClaferModel getClaferFeatures() {
		ClaferFeature algorithm = new ClaferFeature(Constants.FeatureType.ABSTRACT, "algorithm", // Counter as the name to make each addition identifiable.
			null);

		algorithm.getFeatureProperties().add(new FeatureProperty("name", "string"));
		algorithm.getFeatureProperties().add(new FeatureProperty("description", "string"));
		algorithm.getFeatureProperties().add(new FeatureProperty("security", "Security"));
		algorithm.getFeatureProperties().add(new FeatureProperty("performance", "Performance"));
		algorithm.getFeatureProperties().add(new FeatureProperty("classPerformance", "Performance"));

		ClaferFeature cipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "cipher", // Counter as the name to make each addition identifiable.
			"algorithm");

		ClaferFeature symmetricCipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "symmetricCipher", // Counter as the name to make each addition identifiable.
			"cipher");

		symmetricCipher.getFeatureProperties().add(new FeatureProperty("keySize", "integer"));
		symmetricCipher.getFeatureConstraints().add(new ClaferConstraint("classPerformance = Fast"));

		ClaferFeature symmetricBlockCipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "symmetricBlockCipher", // Counter as the name to make each addition identifiable.
			"symmetricCipher");

		symmetricBlockCipher.getFeatureProperties().add(new FeatureProperty("mode", "Mode"));
		symmetricBlockCipher.getFeatureProperties().add(new FeatureProperty("padding", "Padding"));
		symmetricBlockCipher.getFeatureConstraints().add(new ClaferConstraint("mode !=ECB"));
		symmetricBlockCipher.getFeatureConstraints().add(new ClaferConstraint("padding !=NoPadding"));

		ClaferFeature AES = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", // Counter as the name to make each addition identifiable.
			"symmetricBlockCipher");
		AES.getFeatureConstraints().add(new ClaferConstraint("description = Advanced Encryption Standard (AES) cipher"));
		AES.getFeatureConstraints().add(new ClaferConstraint("name = AES"));
		AES.getFeatureConstraints().add(new ClaferConstraint("keySize = 128 || keySize = 192 || keySize = 256"));
		AES.getFeatureConstraints().add(new ClaferConstraint("keySize = 128 => performance = VeryFast && security = Medium"));
		AES.getFeatureConstraints().add(new ClaferConstraint("keySize > 128 => performance = Fast && security = Strong"));

		claferModel.add(algorithm);
		claferModel.add(cipher);
		claferModel.add(symmetricCipher);
		claferModel.add(symmetricBlockCipher);
		claferModel.add(AES);

		return claferModel;

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
		setQuestionElement(questionDetails, combo.getText());

		//this loop executes to delete empty text boxes in the question dialog
		for (int i = 0; i < compositeToHoldAnswers.getListOfAllAnswer().size(); i++) {
			if (Objects.equals(compositeToHoldAnswers.getListOfAllAnswer().get(i).getValue(), null)) {
				compositeToHoldAnswers.deleteAnswer(compositeToHoldAnswers.getListOfAllAnswer().get(i));
				compositeToHoldAnswers.updateAnswerContainer();
				i--;
			}
		}
		
		questionDetails.setAnswers(compositeToHoldAnswers.getListOfAllAnswer());
		checkQuestionHasDefaultAnswer(questionDetails);
		this.questionDetails = questionDetails;

	}

	/**
	 * sets the question element depending on the question type selected
	 * @param question
	 * @param element the value selected for the question type 
	 */
	private void setQuestionElement(Question question, String element) {
		/**
		 * case 1: if the the question type is selected as drop down then sets the element to combo
		 */
		if (element.equals(Constants.dropDown)) {
			question.setElement(Constants.GUIElements.combo);
		}
		/**
		 * case 2: sets the question element to text if the question type is text box
		 */
		else if (element.equals(Constants.textBox)) {
			question.setElement(Constants.GUIElements.text);
		}
		/**
		 * case 3: sets the question element to text if the question type is radio button
		 */
		else if (element.equals(Constants.radioButton)) {
			question.setElement(Constants.GUIElements.button);
		}
	}

	/**
	 * checks if for the question default answer is selected or not if no answer is selected as default answer then the function sets the first answer as the default answer of the
	 * particular question
	 */
	public void checkQuestionHasDefaultAnswer(Question question) {
		boolean hasDefaultAnswer = false;
		for (Answer answer : question.getAnswers()) {
			if (answer.isDefaultAnswer()) {
				hasDefaultAnswer = true;
			}
		}
		if (!hasDefaultAnswer) {
			if (question.getAnswers().size() > 0) {
				question.getAnswers().get(0).setDefaultAnswer(true);
			}
		}
	}

	/**
	 * 
	 * @return the question
	 */
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
