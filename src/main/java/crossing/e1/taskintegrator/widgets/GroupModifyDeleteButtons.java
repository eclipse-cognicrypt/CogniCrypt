package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;

import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.taskintegrator.wizard.QuestionDialog;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;

import java.util.ArrayList;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class GroupModifyDeleteButtons extends Group {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	int counter=0;
	public GroupModifyDeleteButtons(Composite parent) {
		super(parent, SWT.RIGHT_TO_LEFT);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		setLayout(rowLayout);
		
		Button btnDelete = new Button(this, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING
		            | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
				confirmationMessageBox.setText("Deleting Question");
		        int response = confirmationMessageBox.open();
		        if (response == SWT.YES){
		        	((CompositeToHoldGranularUIElements) btnDelete.getParent().getParent().getParent().getParent()).deleteQuestion(((CompositeGranularUIForHighLevelQuestions)btnDelete.getParent().getParent()).getQuestion());// (1) CompositeGranularUIForClaferFeature, (2) composite inside (3) CompositeToHoldGranularUIElements
		        }
			}
		});
		btnDelete.setLayoutData(new RowData(66, SWT.DEFAULT));
		btnDelete.setText("Delete");
		
		Button btnModify = new Button(this, SWT.NONE);
		btnModify.setLayoutData(new RowData(66, SWT.DEFAULT));
		btnModify.setText("Modify");
		
		btnModify.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				/*String questn=((CompositeGranularUIForHighLevelQuestions)btnModify.getParent()).getTextQuestion();
				String type=((CompositeGranularUIForHighLevelQuestions)btnModify.getParent()).getAnswerType();
				String temp=((CompositeGranularUIForHighLevelQuestions)btnModify.getParent().getParent().getParent()).getTextQuestion();
				*/
				QuestionDialog qstnDialog=new QuestionDialog(parent.getShell());
				qstnDialog.setQuestionText("Test");
				int response=qstnDialog.open();
				//qstnDialog.setQuestionText("Test");
			/*	if(response==Window.OK){
					counter++;
					Question tempQuestion = getDummyQuestion(qstnDialog.getQuestionText(),qstnDialog.getquestionType(),qstnDialog.getAnswerValue());
					
					// Update the array list.							
		        	((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent().getParent()).getListOfAllQuestions().add(tempQuestion);
		        	((CompositeToHoldGranularUIElements)btnModify.getParent().getParent().getParent()).addQuestionUIElements(tempQuestion);
		        	// (1) CompositeGranularUIForClaferFeature, (2) composite inside (3) CompositeToHoldGranularUIElements
	
				}*/
				//System.out.println(temp);

			}
		});

		this.setSize(SWT.DEFAULT, 40);
	}

/*	private Question getDummyQuestion(String questionText, String questionType, ArrayList<Answer> answerValues) {
		Question tempQuestion = new Question();
		tempQuestion.setId(counter);
		tempQuestion.setQuestionText(questionText);
		tempQuestion.setQuestionType(questionType);
		
		Answer answer = new Answer();
		answer.setValue("answer");
		answer.setDefaultAnswer(false);
		answer.setNextID(counter);
		ClaferDependency claferDependency = new ClaferDependency();
		claferDependency.setAlgorithm("algoritm");
		claferDependency.setOperand("operand");
		claferDependency.setOperator(Constants.FeatureConstraintRelationship.AND.toString());
		claferDependency.setValue("value");
		ArrayList<ClaferDependency> claferDependencies = new ArrayList<ClaferDependency>();
		claferDependencies.add(claferDependency);		
		
		CodeDependency codeDependency = new CodeDependency();
		codeDependency.setOption("option");
		codeDependency.setValue("value");
		ArrayList<CodeDependency> codeDependencies = new ArrayList<CodeDependency>();
		codeDependencies.add(codeDependency);
		
		answer.setCodeDependencies(codeDependencies);
		answer.setClaferDependencies(claferDependencies);
		
		ArrayList<Answer> answers = new ArrayList<Answer>();
		answers=(ArrayList<Answer>)answerValues.clone();
		//answers.add(answer);
		
		tempQuestion.setAnswers(answerValues);
		
		return tempQuestion;
	}*/
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
