/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import org.eclipse.jface.fieldassist.ControlDecoration;
import de.cognicrypt.integrator.task.UIConstants;

public class ClaferValidation {

	/**
	 * check if the given string is a valid clafer name and return a corresponding message
	 *
	 * @param claferName {@link String} name to be tested
	 * @return {@link String} message containing errors in the name, empty string if valid
	 */
	public static String getNameValidationMessage(final String claferName, final boolean required) {
		if (required && claferName.isEmpty()) {
			return "Please enter a name";
		} else if (claferName.contains(" ")) {
			return "The name must not contain any spaces";
		} else if (claferName.matches("^[0-9].*")) {
			return "The name must not begin with a number";
		} else if (!claferName.matches("^[a-zA-Z0-9_-]*$")) {
			return "The name must not contain any special characters";
		}

		return "";
	}

	public static String getInheritanceValidationMessage(final String inheritanceName, final boolean required) {
		if (inheritanceName.matches("^[0-9].*")) {
			return "The name must not begin with a number";
		}

		return "";
	}

	/**
	 * check if the given string is a valid clafer name and use the {@link ControlDecoration} to give feedback
	 *
	 * @param claferName {@link String} name to be tested
	 * @param decoration {@link ControlDecoration} to display the error message in, hide if the string is valid
	 * @return
	 */
	public static boolean validateClaferName(final String claferName, final boolean required, final ControlDecoration decoration) {
		boolean valid = true;

		final String result = getNameValidationMessage(claferName, required);
		if (!result.isEmpty()) {
			decoration.setImage(UIConstants.DEC_ERROR);
			decoration.setDescriptionText(result);
			decoration.show();
			valid = false;
		} else {
			decoration.hide();
		}

		return valid;
	}

	public static boolean validateClaferInheritance(final String inheritanceName, final boolean required, final ControlDecoration decoration) {
		boolean valid = true;

		final String result = getInheritanceValidationMessage(inheritanceName, required);
		if (!result.isEmpty()) {
			decoration.setImage(UIConstants.DEC_ERROR);
			decoration.setDescriptionText(result);
			decoration.show();
			valid = false;
		} else {
			decoration.hide();
		}

		return valid;
	}

}
