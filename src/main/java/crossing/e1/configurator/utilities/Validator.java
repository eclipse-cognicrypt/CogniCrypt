package crossing.e1.configurator.utilities;

import crossing.e1.configurator.Lables;
import crossing.e1.configurator.ReadConfig;
import crossing.e1.featuremodel.clafer.ClaferModel;
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
				return false;
		}
	}
}
