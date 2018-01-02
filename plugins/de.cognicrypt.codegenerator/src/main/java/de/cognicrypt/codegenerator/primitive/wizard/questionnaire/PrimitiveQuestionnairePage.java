package de.cognicrypt.codegenerator.primitive.wizard.questionnaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerTaskQuestionPage;

public class PrimitiveQuestionnairePage extends WizardPage {

	private final Question quest;
	private final Primitive primitive;
	private PrimitiveQuestionnaire PrimitiveQuestionnaire;
	private LinkedHashMap<String, String> selectionMap = new LinkedHashMap<String, String>();
	private PrimitiveQuestionPageUtility pageUtility;
	private boolean finish = false;
	public String selectedValue = "";
	private String claferDepend;
	private int iteration = 0;
	private final Page page;
	private String rangedSize;
	private MyVerifyListener verifyDecimal = new MyVerifyListener();

	/**
	 * construct a page containing an element other than itemselection
	 * 
	 * @param quest
	 *        question that will be displayed on the page
	 * @param primitive
	 *        primitive for which the page is created
	 */
	public PrimitiveQuestionnairePage(final Question quest, final Primitive primitive) {
		this(quest, primitive, null);
	}

	/**
	 * construct a page containing a single question
	 * 
	 * @param quest
	 *        question that will be displayed on the page
	 * @param primitive
	 *        primitive for which the page is created
	 * @param selectionValues
	 *        list of selectable strings if element type of quest is itemselection, null otherwise
	 */
	public PrimitiveQuestionnairePage(final Question quest, final Primitive primitive, final List<String> selectionValues) {
		super("Display Questions");
		setTitle("Configuring Selected primitive: " + primitive.getName());
		setDescription("");
		this.quest = quest;
		this.primitive = primitive;

		// This variable needs to be initialized.
		this.page = null;
	}

	/**
	 * 
	 * @param page
	 *        page contains the questions that need to be displayed.
	 * @param primitive
	 *        primitive for which the page is created
	 * @param selectionValues
	 *        The call to this constructor needs to have this extra parameter for itemselection. list of selectable strings if element type of quest is itemselection, null
	 *        otherwise
	 */
	public PrimitiveQuestionnairePage(final Page page, final Primitive primitive, final List<String> selectionValues) {
		super("Display Questions");
		//		setTitle("Configuring Selected primitive: " + primitive.getName());
		setDescription("");
		this.page = page;
		this.primitive = primitive;

		//This variable needs to be initialized.
		this.quest = null;
	}

	/**
	 * 
	 * @param page
	 * @param primitive
	 * @param PrimitiveQuestionnaire
	 *        Updated this parameter in the constructor to accept the questionnaire instead of all the questions.
	 * @param selectionValues
	 */
	public PrimitiveQuestionnairePage(final Page page, final Primitive primitive, final PrimitiveQuestionnaire PrimitiveQuestionnaire, final List<String> selectionValues, int iteration) {
		super("Display Questions");
		setTitle("Configuring Selected primitive: " + primitive.getName());
		setDescription("");
		this.PrimitiveQuestionnaire = PrimitiveQuestionnaire;
		this.quest = null;
		this.page = page;
		this.primitive = primitive;
		this.iteration = iteration;
	}

	/**
	 * 
	 * @param PrimitiveQuestionnaire
	 *        Updated this parameter in the constructor to accept the questionnaire instead of all the questions.
	 * @param quest
	 * @param primitive
	 */
	public PrimitiveQuestionnairePage(final PrimitiveQuestionnaire PrimitiveQuestionnaire, final Question quest, final Primitive primitive) {
		super("Display Questions");
		setTitle("Configuring Selected primitive: " + primitive.getName());
		setDescription("");
		this.PrimitiveQuestionnaire = PrimitiveQuestionnaire;
		this.quest = quest;
		this.page = null;
		this.primitive = primitive;
	}

	public PrimitiveQuestionnairePage(final Question quest, final Primitive primitive, int iteration) {
		super("Display Quesitons");
		setTitle("Configuring selected primitive:" + primitive.getName());
		setDescription("Key sizes");
		this.quest = quest;
		this.page = null;
		this.primitive = primitive;
		this.iteration = iteration;

	}

	public PrimitiveQuestionnairePage(final Page page, final Primitive primitive, int iteration) {
		super("Display Questions");
		setTitle("Configuring Selected primitive: " + primitive.getName());
		setDescription("");
		this.page = page;
		this.primitive = primitive;
		this.iteration = iteration;

		//This variable needs to be initialized.
		this.quest = null;
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
		if (page == null) {
			createQuestionControl(container, this.quest);
			Activator.getDefault().logError("Outdated json file is used for task " + this.primitive.getName() + ". Please update.");
		} else {
			// loop through the questions that are to be displayed on the page.
			for (Question question : page.getContent()) {
				createQuestionControl(container, question);
			}
		}

		setControl(container);

	}

