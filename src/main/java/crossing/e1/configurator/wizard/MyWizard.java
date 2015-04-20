package crossing.e1.configurator.wizard;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import crossing.e1.featuremodel.clafer.ClaferModel;

import org.clafer.ast.*;

public class MyWizard extends Wizard {

	protected TaskSelectionPage taskSelectionPage;
	protected MyPageTwo two;
	private ClaferModel model;

	public MyWizard() {
		super();
		setNeedsProgressMonitor(true);
		model = new ClaferModel();
	}

	@Override
	public String getWindowTitle() {
		return "Export My Data";
	}

	@Override
	public void addPages() {
		String[] tasks = model.getClafersByType("Primate").stream()
				.map(clafer -> clafer.getName()).toArray(String[]::new);
		taskSelectionPage = new TaskSelectionPage(tasks);
		two = new MyPageTwo();
		addPage(taskSelectionPage);
		addPage(two);
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		System.out.println(taskSelectionPage.getSelction());
		System.out.println(two.getText1());

		return true;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
	    if (currentPage == taskSelectionPage) {
	    	two.setTitle(((TaskSelectionPage) currentPage).getSelction());
	    	return two;
	    }else{
	    	return super.getNextPage(currentPage);
	    }
	    //return null;
	} 
}
