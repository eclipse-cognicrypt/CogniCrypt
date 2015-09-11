package crossing.e1.configurator.beginner.questions;

import static org.clafer.ast.Asts.$this;
import static org.clafer.ast.Asts.constant;
import static org.clafer.ast.Asts.equal;
import static org.clafer.ast.Asts.greaterThan;
import static org.clafer.ast.Asts.join;
import static org.clafer.ast.Asts.joinRef;

import org.clafer.ast.AstConstraint;

public class PerformanceQuestion extends CryptoQuestion {
	
	
	public PerformanceQuestion(){
		super("Is high performance important to you?");
		choices.put("Yes", equal(
				joinRef($this()),
				constant("High")));
		choices.put("No", null);
	}

}
