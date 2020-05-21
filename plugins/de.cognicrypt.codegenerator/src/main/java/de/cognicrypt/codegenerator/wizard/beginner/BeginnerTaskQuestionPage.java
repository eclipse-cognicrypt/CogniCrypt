/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard.beginner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.GUIElements;

public class BeginnerTaskQuestionPage extends WizardPage {

	private final Task task;
	private Question quest;
	private Page page;

	private boolean finish = false;
	private BeginnerModeQuestionnaire beginnerModeQuestionnaire;
	private final HashMap<Question, Answer> selectionMap = new HashMap<>();
	private Composite container;
	private int count = 0;
	private boolean isActive = true;

	public int getCurrentPageID() {
		return this.page.getId();
	}

	public void setPageInactive() {
		this.isActive = false;
	}

	public boolean isActive() {
		return this.isActive;
	}

	/**
	 * construct a page containing an element other than itemselection
	 *
	 * @param quest
	 *        question that will be displayed on the page
	 * @param task
	 *        task for which the page is created
	 */
	public BeginnerTaskQuestionPage(final Question quest, final Task task) {
		this(task);
		this.quest = quest;
	}

	/**
	 *
	 * @param page
	 *        page contains the questions that need to be displayed.
	 * @param task
	 *        task for which the page is created
	 */
	public BeginnerTaskQuestionPage(final Page page, final Task task) {
		this(task);
		this.page = page;
		this.quest = null;
	}

	/**
	 *
	 * @param page
	 * @param task
	 * @param beginnerModeQuestionnaire
	 *        Updated this parameter in the constructor to accept the questionnaire instead of all the questions.
	 */
	public BeginnerTaskQuestionPage(final Page page, final Task task, final BeginnerModeQuestionnaire beginnerModeQuestionnaire) {
		this(task);
		this.beginnerModeQuestionnaire = beginnerModeQuestionnaire;
		this.quest = null;
		this.page = page;
	}

	/**
	 *
	 * @param beginnerModeQuestionnaire
	 *        Updated this parameter in the constructor to accept the questionnaire instead of all the questions.
	 * @param quest
	 * @param task
	 */
	public BeginnerTaskQuestionPage(final BeginnerModeQuestionnaire beginnerModeQuestionnaire, final Question quest, final Task task) {
		this(task);
		this.beginnerModeQuestionnaire = beginnerModeQuestionnaire;
		this.quest = quest;
		this.page = null;
	}

