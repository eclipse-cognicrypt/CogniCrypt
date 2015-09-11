package crossing.e1.configurator.beginner.questions;

import static org.clafer.ast.Asts.$this;
import static org.clafer.ast.Asts.constant;
import static org.clafer.ast.Asts.equal;
import static org.clafer.ast.Asts.greaterThan;
import static org.clafer.ast.Asts.join;
import static org.clafer.ast.Asts.joinRef;

import org.clafer.ast.AstConstraint;

public class MemoryQuestion extends CryptoQuestion {
	
	
	public MemoryQuestion(){
		super("Is low memory consumption important to you?");
		choices.put("Yes", equal(
				joinRef($this()),
				constant("Low")));
		choices.put("No", null);
	}

}
