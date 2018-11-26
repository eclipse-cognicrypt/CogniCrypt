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
import java.util.List;
import java.util.function.Predicate;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;
import de.cognicrypt.codegenerator.taskintegrator.wizard.XslPage;

public class XSLPageContentProvider extends ClaferModelContentProvider {

	public XSLPageContentProvider() {
		this(null, null);
	}

	public XSLPageContentProvider(Predicate<? super ClaferFeature> featureFilter, Predicate<? super ClaferProperty> propertyFilter) {
		super(featureFilter, propertyFilter);
	}

	@Override
	public Object[] getChildren(Object inputElement) {
		// code dependencies do not have children
		if (inputElement instanceof CodeDependency) {
			return null;
		}

		return super.getChildren(inputElement);
	}

	/**
	 * take an {@link Object}[] array containing {@link ClaferModel} and {@link List}<{@link Question}> elements and get all {@link ClaferFeature} and {@link CodeDependency}
	 * elements
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		Object[] resultList = new Object[] {};
		if (inputElement instanceof Object[]) {
			for (Object elem : (Object[]) inputElement) {
				if (elem instanceof ClaferModel) {
					resultList = XslPage.mergeLists(resultList, super.getElements(elem));
				} else if (elem instanceof List) {
					ArrayList<CodeDependency> codeDeps = new ArrayList<CodeDependency>();

					// get all code dependencies from the list of questions
					for (Object listElement : (List<?>) elem) {
						if (listElement instanceof Question) {
							Question question = (Question) listElement;
							for (Answer answer : question.getAnswers()) {
								if (answer.getCodeDependencies() != null) {
									for (CodeDependency codeDependency : answer.getCodeDependencies()) {
										// TODO fix question page to not create null code dependencies
										if (codeDependency != null && codeDependency.getOption() != null) {
											codeDeps.add(codeDependency);
										}
									}
								}
							}
						}
					}

					resultList = XslPage.mergeLists(resultList, codeDeps.toArray());
				}
			}
		}

		return resultList;
	}

	@Override
	public boolean hasChildren(Object inputElement) {
		if (inputElement instanceof CodeDependency) {
			return false;
		}

		return super.hasChildren(inputElement);
	}

}
