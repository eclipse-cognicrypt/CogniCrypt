package crossing.e1.configurator.wizard;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.configurator.utilities.Utilities;
import crossing.e1.configurator.wizard.advanced.DisplayValuePage;
import crossing.e1.configurator.wizard.advanced.ValueSelectionPage;
import crossing.e1.configurator.wizard.beginner.DummyPage;
import crossing.e1.configurator.wizard.beginner.RelevantQuestionsPage;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

import org.clafer.ast.*;

public class MyWizard extends Wizard {

	protected TaskSelectionPage taskListPage;
	protected WizardPage valueListPage;
	protected InstanceListPage instanceListPage;
	protected DisplayValuePage finalValueListPage;
	private boolean advancedMode;
	private ClaferModel claferModel;
	InstanceGenerator instanceGenerator = new InstanceGenerator();

	public MyWizard() {
		super();
		setWindowTitle("Configurator v1.0");
		setNeedsProgressMonitor(true);
		advancedMode = false;
	}

	@Override
	public void addPages() {
		this.claferModel = new ClaferModel(new ReadConfig().getClaferPath());
		taskListPage = new TaskSelectionPage(claferModel);
		this.setForcePreviousAndNextButtons(true);
		addPage(taskListPage);

	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		if (finalValueListPage.isPageComplete())
			return true;
		return false;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == taskListPage && taskListPage.canProceed()) {
			
			instanceGenerator.setTaskName(taskListPage.getValue());
			instanceGenerator.setNoOfInstances(0);
			
			if(taskListPage.isAdvancedMode())
				valueListPage = new ValueSelectionPage(null, claferModel);
			else
				valueListPage = new RelevantQuestionsPage(claferModel, taskListPage.getSelectedTask().getRelevantQuestions());
				
			addPage(valueListPage);
			
			return valueListPage;
		} else if (currentPage == valueListPage) {
			if (taskListPage.isAdvancedMode() && ((ValueSelectionPage) valueListPage).getPageStatus() == true){
				if (((ValueSelectionPage) valueListPage).validate(instanceGenerator, claferModel)) {
					instanceListPage = new InstanceListPage(instanceGenerator);
					addPage(instanceListPage);
					return instanceListPage;
				}
			}else{
				//running in beginner mode
				if(((RelevantQuestionsPage) valueListPage).validate(instanceGenerator, claferModel)){					
					DummyPage dummyPage = new DummyPage();
					addPage(dummyPage);
					return dummyPage;
				}
			}
		} else if (currentPage == instanceListPage) {
			finalValueListPage = new DisplayValuePage(
					instanceListPage.getValue());
			addPage(finalValueListPage);
			return finalValueListPage;
		}
		return currentPage;

	}

}