	private BeginnerTaskQuestionPage(final Task task) {
		super("Display Questions");
		setTitle("Configuring Selected Task: " + task.getDescription());
		setDescription(Constants.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.task = task;
	}

	@Override
	public boolean isPageComplete() {
		for (final Question question : this.page.getContent()) {
			final Answer answer = question.getEnteredAnswer();
			if (answer == null || answer.getValue().isEmpty()) {
				return false;
			}
			if (Arrays.asList((new GUIElements[] { GUIElements.rbtextgroup, GUIElements.button, GUIElements.radio, GUIElements.checkbox })).contains(question.getElement())) {
				return this.finish;
			}
		}

		return true;
	}

	@Override
	public void createControl(final Composite parent) {

		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.container = new Composite(sc, SWT.NONE);
		//		this.container.setBounds(10, 10, 450, 200);
		// Updated the number of columns to order the questions vertically.
		final GridLayout layout = new GridLayout(1, false);

		// To display the Help view after clicking the help icon
		this.container.setLayout(layout);
		// If legacy JSON files are in effect.
		if (this.page == null) {
			createQuestionControl(this.container, this.quest);
			Activator.getDefault().logError("Outdated json file is used for task " + this.task.getDescription() + ". Please update.");
		} else {
			// loop through the questions that are to be displayed on the page.
			for (final Question question : this.page.getContent()) {
				createQuestionControl(this.container, question);
			}
			//setting focus to the first field on the page
			this.container.getChildren()[0].setFocus();
		}
		sc.setContent(this.container);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(sc.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	private void createQuestionControl(final Composite parent, final Question question) {

		final List<Answer> answers = question.getAnswers();
		final Composite container = getPanel(parent);
		final Label label = new Label(container, SWT.TOP | SWT.FILL | SWT.WRAP);
		final GridData gd_question = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_question.widthHint = 750;
		label.setLayoutData(gd_question);
		label.setText(question.getQuestionText());

		final Composite answerPanel = new Composite(parent, SWT.NONE);
		final GridLayout answerLayout = new GridLayout();
		answerLayout.numColumns = 4;
		answerLayout.verticalSpacing = 15;
		answerLayout.horizontalSpacing = 15;
		answerPanel.setLayout(answerLayout);

		int noOfAnswers = answers.size();
		switch (question.getElement()) {
			case combo:
				final ComboViewer comboViewer = new ComboViewer(answerPanel, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.FILL);
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				comboViewer.setInput(answers);

				comboViewer.addSelectionChangedListener(selectedElement -> {
					final IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
					BeginnerTaskQuestionPage.this.selectionMap.put(question, (Answer) selection.getFirstElement());
					question.setEnteredAnswer((Answer) selection.getFirstElement());
				});
				new Label(parent, SWT.NONE);
				//added description for questions
				if (!question.getNote().isEmpty()) {
					createNote(parent, question, true);
				}
				this.finish = true;
				BeginnerTaskQuestionPage.this.setPageComplete(this.finish);
				if (question.getEnteredAnswer() != null) {
					comboViewer.setSelection(new StructuredSelection(question.getEnteredAnswer()));
				} else {
					comboViewer.setSelection(new StructuredSelection(question.getDefaultAnswer()));
				}
				break;
			case radio:
				final String radioNote = question.getNote();
				Group radioNoteControl = null;
				if (!radioNote.isEmpty()) {
					radioNoteControl = createNote(container, question, !(radioNote.contains("$$$")));
				}
				final Button[] radioButtons = new Button[noOfAnswers];
				boolean shouldBreak = noOfAnswers % 4 == 1;
				for (int i = 0; i < noOfAnswers; i++) {
					final int count = i;
					if (shouldBreak && i + 2 == noOfAnswers) {
						new Label(answerPanel, SWT.NULL);
					}

					final Group finalRadioNote = radioNoteControl;
					final String ans = answers.get(i).getValue();
					radioButtons[i] = new Button(answerPanel, SWT.RADIO);
					radioButtons[i].setText(ans);
					radioButtons[i].addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							BeginnerTaskQuestionPage.this.selectionMap.put(question, answers.get(count));
							question.setEnteredAnswer(answers.get(count));
							if (finalRadioNote != null) {
								try {
									finalRadioNote.setVisible(Integer.parseInt(radioNote.split("\\$\\$\\$")[0]) == count);
								} catch (NumberFormatException nfe) {
									finalRadioNote.setVisible(false);
								}
							}
						}
					});
				}

				Answer evalAnswer = question.getEnteredAnswer();
				if (evalAnswer == null) {
					evalAnswer = question.getDefaultAnswer();
				}
				for (int i = 0; i < noOfAnswers; i++) {
					if (radioButtons[i].getText().equals(evalAnswer.getValue())) {
						radioButtons[i].setSelection(true);
						BeginnerTaskQuestionPage.this.selectionMap.put(question, evalAnswer);
						question.setEnteredAnswer(evalAnswer);
					}
				}

				BeginnerTaskQuestionPage.this.setPageComplete(this.finish = true);
				break;
			case checkbox:
				Group checkboxNoteControl = null;
				final String checkboxNoteText = question.getNote();
				//added description for questions
				if (!checkboxNoteText.isEmpty()) {
					checkboxNoteControl = createNote(container, question, !checkboxNoteText.contains("$$$"));
				}
				final List<Button> cbs = new ArrayList<Button>();
				final List<Button> exclusiveCbs = new ArrayList<Button>(noOfAnswers);

				for (int i = 0; i < noOfAnswers; i++) {
					final int count = i;
					final Group finalCheckBoxControl = checkboxNoteControl;
					final Answer a = answers.get(i);
					final Button curCheckbox = new Button(answerPanel, SWT.CHECK);
					curCheckbox.setText(a.getValue());

					curCheckbox.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent selectionEvent) {
							final Button btn = (Button) selectionEvent.getSource();

							if (btn == curCheckbox) {
								final boolean isExclusive = Boolean.parseBoolean(a.getUIDependency("isExclusive"));
								final boolean isSelected = btn.getSelection();

								if (isExclusive) {
									question.setEnteredAnswer(null);
									BeginnerTaskQuestionPage.this.selectionMap.clear();
									for (final Button b : cbs) {
										if (b != curCheckbox) {
											b.setSelection(false);
										}
									}
								} else {
									exclusiveCbs.forEach(b -> b.setSelection(b == curCheckbox));
								}

								if (isSelected) {
									final Answer prevAns = question.getEnteredAnswer();
									if (prevAns != null && !Boolean.parseBoolean(prevAns.getUIDependency("isExclusive"))) {
										final Answer combinedAnswer = prevAns.combineWith(a);
										question.setEnteredAnswer(combinedAnswer);
										BeginnerTaskQuestionPage.this.selectionMap.put(question, combinedAnswer);

									} else {
										question.setEnteredAnswer(a);
										BeginnerTaskQuestionPage.this.selectionMap.put(question, a);
									}
									if (finalCheckBoxControl != null) {
										try {
											finalCheckBoxControl.setVisible(Integer.parseInt(checkboxNoteText.split("\\$\\$\\$")[0]) == count);
										} catch (NumberFormatException nfe) {
											finalCheckBoxControl.setVisible(false);
										}
									}
								}
								BeginnerTaskQuestionPage.this.finish = cbs.stream().anyMatch(e -> e.getSelection());
								BeginnerTaskQuestionPage.this.setPageComplete(isPageComplete());
							}
						}
					});
					cbs.add(curCheckbox);
					curCheckbox.setSelection(a.isDefaultAnswer());
					question.setEnteredAnswer(a);
					BeginnerTaskQuestionPage.this.selectionMap.put(question, a);

