package de.cognicrypt.codegenerator.primitive.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cognicrypt.codegenerator.primitive.clafer.ClaferGenerator;



@RunWith(Suite.class)
@SuiteClasses({ PageUtilityTest.class, PrimitiveTypesTest.class, ProviderFileWriterTest.class, HelperTest.class, XsltWriterTest.class, ClaferGeneratorTest.class})
public class AllTests {

}
