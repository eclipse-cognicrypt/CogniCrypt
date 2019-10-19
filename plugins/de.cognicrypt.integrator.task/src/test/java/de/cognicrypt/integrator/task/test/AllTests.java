/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({XSLStringGenerationAndManipulationTests.class, ClaferFeatureTest.class, ClaferModelContentProviderTest.class, ClaferModelTest.class,
		ClaferPatternEnumGeneratorTest.class, ClaferValidationTest.class, FileUtilitiesTest.class, ModelForTITasksTests.class, QuestionAndPageModelTest.class,
		QuestionJSONFileTests.class, XmlRegionAnalyzerTests.class, XslPageContentProviderTest.class, XSLTests.class, DefaultFeatureSetTest.class})
public class AllTests {

}
