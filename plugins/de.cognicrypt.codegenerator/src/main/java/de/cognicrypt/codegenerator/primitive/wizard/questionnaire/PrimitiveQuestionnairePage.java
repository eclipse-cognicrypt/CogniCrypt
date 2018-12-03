/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.wizard.questionnaire;

import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;

public class PrimitiveQuestionnairePage extends WizardPage {

	private final Primitive primitive;
	private LinkedHashMap<String, String> selectionMap = new LinkedHashMap<String, String>();
	private PrimitiveQuestionPageUtility pageUtility;
	private boolean finish = false;
	public String selectedValue = "";
	private String claferDepend;
	private int iteration = 0;
	private final Page page;
	private String rangedSize;
	private Text note;
	ControlDecoration deco;

	/**
	 * 
	 * @param page
	 *        page contains the questions that need to be displayed.
	 * @param primitive
	 *        primitive for which the page is created
	 * @param selectionValues
	 * 
	 */
	public PrimitiveQuestionnairePage(final Page page, final Primitive primitive, final List<String> selectionValues) {
		super("Display Questions");
		setTitle("Integrating a new primitive: " + primitive.getName());
		setDescription("Please enter the following data related to the primitive.");
		this.page = page;
		this.primitive = primitive;
	}

	/**
	 * 
	 * @param page
	 * @param primitive
	 * @param PrimitiveQuestionnaire
	 * @param selectionValues
	 * @param iteration
	 *        This parameter is used for number of keysizes.
	 */
	public PrimitiveQuestionnairePage(final Page page, final Primitive primitive, final PrimitiveQuestionnaire PrimitiveQuestionnaire, final List<String> selectionValues, int iteration) {
		super("Display Questions");
		setTitle("Integrating a new primitive: " + primitive.getName());
		setDescription("Please enter the following data related to the primitive.");
		this.page = page;
		this.primitive = primitive;
		this.iteration = iteration;
	}

	@Override
	public boolean canFlipToNextPage() {
		return this.finish && isPageComplete();
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setBounds(10, 10, 200, 300);
		// Updated the number of columns to order the questions vertically.
		final GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		// If legacy JSON files are in effect.

		for (Question question : page.getContent()) {
			createQuestionControl(container, question);
		}
		setControl(container);

	}

