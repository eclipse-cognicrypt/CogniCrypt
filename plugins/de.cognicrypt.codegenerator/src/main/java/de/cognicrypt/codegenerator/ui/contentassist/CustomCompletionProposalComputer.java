package de.cognicrypt.codegenerator.ui.contentassist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.CrySLUtils;


@SuppressWarnings("restriction")
public class CustomCompletionProposalComputer extends JavaCompletionProposalComputer {
	
	static String JCA_LATEST_ECLIPSE_RULES_DIR = Constants.ECLIPSE_RULES_DIR + Constants.innerFileSeparator + Constants.Rules.JavaCryptographicArchitecture.toString() + Constants.innerFileSeparator + 
		CrySLUtils.getRuleVersions(Constants.Rules.JavaCryptographicArchitecture.toString())[CrySLUtils.getRuleVersions(Constants.Rules.JavaCryptographicArchitecture.toString()).length - 1] + 
		Constants.innerFileSeparator + Constants.Rules.JavaCryptographicArchitecture.toString();
	
	static private List<String> ruleNames = readClassnames(JCA_LATEST_ECLIPSE_RULES_DIR);
	
	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List<ICompletionProposal> defaultProposals =  super.computeCompletionProposals(context, monitor);
		
		JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;
		ICompilationUnit cu = javaContext.getCompilationUnit();
		int offset = context.getInvocationOffset();
		SelectionFinder finder = new SelectionFinder(cu, offset);
		
		if (finder.getMemberName() == null) {
			return defaultProposals;
		}
		
		List<ICompletionProposal> customProposals = new ArrayList<ICompletionProposal>();
		for (String name : ruleNames) {
			String s = '"' + name + '"';
			CompletionProposal proposal = new CompletionProposal(s, offset, 0, s.length());
			customProposals.add(proposal);
		}
		
		customProposals.addAll(defaultProposals);
		return customProposals;
	}
	
	private static List<String> readClassnames(String rulesFolder) {
		List<String> classnames = new ArrayList<String>();
		for (File rule : (new File(rulesFolder)).listFiles()) {
			if (rule.isDirectory()) {
				classnames.addAll(readClassnames(rule.getAbsolutePath()));
				continue;
			}
	
			String classname = null;
			try {
				classname = readClassnameFromRule(rule);
			} catch (IOException e) {
				Activator.getDefault().logError(e);
			}
			
			if (classname != null) {
				classnames.add(classname);
			}
		}
		
		return classnames;
	}

	private static String readClassnameFromRule(File ruleFile) throws IOException {
		final String fileName = ruleFile.getName();
		if (!fileName.endsWith(Constants.cryslFileEnding)) {
			return null;
		}
		
		BufferedReader reader = null;
		String classname = null;
		try {
			reader = new BufferedReader(new FileReader(ruleFile));
			String spec = reader.readLine();
			classname = spec.split(" ")[1];
		} finally {
			reader.close();
		}
		
		return classname;
	}
}
