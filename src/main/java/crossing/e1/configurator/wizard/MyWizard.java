package crossing.e1.configurator.wizard;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

import org.clafer.ast.*;
import org.clafer.instance.InstanceClafer;

public class MyWizard extends Wizard {

	protected TaskSelectionPage taskSelectionPage;
	protected MyPageTwo two;
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
		taskSelectionPage = new TaskSelectionPage(tasks, claferModel);

		two = new MyPageTwo(gen);
		addPage(taskSelectionPage);
		addPage(two);
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		System.out.println(taskSelectionPage.isPageComplete());
		System.out.println(two.getText1());

		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		
		if (currentPage == taskSelectionPage && taskSelectionPage.isSecure()) {
			 gen.generateInstances(claferModel, 1, 2);
			 Map<String,InstanceClafer> inst=gen.getInstances();
			if (gen.getNoOfInstances() > 0) {
				two.setValue(inst.keySet());
				addPage(two);
			}
			return two;
		} else {
			return currentPage;
		}
	}

}