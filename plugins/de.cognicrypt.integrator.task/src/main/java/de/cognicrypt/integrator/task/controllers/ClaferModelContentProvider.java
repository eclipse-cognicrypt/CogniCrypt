/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import java.util.ArrayList;
import java.util.function.Predicate;
import org.eclipse.jface.viewers.ITreeContentProvider;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;

public class ClaferModelContentProvider implements ITreeContentProvider {

	private final Predicate<? super ClaferFeature> featureFilter;
	private final Predicate<? super ClaferProperty> propertyFilter;

	/**
	 * create a {@link ClaferModelContentProvider} that yields all of the content's elements
	 */
	public ClaferModelContentProvider() {
		this(null, null);
	}

	/**
	 * create a {@link ClaferModelContentProvider} with filters attached
	 *
	 * @param featureFilter display {@link ClaferFeature}s that this predicate applies to (returns true for)
	 * @param propertyFilter display {@link ClaferProperty}s that this predicate applies to (returns true for)
	 */
	public ClaferModelContentProvider(final Predicate<? super ClaferFeature> featureFilter, final Predicate<? super ClaferProperty> propertyFilter) {
		this.featureFilter = featureFilter;
		this.propertyFilter = propertyFilter;
	}

	@Override
	public Object[] getChildren(final Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			final ClaferFeature inputFeature = (ClaferFeature) inputElement;
			final ArrayList<ClaferProperty> filteredProperties = (ArrayList<ClaferProperty>) inputFeature.getFeatureProperties().clone();

			if (this.propertyFilter != null) {
				filteredProperties.removeIf(this.propertyFilter.negate());
			}

			return filteredProperties.toArray();
		}
		return null;
	}

	/**
	 * get the elements when setInput is called, can only be called on {@link ClaferModel} in this ContentProvider
	 *
	 * @param inputElement an input element of type {@link ClaferModel}
	 * @return returns the Clafer features of the model as {@link Object}[], empty {@link Object}[] if input type wrong
	 */
	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement instanceof ClaferModel) {
			final ClaferModel inputModel = (ClaferModel) inputElement;
			final ClaferModel filteredModel = inputModel.clone();

			if (this.featureFilter != null) {
				filteredModel.getClaferModel().removeIf(this.featureFilter.negate());
			}
			return filteredModel.getClaferModel().toArray();
		}
		return new Object[] {};
	}

	/**
	 * @return always returns null as the ClaferModel only links downwards
	 */
	@Override
	public Object getParent(final Object arg0) {
		// return null if the parent cannot be computed
		return null;
	}

	/**
	 * @return <code>true</code> for {@link ClaferFeature}s that have properties matching the propertyFilter
	 */
	@Override
	public boolean hasChildren(final Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			final ClaferFeature inputFeature = (ClaferFeature) inputElement;
			if (this.propertyFilter != null) {
				return inputFeature.hasPropertiesSatisfying(this.propertyFilter);
			} else {
				return inputFeature.hasProperties();
			}
		}
		return false;
	}

}
