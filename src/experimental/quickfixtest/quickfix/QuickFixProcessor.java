package quickfixtest.quickfix;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * Quickfix Processor that checks for the specific ProblemID of the Marker 
 * and then gets the IJavaCompletionProposal for it.
 * 
 * The completion proposal is the thing that could be seen as the actual Quickfix.
 * 
 * Gets invoked automatically by Eclipse once a Java Problem is found.
 * 
 * @author Patrick Hill
 *
 */
public class QuickFixProcessor implements IQuickFixProcessor {
	
	private final static int JDT_PROBLEM_ID = 10000000; //get correct one
	private static final String MARKER_TYPE = "QuickFixTest.OCCEProblem";

	/**
	 * getCorrections
	 * Method that gets invoked if hasCorrections returns true
	 * Returns a new completion proposal for the specific problem
	 */
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations)
			throws CoreException {
		if (locations == null || locations.length == 0) {
			/*
			 * https://bugs.eclipse.org/444120 Eclipse can call this method any 
			 * locations, if a quick fix is requested without any problems.
			 */
			return null;
		}
		

		return new IJavaCompletionProposal[] { new IJavaCompletionProposal() {

		/**
		 * apply
		 * Method that applys the Quickfix/CompletionProposal
		 * Currently replaces the text of the example case "cypher.getinstance('AES')"
		 * with "cypher.getinstance('AES/CBC/PKCS5PADDING')"
		 * 
		 * @param d Document or File for which the Quickfix is supposed to work
		 */
		public void apply(IDocument d) {
				try {
					d.replace(context.getSelectionOffset(), context.getSelectionLength(), "Cipher.getInstance(\"AES/CBC/PKCS5PADDING\")");
					context.getCompilationUnit().getResource().deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		/**
		 * Not used in the example
		 */
		public String getAdditionalProposalInfo() {
			return null;
		}

		/**
		 * Not used in the example
		 */
		public IContextInformation getContextInformation() {
			return null;
		}

		/**
		 * Display String for the Quickfix
		 */
		public String getDisplayString() {
			String display = "Change to a more secure Encryption";
			return display;
		}

		/**
		 * Not used in the example
		 */
		public Image getImage() {
			return null;
		}

		/**
		 * Not used in the example
		 */
		public Point getSelection(IDocument document) {
			return null;
		}

		/**
		 * Used to define the relevance of a Quickfix
		 */
		public int getRelevance() {
			return 1;
		}}};
	}

	/**
	 * hasCorrections
	 * checks if the Problem is supposed to be solved by this Quickfix Processor
	 */
	public boolean hasCorrections(ICompilationUnit unit, int problemId) {
		return problemId == JDT_PROBLEM_ID;
	}

}
