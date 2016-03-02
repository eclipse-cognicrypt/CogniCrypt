package crossing.e1.configurator.utilities;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import crossing.e1.configurator.Constants;
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
					Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE);
			return false;
		}
	}

	private void displayError(String message) {
		MessageDialog.openError(new Shell(), "Error", message);
	}
}
