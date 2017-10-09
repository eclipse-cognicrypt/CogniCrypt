
package crossing.e1.primitive.questionnaire.wizard;

import java.awt.Checkbox;
import java.util.HashMap;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.GUIElements;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Page;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.utilities.Labels;
import crossing.e1.primitive.types.Primitive;

public class PrimitiveQuestionnairePage extends WizardPage {

	private final Question quest;
	private final Primitive primitive;
	private PrimitiveQuestionnaire PrimitiveQuestionnaire;
	private HashMap<Question, Answer> selectionMap = new HashMap<Question, Answer>();
	private boolean finish = false;
	private List<String> selectionValues;
	public String selectedValue;
	private String claferDepend = "";

	private final Page page;

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
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.quest = quest;
		this.primitive = primitive;
		this.selectionValues = selectionValues;

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
		setTitle("Configuring Selected primitive: " + primitive.getName());
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.page = page;
		this.primitive = primitive;
		this.selectionValues = selectionValues;

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
	public PrimitiveQuestionnairePage(final Page page, final Primitive primitive, final PrimitiveQuestionnaire PrimitiveQuestionnaire, final List<String> selectionValues) {
		super("Display Questions");
		setTitle("Configuring Selected primitive: " + primitive.getName());
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.PrimitiveQuestionnaire = PrimitiveQuestionnaire;
		this.quest = null;
		this.page = page;
		this.primitive = primitive;
		this.selectionValues = selectionValues;
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
		setDescription(Labels.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.PrimitiveQuestionnaire = PrimitiveQuestionnaire;
		this.quest = quest;
		this.page = null;
		this.primitive = primitive;
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
					PrimitiveQuestionnairePage.this.selectionMap.put(question, (Answer) selection.getFirstElement());
					claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
					selectedValue = claferDepend + selection.getFirstElement();
				});
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				comboViewer.setSelection(new StructuredSelection(question.getDefaultAnswer()));
				break;
			case checkbox:
				container.setLayout(new RowLayout(SWT.VERTICAL));
				selectedValue = "";
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
								if (answers.get(getIndex(answers, source.getText())).getClaferDependencies() != null) {
									claferDepend = answers.get(getIndex(answers, source.getText())).getClaferDependencies().get(0).getAlgorithm();
									if (selectedValue.equals("")) {
										selectedValue = claferDepend + source.getText();
									} else {
										if (!selectedValue.contains(source.getText())) {
											selectedValue += "||" + claferDepend + source.getText();

										}

									}
								}
							} else {
								selectedValue = selectedValue.replace("||" + claferDepend + source.getText(), "");

							}

						}
					});
				}

				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				break;
			case text:
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
				final Text inputField = new Text(container, SWT.BORDER);
				inputField.setSize(240, inputField.getSize().y);
				if (answers.get(0).getClaferDependencies() != null) {
					inputField.addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent event) {
							Text text = (Text) event.widget;

							claferDepend = answers.get(0).getClaferDependencies().get(0).getAlgorithm();
							selectedValue = claferDepend + text.getText();

						}
					});
				}
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				break;
			case radiobutton:
				Button[] button = new Button[answers.size()];

				for (int i = 0; i < answers.size(); i++) {
					String ans = answers.get(i).getValue();
					button[i] = new Button(container, SWT.RADIO);
					button[i].setText(ans);
					button[i].addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							Button source = (Button) e.getSource();

							if (source.getSelection()) {
								if (answers.get(getIndex(answers, source.getText())).getClaferDependencies() != null) {
									claferDepend = answers.get(getIndex(answers, source.getText())).getClaferDependencies().get(0).getAlgorithm();
									selectedValue = claferDepend;
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
						selectedValue = claferDepend + text.getText();

					}
				});
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				break;
			case composed:
				container.setLayout(new RowLayout());
				Button[] radioButton = new Button[answers.size()];
				for (int i = 0; i < answers.size(); i++) {
					String ans = answers.get(i).getValue();
					radioButton[i] = new Button(container, SWT.RADIO);
					radioButton[i].setText(ans);
				}
				Text[] textField = new Text[answers.size()];

				for (int i = 0; i < answers.size(); i++) {
					textField[i] = new Text(container, SWT.SINGLE | SWT.BORDER);
					radioButton[i].addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							Button source = (Button) e.getSource();
							if (answers.get(getIndex(answers, source.getText())).getClaferDependencies() != null) {
								claferDepend = answers.get(getIndex(answers, source.getText())).getClaferDependencies().get(0).getAlgorithm();

							}
							if (source.getText().equals("fixed size"))
								textField[1].setEnabled(false);
							else
								textField[1].setEnabled(true);
						}
					});
					textField[i].addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent event) {
							Text text = (Text) event.widget;
							selectedValue = claferDepend + text.getText();
						}

					});

				}
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
			default:
				break;

		}
	}

	public HashMap<Question, Answer> getMap() {
		return this.selectionMap;
	}

	public int getIndex(List<Answer> answers, String value) {
		int index = -1;
		for (int i = 0; i < answers.size(); i++) {
			if (answers.get(i).getValue().equals(value))
				index = i;

		}
		return index;

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

	@Override
	public IWizardPage getPreviousPage() {
		final IWizardPage prev = super.getPreviousPage();
		if (prev != null && prev instanceof PrimitiveQuestionnairePage) {
			return getWizard().getPreviousPage(this);
		}
		return prev;
	}

	public synchronized HashMap<Question, Answer> getSelection() {
		return this.selectionMap;
	}

}
