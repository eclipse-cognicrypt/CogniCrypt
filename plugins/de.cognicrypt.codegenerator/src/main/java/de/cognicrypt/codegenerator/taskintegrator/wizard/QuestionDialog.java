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
			if (Objects.equals(compositeToHoldAnswers.getListOfAllAnswer().get(i).getValue(), null) || Objects.equals(compositeToHoldAnswers.getListOfAllAnswer().get(i).getValue(),
				"")) {
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
