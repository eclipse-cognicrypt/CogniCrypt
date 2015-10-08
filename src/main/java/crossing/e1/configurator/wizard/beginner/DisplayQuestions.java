package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.clafer.ast.AstConcreteClafer;
import org.clafer.choco.constraint.SetNotMember;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import crossing.e1.configurator.Lables;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.StringLabelMapper;
import crossing.e1.xml.export.ReadTaskConfig;

public class DisplayQuestions extends WizardPage {

	List<String> answer;
	String questions = null;
	QuestionsBeginner quest;
	HashMap<String, Integer> selection = new HashMap<String, Integer>();
	private List<Composite> quetsionsList;
	private HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> userOptions;

	public DisplayQuestions(QuestionsBeginner quest) {
		super("Display Questions");
		setTitle(Lables.PROPERTIES);
		setDescription(Lables.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.quest = quest;
		quetsionsList = new ArrayList<Composite>();
	}

	public synchronized HashMap<String, Integer> getSelection() {
		return selection;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 450, 200);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		while (quest.hasQuestions()) {
			questions = quest.nextQuestion();
			String claferName=quest.nextQuestionClafer();
			System.out.println("Question initialized " + questions
					+ " Clafer is " + claferName);
			answer = quest.nextValues();
			nextQuestion(container, claferName);

		}
		layout.numColumns = 1;
		setControl(container);
	}

	private Composite getPanle(Composite parent) {
		Composite titledPanel = new Composite(parent, SWT.NONE);
		Font boldFont = new Font(titledPanel.getDisplay(), new FontData(
				"Arial", 9, SWT.BOLD));
		titledPanel.setFont(boldFont);
		GridLayout layout2 = new GridLayout();

		layout2.numColumns = 4;
		titledPanel.setLayout(layout2);

		return titledPanel;
	}

	public void nextQuestion(Composite parent, String refClafer) {
		ComboViewer option;
		Composite container = getPanle(parent);
		Label label = new Label(container, SWT.CENTER);
		label.setText(questions);
		System.out.println(questions + " => " + answer.toString());
		option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(answer);
		option.setSelection(new StructuredSelection(answer.get(1)));
		option.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				String answerSelection = option.getSelection().toString();
				// FIXME need to replace 4 by the value selected by user , check
				// groupcard property here before assignment
				selection.put(refClafer, 4);

				// Integer.parseInt(answerSelection
				// .substring(answerSelection.indexOf(':') + 1,
				// answerSelection.length()-1))

			}
		});
		quetsionsList.add(container);
	}

	public void setMap(Map<String, Integer> map, ClaferModel model) {
		System.out.println("Set MAAP INVOKED");
//		userOptions = new HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>>();
//		ArrayList<Integer> values = null;
//		ArrayList<AstConcreteClafer> keys = null; // new
//													// ArrayList<AstConcreteClafer>();
//		for (AstConcreteClafer clafer : StringLabelMapper.getPropertyLabels()
//				.keySet()) {
//			values = new ArrayList<Integer>();
//			keys = new ArrayList<AstConcreteClafer>();
//			for (AstConcreteClafer claf : StringLabelMapper.getPropertyLabels()
//					.get(clafer)) {
//				HashMap<HashMap<String, String>, List<String>> qutionare = quest
//						.getQutionare();
//				for (HashMap<String, String> val : qutionare.keySet()) {
//					String st1 = val.get(val.keySet().toArray()[0]);
//					String st2 = claf.getName();
//					if (st2.contains(st1)
//					// && map.containsKey(val .get(val.keySet().toArray()[0]))
//					) {
//						keys.add(clafer);
//						keys.add(claf);
//						values.add(4);
//						values.add(map.get(val.get(val.keySet().toArray()[0])));
//					}
//				}
//			}
//		}
//		if (keys != null && values != null)
//			userOptions.put(keys, values);
//
//		for (ArrayList<AstConcreteClafer> x : userOptions.keySet()) {
//			System.out.println(x.toString());
//		}
	}

	public HashMap<String, Integer> getMap() {
		System.out.println("GET MAPP INVOKED");
		// TODO Auto-generated method stub
		return selection;
	}
}
