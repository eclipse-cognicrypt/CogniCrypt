package de.cognicrypt.cryslhandler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ConstraintTests.class, PredicateConsistencyCheck.class, SMGBuilderTests.class})
public class AllCrySLHandlerTests {

}
