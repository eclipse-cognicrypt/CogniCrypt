package de.cognicrypt.staticanalyzer.contextmenu;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;

import de.cognicrypt.staticanalyzer.handlers.AnalysisKickOff;
import de.cognicrypt.utils.Utils;

public class EnableAnalysisOnContextMenuButton implements IActionDelegate {
	
	@Override
	public void run(IAction action) {
		// TODO Auto-generated metshod stub
		final AnalysisKickOff akf = new AnalysisKickOff();
		IProject ip = Utils.getCurrentlySelectedIProject();
		final IJavaElement iJavaElement = JavaCore.create(ip);
		akf.setUp(iJavaElement);
		akf.run();
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
	
	}
}
