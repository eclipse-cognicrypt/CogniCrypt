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
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.client.DeveloperProject;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;

/**
 * @author André Sonntag
 *
 */
public class SecureSourceAnnotationManager {
	private String methodInvokeName = "";
	private static final String SECURESOURCE_ANNOTATION = "SecureSource";
	private static final String SECURESOURCE_ATTRIBUTE_INFO = "info";
	private static final String SECURESOURCE_ATTRIBUTE_VAR = "var";
	private static final String ANNOTATION_PACKAGE = "de.cognicrypt.staticanalyzer.annotations";
	private String SECURESOURCE_ATTRIBUTE_VAR_ANONYM_TEXT = "Anonymously return object from $method has been loaded securily";
	private String SECURESOURCE_ATTRIBUTE_VAR_KNOWN_TEXT = "Object has been loaded securily";

	
	private final DeveloperProject project;
	private String outerMethodName;
	private String varName;
	private int varIndex;
	private boolean sourceFound = false;

	public SecureSourceAnnotationManager(DeveloperProject project) {
		this.project = project;
	}

	public void annotateProblemSource(IMarker marker, String outerMethodName, String varName, int varIndex)
			throws CoreException, BadLocationException {
		ICompilationUnit unit = getCompilationUnitFromMarker(marker);
		this.outerMethodName = outerMethodName;
		this.varName = varName;

		annotateProblemSource(marker, unit);
		if (!hasSecureSourceAnnotationImport(unit)) {
			insertSecureSourceAnnotationImport(unit);
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

	private void annotateProblemSource(IMarker marker, ICompilationUnit sourceUnit)
			throws CoreException, BadLocationException {

		ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceUnit);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);

		unit.accept(new VariableNameVisitor(unit, sourceUnit));
		unit.accept(new MethodVisitor(unit, sourceUnit));
		unit.accept(new VariableDeclarationVisitor(unit, sourceUnit));
		unit.accept(new FieldDeclarationVisitor(unit, sourceUnit));
	}

	private class VariableNameVisitor extends ASTVisitor{
		private boolean sourceFound = false;
		private CompilationUnit unit = null;
		private ICompilationUnit sourceUnit = null;

		public VariableNameVisitor(CompilationUnit unit, ICompilationUnit sourceUnit) {
			super();
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}
		
