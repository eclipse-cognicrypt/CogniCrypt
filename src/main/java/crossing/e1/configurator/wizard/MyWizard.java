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
		
		List<AstConcreteClafer> tasks =   new ClaferModel().model.getChildren();//model.getClafersByType("c0_Task").stream().toArray(AstClafer[]::new);
		taskSelectionPage = new TaskSelectionPage(tasks);
		two = new MyPageTwo();
		addPage(taskSelectionPage);
		addPage(two);
	}

	@Override
	public boolean performFinish() {
		// Print the result to the console
		System.out.println(taskSelectionPage.getSelction());
		System.out.println(two.getText1()+" ANd here goes values "+new ClaferModel().model.getChildren());

		return true;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
	    if (currentPage == taskSelectionPage) {
	    	// TODO
//	    	AstClafer selectedTask = ((TaskSelectionPage) currentPage).getSelction();
//	    	two.setTitle(selectedTask.getName());
//	    	selectedTask.getChildren().forEach(child -> two.addField(child.getName()));
	    	addPage(two);
	    	return two;
	    }else{
	    	return super.getNextPage(currentPage);
	    }
	    //return null;
	} 
	
}
