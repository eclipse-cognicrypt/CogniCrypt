package de.cognicrypt.codegenerator.taskintegrator.controllers;

import org.eclipse.jface.fieldassist.ControlDecoration;

import de.cognicrypt.codegenerator.UIConstants;

public class ClaferValidation {

	public static boolean validateClaferName(String clafer, ControlDecoration decoration) {
		boolean valid = true;

		if (clafer.contains(" ")) {
			decoration.setImage(UIConstants.DEC_ERROR);
			decoration.setDescriptionText("The name must not contain any spaces");
			decoration.show();
			valid = false;
		} else if (clafer.isEmpty()) {
			decoration.setImage(UIConstants.DEC_ERROR);
			decoration.setDescriptionText("Please enter a name");
			decoration.show();
			valid = false;
		} else {
			decoration.hide();
		}

		return valid;
	}

}
