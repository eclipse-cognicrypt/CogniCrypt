package de.cognicrypt.staticanalyzer.markerresolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.staticanalyzer.utilities.QuickFixUtils;

public class EnsuresPredicateFix implements IMarkerResolution{
	private final String label;
	private DeveloperProject devProject;
	private static String predicate;
	private static String errorParamVarName;
	private static final String INVOKE_METHOD_NAME = "ensuresPredicate";
	private static final String INJAR_CLASS_NAME = "CC";
	private static final String VARIABLE_DECLARATION_NAME = "cc";

	public EnsuresPredicateFix(final String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void run(final IMarker marker) {

		this.devProject = new DeveloperProject(marker.getResource().getProject());
		ICompilationUnit sourceUnit = null;
		int lineNumber = 0;

		try {
			sourceUnit = QuickFixUtils.getCompilationUnitFromMarker(marker);
			QuickFixUtils.addAdditionalFiles("resources/Predicate", "de.cognicrypt.staticanalyzer", this.devProject);
			if (!QuickFixUtils.hasJarImport(sourceUnit, "de.cognicrypt.staticanalyzer.*")) {
				QuickFixUtils.insertJarImport(sourceUnit, "de.cognicrypt.staticanalyzer.*");
			}
			lineNumber = (int) marker.getAttribute(IMarker.LINE_NUMBER);
			EnsuresPredicateFix.predicate = (String) marker.getAttribute("predicate");
			EnsuresPredicateFix.errorParamVarName = (String) marker.getAttribute("errorParam");

		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}

		final ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceUnit);
		final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.accept(new ErrorSourceVisitor(lineNumber, unit, sourceUnit));
	}

	/**
	 * This method creates and inserts the ensuresPredicate(predicate, errorVar) in the code
	 * @param node - node of the ErrorMarker
	 * @param unit
	 * @param sourceUnit
	 * @throws JavaModelException
	 * @throws IllegalArgumentException
	 * @throws MalformedTreeException
	 * @throws BadLocationException
	 */
	private static void addMethodEnsuresPredicate(final ASTNode node, final CompilationUnit unit,
			final ICompilationUnit sourceUnit)
			throws JavaModelException, IllegalArgumentException, MalformedTreeException, BadLocationException {

		final AST ast = unit.getAST();
		final ASTRewrite rewriter = ASTRewrite.create(ast);
		final MethodInvocation ePInvocation = createEnsuresPredicateInvocation(ast);

		if (node.getNodeType() == ASTNode.METHOD_INVOCATION || node.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION) {
			final MethodDeclaration parentMethod = getMethodDeclarationParentNode(node);
			final Statement ePStatement = ast.newExpressionStatement(ePInvocation);
			final ListRewrite listRewrite = rewriter.getListRewrite(parentMethod.getBody(), Block.STATEMENTS_PROPERTY);

			ASTNode index = node;
			if (index.getParent().getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT
					|| index.getParent().getNodeType() == ASTNode.EXPRESSION_STATEMENT) {
				index = index.getParent();
				if (node.getParent().getParent().getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT) {
					index = index.getParent();
				}
			}

			listRewrite.insertBefore(ePStatement, index, null);

		} else if (node.getNodeType() == ASTNode.FIELD_DECLARATION) {
			final VariableDeclarationFragment ePFragment = createVariableDeclarationFragment(ast,
					VARIABLE_DECLARATION_NAME, ePInvocation);
			final FieldDeclaration ePStatementFieldDec = createFieldDeclaration(ast, ePFragment, PrimitiveType.BOOLEAN,
					Modifier.PRIVATE);
			final ListRewrite listRewrite = rewriter.getListRewrite(((TypeDeclaration) unit.types().get(0)),
					TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			listRewrite.insertBefore(ePStatementFieldDec, node, null);

		} else {

		}

		final TextEdit edits = rewriter.rewriteAST();
		final Document document = new Document(sourceUnit.getSource());
		edits.apply(document);
		sourceUnit.getBuffer().setContents(document.get());
	}


	/**
	 * This method determines the next {@link MethodDeclaration} parent node
	 * @param targetNode 
	 * @return MethodDeclaration parent node
	 */
	private static MethodDeclaration getMethodDeclarationParentNode(final ASTNode targetNode) {
		ASTNode parent = targetNode.getParent();
		while (true) {
			if (parent != null) {
				if (parent instanceof MethodDeclaration) {
					return (MethodDeclaration) parent;
				} else {
					parent = parent.getParent();
				}
			} else {
				Activator.getDefault().logError("Parent is null");
				return null;
			}
		}

	}

	/**
	 * This method builds a {@link FieldDeclaration} object
	 * @param ast - current ast
	 * @param fragment
	 * @param type - i.e. PrimitiveType.BOOLEAN
	 * @param modifier - i.e. Modifier.PRIVATE
	 * @return
	 */
	private static FieldDeclaration createFieldDeclaration(final AST ast, final VariableDeclarationFragment fragment,
			final Code type, final int modifier) {
		final FieldDeclaration declaration = ast.newFieldDeclaration(fragment);
		declaration.setType(ast.newPrimitiveType(type));
		declaration.modifiers().addAll(ASTNodeFactory.newModifiers(ast, modifier));
		return declaration;
	}

	/**
	 * This method builds a {@link VariableDeclarationFragment} object
	 * @param ast - current ast
	 * @param variableName - declaration variable
	 * @return VariableDeclarationFragment obj
	 */
	private static VariableDeclarationFragment createVariableDeclarationFragment(final AST ast,
			final String variableName, final Expression initializer) {
		final VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(variableName));
		fragment.setInitializer(initializer);
		return fragment;
	}

