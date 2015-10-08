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

import java.util.ArrayList;
import java.util.List;

import crossing.e1.configurator.questions.beginner.CryptoQuestion;

public abstract class CryptoTask {
	
	protected String description;
	protected String displayText;
	protected List<CryptoQuestion> relevantQuestions;
	protected String claferTaskName;
	
	public CryptoTask(String description, String displayText, String claferTaskName){
		this.description = description;
		this.displayText = displayText;
		this.claferTaskName = claferTaskName;
		relevantQuestions = new ArrayList<CryptoQuestion>();
	}
	
	public CryptoTask(String description, String displayText, String claferTaskName, List<CryptoQuestion> questions){
		this.description = description;
		this.displayText = displayText;
		this.claferTaskName = claferTaskName;
		relevantQuestions = questions;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public List<CryptoQuestion> getRelevantQuestions() {
		return relevantQuestions;
	}

	public void setRelevantQuestions(List<CryptoQuestion> relevantQuestions) {
		this.relevantQuestions = relevantQuestions;
	}

	public String getClaferTaskName() {
		return claferTaskName;
	}

	public void setClaferTaskName(String claferTaskName) {
		this.claferTaskName = claferTaskName;
	}
	
	

}
