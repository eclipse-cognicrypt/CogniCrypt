package crossing.e1.configurator.wizard;

import java.util.List;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import org.clafer.ast.*;

public class MyWizard extends Wizard {

	protected TaskSelectionPage welcomePage;
	protected ValueSelectionPage pageOne;
	protected MyPageTwo pageTwo;
	protected MyPageThree pageThree;
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
		welcomePage = new TaskSelectionPage(claferModel);
		this.setForcePreviousAndNextButtons(true);
		addPage(welcomePage);

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
			gen.generateInstances(claferModel, pageOne.getMap());
			if (gen.getNoOfInstances() > 0) {
				pageTwo = new MyPageTwo(gen);
				addPage(pageTwo);
			}
			return pageTwo;
		} else if (currentPage == pageTwo) {
			pageThree = new MyPageThree(pageTwo.getValue());
			addPage(pageThree);
			return pageThree;
		} else {
			return currentPage;
		}

	}

}