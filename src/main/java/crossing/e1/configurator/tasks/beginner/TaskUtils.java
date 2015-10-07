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

import java.util.HashSet;

public final class TaskUtils {

	public static HashSet<CryptoTask> getAvailableTasks(){
		HashSet<CryptoTask> availableTasks = new HashSet<CryptoTask>();
		//TODO: get all subclasses of CryptoTask instead of adding them here
		availableTasks.add(new SymmEncrTask());
		availableTasks.add(new PwdBasedEncryptionTask());
		availableTasks.add(new SecurePwdQuestion());
		return availableTasks;
	}
}
