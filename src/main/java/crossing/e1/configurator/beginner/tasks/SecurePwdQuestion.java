package crossing.e1.configurator.beginner.tasks;

import crossing.e1.configurator.beginner.questions.MemoryQuestion;
import crossing.e1.configurator.beginner.questions.PerformanceQuestion;

public class SecurePwdQuestion extends CryptoTask {

	public SecurePwdQuestion(){
		//FIXME: hard coding scope c0 for now to be able to match tasks
		super("A hash representation of the password will be created using a key derivation algorithm. You can then store this irreversable hash in the database.", "Represent password in a secure way for storage", "c0_SecurePassword");
		relevantQuestions.add(new PerformanceQuestion());
		relevantQuestions.add(new MemoryQuestion());
	}
}