	/**
	 * This method builds a {@link MethodInvocation} object
	 * @param ast - current ast
	 * @param errorVarName - error variable name
	 * @return ensuresPredicate(predicate, errorVarName) MethodInvocation
	 */
	private static MethodInvocation createEnsuresPredicateInvocation(final AST ast) {
		final Name cc = ast.newName(INJAR_CLASS_NAME);
		final MethodInvocation newInvocation = ast.newMethodInvocation();
		newInvocation.setExpression(cc);
		newInvocation.setName(ast.newSimpleName(INVOKE_METHOD_NAME));
		final StringLiteral literalVar = ast.newStringLiteral();
		literalVar.setLiteralValue(errorParamVarName);
		final StringLiteral literalPredicate = ast.newStringLiteral();
		literalPredicate.setLiteralValue(predicate);
		newInvocation.arguments().add(literalVar);
		newInvocation.arguments().add(literalPredicate);
		return newInvocation;
	}

	private static class ErrorSourceVisitor extends ASTVisitor {

		private final int lineNumber;
		private final CompilationUnit unit;
		private final ICompilationUnit sourceUnit;
		private boolean sourceFound = false;

		public ErrorSourceVisitor(final int lineNumber, final CompilationUnit unit, final ICompilationUnit sourceUnit) {
			this.lineNumber = lineNumber;
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}

		@Override
		public boolean visit(final MethodInvocation node) {
			if (!this.sourceFound) {
				if (this.lineNumber == this.unit.getLineNumber(node.getStartPosition())) {
					try {
						this.sourceFound = true;
						addMethodEnsuresPredicate(node, this.unit, this.sourceUnit);
						return false;
					} catch (JavaModelException | IllegalArgumentException | MalformedTreeException
							| BadLocationException e) {
						Activator.getDefault().logError(e);
					}
				} else {
					return true;
				}
			} else {
				return false;
			}

			return false;
		}

		@Override
		public boolean visit(final ClassInstanceCreation node) {
			if (!this.sourceFound) {
				if (this.lineNumber == this.unit.getLineNumber(node.getStartPosition())) {
					try {
						this.sourceFound = true;
						addMethodEnsuresPredicate(node, this.unit, this.sourceUnit);
						return false;
					} catch (JavaModelException | IllegalArgumentException | MalformedTreeException
							| BadLocationException e) {
						Activator.getDefault().logError(e);
					}
				} else {
					return true;
				}
			} else {
				return false;
			}

			return false;
		}

		@Override
		public boolean visit(final FieldDeclaration node) {
			if (!this.sourceFound) {
				if (this.lineNumber == this.unit.getLineNumber(node.getStartPosition())) {
					try {
						this.sourceFound = true;
						addMethodEnsuresPredicate(node, this.unit, this.sourceUnit);
						return false;
					} catch (JavaModelException | IllegalArgumentException | MalformedTreeException
							| BadLocationException e) {
						Activator.getDefault().logError(e);
					}
				} else {
					return true;
				}
			} else {
				return false;
			}

			return false;
		}
	}

}
