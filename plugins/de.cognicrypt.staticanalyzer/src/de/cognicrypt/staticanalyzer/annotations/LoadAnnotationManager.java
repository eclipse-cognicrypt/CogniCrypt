package de.cognicrypt.staticanalyzer.annotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
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
public class LoadAnnotationManager {

	private static final String LOAD_ANNOTATION = "Load";
	private static final String LOAD_ANNOTATION_PACKAGE = "de.cognicrypt.staticanalyzer.annotations";
	private final DeveloperProject project;

	
	public LoadAnnotationManager(DeveloperProject project) {
		this.project = project;
	}
	
	public void annotateProblemSource(IMarker marker, String varName) throws CoreException {
		ICompilationUnit unit = getCompilationUnitFromMarker(marker);
		annotateProblemSource(varName, unit);
		if(!hasLoadAnnotationImport(unit)) {
			insertLoadAnnotationImport(unit);
		}
	}

	private ICompilationUnit getCompilationUnitFromMarker(IMarker marker) throws CoreException {
		IJavaElement javaElement = JavaCore.create(marker.getResource());
		ICompilationUnit unit = null;
		if (javaElement instanceof ICompilationUnit) {
			unit = (ICompilationUnit) javaElement;
		}
		if (unit == null) {
			throw new CoreException(null);
		}
		return unit;
	}

	private void annotateProblemSource(String varName, ICompilationUnit sourceUnit) throws CoreException {
		ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceUnit);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);

		unit.accept(new ProblemSourceToAnnotateVisitor(varName, unit, sourceUnit));
	}

	private class ProblemSourceToAnnotateVisitor extends ASTVisitor {
		private boolean sourceFound = false;
		private CompilationUnit unit = null;
		private ICompilationUnit sourceUnit = null;
		private String varName = "";

		public ProblemSourceToAnnotateVisitor(String varName, CompilationUnit unit, ICompilationUnit sourceUnit) {
			super();
			this.varName = varName;
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}

		public boolean visit(MethodDeclaration node) {
			if (!sourceFound) {
				if (!node.isConstructor()) {
					List methodParameters = node.parameters();
					Iterator methodParameterIterator = methodParameters.iterator();
					while (methodParameterIterator.hasNext()) {
						SingleVariableDeclaration methodParameter = (SingleVariableDeclaration) methodParameterIterator
								.next();

						if (methodParameter.getName().toString().equals(varName)) {
							annotateProblemSource(methodParameter, varName, unit, sourceUnit);
							sourceFound = true;
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

	}

	private void annotateProblemSource(ASTNode node, String varName, CompilationUnit unit,
			ICompilationUnit sourceUnit) {

		Document sourceDocument = null;
		try {
			sourceDocument = new Document(sourceUnit.getSource());
		} catch (JavaModelException e) {
			Activator.getDefault().logError(e);
		}

		unit.recordModifications();

		AST ast = unit.getAST();
		MarkerAnnotation loadAnnotation = ast.newMarkerAnnotation();
		loadAnnotation.setTypeName(ast.newSimpleName(LOAD_ANNOTATION));
		loadAnnotation.setProperty("var", varName);
		addAnnotation(node, loadAnnotation);

		try {
			Map<String, String> editorOptions = sourceUnit.getJavaProject().getOptions(true);
			editorOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_TYPE_ANNOTATION,JavaCore.DO_NOT_INSERT);

			TextEdit edits = unit.rewrite(sourceDocument, editorOptions);
			edits.apply(sourceDocument);

			String newSource = sourceDocument.get();
			sourceUnit.getBuffer().setContents(newSource);
			sourceUnit.save(null, false);

			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			editor.doSave(null);
		} catch (Throwable t) {
			Activator.getDefault().logError(t.getMessage());

		}
	}

	private void addAnnotation(ASTNode node, IExtendedModifier annotation) {
		VariableDeclarationStatement localVariable = null;
		if (node instanceof VariableDeclarationStatement) {
			localVariable = (VariableDeclarationStatement) node;
			localVariable.modifiers().add(annotation);
		}
		SingleVariableDeclaration variable = null;
		if (node instanceof SingleVariableDeclaration) {
			variable = (SingleVariableDeclaration) node;
			variable.modifiers().add(annotation);
		}
		FieldDeclaration field = null;
		if (node instanceof FieldDeclaration) {
			field = (FieldDeclaration) node;
			field.modifiers().add(annotation);
		}
	}

	private boolean hasLoadAnnotationImport(ICompilationUnit unit) throws CoreException {
		IImportDeclaration importDecalaration = unit.getImport(LOAD_ANNOTATION_PACKAGE + "." + LOAD_ANNOTATION);
		if (importDecalaration.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private void insertLoadAnnotationImport(ICompilationUnit unit) throws CoreException {
		unit.createImport(LOAD_ANNOTATION_PACKAGE + "." + LOAD_ANNOTATION, null, null);
		unit.save(null, true);
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		editor.doSave(null);
	}

	public boolean addAdditionalFiles(String source) {
		if (source.isEmpty()) {
			return true;
		}
		try {
			final File[] members = Utils.getResourceFromWithin(source,"de.cognicrypt.staticanalyzer").listFiles();
			if (members == null) {
				Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_ADDITIONAL_RES_DIRECTORY);
			}
			for (int i = 0; i < members.length; i++) {
				File addFile = members[i];
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
	
	private boolean addAddtionalFile(File fileToBeAdded) throws IOException, CoreException {
		final IFolder libFolder = this.project.getFolder(Constants.pathsForLibrariesInDevProject);
		if (!libFolder.exists()) {
			libFolder.create(true, true, null);
		}

		final Path memberPath = fileToBeAdded.toPath();
		Files
			.copy(
				memberPath, new File(this.project
					.getProjectPath() + Constants.outerFileSeparator + Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + memberPath.getFileName()).toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		final String filePath = fileToBeAdded.toString();
		final String cutPath = filePath.substring(filePath.lastIndexOf(Constants.outerFileSeparator));
		if (Constants.JAR.equals(cutPath.substring(cutPath.indexOf(".")))) {
			if (!this.project.addJar(Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + fileToBeAdded.getName())) {
				return false;
			}
		}
		return true;
	}

}
