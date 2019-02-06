package de.cognicrypt.cryslhandler;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.crysl.reader.CrySLModelReader;

public class SMGBuilderTests {

	@Test
	public void basicTest() throws MalformedURLException {
		CrySLModelReader csmr = new CrySLModelReader();
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
	public void issueCryptoAnalysis119() throws MalformedURLException {
		//see https://github.com/CROSSINGTUD/CryptoAnalysis/issues/119
		
		CrySLModelReader csmr = new CrySLModelReader();
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
	
}
