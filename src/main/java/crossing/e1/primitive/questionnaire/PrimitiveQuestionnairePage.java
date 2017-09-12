
package crossing.e1.primitive.questionnaire;

import java.awt.Checkbox;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.primitive.types.Primitive;

public class PrimitiveQuestionnairePage extends WizardPage {

	private final Question quest;
	private List<Question> allQuestion;
	private static Entry<Question, Answer> selection = new AbstractMap.SimpleEntry<>(null, null);
	private boolean finish = false;
	private List<String> selectionValues;
	

	
	public PrimitiveQuestionnairePage(final Question quest, final Primitive primitive) {
		this(quest, primitive, null);
	}

	public PrimitiveQuestionnairePage(final Question quest, final Primitive primitive, final List<String> selectionValues) {
		super("Display Questions");
		setTitle("To figure out " );
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.quest = quest;
		this.selectionValues = selectionValues;
	}

	public PrimitiveQuestionnairePage(final List<Question> allQuestion, final Question quest, final Primitive primitive) {
		super("Display Questions");
		setTitle("Configuring Selected Task: " + primitive.getName());
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.allQuestion = allQuestion;
		this.quest = quest;
	}
	
	
	@Override
	public boolean canFlipToNextPage() {
		return this.finish && isPageComplete();
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 450, 200);
		final GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		createQuestionControl(container, this.quest);
		setControl(container);
	}

	private void createQuestionControl(final Composite parent, final Question question) {

		final List<Answer> answers = question.getAnswers();
		final Composite container = getPanel(parent);
		final Label label = new Label(container, SWT.TOP);
		label.setText(question.getQuestionText());
		switch (question.getElement()) {
			case combo:
				final ComboViewer comboViewer = new ComboViewer(container, SWT.DROP_DOWN | SWT.READ_ONLY);
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				comboViewer.setInput(answers);

				comboViewer.addSelectionChangedListener(selectedElement -> {
					final IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
					PrimitiveQuestionnairePage.this.selection = new AbstractMap.SimpleEntry<>(question, (Answer) selection.getFirstElement());
				});
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				comboViewer.setSelection(new StructuredSelection(question.getDefaultAnswer()));
				break;
			case checkbox:
				
				for(int i=0;i<answers.size();i++){
					String ans=answers.get(i).getValue();
				 new Button(container, SWT.CHECK).setText(ans);
				}
				this.finish=true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				break;
			case text:
				final Text inputField = new Text(container, SWT.BORDER);
				inputField.setSize(240, inputField.getSize().y);
				inputField.addModifyListener(e -> {
					final Answer a = question.getDefaultAnswer();
					final String cleanedInput = inputField.getText().replaceAll("(?=[]\\[+&|!(){}^\"~*?:\\\\-])", "\\\\");

					a.setValue(cleanedInput);
					this.finish = !cleanedInput.isEmpty();
					PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
					PrimitiveQuestionnairePage.this.selection = new AbstractMap.SimpleEntry<>(question, a);

				});
				inputField.forceFocus();
				break;
			default:
				break;
			
		}
	}



	public static Entry<Question, Answer> getMap() {
		return selection;
	}

	private Composite getPanel(final Composite parent) {
		final Composite titledPanel = new Composite(parent, SWT.NONE);
		final Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 9, SWT.BOLD));
		titledPanel.setFont(boldFont);
		final GridLayout layout2 = new GridLayout();

		layout2.numColumns = 4;
		titledPanel.setLayout(layout2);

		return titledPanel;
	}

	@Override
	public IWizardPage getPreviousPage() {
		final IWizardPage prev = super.getPreviousPage();
		if (prev != null && prev instanceof PrimitiveQuestionnairePage) {
			return getWizard().getPreviousPage(this);
		}
		return prev;
	}

	public synchronized Entry<Question, Answer> getSelection() {
		return this.selection;
	}



}
