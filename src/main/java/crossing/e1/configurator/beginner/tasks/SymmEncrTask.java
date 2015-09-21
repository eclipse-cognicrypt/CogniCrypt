package crossing.e1.configurator.beginner.tasks;

import crossing.e1.configurator.beginner.questions.MemoryQuestion;
import crossing.e1.configurator.beginner.questions.PerformanceQuestion;

public class SymmEncrTask extends CryptoTask{
	
	public SymmEncrTask(){
		//FIXME: hard coding scope c0 for now to be able to match tasks
		super("Encrypt data using a symmetric cipher. Symmetric ciphers use the same secret key for both encryption and decryption", "Encrypt data using a secret key", "c0_SymmetricEncryption");
		relevantQuestions.add(new PerformanceQuestion());
		relevantQuestions.add(new MemoryQuestion());
	}

}