					final String isExlusiveValue = a.getUIDependency("isExclusive");
					if (Boolean.parseBoolean(isExlusiveValue)) {
						exclusiveCbs.add(curCheckbox);
					}
				}

				this.finish = true;
				BeginnerTaskQuestionPage.this.setPageComplete(this.finish);
				break;

			case rbtextgroup:
				final Composite rbbtnControl = new Composite(parent, SWT.NONE);
				final GridData rbbtnControlData = new GridData(GridData.FILL, GridData.FILL, false, false);
				rbbtnControl.setLayoutData(rbbtnControlData);
				rbbtnControl.setLayout(new GridLayout(3, false));

				final Map<Button, List<Control>> rbgroups = new HashMap<Button, List<Control>>();

				for (final Answer answer : answers) {
					final boolean isDefaultAnswer = answer.isDefaultAnswer();
					final String rows = answer.getUIDependency("rows");
					final int numRows = (rows == null) ? 0 : Integer.parseInt(rows);

					final Button radioButton = new Button(rbbtnControl, SWT.RADIO);
					radioButton.setText(answer.getValue());
					radioButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 1));

					rbgroups.put(radioButton, new ArrayList<Control>(numRows));
					for (int i = 1; i <= numRows; i++) {
						final int row = i;
						final String labelOption = "label" + row;
						String labelText = answer.getUIDependency(labelOption);
						if (labelText == null) {
							labelText = "";
						}
						final Label groupLabel = new Label(rbbtnControl, SWT.CENTER);
						groupLabel.setText(labelText);
						groupLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

						final Text pathText = new Text(rbbtnControl, SWT.BORDER);
						pathText.setEnabled(isDefaultAnswer);
						pathText.setEditable(false);
						pathText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
						final Button browse = new Button(rbbtnControl, SWT.PUSH);
						browse.setText(Constants.BROWSE);
						browse.setEnabled(isDefaultAnswer);
						browse.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
						browse.addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(final SelectionEvent e) {
								final FileDialog fileDialog = new FileDialog(getShell(), SWT.NULL);
								final String extension = answer.getUIDependency("extension" + row);
								if (extension != null) {
									fileDialog.setFilterExtensions(new String[] { extension });
								}
								final String path = fileDialog.open();
								if (path != null) {
									pathText.setText(path);
									if (rbgroups.get(radioButton).stream().filter(text -> text instanceof Text).allMatch(text -> !((Text) text).getText().isEmpty())) {
										BeginnerTaskQuestionPage.this.setPageComplete(BeginnerTaskQuestionPage.this.finish = true);
									}
									answer.getCodeDependencies().get(0).setValue(path.replace("\\", "\\\\"));
									question.setEnteredAnswer(answer);
									BeginnerTaskQuestionPage.this.selectionMap.put(question, answer);
								}
							}
						});

						final List<Control> curList = rbgroups.get(radioButton);
						curList.add(pathText);
						curList.add(browse);
						rbgroups.put(radioButton, curList);
					}

					radioButton.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							updateRBGroup(question, rbgroups, answer, (Button) e.getSource());
						}
					});
					if (isDefaultAnswer) {
						radioButton.setSelection(true);
						updateRBGroup(question, rbgroups, answer, radioButton);
					}
				}
				break;

			case text:

				final Text inputField = new Text(answerPanel, SWT.BORDER);
				inputField.setLayoutData(new GridData(100, SWT.DEFAULT));
				inputField.setToolTipText(question.getTooltip());
				inputField.setMessage(question.getMessage());

				final ControlDecoration deco = new ControlDecoration(inputField, SWT.LEFT | SWT.TOP);
				final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
				deco.setImage(fieldDecoration.getImage());
				deco.hide();

				//Adding Browse Button for text field that expects a path as input
				if (question.getTextType().equals(Constants.BROWSE)) {
					inputField.setLayoutData(new GridData(300, SWT.DEFAULT));

					final Button browseButton = new Button(answerPanel, SWT.PUSH);
					browseButton.setText(Constants.BROWSE);
					browseButton.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							final FileDialog fileDialog = new FileDialog(getShell(), SWT.NULL);
							fileDialog.setFilterExtensions(new String[] { question.getExtension() });
							final String path = fileDialog.open();
							if (path != null) {
								inputField.setText(path);
							}
						}
					});

					inputField.addModifyListener(event -> {
						BeginnerTaskQuestionPage.this.count = 0;
						// Get the widget whose text was modified
						if (!inputField.getText().endsWith(".jks")) {
							setCount(BeginnerTaskQuestionPage.this.count + 1);
						}
					});

					text(question, inputField);
					// TODO "Bug fix": Get text out of json file
				} else if (question.getTextType().equals(Constants.PASSWORD)) {
					inputField.setLayoutData(new GridData(120, SWT.DEFAULT));
					inputField.setEchoChar((char) 0x25cf);

					//Check box to show/hide password
					final Button checkBox = new Button(container, SWT.CHECK);
					checkBox.setText(Constants.SHOW_PASSWORD_CHECKBOX);
					checkBox.setSelection(false);
					checkBox.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							if (checkBox.getSelection() == true) {
								inputField.setEchoChar('\0');
							} else {
								inputField.setEchoChar((char) 0x25cf);
							}
						}
					});
					text(question, inputField);
				} else if (question.getTextType().equals(Constants.INTEGER)) {
					inputField.addListener(SWT.Verify, e -> {
						final String string = e.text;
						final char[] chars = new char[string.length()];
						string.getChars(0, chars.length, chars, 0);
						for (int i = 0; i < chars.length; i++) {
							if (!('0' <= chars[i] && chars[i] <= '9')) {
								deco.show();
								deco.showHoverText("Expected an integer >0");
								e.doit = false;
								return;
							}
						}
						deco.hide();
					});

					text(question, inputField);
				} else if (question.getTextType().equals(Constants.PORT_NUMBER)) {
					inputField.addVerifyListener(e -> {
						deco.hide();
						final String currentText = ((Text) e.widget).getText();
						final String port = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
						try {
							final int portNum = Integer.valueOf(port);
							if (portNum < 0 || portNum > 65535) {
								deco.show();
								deco.showHoverText("Must be an integer < 65535");
								e.doit = false;
							}
						} catch (final NumberFormatException ex) {
							if (!port.equals("")) {
								deco.show();
								deco.showHoverText("Expected an integer < 65535");
								e.doit = false;
							}
						}
					});
					text(question, inputField);
				} else if (question.getTextType().equals(Constants.IP_ADDRESS)) {
					inputField.addVerifyListener(e -> {
						final String currentText = ((Text) e.widget).getText();
						final String ip = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
						final String[] ipAddress = ip.split("\\.");
						int i = 0;
						try {
							if (!ip.isEmpty()) {
								deco.hide();
								if (ipAddress.length > 4) {
									deco.show();
									deco.showHoverText("Expected format 255.255.255.255");
									e.doit = false;
								}
								for (i = 0; i <= ipAddress.length - 1; i++) {
									final int j = Integer.parseInt(ipAddress[i]);
									if (j < 0 || j > 255) {
										deco.show();
										deco.showHoverText("Expected format 255.255.255.255");
										e.doit = false;
									}
								}
								if (ip.endsWith("..") || ip.startsWith(".")) {
									deco.show();
									deco.showHoverText("Expected format 255.255.255.255");
									e.doit = false;
								}
								if (ip.endsWith(".") || ip.endsWith("[0-9]")) {
									deco.show();
									deco.showHoverText("Expected format 255.255.255.255");
								}
								if (i == 4 && ip.endsWith(".")) {
									deco.show();
									deco.showHoverText("Expected format 255.255.255.255");
									e.doit = false;
								}
							}
						} catch (final NumberFormatException ex) {
							if (!ip.equals("")) {
								deco.show();
								deco.showHoverText("Expected format 255.255.255.255");
								e.doit = false;
							}
						}
					});
					text(question, inputField);
				} else {
					text(question, inputField);
				}

				inputField.addModifyListener(e -> {
					final Answer a = question.getDefaultAnswer();
					final String cleanedInput = inputField.getText().replaceAll("(?=[]\\[+&|!(){}^\"~*?\\\\-])", "\\\\");
					a.setValue(cleanedInput);
					if (a.getCodeDependencies() != null) {
						for (final CodeDependency codeDep : a.getCodeDependencies()) {
							codeDep.setValue(cleanedInput);
						}
					}
					this.finish = !cleanedInput.isEmpty();
					BeginnerTaskQuestionPage.this.selectionMap.put(question, a);
					question.setEnteredAnswer(a);
					BeginnerTaskQuestionPage.this.setPageComplete(isPageComplete());
				});
				if (question.getDefaultAnswer().getCodeDependencies() != null) {
					inputField.setText(question.getDefaultAnswer().getCodeDependencies().get(0).getValue());
				}
				break;
			case button:
				for (int i = 0; i < 3; i++) {
					new Label(answerPanel, SWT.NULL);
				}

				final Composite comp = new Composite(answerPanel, SWT.NONE);
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
						// Updated this code to pull just specific questions from the questionnaire.
						// getQuestionByID may return null in case of a bad json file. Updated the catch block.
						value = this.beginnerModeQuestionnaire.getQuestionByID(methodParamIds.get(i)).getAnswers().get(0).getValue();

						if (!paramTypes[i].getName().equals("int")) {
							paramObjList.add(paramTypes[i].cast(value));
						} else {
							// updated this code to reuse the value variable instead of the earlier line of code.
							paramObjList.add(Integer.parseInt(value));
						}
					}

					classObj = c.newInstance();
					paramArray = paramObjList.toArray();

				} catch (NullPointerException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException e) {
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
							final boolean methodResult = Boolean.parseBoolean(resStringArray[0]);
							final String feedbackString = resStringArray[1];

							if (methodResult) {
								question.setEnteredAnswer(question.getAnswers().get(0));
							} else {
								question.setEnteredAnswer(question.getAnswers().get(1));
							}

							feedbackLabel.setText(feedbackString);
							feedbackLabel.getParent().pack();
							methodButton.setEnabled(false);

							BeginnerTaskQuestionPage.this.finish = methodResult;
							BeginnerTaskQuestionPage.this.setPageComplete(BeginnerTaskQuestionPage.this.finish);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
							Activator.getDefault().logError(e1);
						}

					}
				});

				this.finish = true;
				BeginnerTaskQuestionPage.this.setPageComplete(this.finish);

				break;

			default:
				break;
		}
	}

	private Group createNote(final Composite parent, final Question question, boolean visible) {
 		IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
		final Group notePanel = new Group(parent, SWT.NONE);
		notePanel.setText("Note:");
		final GridLayout gridLayout = new GridLayout();
		notePanel.setLayout(gridLayout);
		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 1;
		notePanel.setLayoutData(gridData);
		final Font boldFont = new Font(notePanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
		notePanel.setFont(boldFont);
		notePanel.pack();
		setControl(parent);

		Text note = new Text(notePanel, SWT.MULTI | SWT.WRAP);
		note.setLayoutData(new GridData(GridData.FILL_BOTH));
		String noteText = question.getNote();
		if (noteText.contains("$$$")) {
		
			String[] noteText1 = noteText.split("\\$\\$\\$");
			noteText = noteText1[1];
			if (!prefStore.getBoolean(Constants.SUPPRESS_LEGACYCLIENT_ERRORS)) {
				noteText = noteText + noteText1[2];
			}
		}
		note.setText(noteText);
		note.pack();
		note.setBounds(10, 20, 585, 60);
		note.setSize(note.computeSize(585, SWT.DEFAULT));
		setControl(notePanel);
		note.setEditable(false);
		note.setEnabled(true);
		notePanel.setVisible(visible);
		return notePanel;
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
		if (this.selectionMap == null) {
			if (other.selectionMap != null) {
				return false;
			}
		} else if (!this.selectionMap.equals(other.selectionMap)) {
			return false;
		}

		if (this.page != other.page) {
			return false;
		}
		return true;
	}

	public HashMap<Question, Answer> getMap() {
		return this.selectionMap;
	}

	/**
	 *
	 * @return returns the id of the current page.
	 */
	public int getPageMap() {
		return this.page.getId();
	}

	public int getPageNextID() {
		for (final Entry<Question, Answer> entry : this.selectionMap.entrySet()) {
			final int nextID = entry.getValue().getNextID();
			if (nextID > -2) {
				return nextID;
			}
		}
		return this.page.getNextID();
	}

	private Composite getPanel(final Composite parent) {
		final Composite titledPanel = new Composite(parent, SWT.NONE);
		final Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 9, SWT.BOLD));
		titledPanel.setFont(boldFont);
		final GridLayout layout2 = new GridLayout();

		layout2.numColumns = 1;
		titledPanel.setLayout(layout2);

		return titledPanel;
	}

	public synchronized HashMap<Question, Answer> getSelection() {
		return this.selectionMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.finish ? 1231 : 1237);
		result = prime * result + ((this.quest == null) ? 0 : this.quest.hashCode());
		result = prime * result + ((this.page == null) ? 0 : this.quest.hashCode());
		result = prime * result + ((this.selectionMap == null) ? 0 : this.selectionMap.hashCode());
		return result;
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.container.setFocus();
		}
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(final int count) {
		this.count = count;
	}

	public void text(final Question question, final Text inputField) {
		if (question.getEnteredAnswer() != null) {
			final Answer a = question.getEnteredAnswer();
			inputField.setText(a.getValue());
			a.getCodeDependencies().get(0).setValue(a.getValue());
			this.finish = !inputField.getText().isEmpty();
			BeginnerTaskQuestionPage.this.setPageComplete(this.finish);
		}

		inputField.addModifyListener(e -> {
			final Answer a = question.getDefaultAnswer();
			final String cleanedInput = inputField.getText().replaceAll("(?=[]\\[+&|!(){}^\"~*?\\\\-])", "\\\\");
			a.setValue(cleanedInput);
			if (a.getCodeDependencies() != null) {
				for (final CodeDependency codeDep : a.getCodeDependencies()) {
					codeDep.setValue(cleanedInput);
				}
			}
			this.finish = !cleanedInput.isEmpty();
			BeginnerTaskQuestionPage.this.selectionMap.put(question, a);
			question.setEnteredAnswer(a);
			BeginnerTaskQuestionPage.this.setPageComplete(isPageComplete());
		});
		if (question.getDefaultAnswer().getCodeDependencies() != null) {
			inputField.setText(question.getDefaultAnswer().getCodeDependencies().get(0).getValue());
		}
	}

	protected void updateRBGroup(final Question question, final Map<Button, List<Control>> rbgroups, final Answer answer, final Button btn) {
		for (final Button curSelection : rbgroups.keySet()) {
			final boolean isEnabled = curSelection == btn;
			if (isEnabled) {
				question.setEnteredAnswer(answer);
				if (!rbgroups.get(btn).stream().filter(text -> text instanceof Text).allMatch(text -> !((Text) text).getText().isEmpty())) {
					BeginnerTaskQuestionPage.this.setPageComplete(BeginnerTaskQuestionPage.this.finish = false);
				} else {
					BeginnerTaskQuestionPage.this.setPageComplete(BeginnerTaskQuestionPage.this.finish = true);
				}
			}

			rbgroups.get(curSelection).stream().forEach(control -> control.setEnabled(isEnabled));
		}
	}
}

class RadioButtonGroup {

	private Button radioButton;
	private Text[] textfields;
	private Button[] browseButtons;

	public RadioButtonGroup() {

	}

	public void toggle() {
		final boolean isActive = this.radioButton.getSelection();
		for (final Text t : this.textfields) {
			t.setEnabled(isActive);
		}

		for (final Button b : this.browseButtons) {
			b.setEnabled(isActive);
		}
	}
}
