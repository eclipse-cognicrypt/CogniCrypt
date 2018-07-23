/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.util.ArrayList;
import java.util.function.Predicate;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;

public class ClaferModelContentProvider implements ITreeContentProvider {

	private Predicate<? super ClaferFeature> featureFilter;
	private Predicate<? super ClaferProperty> propertyFilter;

	/**
	 * create a {@link ClaferModelContentProvider} that yields all of the content's elements
	 */
	public ClaferModelContentProvider() {
		this(null, null);
	}

	/**
	 * create a {@link ClaferModelContentProvider} with filters attached
	 * 
	 * @param featureFilter
	 *        display {@link ClaferFeature}s that this predicate applies to (returns true for)
	 * @param propertyFilter
	 *        display {@link ClaferProperty}s that this predicate applies to (returns true for)
	 */
	public ClaferModelContentProvider(Predicate<? super ClaferFeature> featureFilter, Predicate<? super ClaferProperty> propertyFilter) {
		this.featureFilter = featureFilter;
		this.propertyFilter = propertyFilter;
	}

	@Override
	public Object[] getChildren(Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			ClaferFeature inputFeature = (ClaferFeature) inputElement;
			ArrayList<ClaferProperty> filteredProperties = (ArrayList<ClaferProperty>) inputFeature.getFeatureProperties().clone();

			if (propertyFilter != null) {
				filteredProperties.removeIf(propertyFilter.negate());
			}

			return filteredProperties.toArray();
		}
		return null;
	}

	/**
	 * get the elements when setInput is called, can only be called on {@link ClaferModel} in this ContentProvider
	 * 
	 * @param inputElement
	 *        an input element of type {@link ClaferModel}
	 * @return returns the Clafer features of the model as {@link Object}[], empty {@link Object}[] if input type wrong
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ClaferModel) {
			ClaferModel inputModel = (ClaferModel) inputElement;
			ClaferModel filteredModel = inputModel.clone();

			if (featureFilter != null) {
				filteredModel.getClaferModel().removeIf(featureFilter.negate());
			}
			return filteredModel.getClaferModel().toArray();
		}
		return new Object[] {};
	}

	/**
	 * @return always returns null as the ClaferModel only links downwards
	 */
	@Override
	public Object getParent(Object arg0) {
		// return null if the parent cannot be computed
		return null;
	}

	/**
	 * @return <code>true</code> for {@link ClaferFeature}s that have properties matching the propertyFilter
	 */
	@Override
	public boolean hasChildren(Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			ClaferFeature inputFeature = (ClaferFeature) inputElement;
			if (propertyFilter != null) {
				return inputFeature.hasPropertiesSatisfying(propertyFilter);
			} else {
				return inputFeature.hasProperties();
			}
		}
		return false;
	}

}
