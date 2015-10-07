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

import java.util.HashMap;

import crossing.e1.configurator.wizard.beginner.Constraint;

public abstract class CryptoQuestion {
	
	protected String questionText;
	protected String correspondingClaferProperty;
	protected HashMap<String, Constraint> choices;

	public CryptoQuestion(String questionText, HashMap<String, Constraint> choices){
		this.questionText = questionText;
		this.choices = choices;
	}
	
	public CryptoQuestion(String questionText){
		this.questionText = questionText;
		this.choices = new HashMap<String, Constraint>();		
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public HashMap<String, Constraint> getChoices() {
		return choices;
	}

	public void setChoices(HashMap<String, Constraint> choices) {
		this.choices = choices;
	}
	
	public Constraint getCorrespondingChoiceConstraint(String key){
		return choices.get(key);
	}
	
	
}
