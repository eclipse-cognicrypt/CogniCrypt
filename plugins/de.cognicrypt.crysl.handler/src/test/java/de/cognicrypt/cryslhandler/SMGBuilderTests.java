package de.cognicrypt.cryslhandler;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.crysl.reader.CrySLModelReader;

public class SMGBuilderTests {

	private static CrySLModelReader csmr = null;

	@BeforeClass
	public static void setUp() throws MalformedURLException {
		csmr = new CrySLModelReader();
	}

	private CryptSLRule readRuleFromFuleName(String ruleName) {
		return csmr.readRule(new File("src/test/resources/" + ruleName + ".cryptsl"));
	}

	@Test
	public void basicTest() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule1").getUsagePattern().getAllTransitions());
	}

	@Test
	public void issueCryptoAnalysis119() {
		// see https://github.com/CROSSINGTUD/CryptoAnalysis/issues/119

		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule2").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockCipherRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false, true);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), one, four));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), four, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp, afp}), four, three));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule3").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockSecureRandomRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, two));
		
		StateMachineGraph actualUsagePattern = readRuleFromFuleName("Testrule4").getUsagePattern();
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}

	@Test
	public void mockSignatureRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);
		StateNode five = new StateNode("5", false);
		StateNode six = new StateNode("6", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);
		expectedUsagePattern.addNode(five);
		expectedUsagePattern.addNode(six);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), three, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), three, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), zero, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), four, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), four, five));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), five, five));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), five, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), six, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), four, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), six, five));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule5").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockCipherInputStreamRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule6").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockKeyPairRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true, true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), two, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), minusOne, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), minusOne, two));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule7").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockAeadPrimitiveRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true, true);
		StateNode zero = new StateNode("0", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), zero, zero));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule8").getUsagePattern().getAllTransitions());
	}
	
	@Test
	public void mockRsaDigestSignerRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);
		StateNode five = new StateNode("5", false);
		StateNode six = new StateNode("6", false);
		StateNode seven = new StateNode("7", false, true);
		
		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);
		expectedUsagePattern.addNode(five);
		expectedUsagePattern.addNode(six);
		expectedUsagePattern.addNode(seven);
		
		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), two, three));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), minusOne, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), four, five));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), five, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), six, seven));
		
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule9").getUsagePattern().getAllTransitions());
	}
	
	@Test
	public void mockKeyStoreRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), one, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), four, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), three, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), three, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), three, four));
		
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule10").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockSSLParametersRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false, true);
		StateNode three = new StateNode("3", false);
		StateNode four = new StateNode("4", false);
		
		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), three, two));
		
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), minusOne, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), four, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), minusOne, two));
		
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule11").getUsagePattern().getAllTransitions());
	}
	
	@Test
	public void mockTrustAnchorRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		
		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), minusOne, zero));
		
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule12").getUsagePattern().getAllTransitions());
	}
	
	@Test
	public void mockPKCS7PaddingRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		
		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule13").getUsagePattern().getAllTransitions());
	}
	
	@Test
	public void mockCBCBlockCipherRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), three, three));
		
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule14").getUsagePattern().getAllTransitions());
	}
	
	@Test
	public void mockSHA256DigestRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);
		StateNode three = new StateNode("3", false);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // SHA256()
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // reset()
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // doFinal()
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // update()

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), three, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));
		
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), zero, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), three, three));
		
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule15").getUsagePattern().getAllTransitions());
	
	}
}
