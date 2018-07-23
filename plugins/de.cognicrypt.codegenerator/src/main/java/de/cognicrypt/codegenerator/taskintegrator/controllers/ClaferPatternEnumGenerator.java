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

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.core.Constants;

public class ClaferPatternEnumGenerator {

	private String patternName;
	private boolean sortable;

	public ClaferPatternEnumGenerator(String patternName, boolean sortable) {
		this.patternName = patternName;
		this.sortable = sortable;
	}

	/**
	 * get the Clafer model implementing the user input
	 *
	 * @param input
	 *        {@link ArrayList}<{@link String}> of user input values, ordered or unordered
	 * @return {@link ClaferModel} representing the user input, modeled using a reference clafer and its instances
	 *
	 */
	public ClaferModel getClaferModel(ArrayList<String> input) {
		ClaferModel resultModel = new ClaferModel();

		String parentFeatureInheritance;

		if (sortable) {
			parentFeatureInheritance = "Enum -> integer";
		} else {
			parentFeatureInheritance = "Enum";
		}
		resultModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, patternName, parentFeatureInheritance));

		for (int i = 0; i < input.size(); i++) {
			String str = input.get(i);
			StringBuilder childFeatureInheritance = new StringBuilder();

			childFeatureInheritance.append(patternName);

			if (sortable) {
				childFeatureInheritance.append(" = ");
				childFeatureInheritance.append(String.valueOf(i + 1));
			}

			resultModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, str.toString(), childFeatureInheritance.toString()));
		}

		return resultModel;
	}

}
