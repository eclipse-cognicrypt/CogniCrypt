package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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

public class DisplayQuestions extends WizardPage {

	private String questions = null;
	private final QuestionsBeginner quest;
	private final HashMap<Question, Answer> selection = new HashMap<Question, Answer>();
	private final List<Composite> quetsionsList;

	public DisplayQuestions(final QuestionsBeginner quest) {
		super("Display Questions");
		setTitle(Labels.PROPERTIES);
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.quest = quest;
		this.quetsionsList = new ArrayList<Composite>();
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 450, 200);
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);
		while (this.quest.hasQuestions()) {
			this.questions = this.quest.nextQuestion().getDef();
			final Question claferName = this.quest.nextQuestion();

			nextQuestion(container, claferName);

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

	public void nextQuestion(final Composite parent, final Question question) {
		final List<Answer> answer = this.quest.nextValues();
		final List<String> answerString = new ArrayList<String>();
		ComboViewer option;
		final Composite container = getPanle(parent);
		final Label label = new Label(container, SWT.CENTER);
		label.setText(this.questions);
		for (final Answer answerObject : answer) {
			answerString.add(answerObject.getValue());
		}
		option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(answerString);
		option.setSelection(new StructuredSelection(answerString.get(1)));
		option.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent arg0) {

				final String answerSelection = option.getSelection().toString();
				// FIXME need to replace 4 by the value selected by user , check groupcard property here before
				// assignment
				final int index = answerString.indexOf(answerSelection.toString().replace("[", "").replace("]", ""));
				DisplayQuestions.this.selection.put(question, answer.get(index));

				// Integer.parseInt(answerSelection
				// .substring(answerSelection.indexOf(':') + 1,
				// answerSelection.length()-1))

			}
		});
		this.quetsionsList.add(container);
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

	private Composite getPanle(final Composite parent) {
		final Composite titledPanel = new Composite(parent, SWT.NONE);
		final Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 9, SWT.BOLD));
		titledPanel.setFont(boldFont);
		final GridLayout layout2 = new GridLayout();

		layout2.numColumns = 4;
		titledPanel.setLayout(layout2);

		return titledPanel;
	}
}
