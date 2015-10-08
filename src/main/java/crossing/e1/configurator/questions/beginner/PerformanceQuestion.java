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

package crossing.e1.configurator.questions.beginner;

import crossing.e1.configurator.wizard.beginner.Constraint;

public class PerformanceQuestion extends CryptoQuestion {
	
	
	public PerformanceQuestion(){
		super("Is high performance important to you?");
		correspondingClaferProperty = "c0_performance";
		choices.put("Yes", new Constraint(correspondingClaferProperty, Constraint.Operator.GTE, 2));
		choices.put("No", null);
	}

}
