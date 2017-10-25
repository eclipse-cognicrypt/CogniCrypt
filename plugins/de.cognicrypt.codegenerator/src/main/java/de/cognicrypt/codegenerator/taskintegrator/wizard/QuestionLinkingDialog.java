package crossing.e1.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;

public class QuestionLinkingDialog extends Dialog {

	private Question question;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public QuestionLinkingDialog(Shell parentShell, Question question) {
		super(parentShell);
		this.question = question;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;

		Label label = new Label(container, SWT.NONE);
		label.setText("Question: ");
		
		Label lblQuestionText = new Label(container, SWT.NONE);
		lblQuestionText.setText(question.getQuestionText());

		Label lblAnswers = new Label(container, SWT.NONE);
		lblAnswers.setText("Answers");

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		ArrayList<String> pageNames = new ArrayList<>();
		pageNames.add("[Default]");
		pageNames.add("Page One");
		pageNames.add("Page Two");

		for (Answer answer : question.getAnswers()) {

			Label lblCurrentAnswer = new Label(composite, SWT.NONE);
			lblCurrentAnswer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			lblCurrentAnswer.setText(answer.getValue());

			Combo combo = new Combo(composite, SWT.DROP_DOWN);
			combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			for (String pageName : pageNames) {
				combo.add(pageName);
			}
		}

		return container;
	}

	/**
	 * Create contents of the button bar.
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
		return new Point(450, 300);
	}
}
