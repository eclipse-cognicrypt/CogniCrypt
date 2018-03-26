package de.cognicrypt.codegenerator.taskintegrator.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClaferFeatureTest.class, ClaferModelContentProviderTest.class, ClaferModelTest.class, ClaferPatternEnumGeneratorTest.class, QuestionAndPageModelTest.class, QuestionJSONFileTests.class, XmlRegionAnalyzerTests.class, XSLStringGenerationAndManipulationTests.class, XSLTests.class })
public class AllTests {

}
