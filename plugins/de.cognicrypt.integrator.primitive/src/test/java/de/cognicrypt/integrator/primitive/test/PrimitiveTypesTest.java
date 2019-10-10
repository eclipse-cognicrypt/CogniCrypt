/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.test;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.primitive.types.Primitive;
import de.cognicrypt.integrator.primitive.types.PrimitiveJSONReader;
import de.cognicrypt.utils.Utils;

public class PrimitiveTypesTest {

	private static PrimitiveJSONReader jsonReader = new PrimitiveJSONReader();
	private final File primitiveTestFile = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "PrimitiveTest.json");
	private static Primitive primitive = new Primitive();

	@BeforeClass
	public static void setupPrimitveTest() {

		primitive.setName("Cipher Test");
		primitive.setXmlFile(Constants.testPrimitverFolder + "PrimitiveQuestionTest.json");
		primitive.setXslFile(Constants.testPrimitverFolder + "xslTest.xsl");

	}

	@Test
	public void test() {
		final List<Primitive> primitives = PrimitiveJSONReader.getPrimitiveTypes(this.primitiveTestFile);
		setupPrimitveTest();
		primitive.setName("Cipher Test");
		primitive.setXmlFile(Constants.testPrimitverFolder + "TestPrimitiveQuestion.json");
		assertEquals(primitives.get(0).getName(), primitive.getName());
		assertEquals(primitives.get(0).getXmlFile(), primitive.getXmlFile());
		assertEquals(primitives.get(0).getXslFile(), primitive.getXslFile());

	}

}