		public boolean visit(ClassInstanceCreation node) {
			if (!sourceFound) {
				if (varName.startsWith("$")) {

					if (!isNodeChildOfNodeWithName(outerMethodName, node)) {
						return true;
					}

					ClassInstanceCreation instanceNode = (ClassInstanceCreation) node;
					if(!isMethodInvoke(instanceNode.arguments().get(varIndex).toString())) {
						varName = instanceNode.arguments().get(varIndex).toString();
					}
					else {
						methodInvokeName = instanceNode.arguments().get(varIndex).toString();
					}
					sourceFound = true;
					Activator.getDefault().logInfo("node instanceof ClassInstanceCreation -> " + varName);
					return false;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		public boolean visit(MethodInvocation node) {
			if (!sourceFound) {
				if (varName.startsWith("$")) {

					if (!isNodeChildOfNodeWithName(outerMethodName, node)) {
						return true;
					}

					MethodInvocation invoceNode = (MethodInvocation) node;
					if(!isMethodInvoke(invoceNode.arguments().get(varIndex).toString())) {
						varName = invoceNode.arguments().get(varIndex).toString();
					}
					else {
						methodInvokeName = invoceNode.arguments().get(varIndex).toString();
					}
					sourceFound = true;
					Activator.getDefault().logInfo("node instanceof MethodInvocation -> " + varName);
					return false;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		
		
		public boolean isMethodInvoke(String var) {
			if(var.endsWith(")")){
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	
	private class MethodVisitor extends ASTVisitor{
		private CompilationUnit unit = null;
		private ICompilationUnit sourceUnit = null;

		public MethodVisitor(CompilationUnit unit, ICompilationUnit sourceUnit) {
			super();
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}
		
		public boolean visit(MethodDeclaration node) {
			if (!sourceFound) {
				if (!node.isConstructor() && node.getName().toString().equals(outerMethodName)) {

					if(varName.startsWith("$")) {
						annotateProblemSource(node, unit, sourceUnit);
					}
					
					// iterate over all parameters
					List methodParameters = node.parameters();
					Iterator methodParameterIterator = methodParameters.iterator();
					while (methodParameterIterator.hasNext()) {
						SingleVariableDeclaration methodParameter = (SingleVariableDeclaration) methodParameterIterator
								.next();
						String currentParameterName = methodParameter.getName().toString();
						if (currentParameterName.equals(varName)) {

							Activator.getDefault().logInfo("visit(MethodDeclaration node)");
							annotateProblemSource(node, unit, sourceUnit);
							sourceFound = true;
							return false;
						}
					}
					return true;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	}
	
	private class VariableDeclarationVisitor extends ASTVisitor{
		private CompilationUnit unit = null;
		private ICompilationUnit sourceUnit = null;

		public VariableDeclarationVisitor(CompilationUnit unit, ICompilationUnit sourceUnit) {
			super();
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}
		
		public boolean visit(VariableDeclarationStatement node) {
			if (!sourceFound) {

				// Check if variable declation is in certain method
				if (!isNodeChildOfNodeWithName(outerMethodName, node)) {
					return true;
				}

				// iterate over all variables
				List variables = node.fragments();
				Iterator variablesIterator = variables.iterator();
				while (variablesIterator.hasNext()) {
					Object o = variablesIterator.next();

					if (o instanceof VariableDeclarationFragment) {
						VariableDeclarationFragment variable = (VariableDeclarationFragment) o;
						String currentVarName = variable.getName().toString();

						if (currentVarName.equals(varName)) {
							Activator.getDefault().logInfo(
									"visit(VariableDeclarationStatement node) -> looked for varName: " + varName);
							annotateProblemSource(node, unit, sourceUnit);
							sourceFound = true;
							return false;
						}
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}
	
	private class FieldDeclarationVisitor extends ASTVisitor{
		private CompilationUnit unit = null;
		private ICompilationUnit sourceUnit = null;

		public FieldDeclarationVisitor(CompilationUnit unit, ICompilationUnit sourceUnit) {
			super();
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}
		
		
		public boolean visit(FieldDeclaration node) {
			if (!sourceFound) {
			
					List variables = node.fragments();
					Iterator variablesIterator = variables.iterator();
					while (variablesIterator.hasNext()) {
						Object o = variablesIterator.next();

						if (o instanceof VariableDeclarationFragment) {
							VariableDeclarationFragment variable = (VariableDeclarationFragment) o;
							if (variable.getName().toString().equals(varName)) {
								sourceFound = true;
								annotateProblemSource(node, unit, sourceUnit);
								return false;
							}

						}
					}
					return true;
				}
			else {
				return false;
			}			
		}

	}

	public boolean isNodeChildOfNodeWithName(String parentNodeName, ASTNode node) {
		ASTNode parent = node.getParent();

		while (true) {
			if (parent != null) {
				if (parent instanceof MethodDeclaration) {
					MethodDeclaration method = (MethodDeclaration) parent;
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
	
	protected MemberValuePair createAnnotationMember(final AST ast, final String name, final String value) {
		final MemberValuePair mV = ast.newMemberValuePair();
		mV.setName(ast.newSimpleName(name));
		final StringLiteral stringLiteral = ast.newStringLiteral();
		stringLiteral.setLiteralValue(value);
		mV.setValue(stringLiteral);
		return mV;
	}

	private void annotateProblemSource(ASTNode node, CompilationUnit unit, ICompilationUnit sourceUnit) {

		Document sourceDocument = null;
		try {
			sourceDocument = new Document(sourceUnit.getSource());
		} catch (JavaModelException e) {
			Activator.getDefault().logError(e);
		}

		unit.recordModifications();
		AST ast = unit.getAST();

		final NormalAnnotation secureSourceAnnotation = ast.newNormalAnnotation();
		secureSourceAnnotation.setTypeName(ast.newSimpleName(SECURESOURCE_ANNOTATION));

		if (varName.startsWith("$")) {
			String infoText = SECURESOURCE_ATTRIBUTE_VAR_ANONYM_TEXT.replace("$method", methodInvokeName);
			secureSourceAnnotation.values().add(
					createAnnotationMember(ast, SECURESOURCE_ATTRIBUTE_INFO, infoText));
		} else {
			secureSourceAnnotation.values().add(
					createAnnotationMember(ast, SECURESOURCE_ATTRIBUTE_INFO, SECURESOURCE_ATTRIBUTE_VAR_KNOWN_TEXT));

		}
		secureSourceAnnotation.values().add(createAnnotationMember(ast, SECURESOURCE_ATTRIBUTE_VAR, varName));
		addAnnotation(node, secureSourceAnnotation, unit);

		try {
			Map<String, String> editorOptions = sourceUnit.getJavaProject().getOptions(true);
			editorOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_TYPE_ANNOTATION,
					JavaCore.DO_NOT_INSERT);

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

	private void addAnnotation(ASTNode node, IExtendedModifier annotation, CompilationUnit sourceUnit) {

		MethodDeclaration method = null;
		if (node instanceof MethodDeclaration) {
		    method = (MethodDeclaration) node;
			if (!hasSecureSourceAnnotation(method.modifiers())) {
		    method.modifiers().add(0, annotation);
			}
		}
		VariableDeclarationStatement localVariable = null;
		if (node instanceof VariableDeclarationStatement) {
			localVariable = (VariableDeclarationStatement) node;
			if (!hasSecureSourceAnnotation(localVariable.modifiers())) {
				localVariable.modifiers().add(annotation);
			}
		}
		SingleVariableDeclaration variable = null;
		if (node instanceof SingleVariableDeclaration) {
			variable = (SingleVariableDeclaration) node;
			if (!hasSecureSourceAnnotation(variable.modifiers())) {
				variable.modifiers().add(annotation);
			}
		}
		FieldDeclaration field = null;
		if (node instanceof FieldDeclaration) {
			field = (FieldDeclaration) node;
			if (!hasSecureSourceAnnotation(field.modifiers())) {
				field.modifiers().add(annotation);
			}
		}

	}

	private boolean hasSecureSourceAnnotation(List<Object> modifiers) {
		for (Object o : modifiers) {
			if (o.toString().equals("@" + SECURESOURCE_ANNOTATION)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasSecureSourceAnnotationImport(ICompilationUnit unit) throws CoreException {
		IImportDeclaration importDecalaration = unit.getImport(ANNOTATION_PACKAGE + "." + SECURESOURCE_ANNOTATION);
		if (importDecalaration.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private void insertSecureSourceAnnotationImport(ICompilationUnit unit) throws CoreException {
		unit.createImport(ANNOTATION_PACKAGE + "." + SECURESOURCE_ANNOTATION, null, null);
		unit.save(null, true);
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		editor.doSave(null);
	}

	public boolean addAdditionalFiles(String source) {
		if (source.isEmpty()) {
			return true;
		}
		try {
			final File[] members = Utils.getResourceFromWithin(source, "de.cognicrypt.staticanalyzer").listFiles();
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

}
