/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.wizard.questionnaire;

import java.util.LinkedHashMap;
import java.util.List;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.primitive.types.Primitive;

public class PrimitiveQuestionnairePage extends WizardPage {

	private final LinkedHashMap<String, String> selectionMap = new LinkedHashMap<String, String>();
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
	public PrimitiveQuestionnairePage(final Page page, final Primitive primitive, final PrimitiveQuestionnaire PrimitiveQuestionnaire, final List<String> selectionValues, final int iteration) {
		super("Display Questions");
		setTitle("Integrating a new primitive: " + primitive.getName());
		setDescription("Please enter the following data related to the primitive.");
		this.page = page;
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

		for (final Question question : this.page.getContent()) {
			createQuestionControl(container, question);
		}
		setControl(container);

	}

	private void createQuestionControl(final Composite parent, final Question question) {
		this.pageUtility = new PrimitiveQuestionPageUtility();
		final List<Answer> answers = question.getAnswers();
		final Composite container = getPanel(parent);
		container.setLayout(new GridLayout(2, false));
		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gridData.widthHint = 260;
		label.setLayoutData(gridData);
		label.setText(question.getQuestionText());
		switch (question.getElement()) {
			case combo:
				final ComboViewer comboViewer = new ComboViewer(container, SWT.DROP_DOWN | SWT.READ_ONLY);
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				comboViewer.setInput(answers);
				final GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
				gd_combo.minimumWidth = 50;
				final Combo comboItem = comboViewer.getCombo();
				comboItem.setLayoutData(gd_combo);

				comboViewer.addSelectionChangedListener(selectedElement -> {
					final IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
					if (answers.get(this.pageUtility.getIndex(answers, selection.getFirstElement().toString())).getClaferDependencies() != null) {
						this.claferDepend = answers.get(this.pageUtility.getIndex(answers, selection.getFirstElement().toString())).getClaferDependencies().get(0).getAlgorithm();
						this.selectionMap.put(this.claferDepend, selection.getFirstElement().toString());

					}
					try {
						this.iteration = Integer.parseInt(selection.getFirstElement().toString());
					} catch (final Exception e) {

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
				final Composite container_1 = new Composite(container, SWT.NULL);
				container_1.setLayout(new GridLayout(2, false));
				final Label[] emptySpace = new Label[answers.size()];
				final Button[] checkbox = new Button[answers.size()];
				for (int i = 0; i < answers.size(); i++) {
					final String ans = answers.get(i).getValue();
					emptySpace[i] = new Label(container_1, SWT.NONE);
					emptySpace[i].setText("     ");
					checkbox[i] = new Button(container_1, SWT.CHECK);
					checkbox[i].setText(ans);
					checkbox[i].addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							final Button source = (Button) e.getSource();
							if (source.getSelection()) {

								if (answers.get(PrimitiveQuestionnairePage.this.pageUtility.getIndex(answers, source.getText())).getClaferDependencies() != null) {
									PrimitiveQuestionnairePage.this.claferDepend = answers.get(PrimitiveQuestionnairePage.this.pageUtility.getIndex(answers, source.getText()))
										.getClaferDependencies().get(0).getAlgorithm();

								}
								if (PrimitiveQuestionnairePage.this.selectedValue.isEmpty()) {
									PrimitiveQuestionnairePage.this.selectedValue = source.getText();
									PrimitiveQuestionnairePage.this.selectionMap.put(PrimitiveQuestionnairePage.this.claferDepend, PrimitiveQuestionnairePage.this.selectedValue);

								} else if (!PrimitiveQuestionnairePage.this.selectedValue.contains(source.getText())) {
									PrimitiveQuestionnairePage.this.selectedValue += "|" + source.getText();
									PrimitiveQuestionnairePage.this.selectionMap.put(PrimitiveQuestionnairePage.this.claferDepend, PrimitiveQuestionnairePage.this.selectedValue);
								}

							} else {
								PrimitiveQuestionnairePage.this.selectedValue = PrimitiveQuestionnairePage.this.selectedValue.replace("|" + source.getText(), "");
								PrimitiveQuestionnairePage.this.selectionMap.put(PrimitiveQuestionnairePage.this.claferDepend, PrimitiveQuestionnairePage.this.selectedValue);
								if (PrimitiveQuestionnairePage.this.selectedValue.equals("")) {}
							}
						}
					});
					this.finish = true;
					PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				}

				break;

			case text:
				final Text inputField = new Text(container, SWT.BORDER | SWT.FILL);
				final GridData textBoxGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
				textBoxGridData.widthHint = 268;
				inputField.setLayoutData(textBoxGridData);

				if (question.getEnteredAnswer() != null) {
					this.finish = !inputField.getText().isEmpty();
					PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				}
				if (answers.get(0).getClaferDependencies() != null) {
					this.claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
					if (this.claferDepend.equals(Constants.BLOCK_SIZE)) {
						textBoxGridData.widthHint = SWT.DEFAULT;
						inputField.addVerifyListener(PrimitiveQuestionnairePage::ensureTextContainsOnlyDigits);
					}

					inputField.addModifyListener(e -> {
						this.finish = !this.selectedValue.isEmpty();
						this.selectedValue = inputField.getText();
						this.selectionMap.put(this.claferDepend, this.selectedValue);

						PrimitiveQuestionnairePage.this.setPageComplete(isPageComplete());

					});

				}
				break;

			case radio:
				final Button[] button = new Button[answers.size()];

				for (int i = 0; i < answers.size(); i++) {
					final String ans = answers.get(i).getValue();
					button[i] = new Button(container, SWT.RADIO);
					button[i].setText(ans);
					button[i].addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							final Button source = (Button) e.getSource();

							if (source.getSelection()) {
								final int index = PrimitiveQuestionnairePage.this.pageUtility.getIndex(answers, source.getText());
								if (answers.get(index).getClaferDependencies() != null) {
									PrimitiveQuestionnairePage.this.claferDepend = answers.get(PrimitiveQuestionnairePage.this.pageUtility.getIndex(answers, source.getText()))
										.getClaferDependencies().get(0).getAlgorithm();
									PrimitiveQuestionnairePage.this.selectedValue = source.getText();
									PrimitiveQuestionnairePage.this.selectionMap.put(PrimitiveQuestionnairePage.this.claferDepend, PrimitiveQuestionnairePage.this.selectedValue);
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
				final GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
				gridData_1.heightHint = 124;
				gridData_1.widthHint = 250;
				inputDescription.setLayoutData(gridData_1);
				inputDescription.addModifyListener(event -> {
					final Text text = (Text) event.widget;
					PrimitiveQuestionnairePage.this.claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
					PrimitiveQuestionnairePage.this.selectedValue = text.getText();
					PrimitiveQuestionnairePage.this.selectionMap.put(PrimitiveQuestionnairePage.this.claferDepend, PrimitiveQuestionnairePage.this.selectedValue);
				});
				break;

			case composed:
				container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				container.setLayout(new GridLayout(1, false));

				final Group[] group = new Group[this.iteration];
				for (int j = 0; j < this.iteration; j++) {
					group[j] = new Group(container, SWT.COLOR_WIDGET_NORMAL_SHADOW);
					group[j].setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
					group[j].setLayout(new GridLayout(4, false));
					final Button[] radioButton = new Button[answers.size()];
					final String key = "keySize" + (j + 1);
					for (int i = 0; i < answers.size(); i++) {
						final String ans = answers.get(i).getValue();
						radioButton[i] = new Button(group[j], SWT.RADIO);
						radioButton[i].setText(ans);

					}
					final Text[] textField = new Text[answers.size()];
					for (int i = 0; i < answers.size(); i++) {
						textField[i] = new Text(group[j], SWT.SINGLE | SWT.BORDER);
						textField[i].setEnabled(false);
						textField[i].addVerifyListener(PrimitiveQuestionnairePage::ensureTextContainsOnlyDigits);

						radioButton[i].addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(final SelectionEvent e) {
								getWizard().getContainer().updateButtons();
								final Button source = (Button) e.getSource();
								if (answers.get(PrimitiveQuestionnairePage.this.pageUtility.getIndex(answers, source.getText())).getClaferDependencies() != null) {
									PrimitiveQuestionnairePage.this.claferDepend = answers.get(PrimitiveQuestionnairePage.this.pageUtility.getIndex(answers, source.getText()))
										.getClaferDependencies().get(0).getAlgorithm();
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
						this.claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
						this.selectedValue = textField[0].getText();
						this.selectionMap.put(key, this.selectedValue);
						this.rangedSize = this.selectedValue;
						PrimitiveQuestionnairePage.this.finish = !textField[0].getText().isEmpty();
						PrimitiveQuestionnairePage.this.setPageComplete(PrimitiveQuestionnairePage.this.finish);
					});

					textField[1].addModifyListener(e -> {
						getWizard().getContainer().updateButtons();
						PrimitiveQuestionnairePage.this.setPageComplete(false);
						this.selectedValue = textField[1].getText();
						this.selectionMap.put(key, this.rangedSize + "-" + this.selectedValue);
						PrimitiveQuestionnairePage.this.finish = !textField[1].getText().isEmpty();
						PrimitiveQuestionnairePage.this.setPageComplete(PrimitiveQuestionnairePage.this.finish);

						//Checking if the integer in the second field is greater than the first field
						if (textField[0] != null && textField[1] != null) {
							final String startRange = textField[0].getText();
							final int startRangeInt = Integer.valueOf(startRange);
							System.out.print(startRangeInt);
							final String endRange = textField[1].getText();
							final int endRangeInt = Integer.valueOf(endRange);
							System.out.print(endRangeInt);

							//Field assit for Error message
							this.deco = new ControlDecoration(textField[1], SWT.TOP | SWT.RIGHT | SWT.WRAP);
							final Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
							this.deco.setDescriptionText("The value in the second text field should be greater than the value in first text field");
							this.deco.setImage(image);
							this.deco.setShowOnlyOnFocus(false);
							this.deco.hide();

							if (endRangeInt > startRangeInt) {
								this.deco.hide();
							} else {
								this.deco.show();
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
	private static void ensureTextContainsOnlyDigits(final VerifyEvent e) {
		final String string = e.text;
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
		return this.page.getId();
	}

	public int getPageNextID() {
		if (this.page != null) {
			return this.page.getNextID();
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
