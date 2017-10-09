package de.cognicrypt.staticanalyzer.handlers;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

import crypto.SourceCryptoScanner;
import de.cognicrypt.staticanalyzer.Utils;

public class AnalysisKickOff {

	private String mainClass;
	private IProject curProj;
	private PrintStream tmp;

	public boolean setUp() {
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
		PrintStream out;
		try {
			tmp = System.out;

			String outputPath = curProj.getLocation().toOSString();
			outputPath += "\\src\\output.txt";
			new File(outputPath).createNewFile();
			out = new PrintStream(outputPath);
			System.setOut(out);
		} catch (IOException io) {
			return false;
		}
		
		return true;
	}

	public boolean run() {
		SourceCryptoScanner.main(curProj.getLocation().toOSString() + "\\bin", mainClass,
			Utils.getResourceFromWithin("/resources/CrySLRules/").toPath().toAbsolutePath().toString());
		return true;
	}

	public boolean cleanUp() {
		System.setOut(tmp);
		try {
			this.curProj.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

}
