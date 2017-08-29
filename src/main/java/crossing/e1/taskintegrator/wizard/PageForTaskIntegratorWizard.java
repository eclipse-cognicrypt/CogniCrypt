package crossing.e1.taskintegrator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


public class PageForTaskIntegratorWizard extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public PageForTaskIntegratorWizard(String name, String title, String description) {
		super(name);
		setTitle(title);
		setDescription(description);
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		setControl(container);
	}

}
