/**
 * Copyright 2015 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * @author Sarah Nadi
 *
 */
package crossing.e1.configurator.wizard.beginner;

import java.util.HashMap;
import java.util.List;

import org.clafer.choco.constraint.SetNotMember;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DummyPage extends WizardPage {

	ComboViewer option;
	Label label;
	Composite container;
	List<String> answer;
	String questions=null;
	QuestionsBeginner quest;
	HashMap<String, Integer> selection = new HashMap<String, Integer>();

	public DummyPage(QuestionsBeginner quest) {
		super("Dummy Page");
		setTitle("QuestionPage");
		this.quest = quest;
	}

	public synchronized HashMap<String, Integer> getSelection() {
		return selection;
	}

	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		container.setBounds(10, 10, 450, 200);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		while (quest.hasQuestions()) {
			questions = quest.nextQuestion();
			System.out.println("Question initialized "+questions);
			answer = quest.nextValues();
			nextQuestion();

		}
		layout.numColumns = 2;
		setControl(container);
	}

	void complete() {

	}

	public void nextQuestion() {
		label = new Label(container, SWT.CENTER);
		label.setText(questions);
		System.out.println(questions + " => " + answer.toString());
		option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(answer);
		option.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				String answerSelection = option.getSelection().toString();

				selection.put(questions, 4);
				
//				Integer.parseInt(answerSelection
//						.substring(answerSelection.indexOf(':') + 1,
//								answerSelection.length()-1))

			}
		});
	}
}
