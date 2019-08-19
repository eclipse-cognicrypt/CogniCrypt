package de.cognicrypt.staticanalyzer.handlers;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;
import crypto.HeadlessCryptoScanner;

public class RunAnalysisOnDependenciesHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		final Boolean dependencyAnalyser = true;
		IProject ip = Utils.getCurrentlySelectedIProject();
		IJavaProject javaProject = JavaCore.create(ip);
		
		if(javaProject == null) {
			Activator.getDefault().logInfo("JavaCore could not create IJavaProject for project "+ ip.getName() +" .");
			return false;
		}
		final AnalysisKickOff akf = new AnalysisKickOff();
		akf.setDepValue(dependencyAnalyser);
		final IJavaElement iJavaElement = JavaCore.create(ip);
		if (akf.setUp(iJavaElement)) {
			akf.run();
		}

		return null;

	}

}