	private void createQuestionControl(final Composite parent, final Question question) {
		pageUtility = new PrimitiveQuestionPageUtility();
		final List<Answer> answers = question.getAnswers();
		final Composite container = getPanel(parent);
		container.setLayout(new GridLayout(2, false));
		final Label label = new Label(container, SWT.NONE);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gridData.widthHint = 260;
		label.setLayoutData(gridData);
		label.setText(question.getQuestionText());
		switch (question.getElement()) {
			case combo:
				final ComboViewer comboViewer = new ComboViewer(container, SWT.DROP_DOWN | SWT.READ_ONLY);
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				comboViewer.setInput(answers);
				GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
				gd_combo.minimumWidth = 50;
				Combo comboItem = comboViewer.getCombo();
				comboItem.setLayoutData(gd_combo);

				comboViewer.addSelectionChangedListener(selectedElement -> {
					final IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
					if (answers.get(pageUtility.getIndex(answers, selection.getFirstElement().toString())).getClaferDependencies() != null) {
						claferDepend = answers.get(pageUtility.getIndex(answers, selection.getFirstElement().toString())).getClaferDependencies().get(0).getAlgorithm();
						selectionMap.put(claferDepend, selection.getFirstElement().toString());

					}
					try {
						this.iteration = Integer.parseInt(selection.getFirstElement().toString());
					} catch (Exception e) {

					}

				});
				//to create a text if the questions have 'note' to display
				if (!question.getNote().isEmpty()) {
					createNote(parent, question);
				}
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				comboViewer.setSelection(new StructuredSelection(question.getDefaultAnswer()));
				break;

			case checkbox:
				new Label(container, SWT.NULL);
				new Label(container, SWT.NULL);
				new Label(container, SWT.NULL);
				Composite container_1 = new Composite(container, SWT.NULL);
				container_1.setLayout(new GridLayout(2, false));
				Label[] emptySpace = new Label[answers.size()];
				Button[] checkbox = new Button[answers.size()];
				for (int i = 0; i < answers.size(); i++) {
					String ans = answers.get(i).getValue();
					emptySpace[i] = new Label(container_1, SWT.NONE);
					emptySpace[i].setText("     ");
					checkbox[i] = new Button(container_1, SWT.CHECK);
					checkbox[i].setText(ans);
					checkbox[i].addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							Button source = (Button) e.getSource();
							if (source.getSelection()) {

								if (answers.get(pageUtility.getIndex(answers, source.getText())).getClaferDependencies() != null) {
									claferDepend = answers.get(pageUtility.getIndex(answers, source.getText())).getClaferDependencies().get(0).getAlgorithm();

								}
								if (selectedValue.isEmpty()) {
									selectedValue = source.getText();
									selectionMap.put(claferDepend, selectedValue);

								} else if (!selectedValue.contains(source.getText())) {
									selectedValue += "|" + source.getText();
									selectionMap.put(claferDepend, selectedValue);
								}

							} else {
								selectedValue = selectedValue.replace("|" + source.getText(), "");
								selectionMap.put(claferDepend, selectedValue);
								if (selectedValue.equals("")) {}
							}
						}
					});
					this.finish = true;
					PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				}

				break;

			case text:
				final Text inputField = new Text(container, SWT.BORDER | SWT.FILL);
				GridData textBoxGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
				textBoxGridData.widthHint = 268;
				inputField.setLayoutData(textBoxGridData);

				if (question.getEnteredAnswer() != null) {
					this.finish = !inputField.getText().isEmpty();
					PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				}
				if (answers.get(0).getClaferDependencies() != null) {
					claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
					if (claferDepend.equals(Constants.BLOCK_SIZE)) {
						textBoxGridData.widthHint = SWT.DEFAULT;
						inputField.addVerifyListener(PrimitiveQuestionnairePage::ensureTextContainsOnlyDigits);
					}

					inputField.addModifyListener(e -> {
						this.finish = !selectedValue.isEmpty();
						selectedValue = inputField.getText();
						selectionMap.put(claferDepend, selectedValue);

						PrimitiveQuestionnairePage.this.setPageComplete(this.isPageComplete());

					});

				}
				break;

			case radio:
				Button[] button = new Button[answers.size()];

				for (int i = 0; i < answers.size(); i++) {
					String ans = answers.get(i).getValue();
					button[i] = new Button(container, SWT.RADIO);
					button[i].setText(ans);
					button[i].addSelectionListener(new SelectionAdapter() {

						public void widgetSelected(SelectionEvent e) {
							Button source = (Button) e.getSource();

							if (source.getSelection()) {
								int index = pageUtility.getIndex(answers, source.getText());
								if (answers.get(index).getClaferDependencies() != null) {
									claferDepend = answers.get(pageUtility.getIndex(answers, source.getText())).getClaferDependencies().get(0).getAlgorithm();
									selectedValue = source.getText();
									selectionMap.put(claferDepend, selectedValue);
								}
							}
						}
					});
				}
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				break;

			case textarea:
				final Text inputDescription = new Text(container, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
				GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
				gridData_1.heightHint = 124;
				gridData_1.widthHint = 250;
				inputDescription.setLayoutData(gridData_1);
				inputDescription.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent event) {
						Text text = (Text) event.widget;
						claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
						selectedValue = text.getText();
						selectionMap.put(claferDepend, selectedValue);
					}
				});
				break;

			case composed:
				container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				container.setLayout(new GridLayout(1, false));

				Group[] group = new Group[iteration];
				for (int j = 0; j < this.iteration; j++) {
					group[j] = new Group(container, SWT.COLOR_WIDGET_NORMAL_SHADOW);
					group[j].setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
					group[j].setLayout(new GridLayout(4, false));
					Button[] radioButton = new Button[answers.size()];
					String key = "keySize" + (j + 1);
					for (int i = 0; i < answers.size(); i++) {
						String ans = answers.get(i).getValue();
						radioButton[i] = new Button(group[j], SWT.RADIO);
						radioButton[i].setText(ans);

					}
					Text[] textField = new Text[answers.size()];
					for (int i = 0; i < answers.size(); i++) {
						textField[i] = new Text(group[j], SWT.SINGLE | SWT.BORDER);
						textField[i].setEnabled(false);
						textField[i].addVerifyListener(PrimitiveQuestionnairePage::ensureTextContainsOnlyDigits);

						radioButton[i].addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(SelectionEvent e) {
								getWizard().getContainer().updateButtons();
								Button source = (Button) e.getSource();
								if (answers.get(pageUtility.getIndex(answers, source.getText())).getClaferDependencies() != null) {
									claferDepend = answers.get(pageUtility.getIndex(answers, source.getText())).getClaferDependencies().get(0).getAlgorithm();
								}
								if (source.getText().equals(Constants.FIXED_SIZE)) {
									textField[0].setEnabled(true);
									textField[1].setEnabled(false);
								}

								else {
									textField[0].setEnabled(true);
									textField[1].setEnabled(true);
								}

							}
						});
					}
					textField[0].addModifyListener(e -> {
						getWizard().getContainer().updateButtons();
						claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
						selectedValue = textField[0].getText();
						selectionMap.put(key, selectedValue);
						rangedSize = selectedValue;
						PrimitiveQuestionnairePage.this.finish = !textField[0].getText().isEmpty();
						PrimitiveQuestionnairePage.this.setPageComplete(PrimitiveQuestionnairePage.this.finish);
					});

					textField[1].addModifyListener(e -> {
						getWizard().getContainer().updateButtons();
						PrimitiveQuestionnairePage.this.setPageComplete(false);
						selectedValue = textField[1].getText();
						selectionMap.put(key, rangedSize + "-" + selectedValue);
						PrimitiveQuestionnairePage.this.finish = !textField[1].getText().isEmpty();
						PrimitiveQuestionnairePage.this.setPageComplete(PrimitiveQuestionnairePage.this.finish);

						//Checking if the integer in the second field is greater than the first field
						if (textField[0] != null && textField[1] != null) {
							String startRange = textField[0].getText();
							int startRangeInt = Integer.valueOf(startRange);
							System.out.print(startRangeInt);
							String endRange = textField[1].getText();
							int endRangeInt = Integer.valueOf(endRange);
							System.out.print(endRangeInt);

							//Field assit for Error message
							deco = new ControlDecoration(textField[1], SWT.TOP | SWT.RIGHT | SWT.WRAP);
							Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
							deco.setDescriptionText("The value in the second text field should be greater than the value in first text field");
							deco.setImage(image);
							deco.setShowOnlyOnFocus(false);
							deco.hide();

							if (endRangeInt > startRangeInt) {
								deco.hide();
							} else {
								deco.show();
							}
						} else {
							//deco.hide();
						}

					});

				}
				this.iteration = 0;
			default:
				break;
		}
	}

	//ensure if the input text field contains only integers
	private static void ensureTextContainsOnlyDigits(VerifyEvent e) {
		String string = e.text;
		e.doit = string.matches("\\d*");
		return;
	}

	public LinkedHashMap<String, String> getMap() {
		return this.selectionMap;
	}

	public int getIteration() {
		return this.iteration;
	}

	/**
	 * 
	 * @return returns the id of the current page.
	 */
	public int getPageMap() {
		return page.getId();
	}

	public int getPageNextID() {
		if (page != null) {
			return page.getNextID();
		} else {
			return Constants.QUESTION_PAGE_NO_STATIC_NEXT_PAGE_ID;
		}
	}

	//add the note below the question
	private void createNote(final Composite parent, final Question question) {
		final Group notePanel = new Group(parent, SWT.NONE);
		notePanel.setText("Note:");
		final Font boldFont = new Font(notePanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
		notePanel.setFont(boldFont);

		this.note = new Text(notePanel, SWT.MULTI | SWT.WRAP);
		this.note.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.note.setText(Constants.DESCRIPTION_KEYSIZES);
		this.note.setBounds(10, 20, 585, 60);
		this.note.setSize(this.note.computeSize(585, SWT.DEFAULT));
		setControl(notePanel);
		this.note.setEditable(false);
		this.note.setEnabled(true);
		new Label(parent, SWT.NULL);
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

	public synchronized LinkedHashMap<String, String> getSelection() {
		return this.selectionMap;
	}

	public String getSelectedValue() {
		return this.selectedValue;
	}

	public String getClaferDepend() {
		return this.claferDepend;
	}
}
