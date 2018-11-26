/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

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

import de.cognicrypt.staticanalyzer.Activator;

/**
 * Quickfix Processor that checks for the specific ProblemID of the Marker and
 * then gets the IJavaCompletionProposal for it.
 *
 * The completion proposal is the thing that could be seen as the actual
 * Quickfix.
 *
 * Gets invoked automatically by Eclipse once a Java Problem is found.
 *
 * @author Patrick Hill
 *
 */
public class QuickFixProcessor implements IQuickFixProcessor {

	private final static int JDT_PROBLEM_ID = 10000000; // get correct one
	private static final String MARKER_TYPE = "QuickFixTest.OCCEProblem";

	/**
	 * getCorrections Method that gets invoked if hasCorrections returns true
	 * Returns a new completion proposal for the specific problem
	 */
	@Override
	public IJavaCompletionProposal[] getCorrections(final IInvocationContext context,
			final IProblemLocation[] locations) throws CoreException {
		if (locations == null || locations.length == 0) {
			/*
			 * https://bugs.eclipse.org/444120 Eclipse can call this method even when there
			 * are no markers, then this case would occur.
			 */
			return null;
		}

		return new IJavaCompletionProposal[] { new IJavaCompletionProposal() {

			/**
			 * apply Method that applys the Quickfix/CompletionProposal Currently replaces
			 * the text of the example case "cypher.getinstance('AES')" with
			 * "cypher.getinstance('AES/CBC/PKCS5PADDING')"
			 *
			 * @param d Document or File for which the Quickfix is supposed to work
			 */
			@Override
			public void apply(final IDocument d) {
				try {
					d.replace(context.getSelectionOffset(), context.getSelectionLength(),
							"Cipher.getInstance(\"AES/CBC/PKCS5PADDING\")");
					context.getCompilationUnit().getResource().deleteMarkers(QuickFixProcessor.MARKER_TYPE, false,
							IResource.DEPTH_ZERO);
				} catch (final CoreException | BadLocationException e) {
					Activator.getDefault().logError(e);
				}
			}

			/**
			 * Not used in the example
			 */
			@Override
			public String getAdditionalProposalInfo() {
				return null;
			}

			/**
			 * Not used in the example
			 */
			@Override
			public IContextInformation getContextInformation() {
				return null;
			}

			/**
			 * Display String for the Quickfix
			 */
			@Override
			public String getDisplayString() {
				final String display = "Change to a more secure Encryption";
				return display;
			}

			/**
			 * Not used in the example
			 */
			@Override
			public Image getImage() {
				return null;
			}

			/**
			 * Not used in the example
			 */
			@Override
			public Point getSelection(final IDocument document) {
				return null;
			}

			/**
			 * Used to define the relevance of a Quickfix
			 */
			@Override
			public int getRelevance() {
				return 1;
			}
		} };
	}

	/**
	 * hasCorrections checks if the Problem is supposed to be solved by this
	 * Quickfix Processor
	 */
	@Override
	public boolean hasCorrections(final ICompilationUnit unit, final int problemId) {
		return problemId == QuickFixProcessor.JDT_PROBLEM_ID;
	}

}
