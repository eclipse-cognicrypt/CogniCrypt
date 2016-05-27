package crossing.e1.configurator.utilities;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class Validator {

	/**
	 * @return Validation method which will be invoked upon clicking next on the valueList page Next widgetPage is only
	 *         accessible if there are more than 0 instances for a given clafer & the chosen values
	 */
	public boolean validate(final InstanceGenerator gen) {
		boolean empty;
		if (!(empty = gen.getNoOfInstances() > 0)) {
			Activator.getDefault().logError(Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE);
		//	displayError(Constants.NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE);			
		}
		return empty;
	}
	
	private void displayError(String message) {
		MessageDialog.openError(new Shell(), "Error", message);
	}

}
