/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
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
package crossing.e1.configurator.wizard.beginner;

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

public class BeginnerTaskQuestionPage extends WizardPage {

	private final Question quest;
	private List<Question> allQuestion;
	private Entry<Question, Answer> selection = new AbstractMap.SimpleEntry<>(null, null);
	private boolean finish = false;
	private List<String> selectionValues;

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

	public BeginnerTaskQuestionPage(final List<Question> allQuestion, final Question quest, final Task task) {
		super("Display Questions");
		setTitle("Configuring Selected Task: " + task.getDescription());
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
				inputField.forceFocus();
				break;

			case itemselection:
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);

				final org.eclipse.swt.widgets.List itemList = new org.eclipse.swt.widgets.List(container, SWT.LEFT | SWT.MULTI | SWT.V_SCROLL);

				final Composite composite = new Composite(container, SWT.CENTER);
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
				gridData.horizontalSpan = 2;
				composite.setLayoutData(gridData);
				composite.setLayout(new GridLayout(1, false));

				gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);

				final Button moveRightButton = new Button(composite, SWT.TOP);
				moveRightButton.setLayoutData(gridData);

				final Button moveLeftButton = new Button(composite, SWT.BOTTOM);

				moveRightButton.setText("-Select->");
				moveLeftButton.setText("<-Deselect-");
				moveRightButton.setEnabled(false);
				moveLeftButton.setEnabled(false);

				final org.eclipse.swt.widgets.List selectedItemList = new org.eclipse.swt.widgets.List(container, SWT.RIGHT | SWT.MULTI | SWT.V_SCROLL);
				selectedItemList.setEnabled(false);

				for (final String value : this.selectionValues) {
					itemList.add(value);
					selectedItemList.add("                                                                           ");
				}

				itemList.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						return;
					}

					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (e.getSource() instanceof org.eclipse.swt.widgets.List) {
							final org.eclipse.swt.widgets.List sel = (org.eclipse.swt.widgets.List) e.getSource();
							moveRightButton.setEnabled(sel.getSelectionCount() > 0);
						}

					}
				});

				selectedItemList.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						return;
					}

					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (e.getSource() instanceof org.eclipse.swt.widgets.List) {
							final org.eclipse.swt.widgets.List sel = (org.eclipse.swt.widgets.List) e.getSource();
							moveLeftButton.setEnabled(sel.getSelectionCount() > 0);
						}

					}
				});

				moveRightButton.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						return;
					}

					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (e.getSource() instanceof Button && (((Button) e.getSource()).getStyle() & SWT.NONE) == SWT.NONE) {
							final String[] sel = itemList.getSelection();
							Answer ans = BeginnerTaskQuestionPage.this.selection.getValue();
							StringBuilder checkedElement = new StringBuilder();
							if (ans == null) {
								ans = new Answer();
								ans.setNextID(-1);
							} else {
								checkedElement.append(ans.getValue());
							}

							if (selectedItemList.getItemCount() > 0 && selectedItemList.getItem(0).trim().isEmpty()) {
								selectedItemList.removeAll();
								selectedItemList.setEnabled(true);
							}

							for (final String item : sel) {
								selectedItemList.add(item);
								itemList.remove(item);
								checkedElement.append(item);
								checkedElement.append(";");
							}
							ans.setValue(checkedElement.toString());
							BeginnerTaskQuestionPage.this.finish = ans.getValue().contains(";");
							BeginnerTaskQuestionPage.this.setPageComplete(BeginnerTaskQuestionPage.this.finish);
							BeginnerTaskQuestionPage.this.selection = new AbstractMap.SimpleEntry<>(question, ans);
							moveRightButton.setEnabled(false);
						}
					}
				});

				moveLeftButton.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						return;
					}

					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (e.getSource() instanceof Button && (((Button) e.getSource()).getStyle() & SWT.NONE) == SWT.NONE) {
							final String[] sel = selectedItemList.getSelection();

							Answer ans = BeginnerTaskQuestionPage.this.selection.getValue();
							if (ans == null) {
								ans = new Answer();
								ans.setNextID(-1);
							}
							String checkedElement = ans.getValue();

							for (final String item : sel) {
								selectedItemList.remove(item);
								if (!item.trim().isEmpty()) {
									itemList.add(item);
									checkedElement = checkedElement.replace(item + ";", "");
								}
							}
							ans.setValue(checkedElement);
							BeginnerTaskQuestionPage.this.finish = ans.getValue().contains(";");
							BeginnerTaskQuestionPage.this.setPageComplete(BeginnerTaskQuestionPage.this.finish);
							BeginnerTaskQuestionPage.this.selection = new AbstractMap.SimpleEntry<>(question, ans);
							moveLeftButton.setEnabled(false);
						}
					}
				});
				break;
				
			case button:
				for (int i = 0; i < 3; i++) {
					new Label(container, SWT.NULL);
				}

				final Composite comp = new Composite(container, SWT.NONE);
				final GridLayout grid = new GridLayout(2, false);
				grid.marginWidth = 0;
				comp.setLayout(grid);

				final Button methodButton = new Button(comp, SWT.PUSH);
				final ArrayList<String> methodArrayList = question.getMethod();
				final String buttonName = methodArrayList.get(0);
				final String className = methodArrayList.get(1);
				final String methodName = methodArrayList.get(2);

				methodButton.setText(buttonName);
				final Label feedbackLabel = new Label(comp, SWT.NONE);

				Object classObj = null;
				Method method = null;
				Object[] paramArray = null;

				try {
					final Class<?> c = Class.forName(className);
					final Method[] methods = c.getMethods();

					for (final Method m : methods) {
						if (m.getName().equals(methodName)) {
							method = m;
							break;
						}
					}

					final Class<?>[] paramTypes = method.getParameterTypes();
					final ArrayList<Object> paramObjList = new ArrayList<>();
					final ArrayList<Integer> methodParamIds = question.getMethodParamIds();
					String value = null;

					for (int i = 0; i < methodParamIds.size(); i++) {
						value = this.allQuestion.get(methodParamIds.get(i) - 1).getAnswers().get(0).getValue();

						if (!paramTypes[i].getName().equals("int")) {
							paramObjList.add(paramTypes[i].cast(value));
						} else {
							paramObjList.add(Integer.parseInt(this.allQuestion.get(methodParamIds.get(i) - 1).getAnswers().get(0).getValue()));
						}
					}

					classObj = c.newInstance();
					paramArray = paramObjList.toArray();

				} catch (SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException e) {
					Activator.getDefault().logError(e);
				}

				final Method invokeMethod = method;
				final Object invokeClassObj = classObj;
				final Object[] invokeParamArray = paramArray;
				
				methodButton.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {

							final Object[] resObjArray = (Object[]) invokeMethod.invoke(invokeClassObj, invokeParamArray);
							final String[] resStringArray = Arrays.copyOf(resObjArray, resObjArray.length, String[].class);

							if (resStringArray[0].equals(true)) {
								question.getDefaultAnswer().setNextID(question.getAnswers().get(0).getNextID());
							} else {
								question.getDefaultAnswer().setNextID(question.getAnswers().get(1).getNextID());
							}

							feedbackLabel.setText(resStringArray[1]);
							feedbackLabel.getParent().pack();
							methodButton.setEnabled(false);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
							Activator.getDefault().logError(e1);
						}

					}
				});

				this.finish = true;
				final Answer a = question.getDefaultAnswer();
				BeginnerTaskQuestionPage.this.setPageComplete(this.finish);
				BeginnerTaskQuestionPage.this.selection = new AbstractMap.SimpleEntry<>(question, a);
				break;
				
			default:
				break;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BeginnerTaskQuestionPage)) {
			return false;
		}
		final BeginnerTaskQuestionPage other = (BeginnerTaskQuestionPage) obj;
		if (this.finish != other.finish) {
			return false;
		}
		if (this.quest == null) {
			if (other.quest != null) {
				return false;
			}
		} else if (!this.quest.equals(other.quest)) {
			return false;
		}
		if (this.selection == null) {
			if (other.selection != null) {
				return false;
			}
		} else if (!this.selection.equals(other.selection)) {
			return false;
		}
		if (this.selectionValues == null) {
			if (other.selectionValues != null) {
				return false;
			}
		} else if (!this.selectionValues.equals(other.selectionValues)) {
			return false;
		}
		return true;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.finish ? 1231 : 1237);
		result = prime * result + ((this.quest == null) ? 0 : this.quest.hashCode());
		result = prime * result + ((this.selection == null) ? 0 : this.selection.hashCode());
		result = prime * result + ((this.selectionValues == null) ? 0 : this.selectionValues.hashCode());
		return result;
	}

}
