/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.XSLPageContentProvider;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;
import de.cognicrypt.integrator.task.wizard.XslPage;

public class XslPageContentProviderTest {

	private static XSLPageContentProvider xslPageContentProvider;

	@BeforeClass
	public static void prepareContentProvider() {
		xslPageContentProvider = new XSLPageContentProvider();
	}

	@Test
	public void testGetChildren() {
		assertNull(xslPageContentProvider.getChildren(new CodeDependency()));

		// if a ClaferFeature object is passed, the call should be passed
		// to the super class ClaferModelContentProvider
		final Object[] claferFeatureChildren = xslPageContentProvider.getChildren(new ClaferFeature(Constants.FeatureType.ABSTRACT, "", ""));
		assertNotNull(claferFeatureChildren);
	}

	@Test
	public void testGetElements() {
		// create a Clafer model containing only a task DigitalSignatures with a description
		final ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "DigitalSignatures", "Task");
		final ArrayList<ClaferProperty> propertyList = new ArrayList<>();
		propertyList.add(new ClaferProperty("description", "string"));
		cfrFeature.setFeatureProperties(propertyList);

		final ClaferModel claferModel = new ClaferModel();
		claferModel.add(cfrFeature);

		// create a question with two possible answers and a code dependency each
		final CodeDependency codeDep1 = new CodeDependency();
		codeDep1.setOption("signing");
		codeDep1.setValue("true");
		final CodeDependency codeDep2 = new CodeDependency();
		codeDep2.setOption("signing");
		codeDep2.setValue("false");

		final Question question = new Question();
		final Answer answer1 = new Answer();
		final Answer answer2 = new Answer();
		final ArrayList<CodeDependency> deps1 = new ArrayList<>();
		deps1.add(codeDep1);
		final ArrayList<CodeDependency> deps2 = new ArrayList<>();
		deps2.add(codeDep2);
		answer1.setCodeDependencies(deps1);
		answer2.setCodeDependencies(deps2);

		final ArrayList<Answer> answers = new ArrayList<>();
		answers.add(answer1);
		question.setAnswers(answers);
		answers.add(answer2);

		final ArrayList<Question> questionList = new ArrayList<Question>();
		questionList.add(question);

		final XSLPageContentProvider xslPageContentProvider = new XSLPageContentProvider();
		final Object[] contentProviderElements = xslPageContentProvider.getElements(new Object[] {claferModel, questionList});

		// there should be three elements, the task clafer and the two code dependencies
		assertEquals(3, contentProviderElements.length);
	}

	@Test
	public void testMergeLists() {
		final ClaferModel claferModel = new ClaferModel();
		claferModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, "featureName", "featureInheritance"));

		final CodeDependency codeDep1 = new CodeDependency();
		codeDep1.setOption("opt");
		codeDep1.setValue("val");
		final CodeDependency codeDep2 = new CodeDependency();
		codeDep2.setOption("opt1");
		codeDep2.setValue("val1");
		final CodeDependency[] depList = new CodeDependency[] {codeDep1, codeDep2};

		final Object[] firstList = claferModel.getClaferModel().toArray();
		final Object[] secondList = depList;

		assertEquals(3, XslPage.mergeLists(firstList, secondList).length);
	}

	@Test
	public void testHasChildren() {
		assertFalse(xslPageContentProvider.hasChildren(new CodeDependency()));
	}

}
