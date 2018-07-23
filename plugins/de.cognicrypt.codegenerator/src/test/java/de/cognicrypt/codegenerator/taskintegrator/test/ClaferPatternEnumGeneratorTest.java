/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import de.cognicrypt.codegenerator.taskintegrator.controllers.ClaferPatternEnumGenerator;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.core.Constants;

public class ClaferPatternEnumGeneratorTest {

	@Test
	public void testGetClaferModelSortable() {
		ArrayList<String> fakeUserInput = new ArrayList<>();
		fakeUserInput.add("Broken");
		fakeUserInput.add("Weak");
		fakeUserInput.add("Medium");
		fakeUserInput.add("Strong");

		ClaferPatternEnumGenerator generator = new ClaferPatternEnumGenerator("Security", true);

		ClaferModel claferImplementation = generator.getClaferModel(fakeUserInput);
		assertEquals(5, claferImplementation.getFeatureCount());

		assertEquals(Constants.FeatureType.ABSTRACT, claferImplementation.getFeature("Security").getFeatureType());
		assertEquals("Enum -> integer", claferImplementation.getFeature("Security").getFeatureInheritance());

		assertEquals(ClaferFeature.class, claferImplementation.getFeature("Broken").getClass());
		assertEquals("Security = 1", claferImplementation.getFeature("Broken").getFeatureInheritance());

		assertEquals(ClaferFeature.class, claferImplementation.getFeature("Weak").getClass());
		assertEquals(ClaferFeature.class, claferImplementation.getFeature("Medium").getClass());
		assertEquals(ClaferFeature.class, claferImplementation.getFeature("Strong").getClass());
	}

	@Test
	public void testGetClaferModelNonSortable() {
		/*
		 * abstract TrustedAuthorities : Enum SignatureBased : TrustedAuthorities Notaries : TrustedAuthorities Both : TrustedAuthorities
		 */

		ArrayList<String> fakeUserInput = new ArrayList<>();
		fakeUserInput.add("SignatureBased");
		fakeUserInput.add("Notaries");
		fakeUserInput.add("Both");

		ClaferPatternEnumGenerator generator = new ClaferPatternEnumGenerator("TrustedAuthorities", false);

		ClaferModel claferImplementation = generator.getClaferModel(fakeUserInput);
		assertEquals(4, claferImplementation.getFeatureCount());

		assertEquals(Constants.FeatureType.ABSTRACT, claferImplementation.getFeature("TrustedAuthorities").getFeatureType());
		assertEquals("Enum", claferImplementation.getFeature("TrustedAuthorities").getFeatureInheritance());

		assertEquals(ClaferFeature.class, claferImplementation.getFeature("SignatureBased").getClass());
		assertEquals("TrustedAuthorities", claferImplementation.getFeature("SignatureBased").getFeatureInheritance());

		assertEquals(ClaferFeature.class, claferImplementation.getFeature("Notaries").getClass());
		assertEquals(ClaferFeature.class, claferImplementation.getFeature("Both").getClass());
	}

}
