package crossing.e1.featuremodel.clafer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cognicrypt.codegenerator.primitive.test.PageUtilityTest;
import de.cognicrypt.codegenerator.primitive.test.ProviderFileWriterTest;

@RunWith(Suite.class)
@SuiteClasses({ ClaferModelUtilsTest.class, ClaferComparatorTest.class, QuestionReaderTest.class, XMLParserTest.class })
public class AllTests {

}