	private void createQuestionControl(final Composite parent, final Question question) {
		pageUtility = new PrimitiveQuestionPageUtility();
		final List<Answer> answers = question.getAnswers();
		final Composite container = getPanel(parent);
		final Label label = new Label(container, SWT.TOP);
		label.setText(question.getQuestionText());
		switch (question.getElement()) {
			case combo:
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				final ComboViewer comboViewer = new ComboViewer(container, SWT.DROP_DOWN | SWT.READ_ONLY);
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				comboViewer.setInput(answers);

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
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				comboViewer.setSelection(new StructuredSelection(question.getDefaultAnswer()));
				break;
			case checkbox:
				new Label(container, SWT.NULL);
				container.setLayout(new RowLayout(SWT.VERTICAL));
				Button[] checkbox = new Button[answers.size()];
				for (int i = 0; i < answers.size(); i++) {
					String ans = answers.get(i).getValue();
					checkbox[i] = new Button(container, SWT.CHECK);
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

									} else if  (!selectedValue.contains(source.getText())) {
											selectedValue += "||" + source.getText();
											selectionMap.put(claferDepend, selectedValue);
										}
								
							} else {
								selectedValue = selectedValue.replace("||" + source.getText(), "");
								selectionMap.put(claferDepend, selectedValue);
								if(selectedValue.equals("")){
								}
							}
						}
					});
					this.finish=true;
					PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				}
				
				break;
			case text:
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
				data.widthHint = 100;
				final Text inputField = new Text(container, SWT.BORDER);
//				inputField.setSize(500, inputField.getSize().y);
				if (question.getEnteredAnswer() != null) {
					this.finish = !inputField.getText().isEmpty();
					PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				}
				if (answers.get(0).getClaferDependencies() != null) {
					claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
					if (claferDepend.equals("Block size")) {
						inputField.addVerifyListener(verifyDecimal);
//						data.widthHint=10;
					}
					
					inputField.setLayoutData(data);
					
					inputField.addModifyListener(e -> {
						this.finish = !selectedValue.isEmpty();
						selectedValue = inputField.getText();
						selectionMap.put(claferDepend, selectedValue);

						PrimitiveQuestionnairePage.this.setPageComplete(this.isPageComplete());

					});
				}
				break;
			case radiobutton:

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
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				final Text inputDescription = new Text(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
				inputDescription.setSize(150, inputDescription.getSize().y);
				// Define a minimum width
				final GridData gridData = new GridData();
				gridData.widthHint = 230;
				gridData.heightHint = 60;
				inputDescription.setLayoutData(gridData);
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
					String key = "Keysize n°" + (j + 1);
					for (int i = 0; i < answers.size(); i++) {
						String ans = answers.get(i).getValue();
						radioButton[i] = new Button(group[j], SWT.RADIO);
						radioButton[i].setText(ans);

					}
					Text[] textField = new Text[answers.size()];
					for (int i = 0; i < answers.size(); i++) {
						textField[i] = new Text(group[j], SWT.SINGLE | SWT.BORDER);
						textField[i].setEnabled(false);
						textField[i].addVerifyListener(verifyDecimal);
						radioButton[i].addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(SelectionEvent e) {
								getWizard().getContainer().updateButtons();
								Button source = (Button) e.getSource();
								if (answers.get(pageUtility.getIndex(answers, source.getText())).getClaferDependencies() != null) {
									claferDepend = answers.get(pageUtility.getIndex(answers, source.getText())).getClaferDependencies().get(0).getAlgorithm();
								}
								if (source.getText().equals("fixed size")) {
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

					textField[0].addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent event) {
							getWizard().getContainer().updateButtons();

							Text text = (Text) event.widget;
							claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
							selectedValue = text.getText();
							selectionMap.put(key, selectedValue);
							rangedSize = selectedValue;
							PrimitiveQuestionnairePage.this.finish = !text.getText().isEmpty();
							PrimitiveQuestionnairePage.this.setPageComplete(PrimitiveQuestionnairePage.this.finish);
						}
					});

					textField[1].addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent event) {
							PrimitiveQuestionnairePage.this.setPageComplete(false);
							Text text = (Text) event.widget;
							selectedValue = text.getText();
							selectionMap.put(key, rangedSize + "-" + selectedValue);
							PrimitiveQuestionnairePage.this.finish = !text.getText().isEmpty();
							PrimitiveQuestionnairePage.this.setPageComplete(PrimitiveQuestionnairePage.this.finish);

						}
					});

				}
				this.iteration = 0;
				System.out.println(PrimitiveQuestionnairePage.this.finish);
			default:
				break;
		}
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

}
