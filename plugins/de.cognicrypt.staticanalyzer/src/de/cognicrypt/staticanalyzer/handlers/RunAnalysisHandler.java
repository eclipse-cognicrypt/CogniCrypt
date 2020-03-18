/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.handlers;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorPart;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.UIUtils;
import de.cognicrypt.utils.Utils;

public class RunAnalysisHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final AnalysisKickOff akf = new AnalysisKickOff();
		IProject ip = Utils.getCurrentlySelectedIProject();
		IEditorPart openEditor = UIUtils.getCurrentlyOpenEditor();

		// check if there are unsaved changes
		if (openEditor != null && openEditor.isDirty()) {
			int answr = saveFile(Utils.getCurrentlyOpenFile());
			// save file and analyze
			if (answr == JOptionPane.YES_OPTION) {
				openEditor.doSave(null);
			}
			// no analyze no save file
			else if (answr == JOptionPane.CLOSED_OPTION) {
				return null;
			}
		}
		final IJavaElement iJavaElement = JavaCore.create(ip);
		if (akf.setUp(iJavaElement)) {
			akf.run();
		}
		return null;
	}

	public static int saveFile(IFile openFileInEditor) {

		JFrame frame = new JFrame();

		String message = "The file [" + openFileInEditor.getName() + "] has unsaved changes. Would you like to save it before CogniCrypt analyzes your project?";
		JLabel label = new JLabel(message);
		String iconPath = Utils.getResourceFromWithin(Constants.COGNICRYPT_ICON_DIR, de.cognicrypt.core.Activator.PLUGIN_ID).getAbsolutePath();
		ImageIcon icon = new ImageIcon(iconPath);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			Activator.getDefault().logError(e, "Error getting systems look and feel");
		}

		return JOptionPane.showConfirmDialog(frame, label, "CogniCrypt Analysis", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);

	}

}
