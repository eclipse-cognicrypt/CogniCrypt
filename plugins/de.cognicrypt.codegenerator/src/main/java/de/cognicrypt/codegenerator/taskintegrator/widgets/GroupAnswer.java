package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;

public class GroupAnswer extends Group {

	public Text txtAnswer;
	private Answer answer;
	private String getAnswer;
	public ArrayList<Answer> answers;
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public GroupAnswer(Composite parent, int style, Answer answerParam, boolean showRemoveButton) {
		super(parent, style);
		setAnswer(answerParam);
		txtAnswer = new Text(this, SWT.BORDER);
		if(answer.getValue()!=null){
			txtAnswer.setText(answer.getValue());
		}
		txtAnswer.setBounds(3, 3, 420, 29);
		
		txtAnswer.addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e){
				answer.setValue(txtAnswer.getText());
			}
		});
		
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
			        ((CompositeToHoldSmallerUIElements) btnRemove.getParent().getParent().getParent()).deleteAnswer(answer);
			       // ((CompositeToHoldSmallerUIElements) btnRemove.getParent().getParent().getParent()).updateAnswerContainer();

			        }
			        
				}

			});
		}
	}

	/**
	 * @return the answer text
	 */
	/*public String retrieveAnswer(){
		getAnswer=txtAnswer.getText();
		return getAnswer;
	}
	*//**
	 * set the answer text
	 *//*
	public void setAnswerValue(){
		answer.setValue(this.retrieveAnswer());
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
	}
}
