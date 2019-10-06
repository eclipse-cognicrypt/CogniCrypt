package de.cognicrypt.staticanalyzer.markerresolution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

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
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution;

import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.creator.CrySLRuleCreator;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.utils.Utils;
import de.cognicrypt.staticanalyzer.utilities.QuickFixUtils;

public class EnsuresPredicateFix implements IMarkerResolution {
	private final String label;
	private static final String INVOKE_METHOD_NAME = "ensuresPredicate";
	private static final String INJAR_CLASS_NAME = "Ensurer";
	private static final String VARIABLE_DECLARATION_NAME = "cognicrypt";
	private final CrySLRuleCreator creator;

	public EnsuresPredicateFix(final String label) {
		this.label = label;
		this.creator = new CrySLRuleCreator();
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void run(final IMarker marker) {
		
		JOptionPane optionPane = new JOptionPane("Now, CogniCrypt recognizes the object as secure", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		JDialog waitingDialog = optionPane.createDialog("CogniCrypt");
		waitingDialog.setModal(false);
		waitingDialog.setVisible(true);
		
		String errorVarName = "";
		int errorVarIndex = 0;
		String predicate = "";
		int lineNumber = 0;

		try {
			lineNumber = (int) marker.getAttribute(IMarker.LINE_NUMBER);
			predicate = (String) marker.getAttribute("predicate");
			errorVarName = (String) marker.getAttribute("errorParam");
//			errorVarIndex = (int) marker.getAttribute("errorParamIndex");
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		
		DeveloperProject devProject = new DeveloperProject(marker.getResource().getProject());
		ICompilationUnit sourceUnit = null;

		try {
			sourceUnit = QuickFixUtils.getCompilationUnitFromMarker(marker);
			QuickFixUtils.addAdditionalFiles("resources/PredicateEnsurer/Jar", "de.cognicrypt.staticanalyzer", devProject);

			if (!QuickFixUtils.hasJarImport(sourceUnit, Constants.PREDICATEENSURER_JAR_IMPORT)) {
				QuickFixUtils.insertJarImport(sourceUnit, Constants.PREDICATEENSURER_JAR_IMPORT);
			}
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		
		final ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceUnit);
		final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.accept(new ErrorSourceVisitor(unit, sourceUnit, errorVarName, errorVarIndex ,lineNumber, predicate));

		final SuppressWarningFix tempFix = new SuppressWarningFix("");
		tempFix.run(marker);
		Utils.getCurrentlyOpenEditor().doSave(null);
		
		String clientPath = Utils.getCurrentProject().getLocation().toOSString();
		String filePath = clientPath+Constants.outerFileSeparator+"."+"Ensurer"+Constants.cryslFileEnding;
		File rule = new File(filePath);
		if (rule.exists()) {
			updateRule(filePath, predicate);
		} else {
			createRule(filePath, predicate);
		}
		
		waitingDialog.setVisible(false);
		waitingDialog.dispose();
	}

	/**
	 * This method creates a CrySL rule for the class de.upb.cognicrypt.predicateensurer.Ensurer
	 * @param filePath
	 * @param pred
	 * @return 
	 */
	private boolean createRule(String filePath, String pred) {
		String spec = "de.upb.cognicrypt.predicateensurer.Ensurer";
		List<String> objects = new ArrayList<String>();
		objects.add("java.lang.Object obj;");
		objects.add("java.lang.String pred;");

		List<String> events = new ArrayList<String>();
		events.add("c: Ensurer();");
		events.add("e: ensuresPredicate(obj,pred);");

		String order = "c?, e";

		List<String> ensures = new ArrayList<String>();
		ensures.add("pred in {\""+ pred + "\"} => " + pred + "[obj];");
		return creator.createRule(filePath, spec, objects, events, order, null, null, ensures);
	}

	/**
	 * This method add a further predicate to the CrySL rule de.upb.cognicrypt.predicateensurer.Ensurer
	 * @param filePath
	 * @param pred
	 * @return
	 */
	private boolean updateRule(String filePath, String pred) {
		return creator.extendRule(filePath, "ENSURES", "pred in {\"" + pred + "\"} => " + pred + "[obj];");
	}
	
	
	/**
	 * This method creates and inserts the ensuresPredicate(predicate, errorVar) in
	 * the code
	 * 
	 * @param node
	 * @param unit
	 * @param sourceUnit
	 * @throws JavaModelException
	 * @throws IllegalArgumentException
	 * @throws MalformedTreeException
	 * @throws BadLocationException
	 */
	private void addMethodEnsuresPredicate(final ASTNode node, final CompilationUnit unit,
			final ICompilationUnit sourceUnit, final String varName,final int varIndex, final String predicate)
			throws JavaModelException, IllegalArgumentException, MalformedTreeException, BadLocationException {

		final AST ast = unit.getAST();
		final ASTRewrite rewriter = ASTRewrite.create(ast);
		final MethodInvocation ePInvocation = createEnsuresPredicateInvocation(ast, varName, predicate);

		if (node.getNodeType() == ASTNode.METHOD_INVOCATION || node.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION) {			
			
			if(varName.startsWith("varReplacer")) {
				//TODO enclose value in variable
			}
			
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
	 * 
	 * @param targetNode
	 * @return MethodDeclaration parent node
	 */
	private MethodDeclaration getMethodDeclarationParentNode(final ASTNode targetNode) {
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
	 * 
	 * @param ast
	 *            - current ast
	 * @param fragment
	 * @param type
	 *            - i.e. PrimitiveType.BOOLEAN
	 * @param modifier
	 *            - i.e. Modifier.PRIVATE
	 * @return
	 */
	private FieldDeclaration createFieldDeclaration(final AST ast, final VariableDeclarationFragment fragment,
			final Code type, final int modifier) {
		final FieldDeclaration declaration = ast.newFieldDeclaration(fragment);
		declaration.setType(ast.newPrimitiveType(type));
		declaration.modifiers().addAll(ASTNodeFactory.newModifiers(ast, modifier));
		return declaration;
	}

	/**
	 * This method builds a {@link VariableDeclarationFragment} object
	 * 
	 * @param ast
	 *            - current ast
	 * @param variableName
	 *            - declaration variable
	 * @return VariableDeclarationFragment obj
	 */
	private VariableDeclarationFragment createVariableDeclarationFragment(final AST ast,
			final String variableName, final Expression initializer) {
		final VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(variableName));
		fragment.setInitializer(initializer);
		return fragment;
	}

	/**
	 * This method builds a {@link MethodInvocation} object
	 * 
	 * @param ast
	 *            - current ast
	 * @param errorVarName
	 *            - error variable name
	 * @return ensuresPredicate(predicate, errorVarName) MethodInvocation
	 */
	private MethodInvocation createEnsuresPredicateInvocation(final AST ast, String varName, String predicate) {
		final Name classname = ast.newName(INJAR_CLASS_NAME);
		final MethodInvocation newInvocation = ast.newMethodInvocation();
		newInvocation.setExpression(classname);
		newInvocation.setName(ast.newSimpleName(INVOKE_METHOD_NAME));
		final StringLiteral literalPredicate = ast.newStringLiteral();
		literalPredicate.setLiteralValue(predicate);
		newInvocation.arguments().add(ast.newSimpleName(varName));
		newInvocation.arguments().add(literalPredicate);
		return newInvocation;
	}

	private class ErrorSourceVisitor extends ASTVisitor {

		private final String varName;
		private final int errorVarIndex;
		private final String predicate;
		private final int lineNumber;
		private final CompilationUnit unit;
		private final ICompilationUnit sourceUnit;
		private boolean sourceFound = false;

		public ErrorSourceVisitor(final CompilationUnit unit, final ICompilationUnit sourceUnit, final String varName, final int errorVarIndex,
				final int lineNumber, final String predicate) {
			this.varName = varName;
			this.errorVarIndex = errorVarIndex;
			this.predicate = predicate;
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
						addMethodEnsuresPredicate(node, this.unit, this.sourceUnit, this.varName, this.errorVarIndex ,this.predicate);
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
						addMethodEnsuresPredicate(node, this.unit, this.sourceUnit, this.varName, this.errorVarIndex ,this.predicate);
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
						addMethodEnsuresPredicate(node, this.unit, this.sourceUnit, this.varName, this.errorVarIndex ,this.predicate);
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
