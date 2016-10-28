package crossing.e1.configurator.wizard.beginner;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Labels;

public class BeginnerTaskQuestionPage extends WizardPage {

	private final Question quest;
	private Entry<Question, Answer> selection = new AbstractMap.SimpleEntry<Question, Answer>(null, null);
	private boolean finish = false;
	private final List<String> selectionValues;

	public BeginnerTaskQuestionPage(final Question quest, final Task task) {
		this(quest, task, null);
	}

	public BeginnerTaskQuestionPage(final Question quest, final Task task, final List<String> selectionValues) {
		super("Display Questions");
		setTitle("Configuring Selected Task: " + task.getDescription());
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.quest = quest;
		this.selectionValues = selectionValues;
	}

	@Override
	public boolean canFlipToNextPage() {
		return this.finish && isPageComplete();
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 450, 200);
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);

		createQuestionControl(container, this.quest);
		layout.numColumns = 1;
		setControl(container);
	}

	public void createQuestionControl(final Composite parent, final Question question) {

		final List<Answer> answers = question.getAnswers();
		final Composite container = getPanel(parent);
		final Label label = new Label(container, SWT.CENTER);
		label.setText(question.getQuestionText());
		switch (question.getElement()) {
			case combo:
				final ComboViewer comboViewer = new ComboViewer(container, SWT.DROP_DOWN | SWT.READ_ONLY);
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				comboViewer.setInput(answers);

				comboViewer.addSelectionChangedListener(arg0 -> {
					final IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
					BeginnerTaskQuestionPage.this.selection = new AbstractMap.SimpleEntry<>(question, (Answer) selection.getFirstElement());
				});
				this.finish = true;
				BeginnerTaskQuestionPage.this.setPageComplete(this.finish);
				comboViewer.setSelection(new StructuredSelection(question.getDefaultAnswer()));
				break;

			case text:
				final Text inputField = new Text(container, SWT.BORDER);
				inputField.setSize(240, inputField.getSize().y);
				inputField.addModifyListener(e -> {
					final Answer a = question.getDefaultAnswer();
					final String cleanedInput = inputField.getText().replaceAll("(?=[]\\[+&|!(){}^\"~*?:\\\\-])", "\\\\");

					a.setValue(cleanedInput);
					a.getCodeDependencies().get(0).setValue(cleanedInput);
					this.finish = !cleanedInput.isEmpty();
					BeginnerTaskQuestionPage.this.setPageComplete(this.finish);
					BeginnerTaskQuestionPage.this.selection = new AbstractMap.SimpleEntry<>(question, a);

				});
				break;

			case itemselection:
				for (final String value : this.selectionValues) {
					final Button checkBox = new Button(container, SWT.CHECK);
					checkBox.setText(value);
					checkBox.addMouseListener(new MouseListener() {

						@Override
						public void mouseDoubleClick(final MouseEvent e) {
							return;
						}

						@Override
						public void mouseDown(final MouseEvent e) {
							return;
						}

						@Override
						public void mouseUp(final MouseEvent e) {
							if (e.getSource() instanceof Button && (((Button) e.getSource()).getStyle() & SWT.CHECK) == SWT.CHECK) {
								Answer ans = BeginnerTaskQuestionPage.this.selection.getValue();
								if (ans == null) {
									ans = new Answer();
									ans.setNextID(-1);
								}
								final Button clickedCheckbox = (Button) e.getSource();
								final String checkedElement = ans.getValue();
								ans.setValue(clickedCheckbox.getSelection() ? clickedCheckbox.getText() + ";" + ((checkedElement != null) ? checkedElement : "")
									: checkedElement.replace(clickedCheckbox.getText() + ";", ""));

								BeginnerTaskQuestionPage.this.finish = ans.getValue().contains(";");
								BeginnerTaskQuestionPage.this.setPageComplete(BeginnerTaskQuestionPage.this.finish);
								BeginnerTaskQuestionPage.this.selection = new AbstractMap.SimpleEntry<>(question, ans);
							}
						}
					});
				}
				break;
			default:
				break;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof BeginnerTaskQuestionPage)) {
			return false;
		}
		final BeginnerTaskQuestionPage cmp = (BeginnerTaskQuestionPage) obj;
		return this.quest.equals(cmp.quest);
	}

	public Entry<Question, Answer> getMap() {
		return this.selection;
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
		if (prev != null && prev instanceof BeginnerTaskQuestionPage) {
			return getWizard().getPreviousPage(this);
		}
		return prev;
	}

	public synchronized Entry<Question, Answer> getSelection() {
		return this.selection;
	}

}
