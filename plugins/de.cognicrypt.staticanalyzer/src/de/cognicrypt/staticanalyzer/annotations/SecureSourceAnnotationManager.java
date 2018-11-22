package de.cognicrypt.staticanalyzer.annotations;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.client.DeveloperProject;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.annotations.impl.AnnotationManager;

/**
 * This class figures out the error variable allocation side and adds the SecureSource(info="" var="") annotation
 * @author André Sonntag
 *
 */
public class SecureSourceAnnotationManager extends AnnotationManager {
	
	private static final String SECURESOURCE_ANNOTATION = "SecureSource";
	private static final String SECURESOURCE_ATTRIBUTE_INFO = "info";
	private static final String SECURESOURCE_ATTRIBUTE_VAR = "var";
	private static final String ANNOTATION_PACKAGE = "de.cognicrypt.staticanalyzer.annotations";
	private final String SECURESOURCE_ATTRIBUTE_VAR_ANONYM_TEXT = "Anonymously return object from $method has been loaded securily";
	private final String SECURESOURCE_ATTRIBUTE_VAR_KNOWN_TEXT = "Object has been loaded securily";
	private String methodInvokeName = "";
	private String outerMethodName;
	private String varName;
	private int varIndex;
	private boolean sourceFound = false;

	public SecureSourceAnnotationManager(final DeveloperProject project) {
		super(project);
	}

	/**
	 * This methods adds the annotation to the node
	 * 
	 * @param node node of the error variable
	 * @param annotation desired annotation
	 * @param sourceUnit ICompliationUnit of the source file
	 */
	@SuppressWarnings("unchecked")
	private void addAnnotation(final ASTNode node, final IExtendedModifier annotation,
			final CompilationUnit sourceUnit) {

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

	/**
	 * This method creates the SecureSource annotation to the AST
	 * 
	 * @param node node of the error variable
	 * @param unit CompliationUnit of the source file
	 * @param sourceUnit ICompliationUnit of the source file
	 */
	@SuppressWarnings("unchecked")
	private void annotateProblemSource(final ASTNode node, final CompilationUnit unit,
			final ICompilationUnit sourceUnit) {

		Document sourceDocument = null;
		try {
			sourceDocument = new Document(sourceUnit.getSource());
		} catch (final JavaModelException e) {
			Activator.getDefault().logError(e);
		}

		unit.recordModifications();
		final AST ast = unit.getAST();

		final NormalAnnotation secureSourceAnnotation = ast.newNormalAnnotation();
		secureSourceAnnotation.setTypeName(ast.newSimpleName(SecureSourceAnnotationManager.SECURESOURCE_ANNOTATION));

		if (this.varName.startsWith("$")) {
			final String infoText = this.SECURESOURCE_ATTRIBUTE_VAR_ANONYM_TEXT.replace("$method",
					this.methodInvokeName);
			secureSourceAnnotation.values().add(
					super.createMemberValuePair(ast, SecureSourceAnnotationManager.SECURESOURCE_ATTRIBUTE_INFO, infoText));
		} else {
			secureSourceAnnotation.values()
					.add(createMemberValuePair(ast, SecureSourceAnnotationManager.SECURESOURCE_ATTRIBUTE_INFO,
							this.SECURESOURCE_ATTRIBUTE_VAR_KNOWN_TEXT));

		}
		secureSourceAnnotation.values().add(
				createMemberValuePair(ast, SecureSourceAnnotationManager.SECURESOURCE_ATTRIBUTE_VAR, this.varName));
		addAnnotation(node, secureSourceAnnotation, unit);

		try {
			final Map<String, String> editorOptions = sourceUnit.getJavaProject().getOptions(true);
			editorOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_TYPE_ANNOTATION,
					JavaCore.DO_NOT_INSERT);

			final TextEdit edits = unit.rewrite(sourceDocument, editorOptions);
			edits.apply(sourceDocument);

			final String newSource = sourceDocument.get();
			sourceUnit.getBuffer().setContents(newSource);
			sourceUnit.save(null, false);

			final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			editor.doSave(null);
		} catch (final Throwable t) {
			Activator.getDefault().logError(t.getMessage());

		}
	}

