package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.taskintegrator.controllers.XSLPageContentProvider;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.wizard.XslPage;


public class XslPageContentProviderTest {

	private static XSLPageContentProvider xslPageContentProvider;

	@BeforeClass
	public static void prepareContentProvider() {
		xslPageContentProvider = new XSLPageContentProvider();
	}

	@Test
	public void testGetChildren() {
		assertNull(xslPageContentProvider.getChildren(new CodeDependency()));

		// if a ClaferFeature object is passed, the call should be passed
		// to the super class ClaferModelContentProvider
		Object[] claferFeatureChildren = xslPageContentProvider.getChildren(new ClaferFeature(Constants.FeatureType.ABSTRACT, "", ""));
		assertNotNull(claferFeatureChildren);
	}

	@Test
	public void testGetElements() {
		XSLPageContentProvider xslPageContentProvider = new XSLPageContentProvider();
		CodeDependency codeDep = new CodeDependency();
		codeDep.setOption("signing");
		codeDep.setValue("true");
		codeDep.setOption("signing");
		codeDep.setValue("false");
		CodeDependency[] depList = new CodeDependency[] { codeDep };

		Object[] contentProviderElements = xslPageContentProvider.getElements(depList);

		assertEquals(1, contentProviderElements.length);
	}

	@Test
	public void testMergeLists() {
		ClaferModel claferModel = new ClaferModel();
		claferModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, "featureName", "featureInheritance"));

		CodeDependency codeDep1 = new CodeDependency();
		codeDep1.setOption("opt");
		codeDep1.setValue("val");
		CodeDependency codeDep2 = new CodeDependency();
		codeDep2.setOption("opt1");
		codeDep2.setValue("val1");
		CodeDependency[] depList = new CodeDependency[] { codeDep1, codeDep2 };

		Object[] firstList = claferModel.getClaferModel().toArray();
		Object[] secondList = depList;

		assertEquals(3, XslPage.mergeLists(firstList, secondList).length);
	}

	@Test
	public void testHasChildren() {
		assertFalse(xslPageContentProvider.hasChildren(new CodeDependency()));
	}

}
