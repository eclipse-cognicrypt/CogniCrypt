package crossing.e1.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.GUIElements;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.ClaferDependency;
import crossing.e1.configurator.beginer.question.CodeDependency;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.FeatureProperty;
import crossing.e1.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import crossing.e1.taskintegrator.widgets.CompositeToHoldSmallerUIElements;
import crossing.e1.taskintegrator.widgets.GroupAnswer;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;

public class QuestionDialog extends Dialog {

	public Text textQuestion;
	private Label lblQuestionContent;
	private String questionText;
	private String questionType;
	private Combo combo;
	private CompositeToHoldSmallerUIElements compositeToHoldAnswers;
	private Question question;
	private Question questionDetails;
	//private CompositeToHoldGranularUIElements compositeToHoldGranularUIElements ;
	private ArrayList<ClaferFeature> claferFeatures;
	int counter = 0;
	private String featureSelected;
	private ArrayList<String> operandItems;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public QuestionDialog(Shell parentShell, Question question, ArrayList<ClaferFeature> claferFeatures) {
		super(parentShell);
		this.question = question;
		this.claferFeatures = claferFeatures;
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
		lblType.setText("Type");

		combo = new Combo(composite, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.setItems(new String[] { Constants.GUIElements.combo.toString(), Constants.GUIElements.text.toString(), Constants.GUIElements.itemselection
			.toString(), Constants.GUIElements.button.toString() });
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

		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				switch (combo.getText()) {
					case "text":
						btnAddAnswer.setVisible(false);
						break;
					case "combo":
						boolean comboSelected = combo.getText().equalsIgnoreCase("combo") ? true : false;
						btnAddAnswer.setVisible(comboSelected);
						break;
					case "itemselection":
						boolean itemSelected = combo.getText().equalsIgnoreCase("itemselection") ? true : false;
						btnAddAnswer.setVisible(itemSelected);
						break;
					case "button":
						boolean buttonSelected = combo.getText().equalsIgnoreCase("button") ? true : false;
						btnAddAnswer.setVisible(buttonSelected);
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

		}
		
				
		TabItem tbtmLinkAnswers = new TabItem(tabFolder, SWT.NONE);
		tbtmLinkAnswers.setText("Link answers");
		

		Composite compositeForLinkAnswerTab = new Composite(tabFolder, SWT.NONE);
		tbtmLinkAnswers.setControl(compositeForLinkAnswerTab);
		compositeForLinkAnswerTab.setLayout(new GridLayout(2, false));
				
		if (question != null) {

			Label lblQuestion_1 = new Label(compositeForLinkAnswerTab, SWT.NONE);
			lblQuestion_1.setText("Question:");

			Label qstnTxt = new Label(compositeForLinkAnswerTab, SWT.NONE);
			qstnTxt.setText(question.getQuestionText());

			Label lblAnswers = new Label(compositeForLinkAnswerTab, SWT.NONE);
			lblAnswers.setText("Answers:");

			Composite compositeForAnswers = new Composite(compositeForLinkAnswerTab, SWT.NONE);
			compositeForAnswers.setLayout(new GridLayout(2, false));
			for (Answer answer : question.getAnswers()) {
				Label lblCurrentAnswer = new Label(compositeForAnswers, SWT.NONE);
				lblCurrentAnswer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				lblCurrentAnswer.setText(answer.getValue());

				Combo combo = new Combo(compositeForAnswers, SWT.DROP_DOWN);
				combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				combo.add("Default");
				for (int i = 1; i <= 5; i++) {
					if (question.getId() != i)
						combo.add("Link to Question"+" "+i);
				}
			}
		}

		TabItem tbtmLinkClaferFeatures = new TabItem(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setText("Link Clafer features");

		Composite compositeForClaferTab = new Composite(tabFolder, SWT.NONE);
		tbtmLinkClaferFeatures.setControl(compositeForClaferTab);
		compositeForClaferTab.setLayout(new GridLayout(2, false));
	

		if (question != null) {
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
			lblForAlgorithm.setText("Select Algorithm");

			Label lblForOperand = new Label(compositeForAnswers1, SWT.NONE);
			lblForOperand.setText("Select Operand");

			Label lblForValue = new Label(compositeForAnswers1, SWT.NONE);
			lblForValue.setText("Select Operator");

			Label lblForOperator = new Label(compositeForAnswers1, SWT.NONE);
			lblForOperator.setText("Set Value");

			claferFeatures = getClaferFeatures();

			for (Answer answer : question.getAnswers()) {

				Label lblCurrentAnswers = new Label(compositeForAnswers1, SWT.NONE);
				lblCurrentAnswers.setText(answer.getValue());

				Combo comboForAlgorithm = new Combo(compositeForAnswers1, SWT.NONE);
				comboForAlgorithm.setVisible(true);
				for (int i = 0; i < claferFeatures.size(); i++) {
					comboForAlgorithm.add(claferFeatures.get(i).getFeatureName());
					;
				}

				Combo comboForOperand = new Combo(compositeForAnswers1, SWT.NONE);
				comboForOperand.setVisible(true);

				Combo comboForOperator = new Combo(compositeForAnswers1, SWT.NONE);
				comboForOperator.setVisible(true);
				GridData gd_Operator = new GridData(SWT.FILL,SWT.NONE, true, true);			
				comboForOperator.setLayoutData(gd_Operator);
				comboForOperator.setItems(Constants.FeatureConstraintRelationship.EQUAL.toString(), Constants.FeatureConstraintRelationship.NOTEQUAL.toString(),
					Constants.FeatureConstraintRelationship.LESSTHAN.toString(), Constants.FeatureConstraintRelationship.GREATERTHAN.toString(),
					Constants.FeatureConstraintRelationship.LESSTHANEQUALTO.toString(), Constants.FeatureConstraintRelationship.GREATERTHANEQUALTO.toString(),
					Constants.FeatureConstraintRelationship.AND.toString(),Constants.FeatureConstraintRelationship.OR.toString());
				
				/*comboForOperator.setItems("Equal"+"("+Constants.FeatureConstraintRelationship.EQUAL.toString()+")", "NOTEQUAL"+"("+Constants.FeatureConstraintRelationship.NOTEQUAL.toString()+")",
					"LESSTHAN"+"("+Constants.FeatureConstraintRelationship.LESSTHAN.toString()+")", "GREATERTHAN"+"("+Constants.FeatureConstraintRelationship.GREATERTHAN.toString()+")",
					"LESSTHANEQUALTO"+"("+Constants.FeatureConstraintRelationship.LESSTHANEQUALTO.toString()+")", "GREATERTHANEQUALTO"+"("+Constants.FeatureConstraintRelationship.GREATERTHANEQUALTO.toString()+")",
					"AND"+"("+Constants.FeatureConstraintRelationship.AND.toString()+")","OR"+"("+Constants.FeatureConstraintRelationship.OR.toString()+")");
				*/
				
				Text txtBoxValue=new Text(compositeForAnswers1,SWT.BORDER);
				txtBoxValue.setVisible(true);
				
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
					}
				});
			}
		}

