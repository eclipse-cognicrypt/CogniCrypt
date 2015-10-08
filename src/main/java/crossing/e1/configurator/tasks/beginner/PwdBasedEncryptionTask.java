/**
 * Copyright 2015 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * @author Sarah Nadi
 *
 */

package crossing.e1.configurator.tasks.beginner;

import crossing.e1.configurator.questions.beginner.MemoryQuestion;
import crossing.e1.configurator.questions.beginner.PerformanceQuestion;

public class PwdBasedEncryptionTask extends CryptoTask {
	
	public PwdBasedEncryptionTask(){
		//FIXME: hard coding scope c0 for now to be able to match tasks
		super("A secret key is derviced from a password and then used to encrypt your data", "Encrypt data using a given password", "c0_PasswordBasedEncryption");
		relevantQuestions.add(new PerformanceQuestion());
		relevantQuestions.add(new MemoryQuestion());
	}
}
