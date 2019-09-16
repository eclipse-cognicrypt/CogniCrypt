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

import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;

public class RunAnalysisHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final AnalysisKickOff akf = new AnalysisKickOff();
		IProject ip = Utils.getCurrentlySelectedIProject();
		
//		check if the open editor is in the same project
		if (ip == Utils.getCurrentProject()) {
//			try {
//				check if there are unsaved changes
				if (Utils.getCurrentlyOpenEditor().isDirty()) {
					int answr = saveFile(Utils.getCurrentlyOpenFile());
//					save file and analyse
					if(answr == JOptionPane.YES_OPTION) {
						Utils.getCurrentlyOpenEditor().doSave(null);	
					}
//					no analyse no save file
					else if (answr == JOptionPane.CLOSED_OPTION) {
						return null;
					}
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
	    String message = "<html> The file below has been changed, would you like to save it before analysis?  <br><br>" + openFileInEditor;
	    String folderPath = Utils.getResourceFromWithin(Constants.COGNICRYPT_ICON_DIR, de.cognicrypt.core.Activator.PLUGIN_ID).getAbsolutePath();
	    ImageIcon icon = new ImageIcon(folderPath);

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				Activator.getDefault().logError(e, "Error getting systems look and feel");
			}

	    JLabel label = new JLabel(message);
	    
	    return JOptionPane.showConfirmDialog(frame, label, "CogniCrypt Analysis", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
	    
	  }
	

}
