package crossing.e1.configurator.beginner.questions;

import static org.clafer.ast.Asts.$this;
import static org.clafer.ast.Asts.constant;
import static org.clafer.ast.Asts.equal;
import static org.clafer.ast.Asts.greaterThan;
import static org.clafer.ast.Asts.join;
import static org.clafer.ast.Asts.joinRef;

import org.clafer.ast.AstConstraint;

import crossing.e1.configurator.wizard.beginner.Constraint;

public class MemoryQuestion extends CryptoQuestion {
	
	
	public MemoryQuestion(){
		super("Is low memory consumption important to you?");
		correspondingClaferProperty = "c0_memory";
		choices.put("Yes", new Constraint(correspondingClaferProperty, Constraint.Operator.GTE, 2));
		choices.put("No", null);
	}

}
