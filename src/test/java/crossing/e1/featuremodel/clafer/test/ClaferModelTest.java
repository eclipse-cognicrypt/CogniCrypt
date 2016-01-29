/**
 * Copyright 2015 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package crossing.e1.featuremodel.clafer.test;

import static org.junit.Assert.*;
import org.clafer.scope.Scope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import crossing.e1.configurator.utilities.ReadConfig;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

/**
 * @author Ram
 *
 */
public class ClaferModelTest {
	private String path = new ReadConfig().getPathFromConfig("claferPath");
	private String taskDescription = "Encrypt data using a secret key";
	ClaferModel claferModel;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		claferModel = new ClaferModel(path);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.test.ClaferModel#ClaferModel(java.lang.String)}
	 * .
	 */
	@Test
	public final void testClaferModel() {

		assertNotNull("failed to create claferModel Object", claferModel);

	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.test.ClaferModel#getScope()}.
	 */
	@Test
	public final void testGetScope() {
		Scope scope = claferModel.getScope();
		assertNotNull("failed to create scope Object", scope);
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.test.ClaferModel#setTaskList(org.clafer.ast.AstModel)}
	 * .
	 */
	@Test
	public final void testSetTaskList() {
		claferModel.setTaskList(claferModel.getModel());
		assertNotNull("failed to create TaskLabelsMap Object in testSetTaskList() method",
				PropertiesMapperUtil.getTaskLabelsMap());
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.test.ClaferModel#setModelName(java.lang.String)}
	 * {@link crossing.e1.featuremodel.clafer.test.ClaferModel#getModelName()}.
	 */
	@Test
	public final void testSetandGetModelName() {
		String testName = "Sample module";
		claferModel.setModelName(testName);
		assertSame("Failed to update claferModel name", testName, claferModel.getModelName());

	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.test.ClaferModel#getModel()}.
	 */
	@Test
	public final void testGetModel() {
		assertNotNull("failed to return claferModel Object", claferModel.getModel());

	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.test.ClaferModel#addClaferProperties(org.clafer.ast.AstClafer)}
	 * .
	 */
	@Test
	public final void testAddClaferPropertiesAstClafer() {
		claferModel.addClaferProperties(PropertiesMapperUtil.getTaskLabelsMap().get(taskDescription));
		assertNotNull("failed to return properties Object", PropertiesMapperUtil.getPropertiesMap());
	}

	/**
	 * Test method for
	 * {@link crossing.e1.featuremodel.clafer.test.ClaferModel#createClaferPropertiesMap(org.clafer.ast.AstConcreteClafer)}
	 * .
	 */
	@Test
	public final void testCreateClaferPropertiestMap() {
		claferModel.createClaferPropertiesMap(PropertiesMapperUtil.getTaskLabelsMap().get(taskDescription));
		assertNotNull("failed to create PropertyLabelsMap Object", PropertiesMapperUtil.getPropertiesMap());

	}

}
