package de.cognicrypt.cryslhandler;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
		
		StateMachineGraph actual = r.getUsagePattern();
		List<CryptSLMethod> label = actual.getInitialTransition().getLabel();
		
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
		
		Assert.assertEquals(s.getAllTransitions(), r.getUsagePattern().getAllTransitions());
	}
}
