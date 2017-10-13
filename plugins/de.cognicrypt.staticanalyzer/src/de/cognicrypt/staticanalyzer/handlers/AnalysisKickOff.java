package de.cognicrypt.staticanalyzer.handlers;

import java.io.PrintStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.Utils;
import de.cognicrypt.staticanalyzer.results.ErrorMarkerGenerator;
import de.cognicrypt.staticanalyzer.sootbridge.SootRunner;

public class AnalysisKickOff {

	private String mainClass;
	private IProject curProj;
	private ErrorMarkerGenerator errGen;

	public boolean setUp() {
		if (errGen == null) {
			errGen = new ErrorMarkerGenerator();
		} else {
			errGen.clearMarkers();
		}
		SearchRequestor requestor = new SearchRequestor() {

			@Override
			public void acceptSearchMatch(SearchMatch match) throws CoreException {
				String name = match.getResource().getProjectRelativePath().toString();
				name = name.substring(name.indexOf('/') + 1);
				name = name.replace("." + match.getResource().getFileExtension(), "");
				name = name.replace("/", ".");
				if (!name.isEmpty()) {
					mainClass = name;
				}
			}
		};
		
		Utils.findMainMethodInCurrentProject(requestor);
		curProj = Utils.getCurrentProject();
		
		return true;
	}

	public boolean run() {
		 try {
			if (curProj == null || !curProj.hasNature(JavaCore.NATURE_ID)){
				 return false;
			 }
			//TODO Stefan, supply your CryptSLAnalysisListener as third argument here.
			SootRunner.runSoot(JavaCore.create(curProj), mainClass, null);
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}

		return true;
	}

	public boolean cleanUp() {
		try {
			this.curProj.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		return true;
	}

}
