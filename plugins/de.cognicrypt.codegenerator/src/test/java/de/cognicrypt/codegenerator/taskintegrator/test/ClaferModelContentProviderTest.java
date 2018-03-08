package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;
import de.cognicrypt.codegenerator.taskintegrator.widgets.ClaferModelContentProvider;

public class ClaferModelContentProviderTest {

	ClaferModel claferModel;
	ClaferModelContentProvider contentProvider;

	@Before
	public final void createContentProvider() {
		contentProvider = new ClaferModelContentProvider();

		claferModel = new ClaferModel();
		ClaferFeature taskFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Task", "");
		claferModel.add(taskFeature);

		ArrayList<FeatureProperty> taskProperties = new ArrayList<>();
		taskProperties.add(new FeatureProperty("description", "string"));

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
	}

	@Test
	public void testGetElements() {
		// test for ClaferModel as an input
		assertEquals(2, contentProvider.getElements(claferModel).length);
		// test for ClaferFeature as an input
		assertEquals(0, contentProvider.getElements(claferModel.getClaferModel().get(0)).length);
	}

	@Test
	public void testGetParent() {
		// always expect null as parent cannot be directly computed

		// test for ClaferModel as an input
		assertNull(contentProvider.getParent(claferModel));
		// test for ClaferFeature as an input
		assertNull(contentProvider.getParent(claferModel.getClaferModel().get(0)));
	}

	@Test
	public void testHasChildren() {
		// test for ClaferModel as an input
		// ClaferModel only returns its children on getElements, not on getChildren
		assertEquals(false, contentProvider.hasChildren(claferModel));
		// test for ClaferFeature as an input
		assertEquals(true, contentProvider.hasChildren(claferModel.getClaferModel().get(0)));
	}

}
