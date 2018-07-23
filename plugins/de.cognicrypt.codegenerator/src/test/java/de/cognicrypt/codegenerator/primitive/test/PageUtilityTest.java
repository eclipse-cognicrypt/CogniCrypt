/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionPageUtility;
import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionsJSONReader;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;

public class PageUtilityTest {

	PrimitiveQuestionPageUtility util = new PrimitiveQuestionPageUtility();
	PrimitiveQuestionsJSONReader pqjr = new PrimitiveQuestionsJSONReader();
	String testFile1 = Constants.testPrimitverFolder + "PrimitiveQuestionTest.json";

	@Test
	public void testGetIndex() {
		List<Question> questions = pqjr.getPages(this.testFile1).get(0).getContent();

		int index = util.getIndex(questions.get(0).getAnswers(), "OFB");
		assertEquals(index, 1);

	}
}
