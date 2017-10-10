package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.beginer.question.Answer;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class GroupAnswer extends Group {

	public Text txtAnswer;
	//public ArrayList<Text> txtAnswers = new ArrayList<Text>();

	private Answer answer;
	private String getAnswer;
	private int counter=0;
	public ArrayList<Answer> answers;
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public GroupAnswer(Composite parent, int style, Answer answerParam, boolean showRemoveButton) {
		super(parent, style);
		counter++;
		//answers.add(answerParam);
		setAnswer(answerParam);
		//setTxtBoxAnswerId(answerId);
		txtAnswer = new Text(this, SWT.BORDER);
		txtAnswer.setBounds(3, 3, 420, 29);
		//txtAnswer.setText("answers");
		//setAnswerValue();
		if (showRemoveButton) {
			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setBounds(429, 3, 79, 31);
			btnRemove.setText("Remove");
			btnRemove.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
					confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
					confirmationMessageBox.setText("Deleting answer");
					int response = confirmationMessageBox.open();
					if (response == SWT.YES){
			        ((CompositeToHoldSmallerUIElements) btnRemove.getParent().getParent().getParent()).deleteAnswer(counter);
			        }
			        
				}

			});
		}
	}

	//To save the answer
	public String retrieveAnswer(){
		getAnswer=txtAnswer.getText();
		return getAnswer;
	}
	
	public void setAnswerValue(){
		answer.setValue(this.retrieveAnswer());
	}
	/*public void setTxtBoxAnswerId(int answerId){
		this.answerId=answerId;
		
	}
	public int getTxtBoxAnswerId(){
		return answerId;
	}*/
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the answer
	 */
	public Answer getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *        the answer to set
	 */
	public void setAnswer(Answer answer) {
		this.answer = answer;
		//answer.setValue(txtAnswer.getText());
	}
}
