/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

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
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;
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

	private CrySLRule readRuleFromFuleName(String ruleName) {
		return csmr.readRule(new File("src/test/resources/" + ruleName + ".crysl"));
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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), two, two));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod aep = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod afp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), two, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), one, four));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), four, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp, afp}), four, three));
		
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), three, two));
		
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), two, four));
		
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), three, four));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), zero, two));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod aep = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod afp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), one, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), two, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), three, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), three, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), zero, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), four, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), four, five));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), five, five));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {afp}), five, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {afp}), six, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {afp}), four, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), six, five));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), two, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), zero, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), two, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), minusOne, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), minusOne, two));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule7").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockAeadPrimitiveRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true, true);
		StateNode zero = new StateNode("0", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), zero, zero));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod aep = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod afp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), two, three));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), minusOne, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), four, five));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), five, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {afp}), six, seven));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod aep = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod afp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), one, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {afp}), four, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), three, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), three, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), three, four));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod aep = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), zero, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), three, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), minusOne, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), four, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), minusOne, two));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule11").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockTrustAnchorRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), minusOne, zero));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), one, one));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), two, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), three, three));

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

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // SHA256()
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // reset()
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // doFinal()
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // update()

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), three, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), three, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), zero, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), zero, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), two, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), two, three));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule15").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockPanathonDemoRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false);
		StateNode four = new StateNode("4", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // SHA256()
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // reset()
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // doFinal()
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // update()
		CrySLMethod aep = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); //

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), three, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), four, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), four, one));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule16").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockPanathonReaderRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false);
		StateNode four = new StateNode("4", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);

		CrySLMethod aap = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // Reader
		CrySLMethod abp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // setPath
		CrySLMethod acp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // readPassword
		CrySLMethod adp = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // storeFile
		CrySLMethod aep = new CrySLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void")); // reset

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {acp}), three, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), three, four));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {aep}), two, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CrySLMethod[] {abp}), four, one));

		// System.out.println(expectedUsagePattern);
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule17").getUsagePattern().getAllTransitions());
	}
}
