package crossing.e1.configurator.wizard.beginner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.clafer.ast.AstConcreteClafer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.StringLabelMapper;
import crossing.e1.xml.export.ReadTaskConfig;

public class QuestionsBeginner {

	private String taskName;
	private HashMap<HashMap<String, String>, List<String>> qutionare;
	int counter = 0;
	private HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> userOptions;

	void setCounter(int value) {
		counter = value;
	}

	public void init() {
		qutionare = new ReadTaskConfig().getQA(taskName);
		setCounter(qutionare.size());
		userOptions = new HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>>();
	}

	public void setTask(String value) {
		taskName = value;

	}

	public HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>> getMap() {
		return userOptions;
	}

	public boolean hasQuestions() {
		if (counter > 0)
			return true;
		else
			return false;
	}

	public List<String> nextValues() {
		List<HashMap<String, String>> key = new ArrayList<HashMap<String, String>>(
				qutionare.keySet());
		List<String> answers = qutionare.get(key.get(key.size() - counter));
		counter = counter - 1;
		return answers;
	}

	public String nextQuestion() {
		List<HashMap<String, String>> x = new ArrayList<HashMap<String, String>>(
				qutionare.keySet());
		System.out.println(x.get(x.size() - counter).keySet().toArray()[0]);
		return x.get(x.size() - counter).keySet().toArray()[0].toString();
	}

	public void setMap(Map<String, Integer> map, ClaferModel model) {
		ArrayList<Integer> values = null;
		ArrayList<AstConcreteClafer> keys = null; // new
													// ArrayList<AstConcreteClafer>();
		for (AstConcreteClafer clafer : StringLabelMapper.getPropertyLabels()
				.keySet()) {
			values = new ArrayList<Integer>();
			keys = new ArrayList<AstConcreteClafer>();
			for (AstConcreteClafer claf : StringLabelMapper.getPropertyLabels()
					.get(clafer)) {
				for (HashMap<String, String> val : qutionare.keySet()) {
					if (claf.getName().contains(
							val.get(val.keySet().toArray()[0]))
							&& map.containsKey(val
									.get(val.keySet().toArray()[0]))) {
						keys.add(clafer);
						keys.add(claf);
						values.add(4);
						values.add(map.get(val.get(val.keySet().toArray()[0])));
					}
				}
			}
		}
		if (keys != null && values != null)
			userOptions.put(keys, values);
		
		for ( ArrayList<AstConcreteClafer> x: userOptions.keySet()){
			System.out.println(x.toString());
		}
	}
	

}
