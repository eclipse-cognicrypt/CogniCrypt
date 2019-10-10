/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import java.util.ArrayList;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;

public class ClaferPatternEnumGenerator {

	private final String patternName;
	private final boolean sortable;

	public ClaferPatternEnumGenerator(final String patternName, final boolean sortable) {
		this.patternName = patternName;
		this.sortable = sortable;
	}

	/**
	 * get the Clafer model implementing the user input
	 *
	 * @param input {@link ArrayList}<{@link String}> of user input values, ordered or unordered
	 * @return {@link ClaferModel} representing the user input, modeled using a reference clafer and its instances
	 */
	public ClaferModel getClaferModel(final ArrayList<String> input) {
		final ClaferModel resultModel = new ClaferModel();

		String parentFeatureInheritance;

		if (this.sortable) {
			parentFeatureInheritance = "Enum -> integer";
		} else {
			parentFeatureInheritance = "Enum";
		}
		resultModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, this.patternName, parentFeatureInheritance));

		for (int i = 0; i < input.size(); i++) {
			final String str = input.get(i);
			final StringBuilder childFeatureInheritance = new StringBuilder();

			childFeatureInheritance.append(this.patternName);

			if (this.sortable) {
				childFeatureInheritance.append(" = ");
				childFeatureInheritance.append(String.valueOf(i + 1));
			}

			resultModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, str.toString(), childFeatureInheritance.toString()));
		}

		return resultModel;
	}

}
