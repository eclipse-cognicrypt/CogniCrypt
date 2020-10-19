/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.ClaferModelContentProvider;
import de.cognicrypt.integrator.task.models.ClaferConstraint;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;

public class ClaferModelContentProviderTest {

	ClaferModel claferModel;
	ClaferModelContentProvider contentProvider;
	ClaferModelContentProvider contentProviderWithFilters;

	@Before
	public final void createContentProvider() {
		this.contentProvider = new ClaferModelContentProvider();

		// create a content provider with filters attached
		// - feature filter: only inheriting features
		// - property filter: only primitive properties
		this.contentProviderWithFilters = new ClaferModelContentProvider(feature -> !feature.getFeatureInheritance().isEmpty(),
				property -> Arrays.asList(Constants.CLAFER_PRIMITIVE_TYPES).contains(property.getPropertyType()));

		this.claferModel = new ClaferModel();
		final ClaferFeature taskFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Task", "");
		this.claferModel.add(taskFeature);

		final ArrayList<ClaferProperty> taskProperties = new ArrayList<>();
		taskProperties.add(new ClaferProperty("description", "string"));

		taskFeature.setFeatureProperties(taskProperties);

		final ClaferFeature symmEncFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "SymmetricEncryption", "Task");
		this.claferModel.add(symmEncFeature);
		final ArrayList<ClaferConstraint> symmEncConstraints = new ArrayList<>();
		symmEncConstraints.add(new ClaferConstraint("description = \"A symmetric encryption task\""));
		symmEncFeature.setFeatureConstraints(symmEncConstraints);
	}

	@Test
	public void testGetChildren() {
		// test for ClaferModel as an input
		// ClaferModel only supports getElements
		assertNull(this.contentProvider.getChildren(this.claferModel));

		// test for ClaferFeature as an input
		assertEquals(1, this.contentProvider.getChildren(this.claferModel.getClaferModel().get(0)).length);

		// with filters attached

		// models as an input still don't yield any children
		assertNull(this.contentProviderWithFilters.getChildren(this.claferModel));

		// does the abstract Task clafer have primitive fields? yes, the description
		final ClaferProperty property = ((ClaferProperty) this.contentProviderWithFilters.getChildren(this.claferModel.getClaferModel().get(0))[0]);
		assertEquals("description", property.getPropertyName());
	}

	@Test
	public void testGetElements() {
		// test for ClaferModel as an input
		assertEquals(2, this.contentProvider.getElements(this.claferModel).length);
		// test for ClaferFeature as an input
		assertEquals(0, this.contentProvider.getElements(this.claferModel.getClaferModel().get(0)).length);

		// with filters attached

		// ClaferModel as an input, abstract Task does not match the filter, as it does not inherit
		assertEquals(1, this.contentProviderWithFilters.getElements(this.claferModel).length);

		// ClaferFeature as an input, does not make sense for getElements
		assertEquals(0, this.contentProviderWithFilters.getElements(this.claferModel.getClaferModel().get(0)).length);
	}

	@Test
	public void testGetParent() {
		// always expect null as parent cannot be directly computed

		// test for ClaferModel as an input
		assertNull(this.contentProvider.getParent(this.claferModel));
		assertNull(this.contentProviderWithFilters.getParent(this.claferModel));
		// test for ClaferFeature as an input
		assertNull(this.contentProvider.getParent(this.claferModel.getClaferModel().get(0)));
		assertNull(this.contentProviderWithFilters.getParent(this.claferModel.getClaferModel().get(0)));
	}

	@Test
	public void testHasChildren() {
		// test for ClaferModel as an input
		// ClaferModel only returns its children on getElements, not on getChildren
		assertEquals(false, this.contentProvider.hasChildren(this.claferModel));
		// test for ClaferFeature as an input
		assertEquals(true, this.contentProvider.hasChildren(this.claferModel.getClaferModel().get(0)));

		// with filters attached

		assertEquals(false, this.contentProviderWithFilters.hasChildren(this.claferModel));
		// the abstract Task clafer has a property of a primitive type
		assertEquals(true, this.contentProviderWithFilters.hasChildren(this.claferModel.getClaferModel().get(0)));
		// the concrete SymmetricEncryption clafer has no property of a primitive type of its own
		// (it does inherit the description field, but this is disregarded for the content provider)
		assertEquals(false, this.contentProviderWithFilters.hasChildren(this.claferModel.getClaferModel().get(1)));
	}

}
