package de.cognicrypt.codegenerator.primitive.wizard.questionnaire;

import java.util.List;

import org.eclipse.swt.widgets.Button;

import de.cognicrypt.codegenerator.question.Answer;

public class PrimitiveQuestionPageUtility {

		/* This method returns the index of the answer that has value in it
		 * 
		 * return @index
		 */
		public int getIndex(List<Answer> answers, String value) {
			int index = -1;
			for (int i = 0; i < answers.size(); i++) {
				if (answers.get(i).getValue().equals(value))
					index = i;

			}
			return index;

		}
		/* This method 
		 * 
		 * 
		 */
		public String getOutputFromWidget(String claferDepend, Button source){
			return claferDepend + source.getSelection();
		
		}
		
		
		
}