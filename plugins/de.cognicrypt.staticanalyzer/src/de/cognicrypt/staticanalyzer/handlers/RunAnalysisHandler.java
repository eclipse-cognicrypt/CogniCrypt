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
	public static void makeDialog(IFile openFile) {
		JFrame frame = new JFrame("Warning");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		 JLabel textLabel = new JLabel("This file has been changed, do you want to save it or not?" + " /n this file" + openFile,SwingConstants.CENTER); textLabel.setPreferredSize(new Dimension(500, 200));
		 JButton yesButton = new JButton("Yes");
		 JButton noButton = new JButton("No");
		 frame.getContentPane().add(textLabel, BorderLayout.CENTER);
		 frame.setLocationRelativeTo(null);
		 frame.pack();
		 frame.setVisible(true);
    }
	public static int saveFile(IFile openFile) throws Exception {
	    JFrame frame = new JFrame();
	    String message = "This file has been changed, do you want to save it or not?" + openFile;
	    ImageIcon icon = new ImageIcon("C:/Users/shahrzad/git/git/CogniCrypt/plugins/de.cognicrypt.core/icons/cognicrypt-codegen.png");
	    
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    JLabel label = new JLabel(message);
	    label.setFont(new Font("Arial", 0, 18));
	    int answer = JOptionPane.showConfirmDialog(frame, label, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon);

	    return answer;
	    
	  }
	

}
