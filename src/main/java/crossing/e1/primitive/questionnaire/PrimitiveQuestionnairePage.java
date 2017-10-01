
package crossing.e1.primitive.questionnaire;

import java.util.HashMap;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
	// Removed the allquestions variable as it was not longer required.
	private PrimitiveQuestionnaire PrimitiveQuestionnaire;
	private HashMap<Question, Answer> selectionMap = new HashMap<Question, Answer>(); 
	private boolean finish = false;
	private List<String> selectionValues;

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
	 * @param PrimitiveQuestionnaire Updated this parameter in the constructor to accept the questionnaire instead of all the questions. 
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
	 * @param PrimitiveQuestionnaire Updated this parameter in the constructor to accept the questionnaire instead of all the questions. 
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
				final ComboViewer comboViewer = new ComboViewer(container, SWT.DROP_DOWN | SWT.READ_ONLY);
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				comboViewer.setInput(answers);

				comboViewer.addSelectionChangedListener(selectedElement -> {
					final IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
					PrimitiveQuestionnairePage.this.selectionMap.put(question, (Answer) selection.getFirstElement());
				});
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				comboViewer.setSelection(new StructuredSelection(question.getDefaultAnswer()));
				break;
			case checkbox:
				container.setLayout(new RowLayout(SWT.VERTICAL));
				for (int i = 0; i < answers.size(); i++) {
					String ans = answers.get(i).getValue();
					new Button(container, SWT.CHECK).setText(ans);
				}
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				break;
			case text:
				
				final Text inputField = new Text(container, SWT.BORDER);
				inputField.setSize(240, inputField.getSize().y);
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				inputField.forceFocus();
				break;
			case radiobutton: 
				for (int i = 0; i < answers.size(); i++) {
					String ans = answers.get(i).getValue();
					new Button(container, SWT.RADIO).setText(ans);
				}
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				break;
			case textarea:
				final Text inputDescription = new Text(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
				// Define a minimum width
				final GridData gridData = new GridData();
				gridData.widthHint = 230;
				gridData.heightHint = 60;
				inputDescription.setLayoutData(gridData);
				this.finish = true;
				PrimitiveQuestionnairePage.this.setPageComplete(this.finish);
				break;
			case composed:
				container.setLayout(new RowLayout());
				for (int i = 0; i < answers.size(); i++) {
					String ans = answers.get(i).getValue();
					new Button(container, SWT.RADIO).setText(ans);
				}
				for (int i = 0; i < answers.size(); i++) {
					new Text(container, SWT.SINGLE|SWT.BORDER);
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
