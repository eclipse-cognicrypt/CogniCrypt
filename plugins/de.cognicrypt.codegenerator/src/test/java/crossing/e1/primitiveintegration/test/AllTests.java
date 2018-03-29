package crossing.e1.primitiveintegration.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import crossing.e1.featuremodel.clafer.test.ClaferModelUtilsTest;
import crossing.e1.featuremodel.clafer.test.QuestionReaderTest;
import crossing.e1.featuremodel.clafer.test.XMLParserTest;


@RunWith(Suite.class)
@SuiteClasses({ PageUtilityTest.class, PrimitiveTypesTest.class, ProviderFileWriterTest.class, HelperTest.class})
public class AllTests {

}
