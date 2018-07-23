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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.taskintegrator.controllers.ClaferModelContentProvider;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;
import de.cognicrypt.core.Constants;

public class ClaferModelContentProviderTest {

	ClaferModel claferModel;
	ClaferModelContentProvider contentProvider;
	ClaferModelContentProvider contentProviderWithFilters;

	@Before
	public final void createContentProvider() {
		contentProvider = new ClaferModelContentProvider();

		// create a content provider with filters attached
		// - feature filter: only inheriting features
		// - property filter: only primitive properties
		contentProviderWithFilters = new ClaferModelContentProvider(feature -> !feature.getFeatureInheritance().isEmpty(), property -> Arrays
			.asList(Constants.CLAFER_PRIMITIVE_TYPES).contains(property.getPropertyType()));

		claferModel = new ClaferModel();
		ClaferFeature taskFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Task", "");
		claferModel.add(taskFeature);

		ArrayList<ClaferProperty> taskProperties = new ArrayList<>();
		taskProperties.add(new ClaferProperty("description", "string"));

		taskFeature.setFeatureProperties(taskProperties);

		ClaferFeature symmEncFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "SymmetricEncryption", "Task");
		claferModel.add(symmEncFeature);
		ArrayList<ClaferConstraint> symmEncConstraints = new ArrayList<>();
		symmEncConstraints.add(new ClaferConstraint("description = \"A symmetric encryption task\""));
		symmEncFeature.setFeatureConstraints(symmEncConstraints);
	}

	@Test
	public void testGetChildren() {
		// test for ClaferModel as an input
		// ClaferModel only supports getElements
		assertNull(contentProvider.getChildren(claferModel));

		// test for ClaferFeature as an input
		assertEquals(1, contentProvider.getChildren(claferModel.getClaferModel().get(0)).length);

		// with filters attached

		// models as an input still don't yield any children
		assertNull(contentProviderWithFilters.getChildren(claferModel));

		// does the abstract Task clafer have primitive fields? yes, the description
		ClaferProperty property = ((ClaferProperty) contentProviderWithFilters.getChildren(claferModel.getClaferModel().get(0))[0]);
		assertEquals("description", property.getPropertyName());
	}

	@Test
	public void testGetElements() {
		// test for ClaferModel as an input
		assertEquals(2, contentProvider.getElements(claferModel).length);
		// test for ClaferFeature as an input
		assertEquals(0, contentProvider.getElements(claferModel.getClaferModel().get(0)).length);

		// with filters attached

		// ClaferModel as an input, abstract Task does not match the filter, as it does not inherit
		assertEquals(1, contentProviderWithFilters.getElements(claferModel).length);

		// ClaferFeature as an input, does not make sense for getElements
		assertEquals(0, contentProviderWithFilters.getElements(claferModel.getClaferModel().get(0)).length);
	}

	@Test
	public void testGetParent() {
		// always expect null as parent cannot be directly computed

		// test for ClaferModel as an input
		assertNull(contentProvider.getParent(claferModel));
		assertNull(contentProviderWithFilters.getParent(claferModel));
		// test for ClaferFeature as an input
		assertNull(contentProvider.getParent(claferModel.getClaferModel().get(0)));
		assertNull(contentProviderWithFilters.getParent(claferModel.getClaferModel().get(0)));
	}

	@Test
	public void testHasChildren() {
		// test for ClaferModel as an input
		// ClaferModel only returns its children on getElements, not on getChildren
		assertEquals(false, contentProvider.hasChildren(claferModel));
		// test for ClaferFeature as an input
		assertEquals(true, contentProvider.hasChildren(claferModel.getClaferModel().get(0)));

		// with filters attached

		assertEquals(false, contentProviderWithFilters.hasChildren(claferModel));
		// the abstract Task clafer has a property of a primitive type
		assertEquals(true, contentProviderWithFilters.hasChildren(claferModel.getClaferModel().get(0)));
		// the concrete SymmetricEncryption clafer has no property of a primitive type of its own
		// (it does inherit the description field, but this is disregarded for the content provider)
		assertEquals(false, contentProviderWithFilters.hasChildren(claferModel.getClaferModel().get(1)));
	}

}
