package de.cognicrypt.codegenerator.ui.contentassist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import crypto.rules.CrySLRule;
import de.cognicrypt.utils.CrySLUtils;


@SuppressWarnings("restriction")
public class CustomCompletionProposalComputer extends JavaCompletionProposalComputer {
	static List<CrySLRule> cryslRules = CrySLUtils.readCrySLRules();
	
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
		
		// FIXME analyze why ruleName for Cookie.crysl is void 
		Set<String> ruleNames = new HashSet<String>();
		for (CrySLRule rule : cryslRules) {
			ruleNames.add(rule.getClassName());
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
}
