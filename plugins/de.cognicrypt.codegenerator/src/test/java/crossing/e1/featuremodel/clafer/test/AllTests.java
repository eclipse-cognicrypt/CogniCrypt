package crossing.e1.featuremodel.clafer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crossing.e1.primitiveintegration.test.PageUtilityTest;
import crossing.e1.primitiveintegration.test.ProviderFileWriterTest;

@RunWith(Suite.class)
@SuiteClasses({ ClaferModelUtilsTest.class, ClaferComparatorTest.class, QuestionReaderTest.class, XMLParserTest.class })
public class AllTests {

}
