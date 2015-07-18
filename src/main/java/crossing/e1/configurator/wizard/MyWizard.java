package crossing.e1.configurator.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

import org.clafer.ast.*;
import org.clafer.instance.InstanceClafer;

public class MyWizard extends Wizard {

	protected TaskSelectionPage pageOne;
	protected MyPageTwo pageTwo;
	private ClaferModel claferModel;
	InstanceGenerator gen = new InstanceGenerator();

	

	public MyWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {
		return "Export My Data";
	}

	@Override
	public void addPages() {
		this.claferModel = new ClaferModel(new ReadConfig().getClaferPath());
		List<AstConcreteClafer> tasks = claferModel.getModel().getChildren();
		pageOne = new TaskSelectionPage(tasks, claferModel);
		this.setForcePreviousAndNextButtons(true);
		addPage(pageOne);
		
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		System.out.println(pageOne.isPageComplete());
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {

		if (currentPage == pageOne && pageOne.isSecure()) {
			gen.generateInstances(claferModel,pageOne.getMap() );
			if (gen.getNoOfInstances() > 0) {
				pageTwo = new MyPageTwo(gen);
				addPage(pageTwo);
			}
			return pageTwo;
		} else {
			return currentPage;
		}
	}

}