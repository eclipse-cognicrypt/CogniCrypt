package crossing.e1.primitive.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import crossing.e1.configurator.Constants.GUIElements;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Page;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.wizard.InstanceListPage;
import crossing.e1.configurator.wizard.advanced.AdvancedUserValueSelectionPage;
import crossing.e1.configurator.wizard.beginner.BeginnerTaskQuestionPage;
import crossing.e1.primitive.questionnaire.wizard.PrimitiveQuestionnaire;
import crossing.e1.primitive.questionnaire.wizard.PrimitiveQuestionnairePage;
import crossing.e1.primitive.types.Primitive;

public class IntegrationNewPrimitive extends Wizard {

	PrimitivePages selectedPrimitivePage;
	PrimitiveQuestionnaire primitiveQuestions;
	WizardPage preferenceSelectionPage;
	private HashMap<Question, Answer> constraints;
	static String test = "";
	StringBuilder data=new StringBuilder();

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

	private void createPrimitivePage(final Page curPage, final PrimitiveQuestionnaire primitiveQuestionnaire, int itr) {
		List<String> selection = null;
		if (curPage.getContent().size() == 1) {
			final Question curQuestion = curPage.getContent().get(0);
			//			if (curQuestion.getElement().equals(GUIElements.itemselection)) {
			//				selection = new ArrayList<>();
			//				for (final AstConcreteClafer childClafer : this.claferModel.getModel().getRoot().getSuperClafer().getChildren()) {
			//					if (childClafer.getSuperClafer().getName().endsWith(curQuestion.getSelectionClafer())) {
			//						selection.add(ClaferModelUtils.removeScopePrefix(childClafer.getName()));
			//					}
			//				}
			//			}
		}
		// Pass the questionnaire instead of the all of the questions. 
		this.preferenceSelectionPage = new PrimitiveQuestionnairePage(curPage, this.primitiveQuestions.getPrimitive(), primitiveQuestionnaire, selection, itr);
	}

	public IWizardPage getNextPage(final IWizardPage currentPage) {
		final Primitive selectedPrimitive = this.selectedPrimitivePage.getSelectedPrimitive();
		if (currentPage == this.selectedPrimitivePage && this.selectedPrimitivePage.isPageComplete()) {

			this.primitiveQuestions = new PrimitiveQuestionnaire(selectedPrimitive, selectedPrimitive.getXmlFile());
			this.preferenceSelectionPage = new PrimitiveQuestionnairePage(this.primitiveQuestions.nextPage(), this.primitiveQuestions.getPrimitive(), null);
			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);

			}

			return this.preferenceSelectionPage;
		}
		final PrimitiveQuestionnairePage primitiveQuestionPage = (PrimitiveQuestionnairePage) currentPage;
		final HashMap<Question, Answer> selectionMap = primitiveQuestionPage.getMap();
		if (primitiveQuestionPage.selectedValue != null)
			data.append("["+ primitiveQuestionPage.selectedValue + "]");
			data.append("\n");
//			test += "\n" + "[" + primitiveQuestionPage.selectedValue + "]";

		for (Entry<Question, Answer> entry : selectionMap.entrySet()) {
//			if (entry.getKey().getElement().equals(GUIElements.itemselection)) {
//
//			}

			this.constraints.put(entry.getKey(), entry.getValue());
		}
		if(primitiveQuestionPage.getItr()>0){
			int itr=primitiveQuestionPage.getItr();
			System.out.println("HERE is :"+ itr);
			
		}


		if (this.primitiveQuestions.hasMorePages()) {
			int nextID = -1;
			if (primitiveQuestionPage.getPageNextID() > -2) {
				nextID = primitiveQuestionPage.getPageNextID();
			} else {
				for (Entry<Question, Answer> entry : selectionMap.entrySet()) {
					nextID = entry.getValue().getNextID();
				}
			}
			if (nextID == 5) {
				System.out.println(data.toString());
			}
			if (nextID > -1) {
				final Page curPage = this.primitiveQuestions.setPageByID(nextID);
				createPrimitivePage(curPage, primitiveQuestions, primitiveQuestionPage.getItr());
				if (checkifInUpdateRound()) {
					this.primitiveQuestions.previousPage();
				}
				final IWizardPage[] pages = getPages();
				for (int i = 1; i < pages.length; i++) {
					if (!(pages[i] instanceof PrimitiveQuestionnairePage)) {
						continue;
					}
					final PrimitiveQuestionnairePage oldPage = (PrimitiveQuestionnairePage) pages[i];
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
		if (!checkifInUpdateRound() &&currentPage instanceof PrimitiveQuestionnairePage || lastPage) {
			if (!this.primitiveQuestions.isFirstPage()) {
				this.primitiveQuestions.previousPage();
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