package crossing.e1.configurator.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

public class IntegrationNewPrimitive extends Wizard {

	public IntegrationNewPrimitive() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addPages() {
		addPage(new PrimitivePages());
		addPage(new PrimitivePage2());

	}

	@Override
	public boolean performFinish() {
		
		// TODO Auto-generated method stub
		return true;
	}
	public boolean performCancel() {
		boolean ans = MessageDialog.openConfirm(getShell(), "Confirmation",
				"Are you sure to close without integrating the new primitve?");
		if (ans)
			return true;
		else
			return false;
	}

}