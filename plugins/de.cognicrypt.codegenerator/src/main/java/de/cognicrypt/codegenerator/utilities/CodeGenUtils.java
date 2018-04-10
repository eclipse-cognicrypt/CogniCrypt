package de.cognicrypt.codegenerator.utilities;

import org.eclipse.core.resources.IProject;

import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

public class CodeGenUtils extends Utils {

	/**
	 * Retrieves the current project. There are several options for what counts as the 'current' project. First, if CogniCrypt was started through context menu, the project
	 * right-clicked is the current project. Second, if the currently opened file is a Java file, its project is returned. Third, if the currently selected project, is a Java
	 * project, it is returned. If none of these conditions is fulfilled, <code>null</code> is returned.
	 * 
	 * @return Current project/<code>null</code> if project could be retrieved succesfully.
	 */
	public static IProject getCurrentProject() {
		if (Constants.WizardActionFromContextMenuFlag) {
			final IProject selectedProject = CodeGenUtils.getIProjectFromSelection();
			if (selectedProject != null) {
				return selectedProject;
			}
		}
		return Utils.getCurrentProject();
	}
}