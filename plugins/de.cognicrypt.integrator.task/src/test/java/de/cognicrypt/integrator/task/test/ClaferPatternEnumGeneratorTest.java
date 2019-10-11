/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.Test;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.ClaferPatternEnumGenerator;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;

public class ClaferPatternEnumGeneratorTest {

	@Test
	public void testGetClaferModelSortable() {
		final ArrayList<String> fakeUserInput = new ArrayList<>();
		fakeUserInput.add("Broken");
		fakeUserInput.add("Weak");
		fakeUserInput.add("Medium");
		fakeUserInput.add("Strong");

		final ClaferPatternEnumGenerator generator = new ClaferPatternEnumGenerator("Security", true);

		final ClaferModel claferImplementation = generator.getClaferModel(fakeUserInput);
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

		final ArrayList<String> fakeUserInput = new ArrayList<>();
		fakeUserInput.add("SignatureBased");
		fakeUserInput.add("Notaries");
		fakeUserInput.add("Both");

		final ClaferPatternEnumGenerator generator = new ClaferPatternEnumGenerator("TrustedAuthorities", false);

		final ClaferModel claferImplementation = generator.getClaferModel(fakeUserInput);
		assertEquals(4, claferImplementation.getFeatureCount());

		assertEquals(Constants.FeatureType.ABSTRACT, claferImplementation.getFeature("TrustedAuthorities").getFeatureType());
		assertEquals("Enum", claferImplementation.getFeature("TrustedAuthorities").getFeatureInheritance());

		assertEquals(ClaferFeature.class, claferImplementation.getFeature("SignatureBased").getClass());
		assertEquals("TrustedAuthorities", claferImplementation.getFeature("SignatureBased").getFeatureInheritance());

		assertEquals(ClaferFeature.class, claferImplementation.getFeature("Notaries").getClass());
		assertEquals(ClaferFeature.class, claferImplementation.getFeature("Both").getClass());
	}

}
