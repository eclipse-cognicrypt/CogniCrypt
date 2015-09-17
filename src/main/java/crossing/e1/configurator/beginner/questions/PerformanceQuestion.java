package crossing.e1.configurator.beginner.questions;

import crossing.e1.configurator.wizard.beginner.Constraint;

public class PerformanceQuestion extends CryptoQuestion {
	
	
	public PerformanceQuestion(){
		super("Is high performance important to you?");
		correspondingClaferProperty = "c0_performance";
		choices.put("Yes", new Constraint(correspondingClaferProperty, Constraint.Operator.GTE, 2));
		choices.put("No", null);
	}

}
