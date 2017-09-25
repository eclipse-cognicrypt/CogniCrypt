package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.beginer.question.Answer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;


public class GroupAnswer extends Group {
	private Text txtAnswer;
	private Answer answer;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupAnswer(Composite parent, int style, Answer answerParam ,boolean showRemoveButton) {
		super(parent, style);
		setAnswer(answerParam);
		
		txtAnswer = new Text(this, SWT.BORDER);
		txtAnswer.setBounds(3, 3, 350, 29);
		if(showRemoveButton){
			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setBounds(359, 3, 79, 31);
			btnRemove.setText("Remove");
		}
		

	}

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
	 * @param answer the answer to set
	 */
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
}
