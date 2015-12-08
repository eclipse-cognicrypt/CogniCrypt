package crossing.e1.configurator.utilities;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class Validator {

	/**
	 * @return Validation method which will be invoked upon clicking next on the
	 *         valueList page Next widgetPage is only accessible if there are
	 *         more than 0 instances for a given clafer & the chosen values
	 */
	public boolean validate(InstanceGenerator gen) {
		if (gen.getNoOfInstances() > 0) {
			return true;
		} else {
			displayError(
					" No possible combinations are available for chosen values , please modify your prefference and try agin.\n \n You can use  \n >= insted of >\n<= insted of <\nto make your selection generic.");
			return false;
		}
	}

	private void displayError(String message) {
		MessageDialog.openError(new Shell(), "Error", message);
	}
}