	/**
	 * This method looks with ASTViewer for the real variable name and the allocation side
	 *
	 * @param marker problem Marker
	 * @param sourceUnit ICompliationUnit of the source file
	 * @throws CoreException
	 * @throws BadLocationException
	 */
	private void annotateProblemSource(final IMarker marker, final ICompilationUnit sourceUnit)
			throws CoreException, BadLocationException {

		final ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceUnit);
		final CompilationUnit unit = (CompilationUnit) parser.createAST(null);

		unit.accept(new VariableNameVisitor(unit, sourceUnit));
		unit.accept(new MethodVisitor(unit, sourceUnit));
		unit.accept(new VariableDeclarationVisitor(unit, sourceUnit));
		unit.accept(new FieldDeclarationVisitor(unit, sourceUnit));
	}

	/**
	 * This method adds the SecureSource annotation to the allocation side of the
	 * insecure variable
	 *
	 * @param marker problem Marker
	 * @param outerMethodName method name of the method where the error occurred in the method body
	 * @param varName name of the variable which triggerd an error
	 * @param varIndex index of the variable which triggered an error 
	 * @throws CoreException
	 * @throws BadLocationException
	 */
	public void annotateProblemSource(final IMarker marker, final String outerMethodName, final String varName, final int varIndex) throws CoreException, BadLocationException {
		final ICompilationUnit unit = super.getCompilationUnitFromMarker(marker);
		this.outerMethodName = outerMethodName;
		this.varName = varName;

		annotateProblemSource(marker, unit);
		if (!hasSecureSourceAnnotationImport(unit)) {
			insertSecureSourceAnnotationImport(unit);
		}
	}

	public boolean addAdditionalFiles(final String source) {
		return super.addAdditionalFiles(source);
	}
	
	protected boolean isNodeChildOfNodeWithName(final String parentNodeName, final ASTNode node) {
		return super.isNodeChildOfNodeWithName(parentNodeName, node);
	}
	
	private boolean hasSecureSourceAnnotation(final List<Object> modifiers) {
		return super.hasAnnotation(modifiers, SecureSourceAnnotationManager.SECURESOURCE_ANNOTATION);
	}

	private boolean hasSecureSourceAnnotationImport(final ICompilationUnit unit) throws CoreException {
		return super.hasAnnotationImport(unit, SecureSourceAnnotationManager.ANNOTATION_PACKAGE + "."
				+ SecureSourceAnnotationManager.SECURESOURCE_ANNOTATION);
	}

	private void insertSecureSourceAnnotationImport(final ICompilationUnit unit) throws CoreException {
		super.insertAnnotationImport(unit, SecureSourceAnnotationManager.ANNOTATION_PACKAGE + "."
				+ SecureSourceAnnotationManager.SECURESOURCE_ANNOTATION);
	}
	

	private class FieldDeclarationVisitor extends ASTVisitor {
		private CompilationUnit unit = null;
		private ICompilationUnit sourceUnit = null;

		public FieldDeclarationVisitor(final CompilationUnit unit, final ICompilationUnit sourceUnit) {
			super();
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}

		@Override
		public boolean visit(final FieldDeclaration node) {
			if (!SecureSourceAnnotationManager.this.sourceFound) {

				final List variables = node.fragments();
				final Iterator variablesIterator = variables.iterator();
				while (variablesIterator.hasNext()) {
					final Object o = variablesIterator.next();

					if (o instanceof VariableDeclarationFragment) {
						final VariableDeclarationFragment variable = (VariableDeclarationFragment) o;
						if (variable.getName().toString().equals(SecureSourceAnnotationManager.this.varName)) {
							SecureSourceAnnotationManager.this.sourceFound = true;
							annotateProblemSource(node, this.unit, this.sourceUnit);
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

	private class MethodVisitor extends ASTVisitor {
		private CompilationUnit unit = null;
		private ICompilationUnit sourceUnit = null;

		public MethodVisitor(final CompilationUnit unit, final ICompilationUnit sourceUnit) {
			super();
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}

		@Override
		public boolean visit(final MethodDeclaration node) {
			if (!SecureSourceAnnotationManager.this.sourceFound) {
				if (!node.isConstructor()
						&& node.getName().toString().equals(SecureSourceAnnotationManager.this.outerMethodName)) {

					if (SecureSourceAnnotationManager.this.varName.startsWith("$")) {
						annotateProblemSource(node, this.unit, this.sourceUnit);
					}

					// iterate over all parameters
					final List methodParameters = node.parameters();
					final Iterator methodParameterIterator = methodParameters.iterator();
					while (methodParameterIterator.hasNext()) {
						final SingleVariableDeclaration methodParameter = (SingleVariableDeclaration) methodParameterIterator
								.next();
						final String currentParameterName = methodParameter.getName().toString();
						if (currentParameterName.equals(SecureSourceAnnotationManager.this.varName)) {
							annotateProblemSource(node, this.unit, this.sourceUnit);
							SecureSourceAnnotationManager.this.sourceFound = true;
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

	private class VariableDeclarationVisitor extends ASTVisitor {
		private CompilationUnit unit = null;
		private ICompilationUnit sourceUnit = null;

		public VariableDeclarationVisitor(final CompilationUnit unit, final ICompilationUnit sourceUnit) {
			super();
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}

		@Override
		public boolean visit(final VariableDeclarationStatement node) {
			if (!SecureSourceAnnotationManager.this.sourceFound) {

				// Check if variable declation is in certain method
				if (!isNodeChildOfNodeWithName(SecureSourceAnnotationManager.this.outerMethodName, node)) {
					return true;
				}

				// iterate over all variables
				final List variables = node.fragments();
				final Iterator variablesIterator = variables.iterator();
				while (variablesIterator.hasNext()) {
					final Object o = variablesIterator.next();

					if (o instanceof VariableDeclarationFragment) {
						final VariableDeclarationFragment variable = (VariableDeclarationFragment) o;
						final String currentVarName = variable.getName().toString();

						if (currentVarName.equals(SecureSourceAnnotationManager.this.varName)) {
					
							annotateProblemSource(node, this.unit, this.sourceUnit);
							SecureSourceAnnotationManager.this.sourceFound = true;
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

	private class VariableNameVisitor extends ASTVisitor {
		private boolean sourceFound = false;

		public VariableNameVisitor(final CompilationUnit unit, final ICompilationUnit sourceUnit) {
			super();
		}

		public boolean isMethodInvoke(final String var) {
			if (var.endsWith(")")) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean visit(final ClassInstanceCreation node) {
			if (!this.sourceFound) {
				if (SecureSourceAnnotationManager.this.varName.startsWith("$")) {
					
					if (!isNodeChildOfNodeWithName(SecureSourceAnnotationManager.this.outerMethodName, node)) {
						return true;
					}

					final ClassInstanceCreation instanceNode = node;
					if (!isMethodInvoke(
							instanceNode.arguments().get(SecureSourceAnnotationManager.this.varIndex).toString())) {
						SecureSourceAnnotationManager.this.varName = instanceNode.arguments()
								.get(SecureSourceAnnotationManager.this.varIndex).toString();
					} else {
						SecureSourceAnnotationManager.this.methodInvokeName = instanceNode.arguments()
								.get(SecureSourceAnnotationManager.this.varIndex).toString();
					}
					this.sourceFound = true;
		
					return false;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		@Override
		public boolean visit(final MethodInvocation node) {
			if (!this.sourceFound) {
				if (SecureSourceAnnotationManager.this.varName.startsWith("$")) {

					if (!isNodeChildOfNodeWithName(SecureSourceAnnotationManager.this.outerMethodName, node)) {
						return true;
					}

					final MethodInvocation invoceNode = node;
					if (!isMethodInvoke(
							invoceNode.arguments().get(SecureSourceAnnotationManager.this.varIndex).toString())) {
						SecureSourceAnnotationManager.this.varName = invoceNode.arguments()
								.get(SecureSourceAnnotationManager.this.varIndex).toString();
					} else {
						SecureSourceAnnotationManager.this.methodInvokeName = invoceNode.arguments()
								.get(SecureSourceAnnotationManager.this.varIndex).toString();
					}
					this.sourceFound = true;
					return false;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}


}
