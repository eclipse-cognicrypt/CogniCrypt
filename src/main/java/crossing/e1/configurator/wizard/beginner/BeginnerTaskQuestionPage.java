package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.featuremodel.clafer.ClaferModel;

public class BeginnerTaskQuestionPage extends WizardPage {

	private final BeginnerModeQuestionnaire quest;
	private final HashMap<Question, Answer> selection = new HashMap<Question, Answer>();
	private final List<Composite> questionsList;

	public BeginnerTaskQuestionPage(final BeginnerModeQuestionnaire quest) {
		super("Display Questions");
		setTitle("Configuring Selected Task: " + quest.getTask().getDescription());
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.quest = quest;
		this.questionsList = new ArrayList<Composite>();
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 450, 200);
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);

		List<Question> questionList = quest.getQutionare();
		for (Question question : questionList) {
			createQuestionControl(container, question);
		}

		layout.numColumns = 1;
		setControl(container);
	}

	public HashMap<Question, Answer> getMap() {
		return this.selection;
	}

	public synchronized HashMap<Question, Answer> getSelection() {
		return this.selection;
	}

	public void createQuestionControl(final Composite parent, final Question question) {

		List<Answer> answers = question.getAnswers();
		Composite container = getPanel(parent);
		Label label = new Label(container, SWT.CENTER);
		label.setText(question.getQuestionText());

		ComboViewer comboViewer = new ComboViewer(container, SWT.NONE);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(answers);

		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent arg0) {
				IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
				BeginnerTaskQuestionPage.this.selection.put(question, (Answer) selection.getFirstElement());
			}
		});

		comboViewer.setSelection(new StructuredSelection(question.getDefaultAnswer()));
		this.questionsList.add(container);
	}

	public void setMap(final HashMap<Question, Answer> hashMap, final ClaferModel model) {

		// userOptions = new HashMap<ArrayList<AstConcreteClafer>,
		// ArrayList<Integer>>();
		// ArrayList<Integer> values = null;
		// ArrayList<AstConcreteClafer> keys = null; // new
		// // ArrayList<AstConcreteClafer>();
		// for (AstConcreteClafer clafer : PropertiesMapperUtil.getPropertyLabels()
		// .keySet()) {
		// values = new ArrayList<Integer>();
		// keys = new ArrayList<AstConcreteClafer>();
		// for (AstConcreteClafer claf : PropertiesMapperUtil.getPropertyLabels()
		// .get(clafer)) {
		// HashMap<HashMap<String, String>, List<String>> qutionare = quest
		// .getQutionare();
		// for (HashMap<String, String> val : qutionare.keySet()) {
		// String st1 = val.get(val.keySet().toArray()[0]);
		// String st2 = claf.getName();
		// if (st2.contains(st1)
		// // && map.containsKey(val .get(val.keySet().toArray()[0]))
		// ) {
		// keys.add(clafer);
		// keys.add(claf);
		// values.add(4);
		// values.add(map.get(val.get(val.keySet().toArray()[0])));
		// }
		// }
		// }
		// }
		// if (keys != null && values != null)
		// userOptions.put(keys, values);
		//
		// for (ArrayList<AstConcreteClafer> x : userOptions.keySet()) {
		// System.out.println(x.toString());
		// }
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
}