		TabItem tbtmLink = new TabItem(tabFolder, SWT.NONE);
		tbtmLink.setText("Link code");
		
		Composite compositeForLinkCodeTab=new Composite(tabFolder,SWT.None);
		tbtmLink.setControl(compositeForLinkCodeTab);
		compositeForLinkCodeTab.setLayout(new GridLayout(2,false));
		
		if (question!=null){
			Label question_3=new Label(compositeForLinkCodeTab,SWT.None);
			question_3.setText("Question: ");
			
			Label question_3Txt=new Label(compositeForLinkCodeTab,SWT.None);
			question_3Txt.setText(question.getQuestionText());
			
			Composite answersCompositeToLinkCode= new Composite(compositeForLinkCodeTab,SWT.None);
			answersCompositeToLinkCode.setLayout(new GridLayout(3,false));
			GridData gd_LinkCode=new GridData(SWT.LEFT,SWT.CENTER,true,false,2,1);
			answersCompositeToLinkCode.setLayoutData(gd_LinkCode);
			
			Label lblAnswersLink=new Label(answersCompositeToLinkCode,SWT.None);
			lblAnswersLink.setText("Answers");
			
			Label lblOption = new Label(answersCompositeToLinkCode,SWT.None);
			lblOption.setText("Set Option");
			
			Label lblText =new Label(answersCompositeToLinkCode,SWT.NONE);
			lblText.setText("Set Value");
			
			for(Answer answer:question.getAnswers()){
				Label lblLinkAnswers=new Label(answersCompositeToLinkCode,SWT.None);
				lblLinkAnswers.setText(answer.getValue());
				
				Text txtOption=new Text(answersCompositeToLinkCode,SWT.BORDER);
				txtOption.setVisible(true);
				
				Text txtValue=new Text(answersCompositeToLinkCode,SWT.BORDER);
				txtValue.setVisible(true);			
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
				if (claferFeature.getFeatureInheritsFromForAbstract() != null) {
					FeatureProperty inheritProperty = claferFeature.getFeatureInheritsFromForAbstract();
					featureSelected = inheritProperty.getPropertyName();
					itemsToAdd(featureSelected);
				}
			}
		}
		return operandItems;
	}

	private ArrayList<ClaferFeature> getClaferFeatures() {
		ClaferFeature algorithm = new ClaferFeature(Constants.FeatureType.ABSTRACT, "algorithm", // Counter as the name to make each addition identifiable.
			null, null);

		algorithm.getfeatureProperties().add(new FeatureProperty("name", "string"));
		algorithm.getfeatureProperties().add(new FeatureProperty("description", "string"));
		algorithm.getfeatureProperties().add(new FeatureProperty("security", "Security"));
		algorithm.getfeatureProperties().add(new FeatureProperty("performance", "Performance"));
		algorithm.getfeatureProperties().add(new FeatureProperty("classPerformance", "Performance"));

		ClaferFeature cipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "cipher", // Counter as the name to make each addition identifiable.
			new FeatureProperty("algorithm", null), null);

		ClaferFeature symmetricCipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "symmetricCipher", // Counter as the name to make each addition identifiable.
			new FeatureProperty("cipher", null), null);

		symmetricCipher.getfeatureProperties().add(new FeatureProperty("keySize", "integer"));
		symmetricCipher.getFeatureConstraints().add("classPerformance = Fast");

		ClaferFeature symmetricBlockCipher = new ClaferFeature(Constants.FeatureType.ABSTRACT, "symmetricBlockCipher", // Counter as the name to make each addition identifiable.
			new FeatureProperty("symmetricCipher", null), null);

		symmetricBlockCipher.getfeatureProperties().add(new FeatureProperty("mode", "Mode"));
		symmetricBlockCipher.getfeatureProperties().add(new FeatureProperty("padding", "Padding"));
		symmetricBlockCipher.getFeatureConstraints().add("mode !=ECB");
		symmetricBlockCipher.getFeatureConstraints().add("padding !=NoPadding");

		ClaferFeature AES = new ClaferFeature(Constants.FeatureType.CONCRETE, "AES", // Counter as the name to make each addition identifiable.
			new FeatureProperty("symmetricBlockCipher", null), null);
		AES.getFeatureConstraints().add("description = Advanced Encryption Standard (AES) cipher");
		AES.getFeatureConstraints().add("name = AES");
		AES.getFeatureConstraints().add("keySize = 128 || keySize = 192 || keySize = 256");
		AES.getFeatureConstraints().add("keySize = 128 => performance = VeryFast && security = Medium");
		AES.getFeatureConstraints().add("keySize > 128 => performance = Fast && security = Strong");

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
		questionDetails.setAnswers(compositeToHoldAnswers.getListOfAllAnswer());
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
