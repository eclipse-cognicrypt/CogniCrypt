package crossing.e1.primitive.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import crossing.e1.configurator.Constants.GUIElements;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.ClaferDependency;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.wizard.InstanceListPage;
import crossing.e1.configurator.wizard.advanced.AdvancedUserValueSelectionPage;
import crossing.e1.configurator.wizard.beginner.BeginnerTaskQuestionPage;
import crossing.e1.featuremodel.clafer.ClaferModelUtils;
import crossing.e1.primitive.questionnaire.PrimitiveQuestionnaire;
import crossing.e1.primitive.questionnaire.PrimitiveQuestionnairePage;
import crossing.e1.primitive.types.Primitive;

public class IntegrationNewPrimitive extends Wizard {

	PrimitivePages selectedPrimitivePage;
	PrimitiveQuestionnaire questionnaire;
	WizardPage preferenceSelectionPage;

	public IntegrationNewPrimitive() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addPages() {
		selectedPrimitivePage = new PrimitivePages();
		setForcePreviousAndNextButtons(true);
		addPage(selectedPrimitivePage);
	}

	private boolean checkifInUpdateRound() {
		boolean updateRound = false;
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (final StackTraceElement el : stack) {
			if (el.getMethodName().contains("updateButtons")) {
				updateRound = true;
				break;
			}
		}
		return updateRound;
	}

	private void createBeginnerPage(final Question curQuestion, final List<Question> allQuestion) {
		if (curQuestion.getElement().equals(GUIElements.itemselection)) {
			final List<String> selection = new ArrayList<>();

			this.preferenceSelectionPage = new PrimitiveQuestionnairePage(curQuestion, this.questionnaire.getPrimitive(), selection);
		} else if (curQuestion.getElement().equals(GUIElements.button)) {
			this.preferenceSelectionPage = new PrimitiveQuestionnairePage(allQuestion, curQuestion, this.questionnaire.getPrimitive());
		} else {
			this.preferenceSelectionPage = new PrimitiveQuestionnairePage(curQuestion, this.questionnaire.getPrimitive());
		}
	}

	public IWizardPage getNextPage(final IWizardPage currentPage) {
		final Primitive selectedPrimitive = this.selectedPrimitivePage.getSelectedPrimitive();
		if (currentPage == this.selectedPrimitivePage && this.selectedPrimitivePage.isPageComplete()) {

			this.questionnaire = new PrimitiveQuestionnaire(selectedPrimitive, selectedPrimitive.getXmlFile());
			this.preferenceSelectionPage = new PrimitiveQuestionnairePage(this.questionnaire.nextQuestion(), this.questionnaire.getPrimitive());

			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);

			}

			return this.preferenceSelectionPage;

		}

		final Entry<Question, Answer> entry = PrimitiveQuestionnairePage.getMap();
		if (this.questionnaire.hasMoreQuestions()) {
			final int nextID = entry.getValue().getNextID();
			if (nextID > -1) {
				final Question curQuestion = this.questionnaire.setQuestionByID(nextID);
				final List<Question> allQuestion = this.questionnaire.getQuestionList();

				createBeginnerPage(curQuestion, allQuestion);
				if (checkifInUpdateRound()) {
					this.questionnaire.previousQuestion();
				}
				final IWizardPage[] pages = getPages();
				for (int i = 1; i < pages.length; i++) {
					if (!(pages[i] instanceof BeginnerTaskQuestionPage)) {
						continue;
					}
					final BeginnerTaskQuestionPage oldPage = (BeginnerTaskQuestionPage) pages[i];
					if (oldPage.equals(this.preferenceSelectionPage)) {
						return oldPage;
					}
				}
				if (this.preferenceSelectionPage != null) {
					addPage(this.preferenceSelectionPage);
				}

				return this.preferenceSelectionPage;
			}
		}
		return currentPage;
	}

	public IWizardPage getPreviousPage(final IWizardPage currentPage) {
		final boolean lastPage = currentPage instanceof InstanceListPage;
		if (currentPage instanceof PrimitiveQuestionnairePage || lastPage) {
			if (!this.questionnaire.isFirstQuestion()) {
				this.questionnaire.previousQuestion();
			}
		}
		return super.getPreviousPage(currentPage);
	}

	@Override
	public boolean performFinish() {

		// TODO Auto-generated method stub
		return true;
	}

	public boolean performCancel() {
		boolean ans = MessageDialog.openConfirm(getShell(), "Confirmation", "Are you sure to close without integrating the new primitve?");
		if (ans)
			return true;
		else
			return false;
	}
}