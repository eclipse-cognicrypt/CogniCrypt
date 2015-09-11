package crossing.e1.configurator.wizard;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.configurator.utilities.Utilities;
import crossing.e1.configurator.wizard.advanced.DisplayValuePage;
import crossing.e1.configurator.wizard.advanced.ValueSelectionPage;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

import org.clafer.ast.*;

public class MyWizard extends Wizard {

	protected TaskSelectionPage taskListPage;
	protected ValueSelectionPage valueListPage;
	protected InstanceListPage instanceListPage;
	protected DisplayValuePage finalValueListPage;
	private ClaferModel claferModel;
	InstanceGenerator instanceGenerator = new InstanceGenerator();

	public MyWizard() {
		super();
		setWindowTitle("Configurator v1.0");
		setNeedsProgressMonitor(true);
		
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
			valueListPage = new ValueSelectionPage(null, claferModel);
			instanceGenerator.setTaskName(taskListPage.getValue());
			addPage(valueListPage);
			instanceGenerator.setNoOfInstances(0);
			return valueListPage;
		} else if (currentPage == valueListPage) {
			if (valueListPage.getPageStatus() == true)
				if (valueListPage.validate(instanceGenerator, claferModel)) {
					instanceListPage = new InstanceListPage(instanceGenerator);
					addPage(instanceListPage);
					return instanceListPage;
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