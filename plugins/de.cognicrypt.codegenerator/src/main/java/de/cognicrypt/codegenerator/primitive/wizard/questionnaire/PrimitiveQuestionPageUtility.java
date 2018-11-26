/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.wizard.questionnaire;

import java.util.List;

import de.cognicrypt.codegenerator.question.Answer;

public class PrimitiveQuestionPageUtility {

	/*
	 * This method returns the index of the answer that has value in it return @index
	 */
	public int getIndex(List<Answer> answers, String value) {
		int index = -1;
		for (int i = 0; i < answers.size(); i++) {
			if (answers.get(i).getValue().equals(value))
				index = i;

		}
		return index;

	}

}
