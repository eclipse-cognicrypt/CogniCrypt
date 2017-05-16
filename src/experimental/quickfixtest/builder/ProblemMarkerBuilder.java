package quickfixtest.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.MethodInvocation;

import crossing.e1.configurator.Activator;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;

/**
 * Builder Class used to set a Marker to a part of a specific resource, that
 * matches the predefined pattern. This would probably be done by the analysis
 * later, as it would find such an "error" and could then directly add such a
 * Marker.
 * 
 * Currently only works for the specific example "cypher.getinstance('AES')"
 * 
 * Can be enabled by right clicking the Project and then selecting
 * Configure->Enable QFBuilder For testing purposes consider disabling the auto
 * build feature to initiate a manual full build
 * 
 * @author Patrick Hill
 */

public class ProblemMarkerBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "QuickFixTest.ProblemMarkerBuilder";

	private static final String MARKER_TYPE = "QuickFixTest.OCCEProblem";

	// define a correct ID (get range of possible ones)
	private static final int JDT_PROBLEM_ID = 10000000;

	private ASTParser parser;

	/**
	 * addMarker Method that adds a Marker to a File, which can then be
	 * displayed as an error/warning in the IDE.
	 * 
	 * @param file
	 *            the IResource of the File to which the Marker is added
	 * @param message
	 *            the message the Marker is supposed to display
	 * @param lineNumber
	 *            the Line to which the Marker is supposed to be added
	 * @param start
	 *            the number of the start character for the Marker
	 * @param end
	 *            the number of the end character for the Marker
	 */
	private void addMarker(IResource file, String message, int lineNumber, int start, int end) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, end);
			// IJavaModelMarker is important for the Quickfix Processor to work
			// correctly
			marker.setAttribute(IJavaModelMarker.ID, JDT_PROBLEM_ID);
		} catch (CoreException e) {
		}
	}

	/**
	 * printProblemIDs Method to print out all JDT Problem IDs which are taken
	 * by default. Used to find a not taken, unique ID, which can be used for
	 * the specific Marker.
	 */
	public void printProblemIDs() {
		Field[] fields = org.eclipse.jdt.core.compiler.IProblem.class.getFields();
		List<Integer> ints = new ArrayList<>();
		for (Field field : fields) {
			try {
				ints.add(field.getInt(null));
			} catch (IllegalArgumentException e) {
				Activator.getDefault().logError(e);
			} catch (IllegalAccessException e) {
				Activator.getDefault().logError(e);
			}
		}
		Collections.sort(ints);
		for (Integer integer : ints) {
			// this prints the JDT Problem IDs if you have to pick a new one
			// call this method.
			System.out.println(integer);
		}

	}

	/**
	 * startMarking Method that starts the Marking process by getting the
	 * IPackageFragments
	 * 
	 * @param project
	 *            Needed for getting the PackageFragments
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private void startMarking(IProject project) throws JavaModelException, CoreException {
		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
			for (IPackageFragment mypackage : packages) {
				getUnitForParser(mypackage);
			}
		}
	}

	/**
	 * getUnitForParser Method to get the CompilationUnits from the
	 * PackageFragments and start the parsing process from there
	 * 
	 * @param mypackage
	 *            IPackageFragment from startMarking
	 * @throws JavaModelException
	 */
	private void getUnitForParser(IPackageFragment mypackage) throws JavaModelException {
		if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
			for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
				setupParser(unit);
			}
		}
	}

	/**
	 * setupParser Method that sets up the Parser and creates a visitor for a
	 * specific unit
	 * 
	 * @param unit
	 *            Unit from getUnitForParser
	 */
	private void setupParser(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(createMarkerVisitor(unit, cu));
	}

	/**
	 * createMarkerVisitor Creates the Visitor that goes through the unit and
	 * sets the Marker based on a case, which is currently hard coded as
	 * cypher.getinstance('AES').
	 * 
	 * @param unit
	 *            Unit from getUnitForParser
	 * @param cu
	 *            same as unit but different type
	 * @return visitor for the unit
	 */
	private ASTVisitor createMarkerVisitor(ICompilationUnit unit, CompilationUnit cu) {
		ASTVisitor visitor = new ASTVisitor() {
			public boolean visit(MethodInvocation node) {
				int lineNumber = cu.getLineNumber(node.getStartPosition()) - 1;
				if ("getInstance".equals(node.getName().toString())
						&& "Cipher".equals(node.getExpression().toString())) {
					List<Expression> l = node.arguments();
					if (!l.isEmpty()) {
						if ("AES".equals(l.get(0).resolveConstantExpressionValue()) && l.size() == 1) {
							addMarker(unit.getResource(), "Error found", lineNumber, node.getStartPosition(),
									node.getStartPosition() + node.getLength());
						}
					}
				}
				return true;
			}
		};
		return visitor;
	}

	/**
	 * build The standard method that gets called by eclipse when initiating a
	 * build for its specified Nature
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		clean(null);
		parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		IProject project = getProject();
		try {
			startMarking(project);
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
		}
		return null;
	}

	/**
	 * clean Method to clean a file of its Markers. Needs to be done before
	 * adding new ones
	 */
	protected void clean(IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}
}
