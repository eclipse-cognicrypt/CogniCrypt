package de.cognicrypt.cryslhandler;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import org.junit.Assert;
import org.junit.Before;
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
	
	@Test
	public void basicTest() {
		File ruleFile = new File("src/test/resources/Testrule1.cryptsl");
		System.out.println(ruleFile.exists());
		CryptSLRule r = csmr.readRule(ruleFile);
		
		StateMachineGraph s = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false, true);

		s.addNode(minusOne);
		s.addNode(zero);
		s.addNode(one);
		
		CryptSLMethod azp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aop = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {azp}), minusOne, zero));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aop}), zero, one));
		
		StateMachineGraph actualUsagePattern = r.getUsagePattern();
		Assert.assertEquals(s.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}

	
	@Test
	public void issueCryptoAnalysis119() {
		//see https://github.com/CROSSINGTUD/CryptoAnalysis/issues/119
		
		File ruleFile = new File("src/test/resources/Testrule2.cryptsl");
		System.out.println(ruleFile.exists());
		CryptSLRule r = csmr.readRule(ruleFile);
		
		StateMachineGraph s = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		s.addNode(minusOne);
		s.addNode(zero);
		s.addNode(one);
		s.addNode(two);
		
		CryptSLMethod azp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aop = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod atp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {azp}), minusOne, zero));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aop}), zero, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aop}), one, one));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {atp}), one, two));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {atp}), two, two));
		
		StateMachineGraph actualUsagePattern = r.getUsagePattern();
		Assert.assertEquals(s.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}
	
	@Test
	public void mockCipherRule() {
		File ruleFile = new File("src/test/resources/Testrule3.cryptsl");
		System.out.println(ruleFile.exists());
		CryptSLRule r = csmr.readRule(ruleFile);
		
		StateMachineGraph s = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false, true);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);

		s.addNode(minusOne);
		s.addNode(zero);
		s.addNode(one);
		s.addNode(two);
		s.addNode(three);
		s.addNode(four);
		
		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), one, three));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), one, four));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), four, four));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp, afp}), four, three));
		
		StateMachineGraph actualUsagePattern = r.getUsagePattern();
		Assert.assertEquals(s.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}
	
	@Test
	public void mockSecureRandomRule() {
		File ruleFile = new File("src/test/resources/Testrule4.cryptsl");
		System.out.println(ruleFile.exists());
		CryptSLRule r = csmr.readRule(ruleFile);
		
		StateMachineGraph s = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		s.addNode(minusOne);
		s.addNode(zero);
		s.addNode(one);
		s.addNode(two);
		
		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, two));
		
		StateMachineGraph actualUsagePattern = r.getUsagePattern();
		Assert.assertEquals(s.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}
	
	
	@Test
	public void mockSignatureRule() {
		File ruleFile = new File("src/test/resources/Testrule5.cryptsl");
		System.out.println(ruleFile.exists());
		CryptSLRule r = csmr.readRule(ruleFile);
		
		StateMachineGraph s = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);
		StateNode five = new StateNode("5", false);
		StateNode six = new StateNode("6", false, true);
		
		s.addNode(minusOne);
		s.addNode(zero);
		s.addNode(one);
		s.addNode(two);
		s.addNode(three);
		s.addNode(four);
		s.addNode(five);
		s.addNode(six);
		
		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), two, three));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), three, three));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), three, two));

		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), zero, four));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), four, four));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), four, five));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), five, five));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), five, six));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), six, six));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), four, six));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), six, five));
		
		StateMachineGraph actualUsagePattern = r.getUsagePattern();
		Assert.assertEquals(s.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}
	
	@Test
	public void mockCipherInputStreamRule() {
		File ruleFile = new File("src/test/resources/Testrule6.cryptsl");
		System.out.println(ruleFile.exists());
		CryptSLRule r = csmr.readRule(ruleFile);
		
		StateMachineGraph s = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false );
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false, true);

		s.addNode(minusOne);
		s.addNode(zero);
		s.addNode(one);
		s.addNode(two);
		
		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		
		StateMachineGraph actualUsagePattern = r.getUsagePattern();
		Assert.assertEquals(s.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}
	
	@Test
	public void mockKeyPairRule() {
		File ruleFile = new File("src/test/resources/Testrule7.cryptsl");
		System.out.println(ruleFile.exists());
		CryptSLRule r = csmr.readRule(ruleFile);
		
		StateMachineGraph s = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true, true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		s.addNode(minusOne);
		s.addNode(zero);
		s.addNode(one);
		s.addNode(two);
		
		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, two));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), two, one));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), minusOne, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), minusOne, two));
		
		System.out.println(s);
		
		StateMachineGraph actualUsagePattern = r.getUsagePattern();
		Assert.assertEquals(s.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}
}
