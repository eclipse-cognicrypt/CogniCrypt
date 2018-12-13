package de.cognicrypt.staticanalyzer.annotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.client.DeveloperProject;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;

/**
 *
 * @author André Sonntag
 *
 */
public abstract class AnnotationManager {

	private final DeveloperProject project;

	public AnnotationManager(final DeveloperProject project) {
		this.project = project;
	}

	/**
	 * This method adds all files of a certain source to the client project
	 * 
	 * @param source
	 *            path to the source
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the adding was (not)
	 *         successfully
	 */
	public boolean addAdditionalFiles(final String source) {
		if (source.isEmpty()) {
			return true;
		}
		try {
			final File[] members = Utils.getResourceFromWithin(source, "de.cognicrypt.staticanalyzer").listFiles();
			if (members == null) {
				Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_ADDITIONAL_RES_DIRECTORY);
			}
			for (int i = 0; i < members.length; i++) {
				final File addFile = members[i];
				if (!addAddtionalFile(addFile)) {
					return false;
				}
			}
		} catch (IOException | CoreException e) {
			Activator.getDefault().logError(e, Constants.CodeGenerationErrorMessage);
			return false;
		}
		return true;
	}

	/**
	 * This method adds a {@link File} to the client project
	 * 
	 * @param fileToBeAdded
	 *            current file
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the adding was (not)
	 *         successfully
	 * @throws IOException
	 * @throws CoreException
	 */
	private boolean addAddtionalFile(final File fileToBeAdded) throws IOException, CoreException {
		final IFolder libFolder = this.project.getFolder(Constants.pathsForLibrariesInDevProject);
		if (!libFolder.exists()) {
			libFolder.create(true, true, null);
		}

		final Path memberPath = fileToBeAdded.toPath();
		Files.copy(memberPath,
				new File(this.project.getProjectPath() + Constants.outerFileSeparator
						+ Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator
						+ memberPath.getFileName()).toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		final String filePath = fileToBeAdded.toString();
		final String cutPath = filePath.substring(filePath.lastIndexOf(Constants.outerFileSeparator));
		if (Constants.JAR.equals(cutPath.substring(cutPath.indexOf(".")))) {
			if (!this.project.addJar(
					Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + fileToBeAdded.getName())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method creates a {@link MamberValuePair}
	 * 
	 * @param ast
	 *            AST
	 * @param name
	 *            name
	 * @param value
	 *            value
	 * @return a new {@link MamberValuePair} object
	 */
	protected MemberValuePair createMemberValuePair(final AST ast, final String name, final String value) {
		final MemberValuePair mV = ast.newMemberValuePair();
		mV.setName(ast.newSimpleName(name));
		final StringLiteral stringLiteral = ast.newStringLiteral();
		stringLiteral.setLiteralValue(value);
		mV.setValue(stringLiteral);
		return mV;
	}

	/**
	 * This method gets a {@link ICompilationUnit} from the marker
	 * 
	 * @param marker
	 *            problem marker
	 * @return the ICompilationUnit of the source file of the problem marker
	 * @throws CoreException
	 */
	protected ICompilationUnit getCompilationUnitFromMarker(final IMarker marker) throws CoreException {
		final IJavaElement javaElement = JavaCore.create(marker.getResource());
		ICompilationUnit unit = null;
		if (javaElement instanceof ICompilationUnit) {
			unit = (ICompilationUnit) javaElement;
		}
		if (unit == null) {
			throw new CoreException(null);
		}
		return unit;
	}

	/**
	 * getter for the {@link DeveloperProject}
	 * 
	 * @return current DeveloperProject
	 */
	protected DeveloperProject getProject() {
		return this.project;
	}

	/**
	 * This method checks if the modifier list contains the annotation name
	 * 
	 * @param modifiers
	 * @param annotationName
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the annotationName is (not)
	 *         in the modifiers list
	 */
	protected boolean hasAnnotation(final List<Object> modifiers, final String annotationName) {
		for (final Object o : modifiers) {
			if (o.toString().equals("@" + annotationName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method checks if a {@link ICompilationUnit} contains a certain import
	 * 
	 * @param unit
	 *            ICompilationUnit of the source file
	 * @param packagePath
	 *            path of the annotation
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the import (not) exists
	 * @throws CoreException
	 */
	protected boolean hasAnnotationImport(final ICompilationUnit unit, final String packagePath) throws CoreException {
		final IImportDeclaration importDecalaration = unit.getImport(packagePath);
		if (importDecalaration.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method inserts a certain import to a {@link ICompilationUnit}
	 * 
	 * @param unit
	 *            ICompilationUnit of the source file
	 * @param packagePath
	 *            path of the annotation
	 * @throws CoreException
	 */
	protected void insertAnnotationImport(final ICompilationUnit unit, final String packagePath) throws CoreException {
		unit.createImport(packagePath, null, null);
		unit.save(null, true);
		final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		editor.doSave(null);
	}

	/**
	 * This method looks for a parent node with a certain name
	 *
	 * @param parentNodeName
	 *            name of the node you are looking for
	 * @param node
	 *            child node
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if parent node with the certain
	 *         node was (not) found
	 */
	protected boolean isNodeChildOfNodeWithName(final String parentNodeName, final ASTNode node) {
		ASTNode parent = node.getParent();

		while (true) {
			if (parent != null) {
				if (parent instanceof MethodDeclaration) {
					final MethodDeclaration method = (MethodDeclaration) parent;
					if (method.getName().toString().equals(parentNodeName)) {
						return true;
					} else {
						return false;
					}
				} else {
					parent = parent.getParent();
				}
			} else {
				return false;
			}
		}
	}

}
