package de.cognicrypt.staticanalyzer.handlers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
//import org.eclipse.swt.graphics.Font;

import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

public class RunAnalysisHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final AnalysisKickOff akf = new AnalysisKickOff();
		IProject ip = Utils.getCurrentlySelectedIProject();
		
//		check if the open editor is in the same project
		if (ip == Utils.getCurrentProject()) {
			try {
//				check if there are unsaved changes
				if (Utils.getCurrentlyOpenEditor().isDirty()) {
					int answr = saveFile(Utils.getCurrentlyOpenFile());
//					save file and analyse
					if (answr == JOptionPane.NO_OPTION) {
					}
//					do not save file and analyse
					else if(answr == JOptionPane.YES_OPTION) {
						Utils.getCurrentlyOpenEditor().doSave(null);	
					}
//					no analyse no save file
					else if (answr == JOptionPane.CLOSED_OPTION) {
						System.out.println("cloooosed");
						return null;
					}
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		final IJavaElement iJavaElement = JavaCore.create(ip);
		if (akf.setUp(iJavaElement)) {
			akf.run();
		}
		return null;
	}

	public static int saveFile(IFile openFile) throws Exception {
	    JFrame frame = new JFrame();
	    String message = "This file has been changed, would you like to save it before analysis?" + "\n" + openFile;
	    String folderPath = Utils.getResourceFromWithin(Constants.COGNICRYPT_ICON_DIR, de.cognicrypt.core.Activator.PLUGIN_ID).getAbsolutePath();
	    ImageIcon icon = new ImageIcon(folderPath);
	   
//	    System.out.println("this is us" + folderPath);
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    JLabel label = new JLabel(message);
	    label.setFont(new Font("Arial", 0, 18));
	    
	    int answer = JOptionPane.showConfirmDialog(frame, label, "CogniCrypt Analysis", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);

	    return answer;
	    
	  }
	

}
