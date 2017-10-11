package de.cognicrypt.staticanalyzer.handlers;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.accessgraph.AccessGraph;
import crypto.SourceCryptoScanner;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CryptSLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import de.cognicrypt.staticanalyzer.Utils;
import de.cognicrypt.staticanalyzer.sootbridge.SootRunner;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import soot.Unit;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

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
		
//
		Utils.findMainMethodInCurrentProject(requestor);
		curProj = Utils.getCurrentProject();
//		PrintStream out;
//		try {
//			tmp = System.out;
//
//			String outputPath = curProj.getLocation().toOSString();
//			outputPath += "\\src\\output.txt";
//			new File(outputPath).createNewFile();
//			out = new PrintStream(outputPath);
//			System.setOut(out);
//		} catch (IOException io) {
//			return false;
//		}
		
		return true;
	}

	public boolean run() {
		 try {
			if (curProj == null || !curProj.hasNature(JavaCore.NATURE_ID)){
				 return true;
			 }
			//TODO Stefan, supply your CryptSLAnalysisListener as third argument here.
			SootRunner.runSoot(JavaCore.create(curProj), mainClass, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public boolean cleanUp() {
//		System.setOut(tmp);
		try {
			this.curProj.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

}
