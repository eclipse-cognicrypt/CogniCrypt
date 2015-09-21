package crossing.e1.configurator.beginner.tasks;

import crossing.e1.configurator.beginner.questions.MemoryQuestion;
import crossing.e1.configurator.beginner.questions.PerformanceQuestion;

public class PwdBasedEncryptionTask extends CryptoTask {
	
	public PwdBasedEncryptionTask(){
		//FIXME: hard coding scope c0 for now to be able to match tasks
		super("A secret key is derviced from a password and then used to encrypt your data", "Encrypt data using a given password", "c0_PasswordBasedEncryption");
		relevantQuestions.add(new PerformanceQuestion());
		relevantQuestions.add(new MemoryQuestion());
	}
}
